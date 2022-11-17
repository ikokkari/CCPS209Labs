import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class BeziergonMain {

    private static class ImagePanel extends JPanel {
        private final Image img;
        public ImagePanel(Image img, String toolTip) {
            this.img = img;
            this.setToolTipText(toolTip);
            this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
            this.setBorder(BorderFactory.createEtchedBorder());
        }
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }
    }

    private static DoubleUnaryOperator[] createSuperellipse(double cx, double cy, double r, double exp, int n) {
        DoubleUnaryOperator x = t -> {
            t = 2*Math.PI * t / n;
            double cost = Math.cos(t);
            double sign = Math.signum(cost);
            return cx + Math.pow(Math.abs(cost), (2/exp)) * r * sign;
        };
        DoubleUnaryOperator y = t -> {
            t = 2*Math.PI * t / n;
            double sint = Math.sin(t);
            double sign = Math.signum(sint);
            return cy + Math.pow(Math.abs(sint), (2/exp)) * r * sign;
        };
        return new DoubleUnaryOperator[] { x, y };
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Beziergon demo");
        BufferedImage img = new BufferedImage(800, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Color.WHITE);
        g2.fill(new Rectangle2D.Double(0, 0, 800, 400));
        g2.setPaint(Color.BLACK);
        g2.setStroke(new BasicStroke(4.5f));

        for(int r = 20; r <= 190; r += 10) {
            DoubleUnaryOperator[] selli1 = createSuperellipse(200, 200, r, 2.5 - r/200, 20);
            List<Point2D.Double> points1 = Beziergon.computeCubicBezierPoints(selli1[0], selli1[1], 20);
            Path2D.Double path1 = Beziergon.createCubicBezierCurve(points1);
            g2.draw(path1);

            DoubleUnaryOperator[] selli2 = createSuperellipse(600, 200, r, 2.5 - r/200, 100);
            List<Point2D.Double> points2 = Beziergon.computeCubicBezierPoints(selli2[0], selli2[1], 100);
            Path2D.Double path2 = Beziergon.createCubicBezierCurve(points2);
            g2.draw(path2);
        }

        f.add(new ImagePanel(img, "Various superellipses rendered as beziergons."));
        f.pack();
        f.setVisible(true);
    }
}