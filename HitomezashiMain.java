import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.BorderFactory;

public class HitomezashiMain {

    private static final Color[] BORDERS = {
            new Color(200, 200, 200), new Color(100, 100, 150)
    };

    private static class ImagePanel extends JPanel {
        private final Image img;
        public ImagePanel(Image img, String toolTip) {
            this.setToolTipText(toolTip);
            this.setBorder(BorderFactory.createEtchedBorder(BORDERS[0], BORDERS[1]));
            this.img = img;
            this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
        }
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }
    }

    public static void main(String[] args) {
        Random rng = new Random(5555);
        final int W = 21, H = 20, SQUARE = 20, ROWS = 2, COLS = 3;
        final int[] PROB = { 25, 30, 35, 40, 45, 50 };
        JFrame f = new JFrame("Hitomezashi Demo");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new GridLayout(ROWS, COLS));
        int imageCount = 0;
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLS; j++) {
                boolean[] horizontal = new boolean[W];
                boolean[] vertical = new boolean[H];
                for(int x = 0; x < W; x++) {
                    horizontal[x] = rng.nextInt(100) < PROB[imageCount];
                }
                for(int y = 0; y < H; y++) {
                    vertical[y] = rng.nextInt(100) < PROB[imageCount];
                }
                BufferedImage pattern = Hitomezashi.createPattern(W, H, SQUARE, horizontal, vertical);
                f.add(new ImagePanel(pattern, PROB[imageCount] + "%"));
                imageCount++;
            }
        }
        f.pack();
        f.setVisible(true);
    }

}
