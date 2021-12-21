import java.util.Arrays;

public final class QuadNode implements QuadTree {
    
    private final QuadTree[] children;
    private long area = 0;
    private int lastScale = Integer.MAX_VALUE;
    
    private QuadNode(QuadTree[] children) {
        this.children = children.clone();
    }
    
    private static final QuadTree[] allWhite = {
        WhiteQuad.get(), WhiteQuad.get(), WhiteQuad.get(), WhiteQuad.get()
    };
    private static final QuadTree[] allBlack = {
        BlackQuad.get(), BlackQuad.get(), BlackQuad.get(), BlackQuad.get()
    };
    
    public static QuadTree of(QuadTree... children) {
        if(children.length != 4) {
            throw new IllegalArgumentException("QuadTree with " + children.length + " children");
        }
        if(Arrays.equals(children, allWhite)) { return WhiteQuad.get(); }
        if(Arrays.equals(children, allBlack)) { return BlackQuad.get(); }
        return new QuadNode(children);
    }
    
    public boolean isOneColour() { return false; }
        
    public long computeArea(int scale) {
        if(scale < 0) {
            throw new IllegalArgumentException("Scale must be natural number, was " + scale);
        }
        // We have already computed the area for this scale, so just look it up.
        if(lastScale == scale) { return area; }
        // We have already computed the area for a smaller scale, so just scale up that.
        if(lastScale < scale) {
            long side = area << (scale - lastScale);
            return side * side;
        }
        // Otherwise, just do the hard work ourselves.
        for(QuadTree qt: children) {
            area += qt.computeArea(scale - 1);
        }
        lastScale = scale;
        return area;
    }
}
