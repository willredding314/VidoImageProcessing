package model.misc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import model.image.VImage;
import model.image.VLayeredImage;
import model.image.pixel.VPixel;
import model.image.pixel.VPixelCoordinate;

/**
 * A utility class for {@link VImage} and {@link VLayeredImage}.
 */
public final class VImageUtils {

  private VImageUtils() {
    // Prevent construction
  }

  /**
   * Produces a new image buffer whose contents are the RGB pixel data contained in the {@link
   * VImage} stored in-memory.
   *
   * <p>The resulting image is a copy of the image that can be written
   * to disk.</p>
   *
   * @param image an image whose contents should be copied
   * @return a new {@link BufferedImage} whose contents are filled with that of the given image
   * @throws IllegalArgumentException if {@code image} is {@code null}
   */
  public static BufferedImage extractToBuffer(VImage image) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(image);

    int width = image.getWidth();
    int height = image.getHeight();

    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int i = 0; i < width; i += 1) {
      for (int j = 0; j < height; j += 1) {
        // Guaranteed to have pixel data at this location
        VPixelCoordinate coord = new VPixelCoordinate(j, i);
        VPixel pixelAtLocation = image.getPixelAt(coord).get();
        Color color = new Color(pixelAtLocation.getRed(),
            pixelAtLocation.getGreen(),
            pixelAtLocation.getBlue());
        newImage.setRGB(i, j, color.getRGB());
      }
    }
    return newImage;
  }

}
