import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

public class CraquelureMain {

    private static class ImagePanel extends JPanel {
        private final Image img;
        public ImagePanel(Image img) {
            this.img = img;
            this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
            this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Craquelure Demo");
        f.setLayout(new GridLayout(1, 2));

        BufferedImage img0 = Craquelure.createImage(new Random(12345), 750, 100, 0.03, 0.02);
        f.add(new ImagePanel(img0));

        BufferedImage img1 = Craquelure.createImage(new Random(12345), 750, 50, 0.1, 0.05);
        f.add(new ImagePanel(img1));

        f.pack();
        f.setVisible(true);
    }
}
