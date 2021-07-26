import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ParticleMain {
    public static void main(String[] args) {
        JFrame f = new JFrame("Particle field demo");
        ParticleField pf = new ParticleField(2000, 800, 800);
        f.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    pf.terminate(); // tell the ParticleField object to release its threads
                    f.dispose(); // and now we can safely dispose of the frame
                }
            });
        f.setLayout(new FlowLayout());
        f.add(pf);
        f.pack();
        f.setVisible(true);        
    }
}
