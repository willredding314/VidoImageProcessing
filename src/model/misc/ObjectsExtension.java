package model.misc;

/**
 * An extension to the {@link java.util.Objects} class that provides verification
 * of objects passed as parameters into functions via its static methods.
 */
public final class ObjectsExtension {

  // Prevent construction to ensure functionality is accessed only through
  // the static methods
  private ObjectsExtension() {}

  /**
   * Tests if any of the given objects are {@code null} and throws
   * and {@link IllegalArgumentException} if any of the objects in the
   * provided list are {@code null}.
   *
   * @param objects a list of objects to test against
   * @throws IllegalArgumentException if any of the objects are null or if
   *                                  {@code objects} is {@code null} itself
   */
  public static void requireNonnull(Object... objects) throws IllegalArgumentException {
    if (objects == null) {
      throw new IllegalArgumentException("Objects must be nonnull");
    }

    for (Object o : objects) {
      if (o == null) {
        throw new IllegalArgumentException("Expected all objects to be nonnull");
      }
    }
  }

  /**
   * Determines if the given value is {@code null} and
   * throws an exception if it is; otherwise the method returns the value.
   *
   * <p>This is a useful for returning the value you wish
   * to check as opposed to merely checking that a list of
   * values is nonnull. To check if a list of items has any null
   * value contained in it, use {@link ObjectsExtension#requireNonnull(Object...)}</p>
   *
   * @param value the value to check for nullity
   * @param <T> the type of the value
   * @return {@code value} is if it is nonnull
   * @throws IllegalArgumentException if {@code value} if it is nonnull
   */
  public static <T> T asNonnull(T value) throws IllegalArgumentException {
    if (value == null) {
      throw new IllegalArgumentException("Argument must be nonnull");
    }
    return value;
  }
}
