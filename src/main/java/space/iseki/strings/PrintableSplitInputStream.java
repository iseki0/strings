package space.iseki.strings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

class PrintableSplitInputStream extends InputStream {
    private final ByteBuffer buffer;
    private final InputStream inputStream;
    private int state = FILLING;

    private static final int FILLING = 1;
    private static final int PRINTING = 2;
    private static final int PAUSE = 3;

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
                case FILLING -> {
                    var i = inputStream.read();
                    if (i == -1) return -1;
                    if (printable(i)) {
                        buffer.put((byte) i);
                    } else {
                        buffer.clear();
                    }
                    if (!buffer.hasRemaining()) {
                        buffer.flip();
                        state = PRINTING;
                    }
                }
                case PRINTING -> {
                    if (buffer.hasRemaining()) return buffer.get() & 0xFF;
                    var i = inputStream.read();
                    if (i == -1) return -1;
                    if (printable(i)) return i;
                    state = PAUSE;
                }
                case PAUSE -> {
                    return -1;
                }
            }
        }
    }

    public void next() {
        if (state != PAUSE) return;
        buffer.clear();
        state = FILLING;
    }

    private boolean printable(int ch) {
        return (ch >= 32 || ch == '\t') && ch < 127;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
