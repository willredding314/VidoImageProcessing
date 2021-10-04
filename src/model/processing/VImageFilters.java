package model.processing;

import model.image.pixel.ChannelType;

/**
 * A factory class for producing standard image filters out-of-the-box such as sepia and gray-scale
 * color filters and sharpening/blur filters.
 *
 * <p>The class defines several factory methods for creating transformations
 * at a high-level.</p>
 */
public final class VImageFilters {

  /**
   * Produces an image transformation which applies a sepia tone to an image it operates on.
   *
   * <p>The sepia tone reflects the classic colors of photos taken
   * in the 19th and early 20th centuries with a distinctive coppery hue. Apply this operation to an
   * image to bring that image back to an old era...
   *
   * <p>Note: applying multiple sepia filters to an image may not be
   * pleasing to the eye, but is otherwise permitted.</p>
   *
   * @return a new operation that applies a sepia tone to any image the operation operates on
   */
  public static VImageContentOperation sepiaColorFilter() {
    double[][] sepiaWeights = new double[][]{{0.393, 0.769, 0.189}, {0.349, 0.686, 0.168},
        {0.272, 0.534, 0.131}};
    return new VImageColorTransformation(new VImageTransformConversion(sepiaWeights));
  }

  /**
   * Produces an image transformation which applies a grayscale tone to an image it operates on.
   *
   * <p>Grayscale images give the feel of a picture
   * taken in the mid-1900s. Apply this filter to an image whose contents you want made retro</p>
   *
   * @return a new operation that applies a grayscale tone to any image the operation operates on
   */
  public static VImageContentOperation grayscaleColorFilter() {
    double[][] sepiaWeights = new double[][]{{0.216, 0.7152, 0.0722}, {0.216, 0.7152, 0.0722},
        {0.216, 0.7152, 0.0722}};
    return new VImageColorTransformation(new VImageTransformConversion(sepiaWeights));
  }

  /**
   * Creates an image filter that sharpens an image.
   *
   * <p>Sharpening an image makes it appear to have
   * more noticeable edges. Applying multiple sharpening filters at once will give an appearance of
   * even sharper edges.</p>
   *
   * @return a new operation which sharpens the image the operation applies itself to
   */
  public static VImageContentOperation sharpenFilter() {
    double[][] contents = new double[][]{
        {-1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0},
        {-1.0 / 8.0, 1.0 / 4.0, 1.0 / 4.0, 1.0 / 4.0, -1.0 / 8.0},
        {-1.0 / 8.0, 1.0 / 4.0, 1.0, 1.0 / 4.0, -1.0 / 8.0},
        {-1.0 / 8.0, 1.0 / 4.0, 1.0 / 4.0, 1.0 / 4.0, -1.0 / 8.0},
        {-1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0}};

    return new VCompositeContentOperation(
        new VImageFilter(contents, ChannelType.RED),
        new VImageFilter(contents, ChannelType.GREEN),
        new VImageFilter(contents, ChannelType.BLUE)
    );
  }


  /**
   * Creates an image filter that blurs an image.
   *
   * <p>Blurring an image gives an appearance of blurriness
   * or fuzziness from the perspective of the viewer. Applying multiple blur filters in sequence
   * enhances the blur result.</p>
   *
   * @return a new operation which blurs the image the operation applies itself to
   */
  public static VImageContentOperation blurFilter() {
    double[][] contents = new double[][]{
        {1.0 / 16.0, 1.0 / 8.0, 1.0 / 16.0},
        {1.0 / 8.0, 1.0 / 4.0, 1.0 / 8.0},
        {1.0 / 16.0, 1.0 / 8.0, 1.0 / 16.0}};

    return new VCompositeContentOperation(
        new VImageFilter(contents, ChannelType.RED),
        new VImageFilter(contents, ChannelType.GREEN),
        new VImageFilter(contents, ChannelType.BLUE)
    );
  }
}
