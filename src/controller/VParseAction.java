package controller;

import java.io.IOException;
import model.image.VFocusableLayeredImage;
import view.VTextualView;

/**
 * Represents an action that should be taken when trying to parse input by the user. A {@code
 * VParseAction} is a representation of how a {@link model.image.VLayeredImage} and a
 * {@link view.VTextualView} should update and how the program should progress (whether
 * or not is should continue); that is, a {@link VParseAction} is effectively a (mutating) function
 * from the set of layered images and views to booleans.
 */
@FunctionalInterface
public interface VParseAction {
  /**
   * Apply the action to the given image and view.
   *
   * @param image an image the action applies to
   * @param view  a view the action renders to
   * @throws IOException if the action could not correctly display
   *         to the view
   */
  void apply(VFocusableLayeredImage image, VTextualView view) throws IOException;
}