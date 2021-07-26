import java.awt.*;
import java.awt.geom.*;

public class Breathe implements AnimationFrame {
    
    private final int n;
    private final double DELAY = 0.03;
    
    public Breathe(int n) { this.n = n; }
    
    public void render(Graphics2D g, int width, int height, double t) {
        double cx = width / 2.0;
        double cy = height / 2.0;
        double r = (Math.min(width, height) / 2.0) - 5.0;
        double rs = r / n;
        for(int c = n; c > 1; c--) {
            double tt = 0.5 * smooth(t + DELAY * c) + 0.5 * t;
            double rr = rs * (c - 1 + circleX(0, 0.4, tt));  
            Ellipse2D.Double ell = new Ellipse2D.Double(cx - rr, cy - rr, 2*rr, 2*rr);
            g.setColor((n - c) % 2 == 0 ? Color.BLACK: Color.WHITE);
            g.fill(ell);
        }
    }
}
