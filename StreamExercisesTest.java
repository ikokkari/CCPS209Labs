
import java.util.*;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.nio.file.*;
import java.io.*;

public class StreamExercisesTest {
    
    private static int TRIALS = 10000;
    private static int SEED = 9999;
    
    @Test
    public void testCountLines() {
        StreamExercises se = new StreamExercises();
        try {
            assertEquals(se.countLines(Paths.get("warandpeace.txt"), 70), 21079);
        }
        catch(IOException e) {
            System.out.println("Unable to read warandpeace.txt!");
            assertTrue(false);
        }
    }
    
    @Test
    public void testCollectWords() {
        StreamExercises se = new StreamExercises();
        try {
            List<String> words = se.collectWords(Paths.get("warandpeace.txt"));
            assertEquals(17465, words.size());
            assertEquals("accomplished", words.get(100));
            assertEquals("sundays", words.get(15000));
        }
        catch(IOException e) {
            System.out.println("Unable to read warandpeace.txt!");
            assertTrue(false);
        }
        
    }
}