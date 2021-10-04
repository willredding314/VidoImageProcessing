package model.image;

import java.util.Optional;

/**
 * A multi-layered image which defines a single layer to be in "focus" at
 * any given point.
 *
 * <p>A {@link VFocusableLayeredImage} is a special kind of {@link VLayeredImage}. At any given
 * point, a tracking image maintains state about a particular layer of interest called
 * the <em>focus layer</em>. The focus layer is simply a single uniquely-identified layer contained
 * in the multi-layered image that serves is marked by clients as important. The focus layer
 * can be changed, and is automatically updated when other changes to the multi-layered image
 * occur such as having layers removed or having layers added. By default, a
 * {@link VFocusableLayeredImage} uses the next top-most available and adjacent layer as its
 * focus layer whenever its current focus layer is removed. If there are no layers contained
 * in the image, the focus layer is set to an empty optional value</p>
 */
public interface VFocusableLayeredImage extends VLayeredImage {

  /**
   * Sets the layer at the specified index as the focus layer.
   *
   * @param index an index identifying the layer in the image to
   *              set as focused
   * @throws IllegalArgumentException if the index is invalid or the image
   *                                  doesn't have any layers
   */
  void setFocusLayer(int index) throws IllegalArgumentException;

  /**
   * Sets the layer with the specified name as the focus layer.
   *
   * @param name a name of a layer in the layered image
   * @throws IllegalArgumentException if no such layer exists in the
   *                                  layered image or if the name is null
   */
  void setFocusLayer(String name) throws IllegalArgumentException;

  /**
   * Determines the index of the focus layer in the layered image.
   *
   * @return an index identifying the focus layer if one currently
   *         exists, or -1 if no layers are contained in the image
   */
  int getFocusLayerIndex();

  /**
   * Gets the layer that is currently in focus.
   *
   * <p>Any layer in an image can become the focus layer. See the
   * {@link VFocusableLayeredImage#setFocusLayer(int)} method for more
   * details</p>
   *
   * @return a copy of the current layer in focus, or {@link Optional#empty()}
   *         if there are no layers in the image
   */
  Optional<VLayer> getFocusLayer();
}
