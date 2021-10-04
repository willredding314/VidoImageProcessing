package model.misc;

/**
 * A utility class for working with comparable types.
 */
public class ComparableUtils {

  /**
   * Performs a clamp function to the given value whose value is constrained between two bounds.
   *
   * <p>CAUTION! If you use clamp with floating-point arithmetic, note that
   * floats do not act precisely as the real numbers do. That is, if you attempt to clamp a value
   * between two bounds which appear "equal", you may get an {@link IllegalArgumentException}
   * pointing out that the upper bound must be greater than the lower bound</p>
   *
   * @param value the value that should be clamped
   * @param lower the smallest the resulting value could be
   * @param upper the largest the resulting value could be
   * @param <T>   a comparable type
   * @return {@code value} if {@code lower} <= {@code value} <= {@code upper}, {@code lower} if
   * {@code value} < {@code lower}, and {@code upper} if {@code upper} < {@code value}
   * @throws IllegalArgumentException if the {@code upper} < {@code lower}.
   */
  public static <T extends Comparable<T>> T clamp(T value, T lower, T upper)
      throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(value, lower, upper);

    if (lower.compareTo(upper) > 0) {
      throw new IllegalArgumentException("Lower bound must not "
          + "compare greater than the upper bound");
    }


    return max(lower, min(upper, value));
  }

  /**
   * Gets the maximum of two values that can be compared to one another.
   *
   * @param a the first value to compare
   * @param b the second value to compare
   * @param <T> a comparable type
   * @return whichever value compares greater. If the two
   *         values compare to 0, the first value is returned
   */
  public static <T extends Comparable<T>> T max(T a, T b) {
    ObjectsExtension.requireNonnull(a, b);
    return a.compareTo(b) >= 0 ? a : b;
  }

  /**
   * Gets the minimum of two values that can be compared to one another.
   *
   * @param a the first value to compare
   * @param b the second value to compare
   * @param <T> a comparable type
   * @return whichever value compares greater. If the two
   *         values compare to 0, the first value is returned
   */
  public static <T extends Comparable<T>> T min(T a, T b) {
    ObjectsExtension.requireNonnull(a, b);
    return a.compareTo(b) <= 0 ? a : b;
  }
}