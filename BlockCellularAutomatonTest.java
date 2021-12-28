import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.zip.CRC32;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BlockCellularAutomatonTest {

    private static class ImagePanel extends JPanel {
        private BufferedImage currImage;
        public ImagePanel(BufferedImage currImage, String title) {
            this.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(EtchedBorder.RAISED), title)
            );
            refresh(currImage);
        }
        public void refresh(BufferedImage currImage) {
            this.currImage = currImage;
            this.setPreferredSize(new Dimension(
                    currImage.getWidth() + 2 * MARGIN,
                    currImage.getHeight() + 2 * MARGIN)
            );
            repaint();
        }
        public void paintComponent(Graphics g) {
            g.drawImage(currImage, MARGIN, 3 * MARGIN / 2 , this);
        }
    }

    private static final int BLACK = Color.BLACK.getRGB();
    private static final int WHITE = Color.WHITE.getRGB();

    public static void fillImage(boolean[][] current, BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        int scale = h / current.length;
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                boolean b = current[y/scale][x/scale];
                img.setRGB(x, y, b? BLACK : WHITE);
            }
        }
    }

    // From https://dmishin.github.io/js-revca/help.html

    private static final int[][] RULES = {
            {0, 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}, // Tron
            {0, 2, 8, 12, 1, 10, 9, 11, 4, 6, 5, 14, 3, 7, 13, 15}, // Rotations I
            {0, 2, 8, 12, 1, 10, 9, 13, 4, 6, 5, 7, 3, 14, 11, 15}, // Rotations II
            {0, 4, 1, 10, 8, 3, 9, 11, 2, 6, 12, 14, 5, 7, 13, 15}, // Rotations III
            {0, 4, 1, 12, 8, 10, 6, 14, 2, 9, 5, 13, 3, 11, 7, 15}, // Rotations IV
            {0, 1, 2, 12, 4, 10, 6, 7, 8, 9, 5, 11, 3, 13, 14, 15}, // String Thing II
            {0, 8, 4, 3, 2, 5, 9, 14, 1, 6, 10, 13, 12, 11, 7, 15}, // Bounce Gas I
            {0, 4, 8, 12, 4, 12, 12, 13, 8, 12, 12, 14, 12, 13, 14, 15}  // Sand (non-reversible)
    };

    private static final String[] RULE_NAMES = {
            "Tron", "Rotations I", "Rotations II", "Rotations III",
            "Rotations IV", "String Thing II", "Bounce Gas I", "Sand"
    };

    private static final int SCALE = 2;
    private static final int WIDTH = 200;
    private static final int HEIGHT = 200;
    private static final int CENTER = 50;
    private static final int MARGIN = 10;

    private static Timer timer = null;

    @Test public void massTest() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < 10; i++) {
            // Create a random starting grid.
            int w = 50 * i + 2 * rng.nextInt(10);
            int h = 50 * i + 2 * rng.nextInt(10);
            boolean[][] current = new boolean[h][w];
            boolean[][] next = new boolean[h][w];
            for(int y = 0; y < h; y++) {
                for(int x = 0; x < w; x++) {
                    current[y][x] = rng.nextBoolean();
                }
            }
            // Create a random rule as a permutation of 0, ..., 15.
            int[] rule = new int[16];
            for(int j = 0; j < 16; j++) { rule[j] = j; }
            for(int j = 1; j < 16; j++) {
                int k = rng.nextInt(j + 1);
                int tmp = rule[k]; rule[k] = rule[j]; rule[j] = tmp;
            }
            // Execute the automaton some number of rounds forward.
            for(int round = 0; round < 10 * i; round++) {
                BlockCellularAutomaton.margolusNextState(current, next, rule, round % 2);
                boolean[][] tmp = current; current = next; next = tmp;
            }
            // Update the checksum.
            for(int y = 0; y < h; y++) {
                for(int x = 0; x < w; x++) {
                    check.update(current[y][x] ? x^y : x&y);
                }
            }
        }
        assertEquals(1607642657L, check.getValue());
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Block Cellular Automata Demo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                timer.stop();
                f.dispose();
            }
        });
        int N = RULES.length;
        f.setLayout(new GridLayout(2, N/2));
        final ImagePanel[] panels = new ImagePanel[N];
        final boolean[][][] currentStates = new boolean[N][HEIGHT][WIDTH];
        final boolean[][][] nextStates = new boolean[1][HEIGHT][WIDTH];
        final BufferedImage[] images = new BufferedImage[N];
        final AtomicInteger timeStamp = new AtomicInteger(0);
        for(int i = 0; i < N; i++) {
            for(int x = 0; x < CENTER; x++) {
                for(int y = 0; y < CENTER; y++) {
                    currentStates[i][(HEIGHT - CENTER)/2 + y][(WIDTH - CENTER)/2 + x] = true;
                }
            }

            images[i] = new BufferedImage(WIDTH * SCALE, HEIGHT * SCALE, BufferedImage.TYPE_INT_RGB);
            panels[i] = new ImagePanel(images[i], RULE_NAMES[i]);
            f.add(panels[i]);
        }
        timer = new Timer(500, ae -> {
            int off = timeStamp.getAndIncrement() % 2;
            for(int i = 0; i < N; i++) {
                BlockCellularAutomaton.margolusNextState(currentStates[i], nextStates[0], RULES[i], off);
                boolean[][] tmp = currentStates[i]; currentStates[i] = nextStates[0]; nextStates[0] = tmp;
                fillImage(currentStates[i], images[i]);
                panels[i].refresh(images[i]);
            }
        });
        timer.start();
        f.pack();
        f.setVisible(true);
    }
}