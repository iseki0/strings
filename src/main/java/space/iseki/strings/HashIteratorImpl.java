package space.iseki.strings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.NoSuchElementException;

class HashIteratorImpl implements Iterator<byte[]> {
    private static final int DEFAULT_BUFFER_SIZE = 16 * 1024;
    private final MessageDigest messageDigest;
    private final byte[] buffer;
    private final PrintableSplitInputStream inputStream;
    private byte[] hash;


    @SuppressWarnings("ConstantValue")
    HashIteratorImpl(@NotNull MessageDigest messageDigest, @NotNull PrintableSplitInputStream inputStream, int bufferSize) {
        if (messageDigest == null) throw new NullPointerException("messageDigest == null");
        if (inputStream == null) throw new NullPointerException("inputStream == null");
        if (bufferSize <= 0) throw new IllegalArgumentException("bufferSize <= 0");
        this.buffer = new byte[bufferSize];
        this.inputStream = inputStream;
        this.messageDigest = messageDigest;
    }

    HashIteratorImpl(@NotNull MessageDigest messageDigest, @NotNull PrintableSplitInputStream inputStream) {
        this(messageDigest, inputStream, DEFAULT_BUFFER_SIZE);
    }

    private @Nullable byte[] doHash() {
        try {
            var n = inputStream.read(buffer);
            if (n == -1) return null;
            messageDigest.update(buffer, 0, n);
            while (true) {
                n = inputStream.read();
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

    @Override
    public boolean hasNext() {
        if (hash == null) hash = doHash();
        return hash != null;
    }

    @Override
    public @NotNull byte[] next() {
        if (hash == null) hash = doHash();
        if (hash == null) throw new NoSuchElementException();
        var t = hash;
        hash = null;
        return t;
    }
}
