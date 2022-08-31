import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Font;
import java.io.IOException;

public class ImageAsTextMain {

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

    public static void main(String[] args) throws IOException {
        // Read the test image from the files.
        Image coffee = ImageIO.read(new File("coffee.jpg"));
        Image ilkka = ImageIO.read(new File("ilkka.jpg"));
        // Create a smaller version of the image.
        coffee = coffee.getScaledInstance(600, 450, Image.SCALE_SMOOTH);
        ilkka = ilkka.getScaledInstance(600, 450, Image.SCALE_SMOOTH);

        // Convert to BufferedImage.
        BufferedImage coffeeB = new BufferedImage(600, 450, BufferedImage.TYPE_INT_RGB);
        coffeeB.getGraphics().drawImage(coffee, 0, 0, null);
        BufferedImage ilkkaB = new BufferedImage(600, 450, BufferedImage.TYPE_INT_RGB);
        ilkkaB.getGraphics().drawImage(ilkka, 0, 0, null);

        // Convert images to text.
        String text1 = "The menu is not the meal. ";
        Image textCoffee = ImageAsText.render(coffeeB, new Font("Papyrus", Font.BOLD, 20), text1, 20);

        String text2 = "We seldom realize, for example that our most private thoughts and emotions are not actually our own. For we think in terms of languages and images which we did not invent, but which were given to us by our society. ";
        Image textIlkka = ImageAsText.render(ilkkaB, new Font("Courier", Font.ITALIC, 10), text2, 10);

        JFrame f = new JFrame("Image as text");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new GridLayout(1, 2));
        f.add(new ImagePanel(textCoffee));
        f.add(new ImagePanel(textIlkka));

        f.pack();
        f.setVisible(true);

    }


}
