package model.image;

import java.util.Optional;
import model.misc.ObjectsExtension;
import model.processing.VImageContentOperation;

/**
 * A concrete implementation of {@link VFocusableLayeredImage} which
 * wraps a {@link VLayeredImage} as a delegate.
 *
 * <p>The {@link VFocusableLayerImageImpl} maintains image state by keeping a reference
 * to another image. When you supply an image to the constructor, a copy of the provided
 * image is made to ensure that modifications to this image do not transfer to the client </p>
 */
public class VFocusableLayerImageImpl implements VFocusableLayeredImage {
  private final VLayeredImage wrappedImage;
  private int focusIndex;

  public VFocusableLayerImageImpl(VLayeredImage image) throws IllegalArgumentException {
    this.wrappedImage = ObjectsExtension.asNonnull(image).copy();
    this.focusIndex = -1;
  }

  /**
   * Ensures that the given index, which is set to become the next
   * focus index, is a valid index.
   *
   * @param index the next focus index
   * @throws IllegalArgumentException if the index is invalid
   */
  private void validateFocusIndex(int index) throws IllegalArgumentException {
    if (index < 0 || index >= this.numLayers()) {
      throw new IllegalArgumentException("Invalid index");
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
  public void setFocusLayer(int index) throws IllegalArgumentException {
    validateFocusIndex(index);
    this.focusIndex = index;
  }

  @Override
  public void setFocusLayer(String name) throws IllegalArgumentException {
    int index = getIdxFromName(name);
    validateFocusIndex(index);
    this.focusIndex = index;
  }

  @Override
  public int getFocusLayerIndex() {
    return this.focusIndex;
  }

  @Override
  public Optional<VLayer> getFocusLayer() {
    if (focusIndex < 0) {
      return Optional.empty();
    }

    return Optional.of(this.getLayer(focusIndex));
  }

  @Override
  public VLayeredImage copy() {
    return wrappedImage.copy();
  }

  @Override
  public void createNewLayer(String layerName, int index, VImage contents)
      throws IllegalArgumentException {
    if (index <= focusIndex) {
      focusIndex += 1;
    }
    else if (wrappedImage.numLayers() == 0) {
      focusIndex = 0;
    }

    wrappedImage.createNewLayer(layerName, index, contents);
  }

  @Override
  public void replace(int index, VImage contents) throws IllegalArgumentException {
    wrappedImage.replace(index, contents);
  }

  @Override
  public void replace(String layerName, VImage contents) throws IllegalArgumentException {
    wrappedImage.replace(layerName, contents);
  }

  @Override
  public void copyLayer(int sourceLayer, String destinationLayer, int destinationIndex)
      throws IllegalArgumentException {
    wrappedImage.copyLayer(sourceLayer, destinationLayer, destinationIndex);

    if (destinationIndex <= focusIndex) {
      focusIndex += 1;
    }
  }

  @Override
  public void copyLayer(String sourceLayer, String destinationLayer, int destinationIndex)
      throws IllegalArgumentException {
    wrappedImage.copyLayer(sourceLayer, destinationLayer, destinationIndex);

    if (destinationIndex <= focusIndex) {
      focusIndex += 1;
    }
  }

  @Override
  public void removeLayer(int index) throws IllegalArgumentException {
    wrappedImage.removeLayer(index);

    if (index <= focusIndex) {
      focusIndex = Math.max(-1, focusIndex - 1);
    }
  }

  @Override
  public void removeLayer(String layerName) throws IllegalArgumentException {

    // Retrieve the index of the layer we are about
    // to remove before removing it
    int index = getIdxFromName(layerName);
    wrappedImage.removeLayer(layerName);

    if (index <= focusIndex) {
      focusIndex = Math.max(-1, focusIndex - 1);
    }
  }

  @Override
  public void setVisible(boolean isVisible, int index) throws IllegalArgumentException {
    wrappedImage.setVisible(isVisible, index);
  }

  @Override
  public void setVisible(boolean isVisible, String layerName) throws IllegalArgumentException {
    wrappedImage.setVisible(isVisible, layerName);
  }

  @Override
  public void rename(String newName, int index) throws IllegalArgumentException {
    wrappedImage.rename(newName, index);
  }

  @Override
  public void rename(String newName, String oldLayerName) throws IllegalArgumentException {
    wrappedImage.rename(newName, oldLayerName);
  }

  @Override
  public void apply(VImageContentOperation operation, int index) throws IllegalArgumentException {
    wrappedImage.apply(operation, index);
  }

  @Override
  public void apply(VImageContentOperation operation, String layerName)
      throws IllegalArgumentException {
    wrappedImage.apply(operation, layerName);
  }

  @Override
  public int numLayers() {
    return wrappedImage.numLayers();
  }

  @Override
  public VLayer getLayer(int index) throws IllegalArgumentException {
    return wrappedImage.getLayer(index);
  }

  @Override
  public VLayer getLayer(String layerName) throws IllegalArgumentException {
    return wrappedImage.getLayer(layerName);
  }

  @Override
  public String getName() {
    return wrappedImage.getName();
  }

  @Override
  public void setName(String name) throws IllegalArgumentException {
    wrappedImage.setName(name);
  }

  @Override
  public int getWidth() {
    return wrappedImage.getWidth();
  }

  @Override
  public int getHeight() {
    return wrappedImage.getHeight();
  }
}
