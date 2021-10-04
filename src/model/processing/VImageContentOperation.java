package model.processing;

import model.image.VImage;

/**
 * Describes a method of transforming the pixel color contents of a {@link VImage}.
 *
 * <p>A {@link VImageContentOperation} is a method of
 * operating on 2D image data. Operations can map both their input's pixel colors and dimensions to
 * different values as output. This means content operations can support actions such as cropping
 * and replacing regions of pixel data with other images.
 *
 * <p>Use {@link VImageContentOperation}s one after another to perform
 * a sequence of changes to an image. For example, you might apply a sharpening filter on an image
 * and then apply a blur effect on the sharpened result.
 *
 * <p>A {@link VImageContentOperation} is intended to change
 * the <em>contents</em> of an image, i.e. the composition and/or number of pixels in an image.
 * However, operations can act as geometric transformations as well. Consider an operation that
 * rotates and image around 90 degrees clockwise.</p>
 */
@FunctionalInterface
public interface VImageContentOperation {

  /**
   * Conceptually applies this operation on the image to produce a new image resulting from the
   * operation.
   *
   * @param image the image to which this transformation will be applied
   * @return a new image whose contents are the result of operating as
   *         described in this particular way
   * @throws IllegalArgumentException if {@code image} is {@code null}
   */
  VImage operateOn(VImage image) throws IllegalArgumentException;

}
