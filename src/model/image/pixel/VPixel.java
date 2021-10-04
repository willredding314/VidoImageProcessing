package model.image.pixel;


/**
 * The smallest visual component on a screen.
 *
 * <p>A {@link VPixel} represents the state of a single pixel on
 * the screen of a device. A {@link VPixel} stores color information
 * about the display but is otherwise unassociated with any particular
 * location on a screen. Rather, {@link VPixel}s are light-weight
 * data types for keeping track of solely color information. Pixels are
 * also void of any color scaling, meaning that a pixel's red, green,
 * or blue values are dependent on the context of an image.
 *
 * <p>Two {@link VPixel}s are considered to be {@link Object#equals(Object)}
 * if and only if their underlying colors are {@link Object#equals(Object)}.</p>
 */
public interface VPixel {

  /**
   * Retrieves the red component of the color of this pixel.
   *
   * @return the red component of the color of this pixel
   */
  int getRed();

  /**
   * Retrieves the blue component of the color of this pixel.
   *
   * @return the blue component of the color of this pixel
   */
  int getBlue();

  /**
   * Retrieves the green component of the color of this pixel.
   *
   * @return the green component of the color of this pixel
   */
  int getGreen();

}
