package model.image;

import model.image.pixel.VPixel;
import model.image.pixel.VPixelCoordinate;

/**
 * A representation of image data which can be modified in-flight.
 *
 * <p>A {@link VMutableImage} is the mutable counterpart of {@link VImage}.
 * It supports changing color values within the image at the pixel level.
 * Mutable images are best suited for modification. When you apply one or more
 * {@link model.processing.VImageContentOperation}s to an immutable image,
 * mutable copies of immutable image instances are temporarily created for
 * modification.
 *
 * <p>It is also possible to provide clients with an immutable version of a
 * mutable image. Such an image has the same contents as the image it
 * copied but whose contents cannot otherwise be modified by clients.
 *
 * <p>A mutable copy of a mutable image only has the effect of producing
 * a mutable clone of this image. This may desired if a mutable image can serve
 * as a look-up table for pixel data in algorithms that operate on images
 * dynamically for example.</p>
 */
public interface VMutableImage extends VImage {

  /**
   * Creates a copy of this image whose contents cannot be changed.
   *
   * <p>Immutable image copies are best suited
   * for when a client wishes to enforce that an image
   * in memory not have its contents further changed</p>
   *
   * @return a new image whose contents and size are
   *         the same as this image but whose contents
   *         cannot be changed
   */
  VImage immutableCopy();

  /**
   * Reassigns the pixel at a location with a new one.
   *
   * @param pixel the new pixel that will replace some specified pixel
   * @param coordinate a coordinate identifying a location in the image
   * @throws IllegalArgumentException if any arguments are {@code null} or
   *                                  if {@code coordinate} refers to a pixel
   *                                  outside of the bounds of the image
   */
  void setPixel(VPixel pixel, VPixelCoordinate coordinate) throws IllegalArgumentException;
}
