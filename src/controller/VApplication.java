package controller;

/**
 * Represents an instance of Vido that Java executes.
 *
 * <p>A {@link VApplication} instance is at the heart of the program.
 * When Java is launched to run Vido, a new {@link VApplication} instance is
 * is created in the main method and control is handed over to the application.
 * Applications can decide how and when to create the program's controllers.</p>
 */
public interface VApplication {
  /**
   * Run Vido with the given command line arguments.
   *
   * @param args command line arguments passed to Vido on launch
   * @throws IllegalArgumentException if the arguments are {@code null}
   */
  void run(String[] args) throws IllegalArgumentException;
}
