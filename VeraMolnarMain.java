import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.Random;

public class VeraMolnarMain {

    private static class ImagePanel extends JPanel {
        private final Image img;
        public ImagePanel(Image img, String title) {
            this.img = img;
            this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
            this.setBorder(BorderFactory.createTitledBorder(title));
        }
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Vera Molnár tribute");
        f.setLayout(new GridLayout(2, 2));
        f.add(new ImagePanel(
                VeraMolnar.centTrapezes(new Random(12345),500, 500, 5, 30),
                "100 Trapezes"
                )
        );
        f.add(new ImagePanel(
                VeraMolnar.centTrapezes(new Random(54321),500, 500, 20, 50),
                "100 Trapezes"
                )
        );
        f.add(new ImagePanel(
                VeraMolnar.desOrdres(new Random(12345), 500, 500, 20, 20, 10, 0.3),
                "DesOrdres"
                )
        );
        f.add(new ImagePanel(
                VeraMolnar.desOrdres(new Random(12345), 500, 500, 5, 7, 50, 0.7),
                "DesOrdres"
                )
        );
        f.pack();
        f.setVisible(true);
    }
}
