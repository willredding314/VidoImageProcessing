package model.processing;

import model.misc.ObjectsExtension;

/**
 * A factory class for producing {@link VKernelResolution} objects
 * based on an {@link KernelEdgeMode} specified.
 *
 * <p>Vido's image filters are created using image processing kernels, small
 * matrices which are dragged around an image to compute new pixel color
 * values. When an image filter reaches a pixel on the edge, it must
 * decide how to treat the pixels on the boundary. See {@link VKernelResolution}</p>
 */
public class VKernelResolutionFactory {

  private VKernelResolutionFactory() {
    // Prevent construction
  }

  /**
   * Produces a kernel resolution object for the given edge mode.
   *
   * @param edgeMode the method for how the kernel resolves pixels on the edge of an image while
   *                 evaluating
   * @return an object that handles kernel resolutions in the way specified by the format given
   * @throws IllegalArgumentException if {@code edgeMode} is {@code null} or if the given edge mode
   *                                  is unsupported
   */
  public static VKernelResolution resolutionFor(KernelEdgeMode edgeMode)
      throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(edgeMode);

    if (edgeMode == KernelEdgeMode.DONT_INCLUDE) {
      return new KernelResolutionExclude();
    }
    else {
      throw new IllegalArgumentException("Edge mode unsupported");
    }
  }
}
