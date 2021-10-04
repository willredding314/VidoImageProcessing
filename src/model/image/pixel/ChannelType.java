package model.image.pixel;

/**
 * Describes one of three color channels available in any
 * pixel.
 *
 * <p>A color can be described as a composition of color
 * <em>channels</em>, each of which groups one particular
 * wavelength of light whose relative magnitude is specified
 * in any given color. For colors in Vido, colors are specified
 * with RGB channels.</p>
 */
public enum ChannelType {
  RED, GREEN, BLUE;
}
