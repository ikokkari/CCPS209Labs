import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class SquareCoralMain {

    private static class ImagePanel extends JPanel {
        private final Image img;
        public ImagePanel(Image img, String toolTip) {
            this.img = img;
            int w = img.getWidth(this);
            int h = img.getHeight(this);
            this.setToolTipText(toolTip);
            this.setPreferredSize(new Dimension(w, h));
            this.setBorder(BorderFactory.createEtchedBorder());
        }
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }
    }

    private static final int SIZE = 512;

    private static BufferedImage render(int depth) {
        BufferedImage coral = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D)(coral.getGraphics());
        g2.setPaint(Color.WHITE);
        g2.fill(new Rectangle2D.Double(0, 0, SIZE, SIZE));
        g2.setPaint(Color.BLACK);
        SquareCoral.render(g2, depth, 0, 0, SIZE, 0);
        return coral;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Square Coral");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new GridLayout(2, 2));
        for(int d = 3; d < 7; d++) {
            f.add(new ImagePanel(render(d), "Depth " + d));
        }
        f.pack();
        f.setVisible(true);
    }

}
