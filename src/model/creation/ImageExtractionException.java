package model.creation;

import model.misc.ObjectsExtension;

/**
 * An exception that is thrown when extracting images from
 * external sources fails.
 */
public final class ImageExtractionException extends RuntimeException {

  /**
   * Construct a new {@code ImageExtractionException} with the
   * given message.
   *
   * @param message the message associated with the error
   * @throws IllegalArgumentException if {@code message} is {@code null}
   */
  public ImageExtractionException(String message) {
    super(ObjectsExtension.asNonnull(message));
  }
}
