package model.processing;

import model.image.VImage;
import model.image.pixel.ChannelType;
import model.image.pixel.VPixelCoordinate;

/**
 * Describes a method of handling edge cases in image kernel operations.
 *
 * <p>All image processing filters using a kernel need to defined
 * how their kernel behaves with edge pixels. The value the kernel uses when dealing with pixels
 * that are out-of-bounds can change from filter to filter. A {@link VKernelResolution} is a method
 * of applying a kernel to an image while handling resolving kernel accesses</p>
 */
public interface VKernelResolution {

  /**
   * Returns the color value of a pixel in an image based on a location in the image and a channel
   * time. If the pixel coordinates do not exist in the image, the value that should be used by the
   * kernel for the entry is returned based on the out-of-bounds behavior defined.
   *
   * @param image  the source image for the pixel
   * @param coords the coordinates of the pixel in the image
   * @param type   the channel type needed from the sought pixel
   * @return the color value of the specified pixel, or an out-of-bounds value.
   * @throws IllegalArgumentException if any arguments are {@code null}
   */
  int getColorVal(VImage image, VPixelCoordinate coords, ChannelType type)
      throws IllegalArgumentException;
}
