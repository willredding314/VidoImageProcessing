package model.image;

import java.awt.Color;
import java.util.Optional;
import java.util.function.Function;
import model.image.pixel.VPixel;
import model.image.pixel.VPixelCoordinate;
import model.image.pixel.VRGBPixel;
import model.misc.ObjectsExtension;

/**
 * An abstract base class that provides shared implementations of 2D image data storage formats.
 *
 * <p>Subclasses of {@link AbstractVImage} provide efficient access to image
 * data via the inherently 2D operations by virtue of their representation</p>
 */
public abstract class AbstractVImage implements VImage {

  //   Each [ ... ] is `height` long
  // [ [ ... ], ..., [ ... ] ]
  // | ------- width ------- |
  protected final VPixel[][] pixels;

  protected final int width;
  protected final int height;

  /**
   * Construct a new image by copying the contents of the given image.
   *
   * @param other the image whose contents to copy
   * @throws IllegalArgumentException if {@code other} is {@code null}
   */
  public AbstractVImage(VImage other) throws IllegalArgumentException {
    // DO NOT call other#immutableCopy() or other.immutableCopy()!
    // This will lead to infinite recursion
    this(ObjectsExtension.asNonnull(other).getWidth(),
        ObjectsExtension.asNonnull(other).getHeight(), (coord) -> other
            .getPixelAt(coord)
            .orElseThrow(() -> new AssertionError("Provided image does not have a "
                + "color at a valid coordinate within it")));
  }

  /**
   * Construct a new {@link AbstractVImage} with the given width and height.
   *
   * <p>The pixels in this image are initialized to {@link java.awt.Color#white}</p>
   *
   * @param width  the width of the image
   * @param height the height of the image
   * @throws IllegalArgumentException if either width or height are negative
   */
  public AbstractVImage(int width, int height) throws IllegalArgumentException {
    this(width, height, (c) -> new VRGBPixel(Color.white));
  }

  /**
   * Construct a new {@link AbstractVImage} with the given width, height and per-pixel
   * transformation.
   *
   * @param width             the width of the image
   * @param height            the height of the image
   * @param perPixelGenerator a function which maps coordinates to colors
   * @throws IllegalArgumentException if either width or height are negative
   *                                  or if {@code perPixelGenerator} is {@code null}.
   */
  public AbstractVImage(int width, int height,
      Function<VPixelCoordinate, VPixel> perPixelGenerator) {
    ObjectsExtension.requireNonnull(perPixelGenerator);

    if (width <= 0) {
      throw new IllegalArgumentException("Width must be positive");
    }

    if (height <= 0) {
      throw new IllegalArgumentException("Height must be positive");
    }

    this.width = width;
    this.height = height;
    this.pixels = new VPixel[width][];

    for (int i = 0; i < width; i += 1) {
      this.pixels[i] = new VPixel[height];

      for (int j = 0; j < height; j += 1) {
        this.pixels[i][j] = perPixelGenerator.apply(new VPixelCoordinate(j, i));
      }
    }
  }

  @Override
  public VMutableImage mutableCopy() {
    return new VMutableImageImpl(this);
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public int numPixels() {
    return this.width * this.height;
  }

  @Override
  public boolean contains(VPixelCoordinate coordinate) {
    ObjectsExtension.requireNonnull(coordinate);
    int rowIndex = coordinate.getRowIndex();
    int colIndex = coordinate.getColumnIndex();
    return rowIndex >= 0 && rowIndex < this.height && colIndex >= 0 && colIndex < this.width;
  }

  @Override
  public Optional<VPixel> getPixelAt(VPixelCoordinate location) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(location);

    if (!this.contains(location)) {
      return Optional.empty();
    }
    return Optional.of(this.pixels[location.getColumnIndex()][location.getRowIndex()]);
  }
}
