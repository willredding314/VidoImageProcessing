package model.image;

import model.image.pixel.VPixel;
import model.image.pixel.VPixelCoordinate;
import model.misc.ObjectsExtension;
import java.util.function.Function;

/**
 * An implementation for a layer, which is an VImage with an additional
 * name and visibility.
 */
public class VLayerImpl extends AbstractVImage implements VLayer {

  private String name;
  private Boolean visible;

  /**
   * Constructs a layer with an image.
   * @param other the image to of the layer
   */
  public VLayerImpl(VImage other) {
    super(other);
    ObjectsExtension.requireNonnull(other);
    this.name = "Base";
    this.visible = true;
  }

  /**
   * Constructs a layer with a width and a height.
   * @param width the width of the layer
   * @param height the height of the layer
   */
  public VLayerImpl(int width, int height) {
    super(width, height);
    ObjectsExtension.requireNonnull(width, height);
    this.name = "Base";
    this.visible = true;
  }

  /**
   * Constructs a layer with a width, height, and an image function.
   * @param width the width of the layer
   * @param height the height of the layer
   * @param perPixelGenerator the function determining the pixels of
   *                          the image
   */
  public VLayerImpl(int width, int height,
                    Function<VPixelCoordinate, VPixel> perPixelGenerator) {
    super(width, height, perPixelGenerator);
    ObjectsExtension.requireNonnull(width, height, perPixelGenerator);
    this.name = "Base";
    this.visible = true;
  }

  /**
   * Constructs a layer with a name and an image.
   * @param name the name of the layer
   * @param other the image of the layer
   */
  public VLayerImpl(String name, VImage other) {
    super(other);
    ObjectsExtension.requireNonnull(name, other);
    this.name = name;
    this.visible = true;
  }

  /**
   * Constructs a layer with a name.
   * @param name the name of the layer
   */
  public VLayerImpl(String name) {
    super(1, 1);
    ObjectsExtension.requireNonnull(name);
    this.name = name;
    this.visible = true;
  }

  /**
   * Constructs a layer with a width, height, and name.
   * @param width the width of the layer
   * @param height the height of the image
   * @param name the name of the layer
   */
  public VLayerImpl(int width, int height, String name) {
    super(width, height);
    ObjectsExtension.requireNonnull(name, width, height);
    this.name = name;
    this.visible = true;
  }

  @Override
  public VLayer copy() {

    VLayer newLayer = new VLayerImpl(this.name, this);
    newLayer.setVisible(this.visible);
    return newLayer;
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
  public boolean isVisible() {
    return this.visible;
  }

  @Override
  public void setVisible(boolean isVisible) {
    ObjectsExtension.requireNonnull(isVisible);
    this.visible = isVisible;
  }
}
