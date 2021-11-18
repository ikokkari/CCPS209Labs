import org.junit.Test;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FloodFillTest {
    
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

    private static BufferedImage createRandomLines(int w, int h, int lines, Random rng) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fill(new Rectangle2D.Double(0, 0, w, h));
        g2.setColor(Color.BLACK);
        g2.drawLine(0, 0, w-1, 0);
        g2.drawLine(0, 0, 0, h-1);
        g2.drawLine(w-1, 0, w-1, h-1);
        g2.drawLine(0, h-1, w-1, h-1);
        int budge = Math.max(w, h) / 2;
        for(int i = 0; i < lines; i++) {
            int sx = rng.nextInt(2 * w) - w/2;
            int sy = rng.nextInt(2 * h) - h/2;
            int ex = rng.nextInt(2 * w) - w/2;
            int ey = rng.nextInt(2 * h) - h/2;
            int mx = (sx + ex)/2 + rng.nextInt(budge) - budge/2;
            int my = (sy + ey)/2 + rng.nextInt(budge) - budge/2;
            int lw = rng.nextInt(4) + 2;
            g2.setStroke(new BasicStroke((float)lw));
            g2.draw(new QuadCurve2D.Double(sx, sy, mx, my, ex, ey));
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
            BufferedImage img = createRandomLines(w, h, lines, rng);

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
        assertEquals(2747513707L, check.getValue());
    }

    public static void main(String[] args) {
        Random rng = new Random(444);
        final int WIDTH = 500, HEIGHT = 600;
        BufferedImage original = createRandomLines(WIDTH, HEIGHT, 30, rng);
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
