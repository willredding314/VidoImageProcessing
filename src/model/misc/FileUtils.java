package model.misc;

/**
 * A utility class for managing data relevant to files
 * such as path names.
 */
public class FileUtils {

  private FileUtils() {
    // Prevent construction
  }

  /**
   * Determines the extension of the resource specified with
   * the given file name.
   *
   * <p>The file name specified can be an absolute or relative
   * path to a file. If the file does not have an extension, then
   * the method simply returns the empty string ""</p>
   *
   * @param filename a name of a file
   * @return the extension of the filepath as a string. If the path refers to a directory
   *         that does not begin with a "." or the file does not have an extension,
   *         the empty string is returned. If the name refers to a directory with a
   *         "." and nothing else, the name of that directory is returned
   * @throws IllegalArgumentException if {@code filepath} is {@code null}
   */
  public static String extensionOf(String filename) throws IllegalArgumentException {
    int index = ObjectsExtension.asNonnull(filename).lastIndexOf('.');
    if (index >= 0) {
      return filename.substring(index + 1);
    } else {
      return "";
    }
  }
}
