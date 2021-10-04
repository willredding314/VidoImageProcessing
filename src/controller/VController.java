package controller;

/**
 * Manages interactions between external events and the image model
 * and keeps the image processing program alive until the user quits.
 *
 * <p>The {@link VController} is responsible for managing the lifetime of
 * Vido. Controllers merely run the program after being given a set of
 * command line arguments.</p>
 */
public interface VController {

  /**
   * Run the controller.
   */
  void run();
}
