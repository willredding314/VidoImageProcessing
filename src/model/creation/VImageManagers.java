package model.creation;

import java.nio.file.Files;
import java.nio.file.Path;
import model.misc.FileUtils;
import model.misc.ObjectsExtension;
import model.persistence.VImageSaver;

/**
 * A factory class which produces {@link model.persistence.VImageSaver} and
 * {@link VImageProvider} implementations from a file format specifier
 *
 * <p>The {@link VImageManagers} is a factory class for producing
 * image providers and savers for different file formats. For example, you can
 * asks for an image saver that can handle loading and saving JPEG files, PPM
 * files, etc.
 */
public class VImageManagers {
  private VImageManagers() {
    // Prevent construction
  }

  /**
   * Produces a new {@link VImageSaver} that can handle saving the given file.
   *
   * <p> Use this factory method when you need to be able to support
   * saving a file whose format may vary with extension. The extension of the
   * file that you are attempting to save will automatically be analyzed and used
   * to create the appropriate object. An {@link IllegalArgumentException} is raised
   * if the given file name does not have any image savers capable of saving files
   * with its extension</p>
   *
   * @param filepath the name of the file that should be saved by this provider as an
   *                 absolute path to the file or a relative path with respect to the
   *                 directory the program is executed from
   * @return a new image saver that can save the file with the given name in
   *         the given format
   * @throws IllegalArgumentException if {@code filename} is {@code null}; or if the
   *                                  given file cannot be saved by Vido; or if the file
   *                                  name is the empty string; or if the file refers to
   *                                  a directory
   */
  public static VImageSaver diskSaverFor(Path filepath) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(filepath);

    if (filepath.toString().isEmpty()) {
      throw new IllegalArgumentException("The file named must be a nonempty string");
    }

    if (Files.isDirectory(filepath)) {
      throw new IllegalArgumentException("Path refers to a directory");
    }

    String extension = FileUtils.extensionOf(filepath.toString());

    if (extension.equals("ppm")) {
      return new PPMImageManager(filepath);
    }
    return new SystemSupportedImageManager(filepath);
  }

  /**
   * Produces an image provider that can produce images from the given
   * file path.
   *
   * <p>Different files have different internal representations, and hence
   * require different image providers to manage their translation into Vido.
   * You use this factory method to produce the correct image provider that
   * can extract images from the given source without having to parse the
   * file path yourself.</p>
   *
   * @param filepath a path to a file containing image data
   * @return a new image provider that can read the file with the given name in
   *         the given format
   * @throws IllegalArgumentException if {@code filename} is {@code null}; or if the
   *                                  given file cannot be saved by Vido; or if the file
   *                                  name is the empty string; or if the file refers to
   *                                  a directory
   */
  public static VImageProvider diskProviderFor(Path filepath) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(filepath);

    if (filepath.toString().isEmpty()) {
      throw new IllegalArgumentException("The file named must be a nonempty string");
    }

    if (Files.isDirectory(filepath)) {
      throw new IllegalArgumentException("Path refers to a directory");
    }

    String extension = FileUtils.extensionOf(filepath.toString());

    if (extension.equals("ppm")) {
      return new PPMImageManager(filepath);
    }
    return new SystemSupportedImageManager(filepath);
  }
}
