package model.processing;

import model.image.VImage;
import model.misc.ObjectsExtension;

/**
 * Describes an operation which bundles other operations within in.
 *
 * <p>A {@link VCompositeContentOperation} is an implementation of the
 * composite pattern using the {@link VImageContentOperation} interface as the shared interface. A
 * composite operation applies many operations in a sequence, one after another, to a particular
 * image. The outputted image of one operation serves as a input into subsequent operations</p>
 */
public class VCompositeContentOperation implements VImageContentOperation {
  private final VImageContentOperation[] operations;

  /**
   * Construct a composite operation from the given operations.
   *
   * @param operations the operations that this composite operation applies in sequence. The order
   *                   in which the operations are provided determines the order in which
   *                   operations
   * @throws IllegalArgumentException if {@code operations} is {@code null} of it any operations in
   *                                  the varargs is {@code null}
   */
  public VCompositeContentOperation(VImageContentOperation... operations)
      throws IllegalArgumentException {
    ObjectsExtension.requireNonnull((Object) operations);
    ObjectsExtension.requireNonnull((Object[]) operations);
    this.operations = operations;
  }

  @Override
  public VImage operateOn(VImage image) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(image);

    VImage accumulatedResult = image;

    for (VImageContentOperation operation : this.operations) {
      accumulatedResult = operation.operateOn(accumulatedResult);
    }

    return accumulatedResult;
  }
}
