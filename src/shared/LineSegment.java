package shared;

/**
 * Represents a line segment. Immutable.
 */
public class LineSegment {
    public final int x1;
    public final int y1;
    public final int x2;
    public final int y2;

    /**
     * @param x1 Start x coordinate
     * @param y1 Start y coordinate
     * @param x2 End x coordinate
     * @param y2 End y coordinate
     */
    public LineSegment(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
}
