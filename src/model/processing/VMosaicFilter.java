package model.processing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import model.image.VImage;
import model.image.VMutableImage;
import model.image.pixel.VPixelCoordinate;
import model.misc.ComparableUtils;

/**
 * An image transformation which turns an image into a "mosaic" artwork.
 * A mosaic is formed by clumping together pixels that are near each other
 * into a single color. This gives a glassy look to an image and makes it appear
 * as if it had been crafted in Medieval Europe!
 *
 * <p>The mosaic filter randomly assigns locations in an image as <em>seeds</em>.
 * Each seed serves as a location that nearby pixels can be grouped together with.
 * Each pixel in the image is then assigned the seed it resides closest to and takes
 * on the color of that seed pixel. By varying the number of seeds used by the mosaic
 * filter, you can change the way the resulting mosaic image appears.
 *
 * <p>If a {@link VMosaicFilter} specifies that more</p>
 */
public class VMosaicFilter implements VImageContentOperation {

  private final int numSeeds;
  private final int randomSeed;

  /**
   * Construct a new mosaic filter using a random seed and
   * a fixed number of mosaic seeds within the image.
   *
   * @param numSeeds   the maximum number of pixels in the images processed
   *                   by the filter that are labeled as mosaic seeds
   * @param randomSeed a seed for the random generation of mosaic seeds
   * @throws IllegalArgumentException if the number of seeds is negative or zero
   */
  public VMosaicFilter(int numSeeds, int randomSeed) throws IllegalArgumentException {

    if (numSeeds < 0) {
      throw new IllegalArgumentException("Seeds cannot be negative.");
    }

    this.numSeeds = numSeeds;
    this.randomSeed = randomSeed;
  }

  @Override
  public VImage operateOn(VImage image) throws IllegalArgumentException {
    VMutableImage imageCopy = image.mutableCopy();
    Set<VPixelCoordinate> mosaicPoints = this.generateMosaicCoordinatesFrom(image);

    // Traverse the entire image and find the closest coordinate
    for (int i = 0; i < image.getWidth(); i += 1) {
      for (int j = 0; j < image.getHeight(); j += 1) {
        VPixelCoordinate coordinate = new VPixelCoordinate(j, i);
        VPixelCoordinate closestPoint = mosaicPoints.stream().min(
                Comparator.comparingInt(c -> c.taxiCabDistance(coordinate)))
                .orElse(coordinate);

        imageCopy.setPixel(image.getPixelAt(closestPoint).get(), coordinate);
      }
    }

    return imageCopy;
  }

  /**
   * Creates a set of pixel coordinates identifying mosaic pixels to
   * be used from the image for making a mosaic image from the image.
   *
   * <p>The number of items in the set will not exceed the total
   * number of pixels {@link VImage#numPixels()} in the image.</p>
   *
   * @param image the image to mark mosaic points in
   * @return a set of locations in the image to be used as mosaic seeds
   * @throws IllegalArgumentException if the image is null
   */
  private Set<VPixelCoordinate> generateMosaicCoordinatesFrom(VImage image)
          throws IllegalArgumentException {
    Random r = new Random(randomSeed);
    int numPointsNeeded = ComparableUtils.clamp(numSeeds, 0, image.numPixels());

    Set<VPixelCoordinate> coords = new HashSet<>();
    List<VPixelCoordinate> allCoords = new ArrayList<>();

    for (int i = 0; i < image.getWidth(); i += 1) {
      for (int j = 0; j < image.getHeight(); j += 1) {
        allCoords.add(new VPixelCoordinate(j, i));
      }
    }

    // Choose a random value from the set of all coordinates
    // (sample space) and do so without replacement
    for (int k = 0; k < numPointsNeeded; k += 1) {
      int randomIndex = r.nextInt(allCoords.size());
      coords.add(allCoords.remove(randomIndex));
    }

    return coords;
  }
}
