package common.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DataInputStreamAdapter implements LineReader {
    private final BufferedReader reader;

    public DataInputStreamAdapter(DataInputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    @Override
    public String readLine() throws IOException {
        return reader.readLine();
    }
}
