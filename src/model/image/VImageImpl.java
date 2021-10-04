package model.image;

import java.util.function.Function;
import model.image.pixel.VPixel;
import model.image.pixel.VPixelCoordinate;

/**
 * An immutable image which supports efficient 2D image access.
 *
 * <p>A {@link VImageImpl} is a {@link VImage} whose contents are managed
 * in an inherently 2D format. That is, a {@link VImageImpl} processes messages sent to working in
 * 2D efficiently because is it is structured to handle such requests.</p>
 */
public class VImageImpl extends AbstractVImage {

  /**
   * Construct a new {@link VImageImpl} with the given width and height.
   *
   * <p>The pixels in this image are initialized to {@link java.awt.Color#white}</p>
   *
   * @param width  the width of the image
   * @param height the height of the image
   * @throws IllegalArgumentException if either width or height are negative
   */
  public VImageImpl(int width, int height) throws IllegalArgumentException {
    super(width, height);
  }

  /**
   * Construct an immutable image from the contents of the given image.
   *
   * @param other the image whose contents should be copied into this image.
   */
  public VImageImpl(VImage other) {
    super(other);
  }

  /**
   * Construct a new {@link VImageImpl} with the given width, height and per-pixel transformation.
   *
   * @param width             the width of the image
   * @param height            the height of the image
   * @param perPixelGenerator a function which maps coordinates to colors
   * @throws IllegalArgumentException if either width or height are negative or if {@code
   *                                  perPixelGenerator} is {@code null}.
   */
  public VImageImpl(int width, int height,
      Function<VPixelCoordinate, VPixel> perPixelGenerator) {
    super(width, height, perPixelGenerator);
  }
}
