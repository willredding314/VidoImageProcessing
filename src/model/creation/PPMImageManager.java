package model.creation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import model.image.VImage;
import model.image.VMutableImage;
import model.image.VMutableImageImpl;
import model.image.pixel.VPixel;
import model.image.pixel.VPixelCoordinate;
import model.image.pixel.VRGBPixel;
import model.misc.FileUtils;
import model.misc.ObjectsExtension;
//import model.misc.VImageFormat;
import model.persistence.ImageSavingException;
import model.persistence.VImageSaver;

/**
 * Produces image data from files on disk with the ".ppm" extension.
 *
 * <p>An image file can be stored on disk as with the ".ppm" file
 * extension. This class converts such files into {@link VImage} objects
 * understood by the application. The {@link PPMImageManager} class also
 * supports saving ppm files to disk.</p>
 */
public class PPMImageManager implements VImageProvider, VImageSaver {
  public static final String PPM_HEADER = "P3";
  public static final int PPM_MAX_VALUE = 255;

  private final Path filepath;

  /**
   * Construct a new image provider which extracts image data
   * from the given file path.
   *
   * @param filepath a file path to a file with the ".ppm" extension as either a relative or
   *                 absolute path
   * @throws IllegalArgumentException if {@code filename} is {@code null}
   *                                  or if the path refers to a directory; or
   *                                  if the given file path does not refer to
   *                                  a PPM image (via its extension)
   *
   */
  public PPMImageManager(Path filepath) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(filepath);

    if (Files.isDirectory(filepath)) {
      throw new IllegalArgumentException("File path must not be a directory to load a ppm image");
    }

    if (!FileUtils.extensionOf(filepath.toString()).equals("ppm")) {
      throw new IllegalArgumentException("PPMImage saver can only save to PPM extensions");
    }

    this.filepath = filepath.toAbsolutePath();
  }

  /**
   * Ensures that the scanner has another string to analyze.
   *
   * <p>Call this method when you expect more strings to be
   * able to be read by the scanner</p>
   *
   * @param scanner the scanner to test
   * @throws IllegalArgumentException if {@code scanner} is {@code null}
   * @throws ImageExtractionException if the scanner runs out of values. Invoking this method
   *                                  implies that the caller expected the scanner to have more
   *                                  values for image extraction.
   */
  private void ensureHasNext(Scanner scanner)
      throws IllegalArgumentException, ImageExtractionException {
    if (!ObjectsExtension.asNonnull(scanner).hasNext()) {
      throw new ImageExtractionException("Unexpectedly ran out of values");
    }
  }

  /**
   * Ensures that the given channel value is valid.
   *
   * <p>A channel value is valid if it is both non-negative
   * and is at most the maximum value specified.</p>
   *
   * @param channel the channel value to test
   * @param maxValue the maximum value this channel could have
   * @throws ImageExtractionException if the channel is invalid
   */
  private void ensureChannelBetween(int channel, int maxValue) throws ImageExtractionException {
    if (channel < 0 || channel > maxValue) {
      throw new ImageExtractionException("A channel value exceeds the maximum value"
          + " specified in the ppm file header");
    }
  }

  @Override
  public VImage extractImage() throws ImageExtractionException {
    Scanner sc;

    try {
      sc = new Scanner(new FileInputStream(filepath.toString()));
    }
    catch (FileNotFoundException e) {
      throw new ImageExtractionException("File " + filepath.toString() + " not found!");
    }
    StringBuilder builder = new StringBuilder();

    // Read the file line by line, and populate a string. This will throw away any comment lines
    while (sc.hasNextLine()) {
      String s = sc.nextLine();
      if (s.charAt(0) != '#') {
        builder.append(s).append(System.lineSeparator());
      }
    }

    // Now set up the scanner to read from the string we just built
    sc = new Scanner(builder.toString());

    if (!sc.hasNext()) {
      throw new ImageExtractionException("The given ppm file is empty");
    }

    if (!sc.next().equals("P3")) {
      throw new ImageExtractionException("Incorrect file format: PPM format expected");
    }

    // PPM:
    //
    // Width, Height
    // max value
    ensureHasNext(sc);
    int width = sc.nextInt();

    ensureHasNext(sc);
    int height = sc.nextInt();

    ensureHasNext(sc);
    int maxValue = sc.nextInt();

    VMutableImage destination = new VMutableImageImpl(width, height);

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        try {
          ensureHasNext(sc);
          int r = sc.nextInt();
          ensureChannelBetween(r, maxValue);

          ensureHasNext(sc);
          int g = sc.nextInt();
          ensureChannelBetween(g, maxValue);

          ensureHasNext(sc);
          int b = sc.nextInt();
          ensureChannelBetween(b, maxValue);

          destination.setPixel(new VRGBPixel(r, g, b),
              new VPixelCoordinate(i, j));
        }
        catch (NumberFormatException e) {
          throw new ImageExtractionException("The supplied ppm file " + filepath.toString()
              + " is improperly formatted and/or corrupt");
        }
      }
    }

    return destination;
  }

  /**
   * Ensures that the given channel value is valid.
   *
   * <p>A channel value is valid if it is both non-negative
   * and is at most the maximum value specified.</p>
   *
   * @param channel the channel value to test
   * @param maxValue the maximum value this channel could have
   * @throws ImageExtractionException if the channel is invalid
   */
  private void testChannel(int channel, int maxValue) throws IllegalArgumentException {
    if (channel < 0 || channel > maxValue) {
      throw new IllegalArgumentException("The image attempted to be saved has an invalid channel");
    }
  }

  @Override
  public void saveImage(VImage image) throws IllegalArgumentException, ImageSavingException {
    ObjectsExtension.requireNonnull(image);

    // Save the image to disk
    int width = image.getWidth();
    int height = image.getHeight();

    StringBuilder toWrite = new StringBuilder();
    toWrite.append(PPM_HEADER).append(System.lineSeparator());
    toWrite.append(width).append(" ").append(height).append(System.lineSeparator());
    toWrite.append(PPM_MAX_VALUE).append(System.lineSeparator());

    for (int i = 0; i < height; i += 1) {
      for (int j = 0; j < width; j += 1) {
        VPixelCoordinate loc = new VPixelCoordinate(i, j);
        VPixel pixel = image.getPixelAt(loc).orElseThrow();
        int r = pixel.getRed();
        int g = pixel.getGreen();
        int b = pixel.getBlue();

        testChannel(r, PPM_MAX_VALUE);
        testChannel(g, PPM_MAX_VALUE);
        testChannel(b, PPM_MAX_VALUE);

        toWrite.append(r).append(System.lineSeparator());
        toWrite.append(g).append(System.lineSeparator());
        toWrite.append(b).append(System.lineSeparator());
      }
    }

    // Write the string to the file
    try (FileWriter writer = new FileWriter(filepath.toString())) {
      writer.write(toWrite.toString());
    }
    catch (IOException e) {
      throw new ImageSavingException("Image could not be saved");
    }
  }
}
