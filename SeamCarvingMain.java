import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SeamCarvingMain {

    // A little utility class to display images as Swing components.
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
        Image carvedCoffee = SeamCarving.carve(coffeeB, 400);
        
        BufferedImage ilkkaB = new BufferedImage(600, 450, BufferedImage.TYPE_INT_RGB);
        ilkkaB.getGraphics().drawImage(ilkka, 0, 0, null);
        Image carvedIlkka = SeamCarving.carve(ilkkaB, 400);
        
        JFrame f = new JFrame("Seam Carving Demo");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new GridLayout(2, 1));
        JPanel coffeeP = new JPanel();
        coffeeP.add(new ImagePanel(coffee, "Original"));
        coffeeP.add(new ImagePanel(carvedCoffee, "Carved"));
        f.add(coffeeP);
        JPanel ilkkaP = new JPanel();
        ilkkaP.add(new ImagePanel(ilkka, "Original"));
        ilkkaP.add(new ImagePanel(carvedIlkka, "Carved"));
        f.add(ilkkaP);
        f.pack();
        f.setVisible(true);                           
    }
}
