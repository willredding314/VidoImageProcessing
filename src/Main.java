import controller.VApplication;
import controller.VApplicationImpl;

/**
 * The entry point into the interactive command line program where the use can start the GUI image
 * processor named Vido.
 *
 * <p>This {@code Main} class initializes the application to be ready
 * for image interaction in a GUI environment (or text environment if the display is standard
 * output)</p>
 */
public final class Main {

  /**
   * The main entry point into Vido.
   *
   * @param args command line arguments passed in when the program started up
   */
  public static void main(String[] args) {
    VApplication application = new VApplicationImpl();

    try {
      application.run(args);
    }
    catch (Exception e) {
      System.err.println("Program quit unexpectedly");
    }
  }

}
