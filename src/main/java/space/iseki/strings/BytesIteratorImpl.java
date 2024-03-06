package space.iseki.strings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

class BytesIteratorImpl implements Iterator<byte[]> {
    private final PrintableSplitInputStream inputStream;
    private boolean alreadyNext;
    private boolean lastNext;

    @SuppressWarnings("ConstantValue")
    BytesIteratorImpl(@NotNull PrintableSplitInputStream inputStream) {
        if (inputStream == null) throw new NullPointerException("inputStream == null");
        this.inputStream = inputStream;
    }

    @Override
    public boolean hasNext() {
        if (alreadyNext) return lastNext;
        try {
            alreadyNext = true;
            return lastNext = inputStream.next();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public byte @NotNull [] next() {
        if (!hasNext()) throw new NoSuchElementException();
        try {
            alreadyNext = false;
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
