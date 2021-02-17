import java.util.*;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.nio.file.*;
import java.io.*;

public class StreamExercisesTest {
        
    @Test public void testCountLines() {
        StreamExercises se = new StreamExercises();
        try {
            assertEquals(se.countLines(Paths.get("warandpeace.txt"), 70), 21079);
        }
        catch(IOException e) {
            System.out.println("Unable to read warandpeace.txt!");
            assertTrue(false);
        }
    }    
    
    @Test public void testCollectWords() {
        StreamExercises se = new StreamExercises();
        try {
            List<String> words = se.collectWords(Paths.get("warandpeace.txt"));
            assertEquals(17465, words.size());
            assertEquals("a", words.get(0));
            assertEquals("accomplished", words.get(100));
            assertEquals("adjoining", words.get(200));
            assertEquals("ambled", words.get(500));
            assertEquals("burdening", words.get(2000));
            assertEquals("elevate", words.get(5000));
            assertEquals("moscovites", words.get(9876));
            assertEquals("sundays", words.get(15000));
        }
        catch(IOException e) {
            System.out.println("Unable to read warandpeace.txt!");
            assertTrue(false);
        }        
    }
}