import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.image.*;

public class AnimationPanel extends JPanel {

    private static final int FRAME_TIME = 50;

    // The AnimationFrame object that renders itself in this panel.
    private final AnimationFrame af;
    // How much time advances in each step.
    private final double step;
    // The animation frame timer. Such "metronomes" are often a lighter
    // alternative to threads and concurrency in animations.
    private final javax.swing.Timer timer;
    // The current time inside this animation.
    private double t;
    // Whether this animation is running or paused.
    private volatile boolean running = true;
    
    public AnimationPanel(AnimationFrame af, double step) {
        this.setBackground(Color.WHITE);
        this.af = af;
        this.step = step;
        this.timer = new javax.swing.Timer(1000 / FRAME_TIME, new MyTimerListener());
        this.timer.start();
    }
    
    public void terminate() {
        timer.stop();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
        af.render(g2, getWidth(), getHeight(), t);
    }
    
    private class MyTimerListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            if(running) {
                t += step;
                repaint();
            }
        }
    }
    
    public static void main(String[] args) {
        final int SIZE = 400;
        
        // Edit this line to try out the AnimationPanel subclasses that you create.
        AnimationPanel[] panels = {
            new AnimationPanel(new Tircle(5), 1 / 150.0),
            new AnimationPanel(new Tircle(6), 1 / 400.0),
            //new AnimationPanel(new Breathe(5), 1 / 50.0),
            //new AnimationPanel(new Breathe(4), 1 / 100.0)
        };
        
        JFrame f = new JFrame("Animation Demo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                // Tell each AnimationPanel inside the frame to terminate.
                for(AnimationPanel panel: panels) { panel.terminate(); }
                f.dispose(); // Now we can safely dispose of the frame.
            }
        });
        int rows = 1;
        while(rows * rows <= panels.length) { rows += 1; }
        rows--;
        int cols = panels.length / rows;
        // GridLayout enforces the component to be uniform in size.
        f.setLayout(new GridLayout(rows, cols));
        // Edit this line to try out the AnimationFrame subclasses you do in labs.
        for(AnimationPanel panel: panels) {
            f.add(panel);
        }
        f.setSize(SIZE * cols, SIZE * rows);
        f.setVisible(true);        
    }
    
}