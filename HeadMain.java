import javax.swing.*;
import java.awt.*;

public class HeadMain {
    public static void main(String[] args) {
        JFrame f = new JFrame("Head demo");
        // Tell the frame to obey the close button
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new GridLayout(2, 2));
        for(int i = 0; i < 4; i++) {
            f.add(new Head());
        }
        f.pack();
        f.setVisible(true);        
    }
}