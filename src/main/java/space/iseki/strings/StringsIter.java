package space.iseki.strings;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Iterator;

/**
 * The StringsIter is used to create iterator of data that derived from "strings".
 */
public class StringsIter {
    /**
     * Returns an iterator that generate hashes of "strings" that extracted from the given inputStream
     *
     * @param messageDigest the MessageDigest will be used to generate hash.
     * @param inputStream the source input stream. It's the caller's responsibility to close it.
     * @return the returned iterator might throw {@link java.io.UncheckedIOException} when the underlying input stream throws {@link java.io.IOException}
     */
    public static @NotNull Iterator<byte[]> of(@NotNull MessageDigest messageDigest, @NotNull InputStream inputStream){
        return new HashIteratorImpl(messageDigest, new PrintableSplitInputStream(new BufferedInputStream(inputStream)));
    }

}
