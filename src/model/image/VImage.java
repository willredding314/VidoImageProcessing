package model.image;

import java.util.Optional;
import model.image.pixel.VPixel;
import model.image.pixel.VPixelCoordinate;

/**
 * A representation of image data as a structured 2D grid.
 *
 * <p>A {@link VImage} represents image data, typically derived from files
 * of different extensions, in a uniform manner within the program. Images
 * are effectively chunks of pixel data enabled with support for conceptually
 * 2D operations. For example, though the storage of an image may be a single
 * linear buffer, concepts such as (a fixed) width and height exist and
 * are defined for images, whereas such notions may not be defined
 * for arbitrarily formatted (or unformatted) data.
 *
 * <p>A {@link VImage} is read-only: you cannot write to pixels in a {@link VImage};
 * however, all {@link VImage}s also support creating mutable versions of
 * themselves via the {@link VImage#mutableCopy()} method. This allows clients
 * to effectively manipulate image data of an immutable image without altering
 * the contents of the image itself. The intent is to provide clients
 * with the ability to manipulate images, while also starting with an image whose contents
 * are known, with a series of {@link model.processing.VImageContentOperation}s.
 * Operations are the main procedure for manipulating and working with images. For
 * more details on image operations, see {@link model.processing.VImageContentOperation}.
 *
 * <p>For more details on mutable images, see {@link VMutableImage}.
 *
 * <p>Two images are considered equal if they have the same width and height and
 * have the same colored pixels in the same locations.</p>
 */
public interface VImage {

  /**
   * Creates a copy of this image whose contents can be changed.
   *
   * <p>Make a mutable copy of an image when you want to modify
   * an image whose contents are known to you at.</p>
   *
   * @return a new image whose contents and size are
   *         the same as this image
   */
  VMutableImage mutableCopy();

  /**
   * Computes the width of the image in pixels.
   *
   * <p>Note that width is assumed to be measured horizontally</p>
   *
   * @return the width of the image in pixels
   */
  int getWidth();

  /**
   * Computes the height of the image in pixels.
   *
   * <p>Note that height is assumed to be measured vertically</p>
   *
   * @return the height of the image in pixels
   */
  int getHeight();

  /**
   * Determines the total number of pixels in this image.
   *
   * @return the total number of pixels in this image
   */
  int numPixels();

  /**
   * Determines whether or not the given coordinate refers
   * to a pixel value in this image.
   *
   * <p>A coordinate is contained in an image if its
   * row index is contained in the range [0, height)
   * and if its column index is contained in the
   * range [0, width) </p>
   *
   * @param coordinate the coordinate to test for intersection
   * @return whether or not the coordinate is contained in this image
   * @throws IllegalArgumentException if {@code coordinate} is {@code}
   */
  boolean contains(VPixelCoordinate coordinate) throws IllegalArgumentException;

  /**
   * Retrieves the pixel at the given location in the image, if such
   * a pixel exists.
   *
   * @param location a specific point in the image identifying
   *                 the pixel of interest
   * @return the pixel at the given location in the image if the location
   *         identifies a pixel within the image, and {@link Optional#empty()}
   *         if no such pixel is identified. A pixel coordinate is a zero-based
   *         row and column index pair, and is considered valid if the indices
   *         are contained in the intervals [0, width()) and [0, height())
   *         respectively. A valid coordinate is guaranteed to return a
   *         pixel value
   * @throws IllegalArgumentException if {@code location} is null
   */
  Optional<VPixel> getPixelAt(VPixelCoordinate location) throws IllegalArgumentException;

}
