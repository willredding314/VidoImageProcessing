package model.image;

/**
 * An single "sheet" that is contained in a {@link VLayeredImage}.
 *
 * <p>A {@link VLayer} is a {@link VImage} that is specifically found
 * within a {@link VLayeredImage}. A {@link VLayer} describes a single component of a multi-layered
 * image. Each layer is an individual image within a multi-layered image whose contents can be
 * changed independently of other layers in the image.
 *
 * <p>Layers can also be named.</p>
 */
public interface VLayer extends VImage {

  /**
   * Creates a copy of this VLayer.
   *
   * @return a copy of this VLayer
   */
  VLayer copy();

  /**
   * Gets the name of this layer.
   *
   * @return the name of this VLayer.
   */
  String getName();

  /**
   * Sets the name of this VLayer to a new name.
   *
   * @param name the new name of the layer
   * @throws IllegalArgumentException if the name is null of the empty string
   */
  void setName(String name) throws IllegalArgumentException;

  /**
   * Gets the visibility of a layer.
   *
   * @return the visibility of the layer
   */
  boolean isVisible();

  /**
   * Sets the visibility of the layer.
   *
   * @param isVisible the visibility to set
   */
  void setVisible(boolean isVisible);

}
