package model.image;

import model.processing.VImageContentOperation;

/**
 * Describes an visual component that manages several images as layers and provides functionality
 * for manipulating those layers independently.
 *
 * <p>A {@link VLayeredImage} represents a collection of {@link VLayer}s. Each
 * layer in the image can be manipulated independently of other layers in the multi-layered {@link
 * VLayeredImage}. Layers in an image can be identified by name or index.</p>
 */
public interface VLayeredImage {

  /**
   * Produces a copy of the given image.
   *
   * <p>The image that is produced has the same number of layers with
   * the same name and contents as the given image</p>
   *
   * @return an image whose contents match those of this image
   */
  VLayeredImage copy();

  /**
   * Creates a new layer in a Layered image based on a name and an index.
   *
   * @param layerName the name of the new layer
   * @param index     the position of the new layer
   * @param contents  the VImage to be placed in this layer
   * @throws IllegalArgumentException if the name or index is null, if the index is not between 0
   *                                  and the number of layers in the layered image, or if the layer
   *                                  name already exists, or if the image is null
   */
  void createNewLayer(String layerName, int index, VImage contents) throws IllegalArgumentException;

  /**
   * Replaces an image of a layer at a specified index.
   *
   *<p>If there is only a single layer in this image, the method
   * changes the overall width and height of the layered image
   * to match that of the given image whose contents are copied</p>
   *
   * @param index    the position of the layer to modify
   * @param contents the image to replace the layer's old image
   * @throws IllegalArgumentException if the index is out of bounds, or if the index or the contents
   *                                  are null
   */
  void replace(int index, VImage contents) throws IllegalArgumentException;

  /**
   * Replaces an image of a layer with a specified name.
   *
   * <p>If there is only a single layer in this image, the method
   * changes the overall width and height of the layered image
   * to match that of the given image whose contents are copied</p>
   *
   * @param layerName the name of the layer to modify
   * @param contents  the image to replace the layer's old image
   * @throws IllegalArgumentException if the layerName does not already exist, or if the layer name
   *                                  or the contents are null
   */
  void replace(String layerName, VImage contents) throws IllegalArgumentException;

  /**
   * Copies a layer based on an index to a new position within the layered image.
   *
   * @param sourceLayer      the index of the layer to be copied
   * @param destinationLayer the name of the new layer
   * @param destinationIndex the index of the new layer
   * @throws IllegalArgumentException if the source index is out of bounds, if the destination index
   *                                  is not between 0 and the number of layers, if the destination
   *                                  layer name already exists or if any of the arguments are null
   */
  void copyLayer(int sourceLayer, String destinationLayer, int destinationIndex)
      throws IllegalArgumentException;

  /**
   * Copies a layer based on a layer name to a new position within the layered image.
   *
   * @param sourceLayer      the name of the layer to be copied
   * @param destinationLayer the name of the new layer
   * @param destinationIndex the index of the new layer
   * @throws IllegalArgumentException if the source layer does not exist, if the destination index
   *                                  is not between 0 and the number of layers, if the destination
   *                                  layer name already exists or if any of the arguments are null
   */
  void copyLayer(String sourceLayer, String destinationLayer, int destinationIndex)
      throws IllegalArgumentException;

  /**
   * Removes a layer at a specified index.
   *
   * @param index the index of the layer to be removed
   * @throws IllegalArgumentException if the index of the layer is out of bounds
   */
  void removeLayer(int index) throws IllegalArgumentException;

  /**
   * Removes a layer with a specified name.
   *
   * @param layerName the name of the layer to be removed
   * @throws IllegalArgumentException if the layer name does not exist
   */
  void removeLayer(String layerName) throws IllegalArgumentException;

  /**
   * Sets a layer's visibility based on an index.
   *
   * @param isVisible if the layer should be visible
   * @param index     the index of the layer
   * @throws IllegalArgumentException if the index is out of bounds, or if either argument is null
   */
  void setVisible(boolean isVisible, int index) throws IllegalArgumentException;

  /**
   * Sets a layer's visibility based on a layer name.
   *
   * @param isVisible if the layer should be visible
   * @param layerName the name of the layer
   * @throws IllegalArgumentException if the layer name does not exist, if or if either argument is
   *                                  null
   */
  void setVisible(boolean isVisible, String layerName) throws IllegalArgumentException;

  /**
   * Renames a layer based on the index of a layer.
   *
   * @param newName the new name for the layer
   * @param index   the index of the layer to be renamed
   * @throws IllegalArgumentException if the name already exists, if the index is out of bounds, of
   *                                  if either argument is null
   */
  void rename(String newName, int index) throws IllegalArgumentException;

  /**
   * Renames a layer based on the name of a layer.
   *
   * @param newName      the new name for the layer
   * @param oldLayerName the name of the old layer
   * @throws IllegalArgumentException if the new name already exists, if the old name does not
   *                                  exist, or if either argument is null
   */
  void rename(String newName, String oldLayerName) throws IllegalArgumentException;

  // Apply an operation to a layer

  /**
   * Applies a single layer operation to a layer at a specified index.
   *
   * @param operation the operation to be applied to a layer
   * @param index     the index of the layer
   * @throws IllegalArgumentException if the index is out of bounds, or if either argument is null
   */
  void apply(VImageContentOperation operation, int index) throws IllegalArgumentException;

  /**
   * Applies a single layer operation to a layer with a specified name.
   *
   * @param operation the operation to be applied to a layer
   * @param layerName the name of the layer
   * @throws IllegalArgumentException if the layer name does not exist, or if either argument is
   *                                  null
   */
  void apply(VImageContentOperation operation, String layerName) throws IllegalArgumentException;

  /**
   * Determines the number of layers in this image.
   *
   * @return the number of layers in the multi-layered image
   */
  int numLayers();

  /**
   * Gets the layer at a specified index.
   *
   * @param index the index of the layer
   * @return the layer at the index
   * @throws IllegalArgumentException if the index is out of bounds or null
   */
  VLayer getLayer(int index) throws IllegalArgumentException;

  /**
   * Gets a layer at a specified name.
   *
   * @param layerName the name of the layer
   * @return the layer with the given name
   * @throws IllegalArgumentException if the layer name is null or does not exist
   */
  VLayer getLayer(String layerName) throws IllegalArgumentException;

  /**
   * Gets the name of the layered image.
   *
   * @return the name of the layered image
   */
  String getName();

  /**
   * Sets the name of the layered image.
   *
   * @param name the new name of the image
   * @throws IllegalArgumentException if the name is null or the empty string
   */
  void setName(String name) throws IllegalArgumentException;

  /**
   * Gets the width of a layered image.
   *
   * <p>If there are no layers in this image, the method returns -1</p>
   *
   * @return the width of the layered image, or -1 if
   *         there are no layers in this image
   */
  int getWidth();

  /**
   * Gets the height of a layered image.
   *
   * <p>If there are no layers in this image, the method returns -1</p>
   *
   * @return the height of the layered image, or -1 if
   *         there are no layers in this image
   */
  int getHeight();

}
