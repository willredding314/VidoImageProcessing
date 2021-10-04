package model.processing;

import java.util.function.Function;
import model.image.VImage;
import model.image.VMutableImage;
import model.image.pixel.VPixel;
import model.image.pixel.VPixelCoordinate;
import model.image.pixel.VRGBPixel;
import model.misc.ObjectsExtension;

/**
 * A map that converts colors within each pixel in an image based on a function that creates a new
 * pixel from an old pixel.
 */
public class VImageColorTransformation implements VImageContentOperation {
  private final Function<VPixel, VPixel> key;

  /**
   * Constructs a color transformation with a key, which is a function that maps pixels to other
   * pixels.
   *
   * @param key the function to be applied to each pixel
   * @throws IllegalArgumentException if {@code key} is null
   */
  public VImageColorTransformation(Function<VPixel, VPixel> key) {
    ObjectsExtension.requireNonnull(key);
    this.key = key;
  }

  @Override
  public VImage operateOn(VImage image) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(image);

    VMutableImage mutableImage = image.mutableCopy();

    int width = image.getWidth();
    int height = image.getHeight();

    for (int i = 0; i < height; i += 1) {
      for (int j = 0; j < width; j += 1) {
        VPixelCoordinate loc = new VPixelCoordinate(i, j);
        mutableImage.setPixel(key
            .apply(image.getPixelAt(loc).orElseThrow()), loc);
      }
    }

    this.clampImage(mutableImage);
    return mutableImage;
  }

  /**
   * Clamps an image, ensuring that no pixels are outside of the bounds of color channel values.
   *
   * @param mutImage the unclamped image
   * @throws IllegalArgumentException if {@code mutImage} is null
   */
  private void clampImage(VMutableImage mutImage) {
    ObjectsExtension.requireNonnull(mutImage);

    int width = mutImage.getWidth();
    int height = mutImage.getHeight();

    for (int i = 0; i < height; i += 1) {
      for (int j = 0; j < width; j += 1) {
        VPixel currPixel = mutImage.getPixelAt(new VPixelCoordinate(i, j)).orElseThrow();
        mutImage.setPixel(this.clampPixel(currPixel), new VPixelCoordinate(i, j));
      }
    }

  }

  /**
   * Clamps a single pixel, ensuring that none of its channels exceed 255 or go below 0.
   *
   * @param currPixel the unclamped pixel.
   * @return a clamped pixel.
   * @throws IllegalArgumentException if {@code currPixel} is {@code null}
   */
  private VPixel clampPixel(VPixel currPixel) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(currPixel);

    int red = currPixel.getRed();
    if (red > 255) {
      red = 255;
    } else if (red < 0) {
      red = 0;
    }

    int green = currPixel.getGreen();
    if (green > 255) {
      green = 255;
    } else if (green < 0) {
      green = 0;
    }

    int blue = currPixel.getBlue();
    if (blue > 255) {
      blue = 255;
    } else if (blue < 0) {
      blue = 0;
    }

    return new VRGBPixel(red, green, blue);
  }
}

