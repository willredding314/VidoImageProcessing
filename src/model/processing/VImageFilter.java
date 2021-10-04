package model.processing;

import java.util.Arrays;
import model.image.VImage;
import model.image.VMutableImage;
import model.image.pixel.ChannelType;
import model.image.pixel.VPixel;
import model.image.pixel.VPixelCoordinate;
import model.image.pixel.VRGBPixel;

import model.misc.ComparableUtils;
import model.misc.ObjectsExtension;

/**
 * Represents a filter that can be applied to a {@link VImage}. A filter modifies each pixel in an
 * image based on a matrix of values called a kernel; the pixels surrounding each individual pixel;
 * and a color channel.
 *
 * <p>In image processing, a <em>kernel</em> is a small matrix. A filter that uses a kernel
 * to modify an image does so by changing the value of a particular color channel for each pixel in
 * the image. For a kernel with odd dimensions, this is accomplished by conceptually centering the
 * kernel on the pixel whose contents are to modified and multiplying the values in the kernel with
 * corresponding channel values of pixels in the vicinity of the targeted pixel.</p>
 */
public class VImageFilter implements VImageContentOperation {

  private final double[][] kernel;
  private final ChannelType channel;
  private final VKernelResolution kernelResolution;

  /**
   * A convenience constructor with a kernel and a channel to apply to. The constructor creates a
   * filter with a kernel edge mode of {@link KernelEdgeMode#DONT_INCLUDE}.
   *
   * @param kernel  the matrix of multipliers for the kernel.
   * @param channel the channel to apply the matrix to.
   * @throws IllegalArgumentException if the kernel is not a square; if the channel is unsupported;
   *                                  if any arguments are {@code null}; if the kernel does not
   *                                  have odd dimensions; or if kernel contains a {@code null}
   */
  public VImageFilter(double[][] kernel, ChannelType channel) throws IllegalArgumentException {
    this(kernel, channel, KernelEdgeMode.DONT_INCLUDE);
  }

  /**
   * A convenience constructor with a kernel and a channel to apply to and a parameter for
   * describing the type of out-of-bounds access.
   *
   * @param kernel   the matrix of multipliers for the kernel.
   * @param channel  the channel to apply the matrix to.
   * @param edgeMode describes at a high-level how the kernel should handle edge cases
   * @throws IllegalArgumentException if the kernel is not a square; if any arguments are {@code
   *                                  null}; if the kernel does not have odd dimensions; or if the
   *                                  edge mode is unsupported
   */
  public VImageFilter(double[][] kernel, ChannelType channel, KernelEdgeMode edgeMode)
      throws IllegalArgumentException {
    this(kernel, channel, VKernelResolutionFactory.resolutionFor(edgeMode));
  }

  /**
   * A constructor for the VImageKernel, with a kernel, a channel to apply to, and an object that
   * deals with pixel accesses.
   *
   * @param kernel       the matrix of multipliers for the kernel.
   * @param channel      the channel to apply the matrix to.
   * @param kernelAction describes how the kernel is executed throughout the images it operates on
   * @throws IllegalArgumentException if the kernel is not a square; if any arguments are {@code
   *                                  null}; or if the kernel does not have odd dimensions
   */
  public VImageFilter(double[][] kernel, ChannelType channel, VKernelResolution kernelAction)
      throws IllegalArgumentException {

    ObjectsExtension.requireNonnull(kernel, channel, kernelAction);
    ObjectsExtension.requireNonnull((Object[]) kernel);

    int gridHeight = kernel.length;

    if (gridHeight % 2 == 0) {
      throw new IllegalArgumentException("Kernel must have "
          + "odd dimensions.");
    }

    for (double[] doubles : kernel) {
      if (doubles.length != gridHeight) {
        throw new IllegalArgumentException("Kernel must be "
            + "in a square shape.");
      }
    }

    // Arrays are references in Java
    this.kernel = Arrays.stream(kernel).map(
        (a) -> Arrays.copyOf(a, a.length)
    ).toArray(double[][]::new);

    this.channel = channel;
    this.kernelResolution = kernelAction;
  }

  @Override
  public VImage operateOn(VImage image) throws IllegalArgumentException {
    VMutableImage mutImage = image.mutableCopy();

    int width = image.getWidth();
    int height = image.getHeight();
    int kernelSize = kernel.length;
    int diff = (kernelSize - 1) / 2;

    for (int i = 0; i < height; i += 1) {
      for (int j = 0; j < width; j += 1) {

        double total = 0;

        for (int k = 0; k < kernelSize; k += 1) {
          for (int l = 0; l < kernelSize; l += 1) {
            int colorVal = kernelResolution.getColorVal(image,
                new VPixelCoordinate(i + k - diff,
                    j + l - diff), this.channel);

            total += (kernel[k][l] * colorVal);
          }
        }

        VPixel oldPixel = mutImage.getPixelAt(new VPixelCoordinate(i, j)).orElseThrow();
        VPixel newPixel;

        switch (this.channel) {
          case RED:
            newPixel = new VRGBPixel((int) Math.round(total), oldPixel.getGreen(),
                oldPixel.getBlue());
            break;
          case GREEN:
            newPixel = new VRGBPixel(oldPixel.getRed(), (int) Math.round(total),
                oldPixel.getBlue());
            break;
          case BLUE:
            newPixel = new VRGBPixel(oldPixel.getRed(),
                oldPixel.getGreen(), (int) Math.round(total));
            break;
          default:
            throw new IllegalArgumentException("Channel selected "
                + "is invalid.");
        }

        mutImage.setPixel(clampPixel(newPixel), new VPixelCoordinate(i, j));
      }
    }

    return mutImage;
  }

  /**
   * Clamps a single pixel, ensuring that none of its channels exceed 255 or go below 0.
   *
   * @param currPixel the unclamped pixel
   * @return a clamped pixel
   */
  private VPixel clampPixel(VPixel currPixel) {
    int red = ComparableUtils.clamp(currPixel.getRed(), 0, 255);
    int green = ComparableUtils.clamp(currPixel.getGreen(), 0, 255);
    int blue = ComparableUtils.clamp(currPixel.getBlue(), 0, 255);

    return new VRGBPixel(red, green, blue);
  }
}
