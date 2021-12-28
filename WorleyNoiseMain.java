import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.function.DoubleFunction;

public class WorleyNoiseMain {

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

    private static BufferedImage render(double[][] height, DoubleFunction<Color> palette) {
        int h = height.length, w = height[0].length;
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                result.setRGB(x, y, palette.apply(height[y][x]).getRGB());
            }
        }
        return result;
    }

    private static double[] randomWeights(int n, Random rng) {
        double[] w = new double[n];
        for(int i = 0; i < n; i++) {
            w[i] = rng.nextBoolean() ? rng.nextGaussian() : 0;
        }
        return w;
    }

    private static double[][] randomNoise(int w, int h, int p, Random rng, double a, double f,
                                          double[] weight, String distance) {
        double[][] height = new double[h][w];
        double[] xs = new double[p];
        double[] ys = new double[p];
        for(int i = 0; i < p; i++) {
            xs[i] = rng.nextInt(w);
            ys[i] = rng.nextInt(h);
        }
        WorleyNoise.compute(height, xs, ys, weight, a, f, distance);
        return height;
    }

    private static final DoubleFunction<Color> grayScale = h -> new Color((float)h, (float)h, (float)h);

    public static void main(String[] args) {
        final int W = 500, H = 400;
        JFrame noiseFrame = new JFrame("Worley Noise Demo");
        noiseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        noiseFrame.setLayout(new GridLayout(2, 2));
        Random rng = new Random(12345);

        double[] w0 = randomWeights(50, rng);
        double[][] n0 = randomNoise(W, H, 100, rng, 1, 30, w0, "euclidean");
        noiseFrame.add(new ImagePanel(render(n0, grayScale)));

        double[] w1 = randomWeights(30, rng);
        double[][] n1 = randomNoise(W, H, 50, rng, 1, 25, w1, "manhattan");
        noiseFrame.add(new ImagePanel(render(n1, grayScale)));

        double[] w2 = randomWeights(50, rng);
        double[][] n2 = randomNoise(W, H, 50, rng, 1, 20, w2, "chessboard");
        noiseFrame.add(new ImagePanel(render(n2, grayScale)));

        double[] w3 = randomWeights(10, rng);
        double[][] n3 = randomNoise(W, H, 20, rng, 1, 30, w3, "manhattan");
        noiseFrame.add(new ImagePanel(render(n3, grayScale)));

        noiseFrame.pack();
        noiseFrame.setVisible(true);
    }
}
