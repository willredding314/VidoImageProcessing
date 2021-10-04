package model.creation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import model.image.VImage;
import model.image.VLayeredImage;
import model.image.VLayeredImageImpl;
import model.misc.ObjectsExtension;

/**
 * A {@link VLayeredImageProvider} which loads its images based on input from a readable that
 * specifies resources to files on disk.
 *
 * <p>A {@link VTextFileLayeredImageProvider} reads its input from a text
 * file or any {@link Readable} to create layered images. It expects that the text file it receives
 * be in a very particular format. The text files of multi-layered images in Vido are formatted as
 * such:
 * <ul>
 *   <li>Header: number of layers in the image</li>
 *   <li>Width of the image</li>
 *   <li>Height of the image</li>
 *   <li>A sequence of <em>layer expressions</em>, each consisting of
 *   <ul>
 *     <li>a number, identifying the depth of the layer</li>
 *     <li>an absolute file path to a resource</li>
 *   </ul></li>
 * </ul>
 *
 * <p>The contents of the resulting image are those of the specified files</p>
 */
public class VTextFileLayeredImageProvider implements VLayeredImageProvider {

  private final Scanner scanner;

  /**
   * Construct a new text multi-layer provider by reading from the file at the specified path.
   *
   * @param textSource a path to a text file
   * @throws IllegalArgumentException if the path is {@code null}
   */
  public VTextFileLayeredImageProvider(Readable textSource) throws IllegalArgumentException {
    this.scanner = new Scanner(ObjectsExtension.asNonnull(textSource));
  }

  @Override
  public VLayeredImage extractLayeredImage() throws ImageExtractionException {
    int numLayers = readNextInt();
    int width = readNextInt();
    int height = readNextInt();

    // The file is malformed if any of the above numbers
    // are negative
    if (numLayers < 0 || width < 0 || height < 0) {
      throw new ImageExtractionException("layers.txt file not properly formatted");
    }

    VLayeredImage layeredImage = new VLayeredImageImpl();

    // Now, for each layer, read an integer and then a path,
    // as this is how the file is formatted
    for (int i = 0; i < numLayers; i += 1) {
      int layerIndex = readNextInt();
      String layerName = readNextString();
      Path absolutePath = Paths.get(readNextString()).toAbsolutePath();

      // Get an image from the path, if such an image can be read
      VImage newImage = VImageManagers.diskProviderFor(absolutePath).extractImage();

      try {
        layeredImage.createNewLayer(layerName, layerIndex, newImage);
      } catch (IllegalArgumentException e) {
        throw new ImageExtractionException(
            "layers.txt file not formatted properly. Reason: " + e.getMessage());
      }
    }

    if (scanner.hasNext()) {
      throw new ImageExtractionException("More layers to decode but only captured "
          + numLayers + " layers");
    }

    return layeredImage;
  }

  /**
   * Reads the next integer from the scanner if such an integer exists and throws an exception
   * otherwise.
   *
   * @return the next integer
   * @throws ImageExtractionException if either the scanner does not have any more elements or an
   *                                  integer could not be extracted
   */
  private int readNextInt() throws ImageExtractionException {
    if (!scanner.hasNext()) {
      throw new ImageExtractionException("Ran out of input");
    }

    try {
      return scanner.nextInt();
    } catch (Exception e) {
      throw new ImageExtractionException("Could not extract another integer when"
          + " one was expected");
    }
  }

  /**
   * Reads the next string from the scanner if such a string exists and throws an exception
   * otherwise.
   *
   * @return the next string
   * @throws ImageExtractionException if the scanner does not have any more elements
   */
  private String readNextString() throws ImageExtractionException {
    if (!scanner.hasNext()) {
      throw new ImageExtractionException("Ran out of input");
    }
    return scanner.next();
  }
}
