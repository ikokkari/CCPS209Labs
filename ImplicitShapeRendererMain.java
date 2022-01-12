import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.function.DoubleBinaryOperator;
import java.awt.geom.Area;

public class ImplicitShapeRendererMain {

    private static final Color[] BORDERS = {
            new Color(200, 200, 200), new Color(100, 100, 150)
    };

    private static class ImagePanel extends JPanel {
        private final Image img;
        public ImagePanel(Image img) {
            this.setBorder(BorderFactory.createEtchedBorder(BORDERS[0], BORDERS[1]));
            this.img = img;
            this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
        }
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }
    }

    private static int superEllipse(double x, double y, double cx, double cy, double r, double exp) {
        double xp = Math.pow(Math.abs(x - cx), exp);
        double yp = Math.pow(Math.abs(y - cy), exp);
        return xp + yp < Math.pow(r, exp) ? -1:  1;
    }

    private static Area wheel;
    static {
        wheel = new Area(new Ellipse2D.Double(100, 100, 300, 300));
        final int SPOKES = 5;
        wheel.subtract(new Area(new Ellipse2D.Double(140, 140, 220, 220)));
        for(int i = 0; i < SPOKES; i++) {
            Area b = new Area(new Rectangle2D.Double(50, 220, 400, 40));
            AffineTransform at = AffineTransform.getRotateInstance(
                    i * 2 * Math.PI / SPOKES, 250, 250
            );
            b.transform(at);
            wheel.add(b);
        }
    }

    private static BufferedImage ilkkaB = new BufferedImage(601, 501, BufferedImage.TYPE_INT_RGB);
    static {
        try {
            Image ilkkaI = ImageIO.read(new File("ilkka.jpg"));
            ilkkaI = ilkkaI.getScaledInstance(601, 501, Image.SCALE_FAST);
            ilkkaB.getGraphics().drawImage(ilkkaI, 0, 0, null);
        }
        catch(Exception e) {
            System.out.println("Error reading image file ilkka.jpg: " + e);
        }
    }

    private static DoubleBinaryOperator[] ops = {
            // Perturbed disk of radius 200, centered at (250, 250)
            (x, y) -> superEllipse(
                    x + 30*Math.sin(0.12*x-0.17*y),
                    y + 30*Math.cos(-0.05*x + 0.33*y),
                    250, 250, 200, 2.0),
            // Annulus of two superellipses, centered at 250, 250
            (x, y) -> {
                int outer = superEllipse(x, y, 250, 250, 200, 2.3);
                int inner = superEllipse(x, y, 250, 250, 100, 2.3);
                return outer == -1 && inner != -1 ? -1: +1;
            },
            // Implicit shape from java.awt.geom.Area
            (x, y) -> wheel.contains(x, y) ? -1: +1,
            // From the pixel colours of the given image
            (x, y) -> {
                int rgb = ilkkaB.getRGB((int)(x+50), (int)(y));
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);
                // From https://www.johndcook.com/blog/2009/08/24/algorithms-convert-color-grayscale/
                double luminosity = 0.21*r + 0.72*g + 0.07*b;
                return luminosity < 195? -1: +1;
            }
    };

    public static void main(String[] args) {
        final int W = 500, H = 500;
        JFrame renderFrame = new JFrame("Implicit Shape Demo");
        renderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        renderFrame.setLayout(new GridLayout(2, 2));

        for(int i = 0; i < ops.length; i++) {
            BufferedImage img = new BufferedImage(W+1, H+1, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D)img.getGraphics();
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(0.5f));
            g.fill(new Rectangle2D.Double(0, 0, W, H));
            ImplicitShapeRenderer.render(ops[i],0, W, 0, H, g);
            renderFrame.add(new ImagePanel(img));
        }

        renderFrame.pack();
        renderFrame.setVisible(true);
    }
}