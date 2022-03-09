import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChaosGameMain {

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

    public static void main(String[] args) {
        int[][] xs = {
                {50, 450, 50},
                {0, 250, 499, 0, 499, 0, 250, 499},
                {0, 499, 250, 0, 499},
                {0, 499, 0, 499}
        };
        int[][] ys = {
                {50, 250, 450},
                {0, 0, 0, 250, 250, 499, 499, 499},
                {0, 0, 250, 499, 499},
                {0, 0, 499, 499}
        };
        int[][] freqs = {
                {10, 4, 5},
                {1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 2, 1, 1},
                {1, 2, 3, 4}
        };
        double[][] weights = {
                {0.3, 0.7, 0.5},
                {0.66667, 0.66667, 0.66667, 0.66667, 0.66667, 0.66667, 0.66667, 0.66667},
                {0.66667, 0.66667, 0.66667, 0.66667, 0.66667},
                {0.53, 0.53, 0.53, 0.53}
        };
        boolean[] canRepeats = { true, true, true, false };

        JFrame frame = new JFrame("Generalized Chaos Game");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 2));
        for(int f = 0; f < 4; f++) {
            BufferedImage result = ChaosGame.playChaosGame(1000000, 500, xs[f], ys[f], freqs[f], weights[f], canRepeats[f]);
            frame.add(new ChaosGameMain.ImagePanel(result, ""));
        }
        frame.pack();
        frame.setVisible(true);
    }
}
