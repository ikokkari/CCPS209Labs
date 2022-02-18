import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LineArtMain {

    private static class ImagePanel extends JPanel {
        private final Image img;
        public ImagePanel(Image img, String toolTip) {
            this.img = img;
            int w = img.getWidth(this);
            int h = img.getHeight(this);
            this.setToolTipText(toolTip + " (" + w + "x" + h + ")");
            this.setPreferredSize(new Dimension(w, h));
            this.setBorder(BorderFactory.createEtchedBorder());
        }
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }
    }

    private static BufferedImage toBI(Image img) {
        int w = img.getWidth(null), h = img.getHeight(null);
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        result.getGraphics().drawImage(img, 0, 0, null);
        return result;
    }

    public static void main(String[] args) throws IOException {
        // Read the image from the file.
        Image ilkka = ImageIO.read(new File("ilkka.jpg"));
        // Create a smaller version of the image.
        ilkka = ilkka.getScaledInstance(1200, 900, Image.SCALE_SMOOTH);
        JFrame f = new JFrame("Line Art");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new GridLayout(1, 1));
        BufferedImage linesIlkka = LineArt.lineArt(toBI(ilkka), 20, 5, 9);
        f.add(new ImagePanel(linesIlkka, "Ilkka broken down into lines"));
        f.pack();
        f.setVisible(true);
    }
}
