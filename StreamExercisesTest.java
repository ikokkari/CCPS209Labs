import org.junit.Test;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StreamExercisesTest {
        
    @Test public void testCountLines() {
        try {
            assertEquals(StreamExercises.countLines(Paths.get("warandpeace.txt"), 70), 21079);
        }
        catch(IOException e) {
            System.out.println("Unable to read warandpeace.txt!");
            fail();
        }
    }    
    
    @Test public void testCollectWords() {
        try {
            List<String> words = StreamExercises.collectWords(Paths.get("warandpeace.txt"));
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
            fail();
        }        
    }
}