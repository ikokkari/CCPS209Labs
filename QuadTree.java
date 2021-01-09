
public interface QuadTree {

    /**
     * Determines if this tree represents a square of uniform colour.
     */
    public boolean isOneColour();
    
    /**
     * Computes the black area of the region represented by this tree, under the assumption
     * that the entire region is a square with side {@code 1 << scale}.
     * @param scale The scale of the region represented by this tree.
     * @return The black area of the region represented by this tree.
     */
    public long computeArea(int scale);
    
}
