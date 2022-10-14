import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class RegexFractalMain {

    private static class ImagePanel extends JPanel {
        private final Image img;
        public ImagePanel(Image img, String title) {
            this.img = img;
            this.setToolTipText(title);
            this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
            this.setBorder(BorderFactory.createEtchedBorder());
        }
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }
    }

    private static final String[] REGEXES = {
            ".*(13|31|24|42).*", ".*[^4][^32]3.*",
            "[34]*2.*", "[124]*3[24]*(3.*)?",
            ".*(12|(2(?![34]))).*", ".*(13|32|((?<!4)24)).*(?!\\1)$"
    };

    public static void main(String[] args) {
        JFrame frame = new JFrame("Regular Expression Fractals");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 4));
        for(String regex: REGEXES) {
            BufferedImage resultF = RegexFractal.render(regex, 256, false);
            frame.add(new ImagePanel(resultF, regex + " with no rotate"));
            BufferedImage resultT = RegexFractal.render(regex, 256, true);
            frame.add(new ImagePanel(resultT, regex + " with rotate"));

        }
        frame.pack();
        frame.setVisible(true);
    }
}
