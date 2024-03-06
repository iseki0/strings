package space.iseki.strings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

class BytesIteratorImpl implements Iterator<byte[]> {
    private final PrintableSplitInputStream inputStream;
    private byte[] last;

    @SuppressWarnings("ConstantValue")
    BytesIteratorImpl(@NotNull PrintableSplitInputStream inputStream) {
        if (inputStream == null) throw new NullPointerException("inputStream == null");
        this.inputStream = inputStream;
    }

    @Override
    public boolean hasNext() {
        if (last == null) last = doRead();
        return last != null;
    }

    @Override
    public byte @NotNull [] next() {
        if (!hasNext()) throw new NoSuchElementException();
        var t = last;
        last = null;
        return t;
    }

    private byte[] doRead() {
        try {
            inputStream.next();
            var arr = inputStream.readAllBytes();
            if (arr.length == 0) return null;
            return arr;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
