package model.creation;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import model.image.VLayeredImage;
import model.misc.ObjectsExtension;
import model.persistence.VLayeredImageSaver;

/**
 * A {@link VLayeredImageDiskProvider} is a {@link VLayeredImageSaver} which saves
 * layered images as directories containing file information on disk.
 *
 * <p>Layered images that are saved to disk are stored as directories containing
 * several files. Each file contains image data describing the contents of
 * a single layer of a multi-layered image, with the exception of one text file
 * called "layers.txt" that describes the relationship of each image to a layer
 * in a multi-layered {@link VLayeredImage} object. Each image thus holds image data
 * for a single {@link model.image.VLayer} instance that can be initialized at runtime.
 *
 * <p>Use a {@link VLayeredImageDiskProvider} to load multi-layered images stored on disk
 * by a {@link VLayeredImageSaver}. The corresponding image saver is responsible</p>
 */
public class VLayeredImageDiskProvider implements VLayeredImageProvider {

  private static final String LAYERS_FILE_TYPE = "layers.txt";

  // The path to the multilayer image

  // INVARIANT: Represents a path to a DIRECTORY and not a file
  private final Path path;

  /**
   * Construct a new {@link VLayeredImageDiskProvider} which loads layered images from the contents
   * of the path to a directory that already exists.
   *
   * <p>The {@link VLayeredImageDiskProvider} will use the name of the directory specified
   * as the name of the multi-layered image</p>
   *
   * @param layeredImagePath a file path to a directory, specified as either an absolute or
   *                        relative path
   * @throws IllegalArgumentException if the given path does not identify a
   *                                  directory or if the given path is {@code null}
   */
  public VLayeredImageDiskProvider(Path layeredImagePath) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(layeredImagePath);

    if (!Files.isDirectory(layeredImagePath.toAbsolutePath())) {
      throw new IllegalArgumentException("Only multi-layered images that already exist can be "
          + "loaded from disk");
    }

    this.path = layeredImagePath.toAbsolutePath();
  }

  @Override
  public VLayeredImage extractLayeredImage() throws ImageExtractionException {
    // Go through each file in the directory.
    // If there are other directories in the directory,
    // they are ignored
    try (Stream<Path> paths = Files.list(path)) {
      Stream<Path> filePaths = paths.filter(Files::isRegularFile);

      Path toLayersTXT = findLayersTextFilePathIn(filePaths);
      Readable fileReadable = new FileReader(toLayersTXT.toFile());

      // We want to process this text file now
      VLayeredImageProvider textImageProvider = new VTextFileLayeredImageProvider(fileReadable);

      return textImageProvider.extractLayeredImage();
    }
    catch (IOException exception) {
      throw new ImageExtractionException(exception.getMessage());
    }
  }

  /**
   * Locates the path of the "layers.txt" file that formats each
   * layer.
   *
   * @param filepaths a stream of file paths which might contain the
   *                  given text file path
   * @return a path to the "layers.txt" file
   * @throws IllegalArgumentException if {@code filepaths} is {@code null}
   */
  private Path findLayersTextFilePathIn(Stream<Path> filepaths) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(filepaths);

    return filepaths.filter((path) -> path.getFileName().toString().equals(LAYERS_FILE_TYPE))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No \"layers.txt\" found"));
  }
}
