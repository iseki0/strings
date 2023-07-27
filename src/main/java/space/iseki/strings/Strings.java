package space.iseki.strings;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Strings {
}

class StringByteChannelIterator implements AutoCloseable, Iterator<String> {

    private static final int BUFFER_SIZE = 16 * 1024;
    private static final int MAX_LENGTH = 256;
    private int minLength = 4;

    private int lastPos = 0;

    private String keep;

    /**
     * after init, remember to flip the buffer.
     */
    private ByteBuffer buffer;
    private ByteChannel channel;

    private String peekNext() throws IOException {
        var pos = read();
        String r = null;
        if (pos - lastPos > minLength) {
            var arr = new byte[pos - lastPos];
            buffer.get(lastPos, arr);
            r = new String(arr, StandardCharsets.ISO_8859_1);
        }
        lastPos = pos;
        return r;
    }

    private int read() throws IOException {
        while (true) {
            for (int i = 0; i < buffer.remaining(); i++) {
                int ch = buffer.get();
                if (ch <= 32) {
                    if (buffer.position() - lastPos <= minLength) {
                        lastPos = buffer.position();
                        continue;
                    }
                    return buffer.position();
                }
                if (buffer.position() - lastPos >= MAX_LENGTH) {
                    return buffer.position();
                }
            }
            var t = buffer.position();
            buffer.position(lastPos);
            buffer.compact();
            buffer.position(t - lastPos);
            buffer.flip();
            var n = channel.read(buffer);
            buffer.flip();
            if (n == -1) {
                return buffer.position();
            }
        }
    }


    @Override
    public void close() throws Exception {
        buffer = null;
        if (channel != null) channel.close();
        channel = null;
    }

    @Override
    public boolean hasNext() {
        try {
            keep = peekNext();
            return keep != null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String next() {
        return keep;
    }
}
