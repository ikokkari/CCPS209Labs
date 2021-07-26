import javax.swing.*;
import java.awt.*;

public class LissajousMain {
    public static void main(String[] args) {
        JFrame f = new JFrame("Lissajous demo");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new FlowLayout());
        f.add(new Lissajous(600));
        f.pack();
        f.setVisible(true);        
    }
}
