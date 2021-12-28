import org.junit.Test;
import javax.swing.JFrame;
import java.awt.FlowLayout;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;

public class UlamTest {

    @Test public void massTest() {
        int[][] start = { {100, 100}, {100, 200}, {200, 100}, {200, 200} };
        Ulam ulam = new Ulam(300, 300, start, 500);
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int c = 0; c < 1000; c++) {
            int i = rng.nextInt(300);
            int j = rng.nextInt(300);
            int s = rng.nextInt(500);
            check.update(ulam.getCellState(i, j, s) ? c : 0);
        }
        assertEquals(1297780951L, check.getValue());
    }
    
    @Test public void launchUlam() {
        JFrame f = new JFrame("Ulam");
        // Tell the frame to obey the close button
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new FlowLayout());
        int[][] start = { {100, 100}, {100, 200}, {200, 100}, {200, 200} };
        f.add(new Ulam(300, 300, start, 500));
        f.pack();
        f.setVisible(true);        
    }
}
