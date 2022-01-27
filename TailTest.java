import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;

public class TailTest {
    
    @Test public void scanWarAndPeace() throws IOException {
        CRC32 check = new CRC32();
        int totalLines = 0;
        for(int k = 10; k <= 100000; k = k * 10) {
            Tail tail = new Tail(k);
            BufferedReader fr = new BufferedReader(
                new InputStreamReader(new FileInputStream("warandpeace.txt"), StandardCharsets.UTF_8)
                );
            List<String> result = tail.processFile(fr);
            totalLines += result.size();
            fr.close();
            for(String line: result) {
                try {
                    check.update(line.getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) {}
            }
        }
        assertEquals(10 + 100 + 1000 + 10000 + 63852, totalLines);
        assertEquals(1946545910L, check.getValue());
    }
}