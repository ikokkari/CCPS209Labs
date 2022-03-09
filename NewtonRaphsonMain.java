import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.function.Function;

public class NewtonRaphsonMain {

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

    public static void main(String[] args) {
        final Complex ONE = new Complex(1, 0);
        final Complex TWO = new Complex(2, 0);
        Function<Complex, Complex> f0 = z -> z.multiply(z).multiply(z).subtract(ONE);
        Function<Complex, Complex> fp0 = z -> TWO.multiply(z).multiply(z);

        float[] comp = new float[3];
        Color.RGBtoHSB(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), comp);
        float blueHue = comp[0];
        Color.RGBtoHSB(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), comp);
        float redHue = comp[0];
        Color.RGBtoHSB(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), comp);
        float yellowHue = comp[0];

        Function<Complex, Float> hue0 = z -> {
            double re = z.getRe();
            double im = z.getIm();
            if(re > 0) { return blueHue; }
            if(im > 0) { return redHue; }
            return yellowHue;
        };
        BufferedImage result = NewtonRaphson.createNewtonRaphson(
                new Complex(-3,3), 1000, 6/1000.0, f0, fp0, hue0
        );

        JFrame frame = new JFrame("Newton-Raphson Demo");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 1));
        frame.add(new ImagePanel(result, "z^3 - 1"));
        frame.pack();
        frame.setVisible(true);
    }
}