package model.creation;

import model.image.VImage;

/**
 * A creator of images from an arbitrary source encoding image data. A {@link VImageProvider} acts
 * as a link between the program's internal representation of images and the system's
 * representation.
 *
 * <p>An image provider defines an interface for creating new images from
 * arbitrary sources. Image data can be stored or provided in several different via different files
 * formats (e.g. .png, .jpeg, .ppm, etc.) or via different methods entirely (perhaps as a custom
 * structure in memory). Any implementation of {@link VImageProvider} describes a particular
 * translation from one storage format of an image to an {@link VImage} object in memory.</p>
 *
 * <p>Note that the cost of extracting an image from a source should not be
 * depended upon. It can be expensive to extract an image for a file.</p>
 */
public interface VImageProvider {

  /**
   * Produces a new image from the data wrapped by this provider.
   *
   * @return a new image encoded in a data source
   * @throws ImageExtractionException if the image could not be extracted because the data could not
   *                                  be read properly
   */
  VImage extractImage() throws ImageExtractionException;
}
