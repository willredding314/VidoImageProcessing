package controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import model.creation.ImageExtractionException;
import model.creation.VImageManagers;
import model.creation.VImageProvider;
import model.creation.VLayeredImageDiskProvider;
import model.creation.VLayeredImageProvider;
import model.image.VFocusableLayerImageImpl;
import model.image.VFocusableLayeredImage;
import model.image.VImage;
import model.image.VImageImpl;
import model.image.VLayer;
import model.image.VLayeredImage;
import model.image.VLayeredImageImpl;
import model.misc.ObjectsExtension;
import model.persistence.ImageSavingException;
import model.persistence.VImageSaver;
import model.persistence.VLayeredImageDiskSaver;
import model.persistence.VLayeredImageSaver;
import model.processing.VImageContentOperation;
import model.processing.VImageFilters;
import view.VTextView;
import view.VTextualView;

/**
 * Manages interactions with the user through a terminal or interactions passed to the program as a
 * script.
 *
 * <p>A user can interact using the {@link VTerminalController} in one of two ways:
 * <ul>
 *   <li>Upon launching Vido, the user can simply invoke Java with the Vido.jar
 *   to launch an interactive program. This allows the user to manually manipulate
 *   images and layers within a multi-layered image.</li>
 *   <li>Alternatively, the user can pass a file name containing a simple script
 *   as a command line argument to the program which is then processed as a standalone
 *   input.</li>
 * </ul>
 * <p>The controller only accepts a single file as input; that is, if more than one command
 * line argument is supplied to Vido, the controller looks for the first file name it sees
 * and assumes that was the intended file to be scripted from. If the file does not contain
 * scripting commands or is otherwise unreadable, the user receives an error message.</p>
 */
public class VTerminalController implements VController {

  // Initially null at the start of the program
  private final VTextualView view;
  private final Scanner scanner;
  private final VFocusableLayeredImage workingImage;

  /**
   * Describes a method of performing an action on the contents of a layered image and a textual
   * view based on the data managed by a {@link Scanner} object.
   */
  private interface ScannerToParseAction {

    /**
     * Creates an action by decoding information held in a scanner.
     *
     * @param scanner a scanner which can have information extracted from it
     * @return an action which can be applied to change and image and view
     * @throws IllegalArgumentException if the scanner is {@code null}
     */
    VParseAction createAction(Scanner scanner) throws IllegalArgumentException;
  }

  private final Map<String, ScannerToParseAction> commandMap;

  /**
   * Construct a new terminal controller which processes input
   * from some destination source and writes output into the
   * given {@link Appendable} instance.
   * @param readable an object containing commands the controller should execute
   *                 stored as data that can be read by a scanner object
   * @param appendable an object where output from this controller can be
   *                   sent for visual display
   * @throws IllegalArgumentException if any arguments are null
   */
  public VTerminalController(Readable readable, Appendable appendable)
      throws IllegalArgumentException {
    this(readable, appendable, new VFocusableLayerImageImpl(new VLayeredImageImpl()));
  }

  /**
   * Constructs a VTerminal Controller from a readable, appendable, and a VFocusableLayeredImage.
   * @param readable   the readable that provides input to the terminal controller
   * @param appendable the appendable that received the controller output
   * @param model      the layered image model
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public VTerminalController(Readable readable, Appendable appendable,
      VFocusableLayeredImage model) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(readable, model, appendable);
    this.scanner = new Scanner(readable);
    this.view = new VTextView(appendable);
    this.workingImage = model;
    commandMap = new HashMap<>();

    // Map each method to its call
    commandMap.put("create", this::parseCreate);
    commandMap.put("copy", this::parseCopyLayer);
    commandMap.put("remove", this::parseRemove);
    commandMap.put("load", this::parseLoadImage);
    commandMap.put("loadproj", this::parseLoadMultiImage);
    commandMap.put("save", this::parseSaveImage);
    commandMap.put("saveall", this::parseSaveAll);
    commandMap.put("apply", this::parseApply);
    commandMap.put("workon", this::parseSetCurrent);
    commandMap.put("visibility", this::parseSetVisibilty);
  }

  /**
   * Contructs a VTerminal controller with a readable and a model.
   * @param readable the input for the controller
   * @param model    the layered image for the model
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public VTerminalController(Readable readable, VFocusableLayeredImage model)
      throws IllegalArgumentException {
    this(readable, new StringBuilder(), model);
  }


  @Override
  public void run() throws IllegalArgumentException {
    while (scanner.hasNext()) {
      String commandName = scanner.next();

      try {
        if (!commandMap.containsKey(commandName)) {
          view.renderMessage("Invalid command keyword");
        } else {
          commandMap.get(commandName)
              .createAction(scanner)
              .apply(workingImage, view);
        }
      } catch (IOException e) {
        throw new IllegalStateException("Failed to write to output");
      }
    }
  }

  /**
   * Produces an action that prints that the command is not in a valid format.
   *
   * @return an action which renders a message that command is invalid.
   */
  private VParseAction invalidInputAction() {
    return (image, view) -> {
      view.renderMessage("Invalid command format");
    };
  }


  /**
   * Produces an action which displays an error message to the user.
   *
   * @param errorMessage the message to display
   * @return an action which tells the view to display an error message.
   * @throws IllegalArgumentException if the message is null
   */
  private VParseAction errorAction(String errorMessage) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(errorMessage);
    return (image, view) -> {
      view.renderMessage(errorMessage);
    };
  }

  /**
   * Creates a parsing action for creating a layer.
   *
   * @param scanner the scanner providing commands
   * @return a parsing action for creating this new layer
   * @throws IllegalArgumentException if the scanner is null
   */
  private VParseAction parseCreate(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);
    if (!scanner.hasNext()) {
      return invalidInputAction();
    }
    String layerName = scanner.next();
    if (this.isKeyWord(layerName)) {
      return (image, view) -> {
        view.renderMessage("Cannot use a key word for a layer name.");
      };
    }
    if (!scanner.hasNextInt()) {
      return invalidInputAction();
    }
    int layerIndex = scanner.nextInt();
    return (image, view) -> {
      if (image.numLayers() == 0) {
        try {
          image.createNewLayer(layerName, layerIndex, new VImageImpl(100, 100));
          view.renderMessage("Created layer " + layerName
                  + " at position " + (layerIndex + 1));
        } catch (IllegalArgumentException e) {
          view.renderMessage("Error: Invalid layer creation.");
        }
      } else {
        try {
          image.createNewLayer(layerName, layerIndex,
              new VImageImpl(image.getWidth(),
                  image.getHeight()));
          view.renderMessage("Created layer " + layerName
                  + " at position " + (layerIndex + 1));
        } catch (IllegalArgumentException e) {
          view.renderMessage("Error: Invalid layer creation.");
        }
      }
    };
  }

  /**
   * Creates a parsing action for removing a layer.
   *
   * @param scanner the scanner providing commands
   * @return a parsing action for removing a certain layer
   * @throws IllegalArgumentException if the scanner is null
   */
  private VParseAction parseRemove(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);
    if (!scanner.hasNext()) {
      return invalidInputAction();
    }
    String layerName = scanner.next();
    if (this.isKeyWord(layerName)) {
      return (image, view) -> {
        view.renderMessage("Cannot use a key word for a layer name.");
      };
    }
    return (image, view) -> {
      if (image.numLayers() == 0) {
        view.renderMessage("Cannot remove the only layer.");
      } else {
        try {
          image.removeLayer(layerName);
          view.renderMessage("Layer " + layerName + " removed.");
        } catch (IllegalArgumentException e) {
          view.renderMessage("Error: Invalid layer removal.");
        }
      }
    };
  }

  /**
   * Determines if a given word is a keyword, a word used for commands.
   *
   * @param layerName the given word
   * @return if the word is a keyword
   */
  private boolean isKeyWord(String layerName) {
    ArrayList<String> keyWords = new ArrayList<String>(
        List.of("load", "loadproj", "save", "saveall", "workon",
            "create", "remove", "apply", "grayscale",
            "sepia", "blur", "sharpen", "copy",
            "visibility", "on", "off"));
    return keyWords.contains(layerName);
  }

  /**
   * Creates a parsing action for applying an operation to a layer.
   *
   * @param scanner the scanner supplying the commands
   * @return a parsing action for applying the selected operation
   * @throws IllegalArgumentException if the scanner is null
   */
  private VParseAction parseApply(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);
    if (!scanner.hasNext()) {
      return invalidInputAction();
    }
    String opType = scanner.next();
    VImageContentOperation op;
    switch (opType) {
      case "sepia":
        op = VImageFilters.sepiaColorFilter();
        break;
      case "grayscale":
        op = VImageFilters.grayscaleColorFilter();
        break;
      case "blur":
        op = VImageFilters.blurFilter();
        break;
      case "sharpen":
        op = VImageFilters.sharpenFilter();
        break;
      default:
        return invalidInputAction();
    }
    return (image, view) -> {
      try {
        image.apply(op, image.getFocusLayerIndex());
        view.renderMessage("Applied " + opType + ".");
      } catch (IllegalArgumentException e) {
        view.renderMessage("Error: Could not modify this layer");
      }
    };
  }

  /**
   * Creates a parsing action for copying a layer.
   *
   * @param scanner the scanner providing commands
   * @return a parsing action for copying a specific layer to a new location
   * @throws IllegalArgumentException if the scanner is null
   */
  private VParseAction parseCopyLayer(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);
    if (!scanner.hasNext()) {
      return invalidInputAction();
    }
    String layerName = scanner.next();
    if (this.isKeyWord(layerName)) {
      return (image, view) -> {
        view.renderMessage("Cannot use a key word for a layer name.");
      };
    }
    if (!scanner.hasNext()) {
      return invalidInputAction();
    }
    String newLayerName = scanner.next();
    if (this.isKeyWord(newLayerName)) {
      return (image, view) -> {
        view.renderMessage("Cannot use a key word for a layer name.");
      };
    }
    if (!scanner.hasNextInt()) {
      return invalidInputAction();
    }
    int newPos = scanner.nextInt();
    return (image, view) -> {
      try {
        image.copyLayer(layerName, newLayerName, newPos);
        view.renderMessage("Copied " + layerName + " to "
            + newLayerName + " at " + newPos);
      } catch (IllegalArgumentException e) {
        view.renderMessage("Error: Could not copy this layer.");
      }
    };
  }

  /**
   * Creates a new parsing layer for setting visibility.
   *
   * @param scanner the scanner providing commands
   * @return a parsing commands for setting visibility to the current layer
   * @throws IllegalArgumentException if the scanner is null
   */
  private VParseAction parseSetVisibilty(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);
    if (!scanner.hasNext()) {
      return invalidInputAction();
    }
    String visibility = scanner.next();
    boolean isVisible;
    switch (visibility) {
      case "on":
        isVisible = true;
        break;
      case "off":
        isVisible = false;
        break;
      default:
        return invalidInputAction();
    }
    boolean finalIsVisible = isVisible;
    return (image, view) -> {
      try {
        image.setVisible(finalIsVisible, image.getFocusLayerIndex());
        view.renderMessage("Set visibility to " + visibility);
      } catch (IllegalArgumentException e) {
        view.renderMessage("Error: Could not set visibility.");
      }
    };
  }

  /**
   * Creates a new parse action to load a multi-layered image already created by another instance of
   * Vido run prior to this execution context.
   *
   * <p>If a multi-layered image is already loaded</p>
   *
   * @param scanner a scanner containing input from the user or script
   * @return a new parsing action that loads all of the images from the given multi-layered image to
   *         be used immediately
   * @throws IllegalArgumentException if the scanner is {@code null}
   */
  private VParseAction parseLoadMultiImage(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);

    try {
      String projectName = readNextString(scanner);
      Path projectPath = Path.of(projectName).toAbsolutePath();

      // Get the correct image saver for the given file path
      VLayeredImageProvider loader = new VLayeredImageDiskProvider(projectPath);

      return (image, view) -> {
        try {
          view.renderMessage("Loading multi-layered image");
          VLayeredImage imageOnDisk = loader.extractLayeredImage();

          for (int i = 0; i < imageOnDisk.numLayers(); i += 1) {
            VLayer layer = imageOnDisk.getLayer(i);
            image.createNewLayer(layer.getName(), i, layer);
          }

          // Set the current layer to the first layer
          image.setFocusLayer(0);
          image.setName(imageOnDisk.getName());
          view.renderMessage("Loading complete");
        } catch (IllegalArgumentException e) {
          view.renderMessage("Error: Failed to load multi-layered image into the current project");
        } catch (ImageExtractionException e) {
          view.renderMessage("Error: Failed to extract image from " + projectPath.toString());
        }
      };
    } catch (IllegalArgumentException exception) {
      return errorAction("Error: " + exception.getMessage());
    }
  }

  /**
   * Creates a new parse action to load a single image into the current layer of the working image.
   *
   * @param scanner a scanner containing input from the user or script
   * @return a new parsing action that replaces the contents of the current layers with those of the
   *         image specified
   * @throws IllegalArgumentException if the scanner is {@code null}
   */
  private VParseAction parseLoadImage(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);

    try {
      String fileName = readNextString(scanner);
      Path filePath = Path.of(fileName).toAbsolutePath();

      // Get the correct image saver for the given file path
      VImageProvider provider = VImageManagers.diskProviderFor(filePath);

      // Test that the name is not a keyword

      return (image, view) -> {
        try {
          VImage loaded = provider.extractImage();
          int workingLayer = image.getFocusLayerIndex();

          if (workingLayer < 0) {
            throw new IllegalArgumentException("No layer is currently in focus");
          }

          image.replace(workingLayer, loaded);
          view.renderMessage("Loading image \"" + fileName + "\"");
        } catch (IllegalArgumentException e) {
          view.renderMessage("Error: " + e.getMessage());
        } catch (ImageExtractionException e) {
          view.renderMessage("Error: Failed to load image \"" + fileName + "\"");
        }
      };
    } catch (IllegalArgumentException exception) {
      return errorAction("Error: " + exception.getMessage());
    }
  }

  /**
   * Creates a new parse action to save the currently focused layer of a multi-layered image to
   * disk.
   *
   * @param scanner a scanner containing input from the user or script
   * @return a new parsing action that asks the current layer to be saved to disk to be retrieved
   *         later
   * @throws IllegalArgumentException if the scanner is {@code null}
   */
  private VParseAction parseSaveImage(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);

    try {
      String fileName = readNextString(scanner);
      Path filePath = Path.of(fileName).toAbsolutePath();

      // Get the correct image saver for the given file path
      VImageSaver saver = VImageManagers.diskSaverFor(filePath);

      return (image, view) -> {
        try {
          VLayer layer = image.getFocusLayer()
              .orElseThrow(() -> new IllegalArgumentException("No layer to save"));
          view.renderMessage("Saving layer \"" + layer.getName() + "\"");
          saver.saveImage(layer);
        } catch (IllegalArgumentException e) {
          view.renderMessage("Error: No layer is in focus");
        } catch (ImageSavingException e) {
          view.renderMessage("Error: Failed to save image with name " + fileName);
        }
      };
    } catch (IllegalArgumentException exception) {
      return errorAction("Error: Failed to save image");
    }
  }

  /**
   * Creates a new parse action to save the entire multi-layered image to disk.
   *
   * @param scanner a scanner containing input from the user or script
   * @return a new parsing action that asks the image to be saved
   * @throws IllegalArgumentException if the scanner is {@code null}
   */
  private VParseAction parseSaveAll(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);

    // Saves with respect to the directory the program
    // is executed from (unless the path is an absolute path)
    try {
      // Extract the other two arguments, if they are present
      String multiLayerFileName = this.readNextString(scanner);
      String newImageName = Path.of(multiLayerFileName).getFileName().toString();
      String internalFileExtension = this.readNextString(scanner);

      // Create the appropriate path and save the image
      Path filePath = Path.of(multiLayerFileName).toAbsolutePath();
      VLayeredImageSaver saver = new VLayeredImageDiskSaver(internalFileExtension, filePath);

      return (image, view) -> {
        view.renderMessage("Saving multi-layered image " + multiLayerFileName);
        image.setName(newImageName);

        try {
          saver.saveImage(image);
        } catch (ImageSavingException e) {
          view.renderMessage("Failed to save multi-layered image. " + e.getMessage());
        }
      };
    } catch (IllegalArgumentException exception) {
      return errorAction("Error: " + exception.getMessage());
    }
  }

  /**
   * Creates a new parse action to set a current layer as the focus layer.
   *
   * @param scanner a scanner containing input from the user or script
   * @return a new parsing action that sets a layer as the layer in focus
   * @throws IllegalArgumentException if the scanner is {@code null}
   */
  private VParseAction parseSetCurrent(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);
    try {
      String layerName = readNextString(scanner);

      // Test that the name is not a keyword
      if (this.isKeyWord(layerName)) {
        return this.errorAction("Current layer refers to a keyword");
      }

      return (image, view) -> {
        try {
          image.setFocusLayer(layerName);
          view.renderMessage("Setting focus layer " + layerName);
        } catch (IllegalArgumentException e) {
          view.renderMessage("Invalid layer name " + layerName);
        }
      };
    } catch (IllegalArgumentException exception) {
      return errorAction("Error: " + exception.getMessage());
    }
  }

  /**
   * Reads the next integer from the scanner if such an integer exists and throws an exception
   * otherwise.
   *
   * @return the next integer
   * @throws IllegalArgumentException if the scanner is null
   */
  private int readNextInt(Scanner scanner) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(scanner);
    if (!scanner.hasNext()) {
      throw new IllegalArgumentException("Ran out of input");
    }

    try {
      return scanner.nextInt();
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not extract another integer when"
          + " one was expected");
    }
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
