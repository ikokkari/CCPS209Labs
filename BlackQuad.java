public final class BlackQuad implements QuadTree {

    // Private constructor prevents objects from being created.
    private BlackQuad() { }
    
    // The only instance of this singleton class.
    private static final BlackQuad instance = new BlackQuad();
    
    /**
     * Returns the only instance of this singleton class.
     * @return The instance of BlackQuad.
     */
    public static BlackQuad get() { return instance; }
    
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
    public long computeArea(int scale) { return ((long)1 << scale) * ((long)1 << scale); }
}
