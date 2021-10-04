package model.processing;

import model.image.VImage;
import model.image.pixel.ChannelType;
import model.image.pixel.VPixel;
import model.image.pixel.VPixelCoordinate;
import model.misc.ObjectsExtension;

/**
 * Represents an object that applies a single kernel value to an image, handling out-of-bounds cases
 * for when the kernel value does not align with the pixel.
 */
public class KernelResolutionExclude implements VKernelResolution {

  /**
   * Returns the color value of a pixel in an image based on a location in the image and a channel
   * time. If the pixel coordinates do not exist in the image, the out of bounds response is
   * returned based on the OutOfBoundsType.
   *
   * @param image  the source image for the pixel.
   * @param coords the coordinates of the pixel in the image
   * @param type   the channel type needed from the sought pixel
   * @return the color value of the specified pixel, or an out of bounds value.
   */
  public int getColorVal(VImage image, VPixelCoordinate coords,
      ChannelType type) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(image, coords, type);

    if (image.getPixelAt(coords).isEmpty()) {
      return 0;
    } else {
      VPixel currPixel = image.getPixelAt(coords).get();

      switch (type) {
        case RED:
          return currPixel.getRed();
        case GREEN:
          return currPixel.getGreen();
        case BLUE:
          return currPixel.getBlue();
        default:
          throw new IllegalArgumentException("Channel selected "
              + "is invalid.");
      }
    }
  }
}
