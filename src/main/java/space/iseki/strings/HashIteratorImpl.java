package space.iseki.strings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class HashIteratorImpl implements Iterator<byte[]> {
    private static final int DEFAULT_BUFFER_SIZE = 16 * 1024;
    private final MessageDigest messageDigest;
    private final PrintableSplitInputStream inputStream;
    private final int bufferSize;
    private byte[] buffer;
    private boolean alreadyNext;
    private boolean lastNext;


    @SuppressWarnings("ConstantValue")
    HashIteratorImpl(@NotNull MessageDigest messageDigest, @NotNull PrintableSplitInputStream inputStream, int bufferSize) {
        if (messageDigest == null) throw new NullPointerException("messageDigest == null");
        if (inputStream == null) throw new NullPointerException("inputStream == null");
        if (bufferSize <= 0) throw new IllegalArgumentException("bufferSize <= 0");
        this.bufferSize = bufferSize;
        this.inputStream = inputStream;
        this.messageDigest = messageDigest;
    }

    HashIteratorImpl(@NotNull MessageDigest messageDigest, @NotNull PrintableSplitInputStream inputStream) {
        this(messageDigest, inputStream, DEFAULT_BUFFER_SIZE);
    }

    @Override
    public boolean hasNext() {
        if (alreadyNext) return lastNext;
        try {
            alreadyNext = true;
            lastNext = inputStream.next();
            if (!lastNext) buffer = null;
            return lastNext;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public byte @NotNull [] next() {
        if (!hasNext()) throw new NoSuchElementException();
        if (this.buffer == null) this.buffer = new byte[bufferSize];
        try {
            alreadyNext = false;
            while (true) {
                var n = inputStream.read();
                if (n == -1) break;
                messageDigest.update(buffer, 0, n);
            }
            var hash = messageDigest.digest();
            messageDigest.reset();
            return hash;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
