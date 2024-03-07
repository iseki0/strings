package space.iseki.strings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

class RebuildStringsInputStream extends InputStream {
    private final PrintableSplitInputStream inputStream;
    private final byte[] separator;
    private boolean goon;
    private int sPos;

    RebuildStringsInputStream(PrintableSplitInputStream inputStream, byte[] separator) {
        this.inputStream = Objects.requireNonNull(inputStream, "inputStream == null");
        this.separator = separator;
        this.sPos = separator.length;
        if (separator.length == 0) {
            throw new IllegalArgumentException("separator.length == 0");
        }
    }


    @Override
    public int read() throws IOException {
        if (goon) {
            var i = inputStream.read();
            if (i >= 0) {
                return i;
            }
            goon = false;
            sPos = 0;
        }
        if (sPos < separator.length) {
            return separator[sPos++] & 0xff;
        }
        goon = inputStream.next();
        return inputStream.read();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

}
