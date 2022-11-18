import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;

public class HuntAndKillMain {

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
        JFrame f = new JFrame("Hunt & Kill Maze Generation");
        f.setLayout(new GridLayout(2, 2));

        BufferedImage img0 = HuntAndKill.createGrid(20, 20, 25, 25, 4.0f);
        f.add(new ImagePanel(img0));

        BufferedImage img1 = HuntAndKill.generateMaze(new Random(12345), 10, 10, 50, 50);
        f.add(new ImagePanel(img1));

        BufferedImage img2 = HuntAndKill.generateMaze(new Random(54321), 20, 20, 25, 25);
        f.add(new ImagePanel(img2));

        BufferedImage img3 = HuntAndKill.generateMaze(new Random(54321), 50, 50, 10, 10);
        f.add(new ImagePanel(img3));

        f.pack();
        f.setVisible(true);
    }
}