import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

import java.io.*;
import java.util.*;

public class WordCountTest {
    
    @Test
    public void scanWarAndPeace() throws IOException {
        WordCount wc = new WordCount();
        BufferedReader fr = new BufferedReader(
            new InputStreamReader(new FileInputStream("warandpeace.txt"), "UTF-8")
        );
        List<Integer> result = wc.processFile(fr);
        fr.close();
        assertEquals(3138473, result.get(0).intValue()); // characters
        assertEquals(562491, result.get(1).intValue()); // words
        assertEquals(63852, result.get(2).intValue()); // lines
    }
}
