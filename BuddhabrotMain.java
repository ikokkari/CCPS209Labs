import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BuddhabrotMain {

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
        // Change these three settings during your development of Buddhabrot.
        final int[] THRESHOLDS = {50, 500, 5000};
        final int SIZE = 1000;
        final long SAMPLES = 50_000_000L;

        BufferedImage result = Buddhabrot.renderBuddhabrot(SIZE, SAMPLES, THRESHOLDS);
        JFrame frame = new JFrame("The Buddhabrot Is Silent");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 1));
        frame.add(new BuddhabrotMain.ImagePanel(result, "Ommmm"));
        frame.pack();
        frame.setVisible(true);
    }
}
