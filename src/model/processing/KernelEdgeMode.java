package model.processing;

/**
 * Represents different types of responses for when a pixel
 * is not within the bounds of the image during a kernel modification.
 *
 * <p>A {@link KernelEdgeMode} describes the different pixel
 * wrapping modes that can be employed by image kernels. For example,
 * an image filter may have its kernel simply treat pixels
 * that extend beyond the image as having zero weight, or it may decide
 * to treat out-of-bounds reads by wrapping edges.</p>
 */
public enum KernelEdgeMode {
  DONT_INCLUDE
}
