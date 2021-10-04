package model.persistence;

import model.misc.ObjectsExtension;

/**
 * An exception that is thrown to indicate that saving
 * a {@link model.image.VImage} has failed.
 */
public final class ImageSavingException extends RuntimeException {

  /**
   * Construct a new {@code ImageSavingException} with the
   * given message.
   *
   * @param message the message associated with the error
   * @throws IllegalArgumentException if {@code message} is {@code null}
   */
  public ImageSavingException(String message) {
    super(ObjectsExtension.asNonnull(message));
  }
}