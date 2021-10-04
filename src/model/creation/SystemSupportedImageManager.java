package model.creation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import model.image.VImage;
import model.image.VMutableImage;
import model.image.VMutableImageImpl;
import model.image.pixel.VPixelCoordinate;
import model.image.pixel.VRGBPixel;
import model.misc.FileUtils;
import model.misc.ObjectsExtension;
import model.misc.VImageUtils;
import model.persistence.ImageSavingException;
import model.persistence.VImageSaver;

/**
 * Processes image data from files on disk with three popular formats: Gif, PNG, and JPEG.
 *
 * <p>A {@link SystemSupportedImageManager} is capable of loading and saving
 * images stored as PNGs, GIFs, and JPEGs. You provide it the appropriate location to load images
 * from and call the {@link VImageProvider#extractImage()} method to extract the image from a
 * particular location if such data exists. You can then save new image data at that location using
 * the {@link VImageSaver#saveImage(VImage)} method to overwrite data stored in a particular
 * location. The location is automatically decoded to determine the extension of the file.</p>
 */
public class SystemSupportedImageManager implements VImageProvider, VImageSaver {
  private final Path filepath;
  private final String extension;

  /**
   * Create a new image provider which loads an image
   * from some file path.
   *
   * @param filepath a path to an image resource as either an absolute or relative path
   * @throws IllegalArgumentException if {@code filename} is {@code null} or if the file has an
   *                                  unsupported file extension (one other than JPEG, PNG, or GIF);
   *                                  or if the file name is the empty string; or if the file name
   *                                  refers to a directory instead of a path
   */
  public SystemSupportedImageManager(Path filepath) throws IllegalArgumentException {
    if (filepath.toString().isEmpty()) {
      throw new IllegalArgumentException("File must have a name specified");
    }

    if (Files.isDirectory(filepath)) {
      throw new IllegalArgumentException("Cannot load an image with a directory path");
    }

    this.extension = FileUtils.extensionOf(filepath.toString());
    this.filepath = filepath.toAbsolutePath();
  }

  /**
   * Extracts a {@link VImage} from the buffered image provided.
   *
   * @param image a buffered image containing pixel data loaded from disk
   * @return a new {@link VImage} whose contents match those of the buffered image
   * @throws IllegalArgumentException if {@code image} is {@code null}
   */
  private VImage extractFromBufferedImage(BufferedImage image) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(image);

    int width = image.getWidth();
    int height = image.getHeight();

    VMutableImage newImage = new VMutableImageImpl(width, height);

    for (int i = 0; i < width; i += 1) {
      for (int j = 0; j < height; j += 1) {
        int rgb = image.getRGB(i, j);
        VPixelCoordinate coord = new VPixelCoordinate(j, i);
        newImage.setPixel(new VRGBPixel(new Color(rgb)), coord);
      }
    }

    return newImage;
  }

  @Override
  public VImage extractImage() throws ImageExtractionException {
    File file = filepath.toFile();

    try {
      BufferedImage bufferedImage = ImageIO.read(file);
      return this.extractFromBufferedImage(bufferedImage);
    } catch (IOException exception) {
      throw new ImageExtractionException("Could not read from file " + file.toString());
    }
  }

  @Override
  public void saveImage(VImage image) throws IllegalArgumentException, ImageSavingException {
    ObjectsExtension.requireNonnull(image);
    File locationToSave = filepath.toFile();
    RenderedImage allocatedImage = VImageUtils.extractToBuffer(image);

    try {
      // Ensure that the write actually succeeded for the given extension. If it
      // did not succeed, there was an issue
      boolean didSucceed = ImageIO.write(allocatedImage, extension, locationToSave);
      if (!didSucceed) {
        throw new IOException("Could not write to file " + filepath.toString());
      }
    }
    catch (IOException e) {
      throw new ImageSavingException("Could not save individual image");
    }
  }
}
