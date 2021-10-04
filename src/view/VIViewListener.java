package view;

import model.processing.VImageContentOperation;

/**
 * Represents a listener for a VIView, able to respond to any
 * commands from the GUI.
 */
public interface VIViewListener {

  /**
   * Saves the project, prompting the user for a location,
   * a name, and a file type.
   */
  void saveProject();

  /**
   * Saves the current layer of the project, prompting the
   * use for the name of the file.
   */
  void saveLayer();

  /**
   * Saves the topMostVisible layer of the project,
   * prompting the user for the name of the file.
   */
  void saveTopMostVisibleLayer();

  /**
   * Adds a layer to the project immediately after the
   * current layer, based on a name from user input.
   */
  void addLayer();

  /**
   * Loads an image into the current layer, based on a
   * user selected file.
   */
  void loadImage();

  /**
   * Loads a saved project into the current project, based
   * on a user selected directory.
   */
  void loadProject();

  /**
   * Adds a layer to the project that is a copy of the current
   * layer, based on a name and position from user input.
   */
  void addFromCopy();

  /**
   * Removes the current layer from the project, prompting the
   * user to ensure that they want the layer removed.
   */
  void removeLayer();

  /**
   * Sets the visibility of the current layer to on.
   */
  void setCurrentVisibleOn();

  /**
   * Sets the visibility of the current layer to off.
   */
  void setCurrentVisibleOff();

  /**
   * Changes the name of the current layer, from a name of
   * user input.
   */
  void changeName();

  /**
   * Selects a layer as the current layer.
   * @param index the index of the selected layer
   */
  void layerWithIndexSelected(int index);

  /**
   * Applies a given operation to the current layer
   * of the project.
   * @param op the operation to be applied
   */
  void applyOperation(VImageContentOperation op);

  /**
   * Applies the mosiac operation to the current layer,
   * prompting the user for the number of seeds.
   */
  void applyMosaic();

  /**
   * Executes a file script, prompting the user for the
   * location of the file.
   */
  void executeScript();

}
