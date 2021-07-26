import java.awt.*;
import java.awt.geom.*;

public class Tircle implements AnimationFrame {

    /**
     * Render this AnimationFrame into the given {@code Graphics2D} canvas in
     * the desired resolution at the given time.
     * @param g The {@code Graphics2D} canvas to render the frame into.
     * @param width The desired width of the frame.
     * @param height The desired height of the frame.
     * @param t The virtual time of the frame.
     */
    public void render(Graphics2D g, int width, int height, double t) {
        double cx = width / 2.0;
        double cy = height / 2.0;
        double r = Math.min(cx - 5, cy - 5); // Five-pixel margins
        double tt = smooth(k * t, .8) / k;
        tircle(g, cx, cy, r, tt, 0);
    }
    
    // The colours that the recursion cycles through.
    private static final Color[] colors = {
        new Color(.9f, .9f, .95f), 
        new Color(.87f, .8f, .75f), 
        new Color(.75f, .71f, .69f), 
        Color.YELLOW, 
        Color.CYAN,
        Color.RED
    };
    
    // How many breathing circles are rendered.
    private final int k;
    
    public Tircle(int k) { this.k = k; }
    
    // The recursive rendering has been extracted into a separate method of its own.
    // Render the tircle centered at (cx, cy) with the radius r and recursion depth d.
    private void tircle(Graphics2D g, double cx, double cy, double r, double t, int d) {
        // Render the tircle as a shape for the base case.
        Ellipse2D.Double c = new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r);
        g.setPaint(colors[d % colors.length]);
        g.fill(c);
        g.setPaint(Color.BLACK);
        g.setStroke(new BasicStroke((float)(r / 30)));
        g.draw(c);
        // If the current shape is not too small, recursively render the tircles inside it.
        if(r > 10) {
            for(int i = 0; i < 3; i++) {
                double tt = t + i * (1.0 / 3);
                double cxx = circleX(cx, r * 0.666, tt);
                double cyy = circleY(cy, r * 0.666, tt);
                tircle(g, cxx, cyy, r * 0.333, tt * 2 - .05, d + i + 1);
            }
            // Yes, time can be turned backwards inside the image.
            tircle(g, cx, cy, r * 0.33, -t, d + 4);
        }
    }
}