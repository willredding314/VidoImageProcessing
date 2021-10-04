package model.image;

import model.misc.ObjectsExtension;
import model.processing.VImageContentOperation;

import java.util.ArrayList;

/**
 * An implementation for a layered image.
 */
public class VLayeredImageImpl implements VLayeredImage {

  private final ArrayList<VLayer> layers;
  private String name;
  private int width;
  private int height;

  /**
   * Constructs a layered image with an unspecified size.
   *
   * <p>By default, any newly created image is named as
   * "untitled".</p>
   */
  public VLayeredImageImpl() {
    this.layers = new ArrayList<VLayer>();
    this.name = "untitled";
    this.width = -1;
    this.height = -1;
  }

  /**
   * Construct a new layered image by copying the contents of the
   * given image.
   *
   * @param other another layered image to copy
   * @throws IllegalArgumentException if {@code other} is {@code null}
   */
  private VLayeredImageImpl(VLayeredImage other) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(other);

    this.name = other.getName();
    this.layers = new ArrayList<VLayer>();
    this.width = -1;
    this.height = -1;

    for (int i = 0; i < other.numLayers(); i += 1) {
      VLayer layerInOther = other.getLayer(i);
      this.createNewLayer(layerInOther.getName(), i, layerInOther);
    }
  }

  @Override
  public void createNewLayer(String layerName, int index, VImage contents)
      throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(name, index, contents);

    if (this.nameInLayers(layerName)) {
      throw new IllegalArgumentException("Cannot have duplicate "
              + "layer names.");
    }

    // You should be able to add a new layer to the end
    if (index < 0 || index > this.numLayers()) {
      throw new IllegalArgumentException("Index is out of bounds");
    }

    if (contents.getWidth() != width || contents.getHeight() != height) {
      if (this.numLayers() == 0) {
        this.layers.add(index, new VLayerImpl(layerName, contents));
        this.width = contents.getWidth();
        this.height = contents.getHeight();
      } else {
        throw new IllegalArgumentException("Layers must have matching dimensions.");
      }
    } else {
      this.layers.add(index, new VLayerImpl(layerName, contents));
    }
  }

  /**
   * Determines if a layer of the given name exists.
   *
   * @param layerName the name of the layer
   * @return if the given layer name exists
   * @throws IllegalArgumentException if the layer name is null
   */
  private boolean nameInLayers(String layerName)
      throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(layerName);

    for (int i = 0; i < this.numLayers(); i += 1) {
      if (this.getLayer(i).getName().equals(layerName)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void replace(int index, VImage contents) throws IllegalArgumentException {

    ObjectsExtension.requireNonnull(contents, index);

    if (index < 0 || index >= this.numLayers()) {
      throw new IllegalArgumentException("Index is out of bounds");
    }

    this.replaceFromIdx(index, contents);
  }

  @Override
  public void replace(String layerName, VImage contents) throws IllegalArgumentException {

    ObjectsExtension.requireNonnull(contents, layerName);

    if (!this.nameInLayers(layerName)) {
      throw new IllegalArgumentException("Name does not exist.");
    }

    this.replaceFromIdx(this.getIdxFromName(layerName), contents);

  }

  /**
   * Replaces a layer at a given index.
   *
   * @param index    the index of the replaced layer
   * @param contents the contents of the new layer
   * @throws IllegalArgumentException if the new contents have incompatible dimensions, or if the
   *                                  index or contents are null
   */
  private void replaceFromIdx(int index, VImage contents)
      throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(contents, index);
    VLayer oldLayer = this.getLayer(index);

    if (contents.getWidth() != width || contents.getHeight() != height) {
      if (this.numLayers() == 1) {
        this.layers.set(index, new VLayerImpl(oldLayer.getName(),
            contents));
        this.width = contents.getWidth();
        this.height = contents.getHeight();
      } else {
        throw new IllegalArgumentException("Layers must have matching dimensions.");
      }
    } else {
      this.layers.set(index, new VLayerImpl(oldLayer.getName(),
          contents));
    }
  }

  /**
   * Gets the index of a layer name.
   *
   * @param layerName the name of the layer
   * @return the index of the layer with the given name
   * @throws IllegalArgumentException if the layer name is null, or if the layer does not exist
   */
  private int getIdxFromName(String layerName) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(layerName);
    for (int i = 0; i < this.numLayers(); i += 1) {
      if (this.getLayer(i).getName().equals(layerName)) {
        return i;
      }
    }

    throw new IllegalArgumentException("Layer name not found.");
  }

  @Override
  public void copyLayer(int sourceLayer, String destinationLayer, int destinationIndex)
      throws IllegalArgumentException {

    ObjectsExtension.requireNonnull(sourceLayer, destinationLayer, destinationIndex);

    if (sourceLayer < 0 || sourceLayer >= this.numLayers()) {
      throw new IllegalArgumentException("Index is out of bounds");
    }

    if (destinationIndex < 0 || destinationIndex > this.numLayers()) {
      throw new IllegalArgumentException("Index is out of bounds");
    }

    if (this.nameInLayers(destinationLayer)) {
      throw new IllegalArgumentException("Cannot have duplicate "
              + "layer names.");
    }

    this.copyLayerWithIdx(sourceLayer, destinationLayer, destinationIndex);

  }

  @Override
  public void copyLayer(String sourceLayer, String destinationLayer, int destinationIndex)
      throws IllegalArgumentException {

    ObjectsExtension.requireNonnull(sourceLayer, destinationLayer, destinationIndex);

    if (!this.nameInLayers(sourceLayer)) {
      throw new IllegalArgumentException("Name does not exist.");
    }

    if (destinationIndex < 0 || destinationIndex > this.numLayers()) {
      throw new IllegalArgumentException("Index is out of bounds");
    }

    if (this.nameInLayers(destinationLayer)) {
      throw new IllegalArgumentException("Cannot have duplicate "
              + "layer names.");
    }

    this.copyLayerWithIdx(this.getIdxFromName(sourceLayer),
        destinationLayer, destinationIndex);
  }

  /**
   * Copies a layer based on an index.
   *
   * @param index            the index of the layer to be copied
   * @param destinationLayer the name of the new layer
   * @param destinationIndex the index of the new layer
   * @throws IllegalArgumentException if any of the arguments are null
   */
  private void copyLayerWithIdx(int index, String destinationLayer, int destinationIndex)
      throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(index, destinationLayer, destinationIndex);
    VLayer oldLayer = this.getLayer(index);
    VLayer newLayer = oldLayer.copy();
    newLayer.setName(destinationLayer);

    this.layers.add(destinationIndex, newLayer);
  }

  @Override
  public void removeLayer(int index) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(index);

    if (index < 0 || index > this.numLayers() - 1) {
      throw new IllegalArgumentException("Index out of bounds.");
    }

    this.removeLayerWithIdx(index);
  }

  @Override
  public void removeLayer(String layerName) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(layerName);

    if (!this.nameInLayers(layerName)) {
      throw new IllegalArgumentException("Name does not exist.");
    }

    this.removeLayerWithIdx(this.getIdxFromName(layerName));
  }

  /**
   * Removes a layer at a given index.
   *
   * @param idxFromName the index of the layer
   * @throws IllegalArgumentException if there is only one layer, or if any of the arguments are
   *                                  null
   */
  private void removeLayerWithIdx(int idxFromName)
      throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(idxFromName);
    this.layers.remove(idxFromName);
  }

  @Override
  public void setVisible(boolean isVisible, int index) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(isVisible, index);

    if (index < 0 || index > this.numLayers() - 1) {
      throw new IllegalArgumentException("Index out of bounds.");
    }

    this.setVisibleWithIdx(isVisible, index);
  }

  @Override
  public void setVisible(boolean isVisible, String layerName) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(isVisible, layerName);

    if (!this.nameInLayers(layerName)) {
      throw new IllegalArgumentException("Name does not exist.");
    }

    this.setVisibleWithIdx(isVisible, this.getIdxFromName(layerName));
  }

  private void setVisibleWithIdx(boolean isVisible, int index) {
    ObjectsExtension.requireNonnull(index, isVisible);
    VLayer layer = this.getLayer(index);
    layer.setVisible(isVisible);
  }

  @Override
  public void rename(String newName, int index) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(newName, index);

    if (this.nameInLayers(newName)) {
      throw new IllegalArgumentException("Cannot have duplicate "
              + "layer names.");
    }

    if (index < 0 || index > this.numLayers() - 1) {
      throw new IllegalArgumentException("Index out of bounds.");
    }

    this.renameWithIdx(newName, index);

  }

  @Override
  public void rename(String newName, String oldLayerName) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(newName, oldLayerName);

    if (this.nameInLayers(newName)) {
      throw new IllegalArgumentException("Cannot have duplicate "
              + "layer names.");
    }

    if (!this.nameInLayers(oldLayerName)) {
      throw new IllegalArgumentException("Name does not exist.");
    }

    this.renameWithIdx(newName, this.getIdxFromName(oldLayerName));
  }

  /**
   * Renames a layer at a given index.
   *
   * @param newName     the new name of the layer
   * @param idxFromName the index of the layer
   * @throws IllegalArgumentException if the name or index are null
   */
  private void renameWithIdx(String newName, int idxFromName) {
    ObjectsExtension.requireNonnull(newName, idxFromName);
    VLayer layer = this.getLayer(idxFromName);
    layer.setName(newName);
  }

  @Override
  public void apply(VImageContentOperation operation, int index) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(operation, index);

    if (index < 0 || index > this.numLayers() - 1) {
      throw new IllegalArgumentException("Index out of bounds.");
    }

    this.applyWithIdx(operation, index);
  }

  @Override
  public void apply(VImageContentOperation operation, String layerName)
      throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(operation, layerName);

    if (!this.nameInLayers(layerName)) {
      throw new IllegalArgumentException("Name does not exist.");
    }

    this.applyWithIdx(operation, this.getIdxFromName(layerName));

  }

  /**
   * Applies an operation based on an index.
   *
   * @param operation the operation to be applied
   * @param index     the index of the layer
   * @throws IllegalArgumentException if the modified layer has an incompatible size, or if the
   *                                  operation or index are null
   */
  private void applyWithIdx(VImageContentOperation operation, int index)
      throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(operation, index);

    VLayer oldLayer = this.getLayer(index);
    VLayer newLayer = new VLayerImpl(oldLayer.getName(), operation.operateOn(oldLayer));

    if (this.numLayers() == 1) {
      this.replace(index, newLayer);
    } else {
      if (newLayer.getWidth() != this.width || newLayer.getHeight() != this.height) {
        throw new IllegalArgumentException("Layers must have matching dimensions.");
      } else {
        this.replace(index, newLayer);
      }
    }
  }

  @Override
  public int numLayers() {
    return this.layers.size();
  }

  @Override
  public VLayer getLayer(int index) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(index);
    if (index < 0 || index > this.numLayers() - 1) {
      throw new IllegalArgumentException("Index out of bounds.");
    }

    return this.layers.get(index);
  }

  @Override
  public VLayer getLayer(String layerName) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(layerName);

    for (int i = 0; i < this.numLayers(); i += 1) {
      if (this.getLayer(i).getName().equals(layerName)) {
        return this.getLayer(i);
      }
    }

    throw new IllegalArgumentException("Layer name does not exist.");
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String name) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(name);

    if (name.isEmpty()) {
      throw new IllegalArgumentException("Cannot name a layer with the empty string");
    }

    this.name = name;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public VLayeredImage copy() {
    return new VLayeredImageImpl(this);
  }
}
