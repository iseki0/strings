package space.iseki.strings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

class ByteChannelSplitter {
    private final ReadableByteChannel channel;
    private final ByteBuffer buffer;
    private final int maxLength;
    private final int minLength;

    public static @NotNull ByteChannelSplitter of(@NotNull ReadableByteChannel channel) {
        return new ByteChannelSplitter(channel, 256, 4, 16 * 1024);
    }

    public static @NotNull ByteChannelSplitter of(@NotNull ReadableByteChannel channel,
                                                  int maxLength,
                                                  int minLength,
                                                  int bufferSize) {
        return new ByteChannelSplitter(channel, maxLength, minLength, bufferSize);
    }

    private ByteChannelSplitter(ReadableByteChannel channel, int maxLength, int minLength, int bufferSize) {
        if (channel == null) throw new NullPointerException("buffer is null");
        if (maxLength < minLength) throw new IllegalArgumentException("maxLength < minLength");
        if (minLength < 1) throw new IllegalArgumentException("minLength < 1");
        if (bufferSize < maxLength) throw new IllegalArgumentException("bufferSize < maxLength");
        this.channel = channel;
        this.buffer = ByteBuffer.allocate(bufferSize);
        this.buffer.flip();
        this.maxLength = maxLength;
        this.minLength = minLength;
    }

    public @Nullable byte[] getPrintableBytes() throws IOException {
        while (true) {
            var end = findPos(maxLength);
            if (end == -1) return null;
            var len = end - buffer.position();
            if (len >= minLength) {
                var r = new byte[len];
                buffer.get(r);
                return r;
            } else {
                buffer.position(buffer.position() + len);
            }
        }
    }

    private int findPos(int max) throws IOException {
        int start = 0;
        while (true) {
            if (!buffer.hasRemaining()) {
                // fill buffer
                buffer.clear();
                var read = channel.read(buffer);
                if (read == -1) return -1; // EOF
                if (read == 0) throw new IllegalStateException("read zero bytes");
                buffer.flip();
            }
            if (printable(buffer.get())) {
                start = buffer.position() - 1;
                break;
            }
        }
        for (int i = 0; i < max; i++) {
            if (!buffer.hasRemaining()) {
                // compat
                buffer.position(start);
                buffer.compact();
                start = 0; // after compact, position == 0
                // fill buffer
                buffer.flip();
                var read = channel.read(buffer);
                buffer.flip();
                if (read == -1) return buffer.limit();
                if (read == 0) throw new IllegalStateException("read zero bytes");
                buffer.position(buffer.limit() - read); // skip already read bytes
            }
            if (!printable(buffer.get())) {
                buffer.position(buffer.position() - 1);
                break;
            }
        }
        var r = buffer.position();
        buffer.position(start);
        return r;
    }


    private boolean printable(int ch) {
        return (ch >= 32 || ch == '\t') && ch < 127;
    }

}