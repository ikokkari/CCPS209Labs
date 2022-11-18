import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.function.IntBinaryOperator;

public class EllersAlgorithmMain {

    private static class ImagePanel extends JPanel {
        private final Image img;
        public ImagePanel(Image img) {
            this.img = img;
            this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));;
        }
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }
    }

    private static final IntBinaryOperator digProbNormal = (x, y) -> 40;
    private static final IntBinaryOperator digProbNake = (x, y) -> {
        if(y <= 14) { return 4 * y; }
        else if(15 <= y && y <= 35) { return 95; }
        else { return 4 * (50 - y); }
    };

    private static final IntBinaryOperator mergeProbNormal = (x, y) -> 50;
    private static final IntBinaryOperator mergeProbNake = (x, y) -> {
        if(y <= 14) { return 60 - 3 * y; }
        else if(15 <= y && y <= 35) { return 15; }
        else { return 60 - 3 * (50 - y); }
    };

    public static void main(String[] args) {
        JFrame f = new JFrame("Eller's Algorithm Maze Generation");
        f.setLayout(new GridLayout(2, 2));

        BufferedImage img1 = EllersAlgorithm.generateMaze(
                new Random(1729), 10, 10, 50, 50,
                digProbNormal, mergeProbNormal);
        f.add(new ImagePanel(img1));

        BufferedImage img2 = EllersAlgorithm.generateMaze(
                new Random(54321), 50, 50, 10, 10,
                digProbNormal, mergeProbNormal);
        f.add(new ImagePanel(img2));

        BufferedImage img3 = EllersAlgorithm.generateMaze(
                new Random(54321), 50, 50, 10, 10,
                digProbNake, mergeProbNake);
        f.add(new ImagePanel(img3));

        BufferedImage img4 = EllersAlgorithm.generateMaze(
                new Random(54321), 50, 50, 10, 10,
                digProbNake, mergeProbNake);
        f.add(new ImagePanel(img4));

        f.pack();
        f.setVisible(true);
    }
}