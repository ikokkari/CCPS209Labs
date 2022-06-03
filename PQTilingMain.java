import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.FlowLayout;
import java.awt.Image;

public class PQTilingMain {

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

    public static void main(String[] args) {
        JFrame fSmall = new JFrame("Small PQ");
        Image small = PQTiling.render(20, 80, 15);
        fSmall.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fSmall.setLayout(new FlowLayout());
        fSmall.add(new ImagePanel(small, "PQ Tiling"));
        fSmall.pack();
        fSmall.setVisible(true);
        JFrame fLarge = new JFrame("Large PQ");
        Image large = PQTiling.render(20, 300, 5);
        fLarge.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fLarge.setLayout(new FlowLayout());
        fLarge.add(new ImagePanel(large, "PQ Tiling"));
        fLarge.pack();
        fLarge.setVisible(true);
    }
}
