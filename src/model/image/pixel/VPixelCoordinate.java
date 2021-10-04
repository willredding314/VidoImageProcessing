package model.image.pixel;

import model.misc.ObjectsExtension;

/**
 * Represents a 2D location in a grid of pixels.
 *
 * <p>When a pixel is situated in the screen of a device, it
 * has a unique position relative to other pixels on the screen.
 * Different coordinate systems will coordinates in differing ways.
 * A {@link VPixelCoordinate} describes a location of a pixel with respect
 * to an index identifying its row and column within the 2D screen
 * within which the pixel lies.</p>
 */
public final class VPixelCoordinate {
  private final int rowIndex;
  private final int columnIndex;

  /**
   * Construct a new coordinate with the given row and column indices.
   *
   * @param rowIndex the row index
   * @param columnIndex the column index
   */
  public VPixelCoordinate(int rowIndex, int columnIndex) {
    this.rowIndex = rowIndex;
    this.columnIndex = columnIndex;
  }

  /**
   * Retrieve the row index of the coordinate.
   *
   * @return the row index of this coordinate
   */
  public int getRowIndex() {
    return this.rowIndex;
  }

  /**
   * Retrieve the column index of the coordinate.
   *
   * @return the column index of this coordinate
   */
  public int getColumnIndex() {
    return this.columnIndex;
  }

  @Override
  public String toString() {
    return "VPixelCoordinate{"
        + "rowIndex=" + rowIndex
        + ", columnIndex=" + columnIndex
        + '}';
  }

  /**
   * Determines the taxi cab distance between this coordinate and
   * the given one.
   *
   * <p>The taxi cab distance between two points is the sum of the
   * lengths of the triangle formed by walking from the first point
   * to the second point. It measures how far a taxi cab car would have
   * to travel if it could not travel along the line joining the two
   * points (for example, in a city where you can't drive through buildings.
   * Note that the triangle inequality implies that this distance is greater
   * than the length of the line joining the two points.</p>
   *
   * @param other the point with respect to this point we are measuring
   *              distance to
   * @return the taxicab distance in units
   * @throws IllegalArgumentException if the other point is {@code null}
   */
  public int taxiCabDistance(VPixelCoordinate other) throws IllegalArgumentException {

    ObjectsExtension.requireNonnull(other);
    return Math.abs(this.rowIndex - other.rowIndex)
            + Math.abs(this.columnIndex - other.columnIndex);
  }
}
