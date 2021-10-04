package model.image.pixel;

import java.awt.Color;
import java.util.Objects;
import model.misc.ObjectsExtension;

/**
 * A {@link VPixel} which manages RGB color data directly.
 *
 * <p>A {@link VRGBPixel} can represent a colored pixel in RGB directly
 * and is constructed using RGB color values directly. A pixel's actual</p>
 */
public class VRGBPixel implements VPixel {

  private final int red;
  private final int green;
  private final int blue;

  /**
   * Construct a new pixel with the given color value.
   *
   * @param color the color the pixel will have
   * @throws IllegalArgumentException if {@code color} is {@code null}
   */
  public VRGBPixel(Color color) throws IllegalArgumentException {
    this(ObjectsExtension.asNonnull(color).getRed(),
        ObjectsExtension.asNonnull(color).getGreen(),
        ObjectsExtension.asNonnull(color).getBlue());
  }

  /**
   * Construct a new pixel with the given color color components. The components are only defined
   * within the context of a particular scale. For instance, in some scales it may be defined to
   * specify a color whose red is greater than 255 if the scale is more refined for example.
   *
   * @param red   the red component's value
   * @param green the green component's value
   * @param blue  the blue component's value
   */
  public VRGBPixel(int red, int green, int blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  /**
   * Construct a new pixel by copying the color of the given one.
   *
   * @param other a pixel to copy
   * @throws IllegalArgumentException if {@code other} is {@code null}
   */
  public VRGBPixel(VPixel other) throws IllegalArgumentException {
    this(ObjectsExtension.asNonnull(other).getRed(),
        ObjectsExtension.asNonnull(other).getGreen(),
        ObjectsExtension.asNonnull(other).getBlue());
  }

  @Override
  public int getRed() {
    return this.red;
  }

  @Override
  public int getBlue() {
    return this.blue;
  }

  @Override
  public int getGreen() {
    return this.green;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof VPixel)) {
      return false;
    }
    VPixel vPixel = (VPixel) o;
    return red == vPixel.getRed() && green == vPixel.getGreen() && blue == vPixel.getBlue();
  }

  @Override
  public int hashCode() {
    return Objects.hash(red, green, blue);
  }

  @Override
  public String toString() {
    return "VRGBPixel{"
        + "red=" + red
        + ", green=" + green
        + ", blue=" + blue
        + '}';
  }
}
