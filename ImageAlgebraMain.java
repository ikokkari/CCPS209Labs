import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

public class ImageAlgebraMain {
    
    // A little utility class to display images as Swing components.
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
    
    public static void main(String[] args) throws IOException {
        // Read the image from the file.
        Image coffee = ImageIO.read(new File("coffee.jpg"));
        // Create a smaller version of the image.
        coffee = coffee.getScaledInstance(800, 600, Image.SCALE_SMOOTH);
        // Crop a square area from the image.
        ImageFilter cf = new CropImageFilter(120, 0, 512, 512);
        ImageProducer producer = new FilteredImageSource(coffee.getSource(), cf);
        // Now we have a square Image to use.
        coffee = Toolkit.getDefaultToolkit().createImage(producer);
        
        // To test hstack and vstack, build an image from rows of smaller images.
        Image c = coffee.getScaledInstance(128, 128, Image.SCALE_SMOOTH);
        Image r = ImageAlgebra.hstack(c, c, c, c);
        Image all = ImageAlgebra.vstack(r, r, r, r);
        
        JFrame f = new JFrame("Image Operations Demo");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new FlowLayout());
        f.add(new ImagePanel(all, "Stack"));
        f.add(new ImagePanel(ImageAlgebra.halving(coffee, 5), "Halving"));
        
        f.pack();
        f.setVisible(true);                           
    }
}
