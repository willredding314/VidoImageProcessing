package view;

import java.io.IOException;
import model.misc.ObjectsExtension;

/**
 * A view that renders its textual output to a single source.
 */
public class VTextView implements VTextualView {

  private final Appendable appendable;

  /**
   * Construct a new text view by writing to the given source.
   *
   * @param appendable a source to write text to
   * @throws IllegalArgumentException if the appendable is null
   */
  public VTextView(Appendable appendable) throws IllegalArgumentException {
    this.appendable = ObjectsExtension.asNonnull(appendable);
  }

  @Override
  public void renderMessage(String message) throws IllegalArgumentException, IOException {
    appendable.append(message).append(System.lineSeparator());
  }
}
