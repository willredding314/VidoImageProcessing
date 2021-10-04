package model.persistence;

import model.image.VLayeredImage;

/**
 * Describes a process of saving layered image content
 * described by the {@link VLayeredImage} interface.
 *
 * <p>Each {@link VLayeredImageSaver} describes a method of saving an
 * image that is composed of several different layers of images.
 * For example, image savers might save their image content to disk
 * in a specific directory structure for re-loading later.
 */
public interface VLayeredImageSaver {
  /**
   * Saves the given multi-layered image.
   *
   * <p>Each implementation of {@link VLayeredImageSaver} decides how
   * it wants to save images. Therefore, clients should not expect
   * that this method run quickly. As a synchronous function call,
   * the calling thread will be busy with work until the save
   * completes, which could be some time for image writes to disk
   * for example</p>
   *
   * @param image the image whose contents should be saved
   * @throws IllegalArgumentException if the image is null
   * @throws ImageSavingException if the image could not successfully be saved
   */
  void saveImage(VLayeredImage image) throws IllegalArgumentException, ImageSavingException;
}
