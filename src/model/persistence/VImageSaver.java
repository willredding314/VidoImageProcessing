package model.persistence;

import model.image.VImage;

/**
 * Describes a process of saving images.
 *
 * <p>Each {@link VImageSaver} describes a method of saving an
 * image. For example, image savers might save their image
 * content to disk in a variety of file formats. See
 * {@link model.creation.PPMImageManager} for an example</p>
 */
public interface VImageSaver {
  /**
   * Saves the given image.
   *
   * <p>Each implementation of {@link VImageSaver} decides how
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
  void saveImage(VImage image) throws IllegalArgumentException, ImageSavingException;
}
