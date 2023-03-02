import java.io.BufferedReader;
import java.io.IOException;

public abstract class FileProcessor<R> {
    protected abstract void startFile();
    protected abstract void processLine(String line);
    protected abstract R endFile();

    public final R processFile(BufferedReader in) throws IOException {
        startFile();
        String line;
        while((line = in.readLine()) != null) {
            processLine(line);
        }
        return endFile();
    }
}
