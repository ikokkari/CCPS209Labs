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
import java.util.function.DoubleBinaryOperator;

public class ShadesMain {
    
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
        Image coffee = ImageIO.read(new File("coffee.jpg"));
        Image ilkka = ImageIO.read(new File("ilkka.jpg"));
        // Create a smaller version of the image.
        coffee = coffee.getScaledInstance(600, 450, Image.SCALE_SMOOTH);
        ilkka = ilkka.getScaledInstance(600, 450, Image.SCALE_SMOOTH);
        JFrame f = new JFrame("Image Operations Demo");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new GridLayout(2, 1));
        
        // Desaturation can be done for arbitrary Images. 
        f.add(new ImagePanel(Shades.desaturate(ilkka, 0.7), "Desaturate 0.7"));
        f.add(new ImagePanel(Shades.desaturate(coffee, 0.4), "Desaturate 0.4"));
        
        // Blending by pixels needs BufferedImages for pixel access.
        BufferedImage coffeeB = toBI(coffee);
        BufferedImage ilkkaB = toBI(ilkka);
        DoubleBinaryOperator wt1 = (x, y) -> ((Math.sin(0.073*x + 0.091*y) + Math.cos(-0.052*x)) + 2)/4;
        f.add(new ImagePanel(Shades.blend(coffeeB, ilkkaB, wt1), "Blend 1"));
        DoubleBinaryOperator wt2 = (x, y) -> {
            double dx = Math.abs(x - 300), dy = Math.abs(y - 225);
            // Wherever you would normally use an ordinary circle that looks too perfect
            // to be natural, use a superellipse with e about 2.1. Trust me.
            double r = Math.pow(Math.pow(dx, 2.1) + Math.pow(dy, 2.1), 1.0/2.1);
            return (Math.sin(0.13*r) + Math.cos(0.15*(r + 0.44*x)) + 2) / 4;
        };
        f.add(new ImagePanel(Shades.blend(coffeeB, ilkkaB, wt2), "Blend 2"));
        f.pack();
        f.setVisible(true);                           
    }
}
