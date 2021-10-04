package view;

import java.io.IOException;

/**
 * Represents an object which can be displayed to the user on.
 *
 * <p>Any {@link VTextualView} can display itself as text to the terminal.</p>
 */
public interface VTextualView {
  /**
   * Render a particular message to the text view.
   * @param message a message to render
   * @throws IllegalArgumentException if the message is null
   * @throws IOException if the message could not successfully be rendered
   */
  void renderMessage(String message) throws IllegalArgumentException, IOException;
}
