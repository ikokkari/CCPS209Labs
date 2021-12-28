import javax.swing.BorderFactory;
import javax.swing.JFrame;
import java.awt.GridLayout;
import java.util.Random;

public class TruchetMain {

    public static void main(String[] args) {
        JFrame f = new JFrame("Truchet demo");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new GridLayout(2, 3));
        Random rng = new Random(1234);
        boolean[][] p1 = new boolean[40][40];
        boolean[][] d1 = new boolean[40][40];
        for(int x = 0; x < 40; x++) {
            for(int y = 0; y < 40; y++) {
                p1[x][y] = rng.nextBoolean();
                d1[x][y] = rng.nextBoolean();
            }
        }
        
        Truchet[] truchets = {
            new Truchet(200, 2, 2, (x, y) -> y % 2 == 1, (x, y) -> (x+y) % 2 == 1),
            new Truchet(20, 20, 20, (x, y) -> (2*x+y) % 2 == 1, (x, y) -> (x+2*y) % 2 == 0),
            new Truchet(20, 20, 20, (x, y) -> ((x ^ y) & 4) != 0, (x, y) -> (x + y*y) % 2 == 0),
            new Truchet(20, 20, 20, (x, y) -> (x + y) % 2 == 0, (x, y) -> (3*x + 2*y) % 3 == 1),
            new Truchet(20, 20, 20, (x, y) -> (2*x + 7*y) % 3 == 0, (x, y) -> (4*x + 3*y) % 4 == 1),
            new Truchet(10, 40, 40, (x, y) -> p1[x][y], (x, y) -> d1[x][y])
        };
        
        for(Truchet truchet: truchets) {
            truchet.setBorder(BorderFactory.createRaisedBevelBorder());
            f.add(truchet);
        }
        f.pack();
        f.setVisible(true);        
    }
}
