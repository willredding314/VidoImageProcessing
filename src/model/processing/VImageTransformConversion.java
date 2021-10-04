package model.processing;

import java.util.Arrays;
import model.image.pixel.VPixel;
import model.image.pixel.VRGBPixel;

import java.util.function.Function;
import model.misc.ObjectsExtension;

/**
 * Represents a function that converts a single pixel with three color channels to a new pixel,
 * based on a set of multipliers to apply to the colors within the pixel.
 */
public class VImageTransformConversion implements Function<VPixel, VPixel> {

  private final double[][] conversionMatrix;

  /**
   * Constructs a VImageTranformConversion based a given conversion matrix.
   *
   * @param conversionMatrix the set of multipliers to be applied to the color channels within a
   *                         pixel.
   * @throws IllegalArgumentException if the conversion matrix isn't a 3 by 3 square or if any of
   *                                  the arrays are {@code null} (including nested arrays)
   */
  public VImageTransformConversion(double[][] conversionMatrix) throws
      IllegalArgumentException {

    ObjectsExtension.requireNonnull((Object) conversionMatrix);
    ObjectsExtension.requireNonnull((Object[]) conversionMatrix);

    if (conversionMatrix.length != 3) {
      throw new IllegalArgumentException("Matrix must "
          + "be 3 by 3.");
    }

    for (int i = 0; i < 3; i += 1) {
      if (conversionMatrix[i].length != 3) {
        throw new IllegalArgumentException("Matrix must "
            + "be 3 by 3.");
      }
    }

    this.conversionMatrix = Arrays.stream(conversionMatrix).map(
        (a) -> Arrays.copyOf(a, a.length)
    ).toArray(double[][]::new);
  }

  @Override
  public VPixel apply(VPixel vPixel) {
    ObjectsExtension.requireNonnull(vPixel);

    int red = vPixel.getRed();
    int green = vPixel.getGreen();
    int blue = vPixel.getBlue();

    int newRed = (int) (conversionMatrix[0][0] * red
        + conversionMatrix[0][1] * green
        + conversionMatrix[0][2] * blue);

    int newGreen = (int) (conversionMatrix[1][0] * red
        + conversionMatrix[1][1] * green
        + conversionMatrix[1][2] * blue);

    int newBlue = (int) (conversionMatrix[2][0] * red
        + conversionMatrix[2][1] * green
        + conversionMatrix[2][2] * blue);

    return new VRGBPixel(newRed, newGreen, newBlue);
  }
}
