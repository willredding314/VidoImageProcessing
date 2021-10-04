package model.creation;

import model.image.VLayeredImage;

/**
 * Creates multi-layered images from an arbitrary source encoding multi-layer image data.
 * A {@link VLayeredImageProvider} acts as a link between the program's internal representation
 * of multi-layers images and external representations. of those images.
 *
 * <p>A {@link VLayeredImageProvider} can construct images from a source containing image data
 * encoded in a variety of forms. For example, one such image provider might load multi-layer images
 * from multiple files on disk, one for each layer.
 *
 * <p>A {@link VLayeredImageProvider} differs from a {@link VImageProvider} in that it produces
 * images with more dimensionality than the {@link VImageProvider}. That is, images produced
 * by a {@link VLayeredImageProvider} are actually <em>composed</em> of several different
 * {@link model.image.VLayer}s, whereas a {@link VImageProvider} produces a single image sheet.</p>
 */
public interface VLayeredImageProvider {
  /**
   * Produces a new multi-layer image from the data wrapped by this provider.
   *
   * @return a new multi-layer image encoded in a data source
   * @throws ImageExtractionException if the image could not be extracted because the data could not
   *                                  be read properly
   */
  VLayeredImage extractLayeredImage() throws ImageExtractionException;
}
