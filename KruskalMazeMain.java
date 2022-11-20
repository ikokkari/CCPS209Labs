import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;

public class KruskalMazeMain {

    private static class ImagePanel extends JPanel {
        private final Image img;
        public ImagePanel(Image img) {
            this.img = img;
            this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
        }
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Kruskal Maze Generation");
        f.setLayout(new GridLayout(2, 2));

        BufferedImage img0 = KruskalMaze.generateMaze(
                new Random(4242), 5, 5, 100, 100
        );
        f.add(new ImagePanel(img0));

        BufferedImage img1 = KruskalMaze.generateMaze(
                new Random(2424), 10, 10, 50, 50
        );
        f.add(new ImagePanel(img1));

        BufferedImage img2 = KruskalMaze.generateMaze(
                new Random(12345), 25, 25, 20, 20
        );
        f.add(new ImagePanel(img2));

        BufferedImage img3 = KruskalMaze.generateMaze(
                new Random(54321), 50, 50, 10, 10
        );
        f.add(new ImagePanel(img3));

        f.pack();
        f.setVisible(true);
    }

}
