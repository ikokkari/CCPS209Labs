public final class WhiteQuad implements QuadTree {

    // Private constructor prevents objects from being created.
    private WhiteQuad() { }
    
    // The only instance of this singleton class.
    private static final WhiteQuad instance = new WhiteQuad();
    
    /**
     * Returns the only instance of this singleton class.
     * @return The instance of WhiteQuad.
     */
    public static WhiteQuad get() { return instance; }
    
    /**
     * Determines if this tree represents a square of uniform colour.
     */
    public boolean isOneColour() { return true; }
    
    /**
     * Computes the black area of the region represented by this tree, under the assumption
     * that the entire region is a square with side {@code 1 << scale}.
     * @param scale The scale of the region represented by this tree.
     * @return The black area of the region represented by this tree.
     */
    public long computeArea(int scale) { return 0; }
}
