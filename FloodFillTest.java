import org.junit.Test;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import javax.swing.JFrame;
import javax.swing.JPanel;

// Ensure that the floating point arithmetic works the same in all environments.
// We finally get to use this rarest of the rare Java keywords.
public strictfp class FloodFillTest {
    
    private static final Color[] colors = {
        Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA
    };

    private static Color createRandomColour(Random rng) {
        // Create a random peppy colour in HSB space.
        float hue = (float)(rng.nextDouble());
        float saturation = (float)(rng.nextDouble() * 0.1 + 0.9);
        // Avoid creating pure white used here as background colour.
        float brightness = (float)(rng.nextDouble() * 0.3 + 0.69);
        return new Color(Color.HSBtoRGB(hue, saturation, brightness));
    }

    // For the main method, create some fancier curves.
    private static strictfp BufferedImage createRandomCurves(int w, int h, int lines, Random rng) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fill(new Rectangle2D.Double(0, 0, w, h));
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3.0f));
        g2.drawLine(0, 0, w-1, 0);
        g2.drawLine(0, 0, 0, h-1);
        g2.drawLine(w-1, 0, w-1, h-1);
        g2.drawLine(0, h-1, w-1, h-1);
        for(int i = 0; i < lines; i++) {
            int width = rng.nextInt(w/2) + w/8;
            int height = rng.nextInt(h/2) + h/8;
            int x = rng.nextInt(w) - w/4;
            int y = rng.nextInt(h) - h/4;
            double start = rng.nextInt(360);
            double extent = 180 + rng.nextInt(180);
            g2.draw(new Arc2D.Double(x, y, width, height, start, extent, Arc2D.OPEN));
        }
        return img;
    }
    
    private static class ImagePanel extends JPanel {
        private final Image img;
        public ImagePanel(Image img, String toolTip) {
            this.img = img;
            this.setToolTipText(toolTip);
            this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
            //this.setBorder(BorderFactory.createEtchedBorder());
        }
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }
    }

    @Test public void testFloodFill() {
        Random rng = new Random(4321);
        CRC32 check = new CRC32();
        int stillWhite = Color.WHITE.getRGB();
        for(int i = 0; i < 30; i++) {
            // Create a new random image to flood fill.
            int w = 50 + rng.nextInt(30 * i + 1);
            int h = 50 + rng.nextInt(30 * i + 1);
            int lines = 3 * i;
            BufferedImage img = createRandomCurves(w, h, lines, rng);

            // Flood fill all the white nooks of current image
            int colIdx = 0;
            for(int y = 0; y < h; y++) {
                for(int x = 0; x < w; x++) {
                    if(img.getRGB(x, y) == stillWhite) {
                        FloodFill.floodFill(img, x, y, colors[colIdx]);
                        colIdx = (colIdx + 1) % colors.length;
                    }
                }
            }

            // Update the checksum from every pixel of the resulting image.
            for(int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    check.update(img.getRGB(x, y));
                }
            }
        }
        assertEquals(2800237690L, check.getValue());
    }

    public static void main(String[] args) {
        Random rng = new Random(5555);
        final int WIDTH = 500, HEIGHT = 600;
        BufferedImage original = createRandomCurves(WIDTH, HEIGHT, 16, rng);
        BufferedImage floodFilled = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        floodFilled.getGraphics().drawImage(original, 0, 0, new JPanel());
        for(int y = 0; y < HEIGHT; y++) {
            for(int x = 0; x < WIDTH; x++) {
                if(floodFilled.getRGB(x, y) == Color.WHITE.getRGB()) {
                    Color col = createRandomColour(rng);
                    FloodFill.floodFill(floodFilled, x, y, col);
                }
            }
        }

        JFrame f = new JFrame("Flood Fill Demo");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new FlowLayout());
        f.add(new ImagePanel(original, "Random lines"));
        f.add(new ImagePanel(floodFilled, "Flood Fill"));
        f.pack();
        f.setVisible(true);
    }
}
