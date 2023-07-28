package space.iseki.strings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

class PrintableSplitInputStream extends InputStream {
    private final ByteBuffer buffer;
    private final InputStream inputStream;
    private int state = S_FILL;
    private static final int S_FILL = 0;
    private static final int S_OUT = 1;
    private static final int S_CONT = 2;
    private static final int S_PAUSE = 3;

    PrintableSplitInputStream(@NotNull InputStream inputStream, int min) {
        //noinspection ConstantValue
        if (inputStream == null) throw new NullPointerException("inputStream == null");
        buffer = ByteBuffer.allocate(min);
        this.inputStream = inputStream;
    }

    PrintableSplitInputStream(@NotNull InputStream inputStream) {
        this(inputStream, 4);
    }

    @Override
    public int read() throws IOException {
        while (true) {
            switch (state) {
                case S_FILL -> {
                    if (!buffer.hasRemaining()) {
                        state = S_OUT;
                        buffer.flip();
                        continue;
                    }
                    var ch = inputStream.read();
                    if (ch == -1) return -1;
                    if (printable(ch)) {
                        buffer.put((byte) ch);
                    } else {
                        buffer.clear();
                    }
                }
                case S_OUT -> {
                    if (buffer.hasRemaining()) return buffer.get();
                    state = S_CONT;
                    buffer.clear();
                }
                case S_CONT -> {
                    var ch = inputStream.read();
                    if (ch == -1 || printable(ch)) return ch;
                    state = S_PAUSE;
                }
                case S_PAUSE -> {
                    return -1;
                }
            }
        }
    }

    public void next() {
        if (state != S_PAUSE) return;
        state = S_FILL;
    }

    private boolean printable(int ch) {
        return (ch >= 32 || ch == '\t') && ch < 127;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
