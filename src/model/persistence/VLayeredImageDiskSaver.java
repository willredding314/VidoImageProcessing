package model.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import model.creation.VImageManagers;
import model.image.VLayer;
import model.image.VLayeredImage;
import model.misc.ObjectsExtension;

/**
 * A {@link VLayeredImageDiskSaver} is a {@link VLayeredImageSaver} which saves layered images as
 * directories containing file information on disk. This particular image saver saves all layers of
 * a {@link VLayeredImage} as separate files in a directory that is titled based on the name of the
 * layered image. Each file in the directory has an extension that is expected
 * to be of the same type. A final text file is added in the directory which identifies
 * the relationship between the separate image files as layers in the program.
 */
public class VLayeredImageDiskSaver implements VLayeredImageSaver {
  private final String extension;
  private final Path directory;

  /**
   * Construct a multi-layer image saver that saves its images in the given format.
   *
   * @param extension the file extension of the images in the layered image directory.
   * @param directory a path to a directory to save the layered image
   * @throws IllegalArgumentException if {@code format} if {@code null} or if the
   *                                  extension is the empty string
   */
  public VLayeredImageDiskSaver(String extension, Path directory) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(directory, extension);

    if (extension.isEmpty()) {
      throw new IllegalArgumentException("A multi-layered image must save its files"
          + " with a non-empty extension");
    }

    this.extension = extension;
    this.directory = ObjectsExtension.asNonnull(directory).toAbsolutePath();
  }

  @Override
  public void saveImage(VLayeredImage image) throws IllegalArgumentException, ImageSavingException {
    ObjectsExtension.requireNonnull(image);

    // If there are no layers in this image, you cannot
    // save it
    if (image.numLayers() == 0) {
      throw new ImageSavingException("Cannot save a multi-layered image without any layers");
    }


    File dir = directory.toFile();

    // Create a directory from the file and then save the contents
    // of the other files into this new directory
    try {

      // If the path is not a directory (if it does not
      // already exist) make it
      if (!dir.isDirectory()) {
        boolean didMakeDir = dir.mkdir();

        if (!didMakeDir) {
          throw new ImageSavingException("Could not create a directory for the given image");
        }
      }
    } catch (SecurityException e) {
      throw new ImageSavingException(e.getMessage());
    }

    StringBuilder inTextFile = new StringBuilder();

    // First, write the number
    // of layers in the image
    inTextFile.append(image.numLayers()).append(System.lineSeparator());
    inTextFile.append(image.getWidth())
        .append(" ")
        .append(image.getHeight())
        .append(System.lineSeparator());

    // Save each layer in the directory with the format specified by this disk saver
    for (int i = 0; i < image.numLayers(); i += 1) {
      VLayer layer = image.getLayer(i);
      String layerFileName = layer.getName() + "."
          + this.extension;
      Path layerFilePath = Path.of(directory.toString(), layerFileName);
      String fileName = layerFilePath.toString();

      // Add the layer index
      inTextFile.append(i).append(" ");

      // Add the layer name
      inTextFile.append(layer.getName()).append(" ");

      // Add the file name
      inTextFile.append(fileName).append(System.lineSeparator());

      VImageSaver saver = VImageManagers.diskSaverFor(Path.of(fileName));
      saver.saveImage(layer);
    }

    // Create a text file that Vido uses
    // to re-create layered images
    Path layersFilePath = Path.of(directory.toString(), "layers.txt");
    File layersFile = layersFilePath.toFile();

    try (PrintWriter writer = new PrintWriter(new FileWriter(layersFile))) {
      writer.println(inTextFile.toString());
    } catch (IOException e) {
      throw new IllegalArgumentException("Layers file could not be created successfully");
    }
  }
}
