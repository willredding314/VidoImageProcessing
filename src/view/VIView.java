package view;

import model.image.VLayer;

/**
 * Defines aspects required for a custom Vido view to
 * be functional in the eyes of the user.
 */
public interface VIView {

  /**
   * Renders the given layer as the top most visible layer.
   * @param image the top most visible layer in a given layer
   * @thrwos IllegalArgumentException if the layer is null
   */
  void renderAsTopMostVisibleLayer(VLayer image) throws IllegalArgumentException;

  /**
   * Renders the given layer as the current layer.
   * @param image the current layer in the project
   * @throws IllegalArgumentException if the layer is null
   */
  void renderAsCurrentLayer(VLayer image) throws IllegalArgumentException;

  /**
   * Renders the layer names based on a series of names.
   * @param names the names of the layers in the project
   * @throws IllegalArgumentException if the array of names is null, or if any
   *         name in the array of names is null
   */
  void renderLayerNames(String[] names, int index) throws IllegalArgumentException;

  /**
   * Adds a listener to the available listeners to enable a new action.
   * @param listener the listener to be added
   * @throws IllegalArgumentException if the listener is null
   */
  void addListener(VIViewListener listener) throws IllegalArgumentException;

  /**
   * Starts the view.
   */
  void display();

  /**
   * Removes all of the layer names displayed to
   * the user from being shown.
   */
  void clearLayerNames();
}
