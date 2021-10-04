package controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import model.image.VFocusableLayerImageImpl;
import model.image.VLayeredImageImpl;
import model.misc.ObjectsExtension;
import view.VPaletteView;

/**
 * A concrete application object that defers control to a single controller in the program.
 *
 * <p>This application object expects the program to have been launched with command line arguments
 * specified in one of the following ways:
 *  <ul>
 *      <li>{@code -script} + {@code path to script}</li>
 *      <li>{@code -text}</li>
 *      <li>{@code -interactive}</li>
 *  </ul>
 * Each argument corresponds to one of the three ways that Vido can be used when expressed with this
 * application object: interactively as a GUI, interactively as text, and automatically by reading
 * a file containing a script</p>
 */
public class VApplicationImpl implements VApplication {
  private final Map<String, ScannerToController> commandMap;

  /**
   * Describes a method of creating a {@link VController} instance
   * based on the contents of command line arguments passed to Vido.
   */
  private interface ScannerToController {
    /**
     * Constructs a new {@link VController} based on the contents
     * to be read in a scanner.
     *
     * @param scanner the scanner containing strings which are processed
     *                and used to determine a controller that should be
     *                constructed
     * @return a new {@link VController} instance that is suited for running
     *         the program based on the specifications given by the scanner
     * @throws IllegalArgumentException if the scanner is null of if the scanner doesn't
     *                                  contain enough or proper information to create a
     *                                  controller instance
     * @throws FileNotFoundException if any file information provided for decoding is faulty
     */
    VController controllerFrom(Scanner scanner) throws IllegalArgumentException,
            FileNotFoundException;
  }

  /**
   * Construct a new {@link VApplicationImpl} that can handle three possible
   * launch conditions for Vido (as described in the JavaDoc for the class).
   */
  public VApplicationImpl() {
    commandMap = new HashMap<>();
    commandMap.put("-script", (scanner) -> {
      Path pathToFile = Path.of(readNextString(scanner)).toAbsolutePath();
      return new VTerminalController(new FileReader(pathToFile.toFile()), System.out);
    });

    commandMap.put("-text", (scanner) -> new VTerminalController(
            new InputStreamReader(System.in), System.out));
    commandMap.put("-interactive", (scanner) -> new VGUIController(new VPaletteView(),
            new VFocusableLayerImageImpl(new VLayeredImageImpl())));
  }

  @Override
  public void run(String[] args) throws IllegalArgumentException {
    try {
      VController controller = controllerFor(args);
      controller.run();
    } catch (FileNotFoundException e) {
      System.err.println("The script file passed to as an argument could not be read");
    } catch (IllegalArgumentException e) {
      System.err.println("Invalid command line arguments supplied to Vido. Try again");
    }
  }

  /**
   * Produces an appropriate {@link VController} instance based on the command line arguments
   * supplied to Vido upon being launched.
   *
   * <p>There are three ways that Vido can be launched according to this
   * application instance:
   *
   * <ul>
   *   <li>Using the {@code -script} command line argument followed by the name of a
   *   script file (a text file) containing script commands to be run.</li>
   *   <li>Using the {@code -text} command line argument to run Vido in interactive
   *   text mode</li>
   *   <li>Using the {@code -interactive} command line argument to run Vido's GUI program</li>
   * </ul>
   * </p>
   *
   * @param args command line arguments passed to the program on launch
   * @return a new controller that has the appropriate capabilities based on the arguments provided
   * @throws IllegalArgumentException if the arguments are null or if the arguments do not contain a
   *                                  file that exists on disk that can be loaded with commands
   */
  private VController controllerFor(String[] args)
      throws IllegalArgumentException, FileNotFoundException {
    ObjectsExtension.requireNonnull((Object) args);
    ObjectsExtension.requireNonnull((Object[]) args);

    if (args.length == 0) {
      throw new IllegalArgumentException("Expected at least one command line argument");
    }

    String allArguments = Arrays.stream(args).reduce("", (prev, next) -> prev + " " + next);
    Readable readable = new StringReader(allArguments);
    Scanner scanner = new Scanner(readable);

    // The `allArgumentsString` will not be empty since there
    // is at least one string supplied after the first if check
    String argumentKey = scanner.next();

    if (!commandMap.containsKey(argumentKey)) {
      throw new IllegalArgumentException("Unrecognized command line argument");
    }

    return commandMap.get(argumentKey).controllerFrom(scanner);
  }

  /**
   * Locates the first file listed in the command line arguments.
   *
   * @param args command line arguments to parse
   * @return a path to a script file
   */
  private Path parseForScriptFile(String[] args) throws IllegalArgumentException {
    return Arrays.stream(args)
        .map((arg) -> Paths.get(arg).toAbsolutePath())
        .filter(Files::isRegularFile)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No valid file provided in the commands"));
  }

  /**
   * Reads the next string from the scanner if such a string exists and throws an exception
   * otherwise.
   *
   * @return the next string
   * @throws IllegalArgumentException if the scanner is null
   */
  private String readNextString(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);
    if (!scanner.hasNext()) {
      throw new IllegalArgumentException("Ran out of input");
    }
    return scanner.next();
  }
}
