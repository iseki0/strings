package space.iseki.strings;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Iterator;

/**
 * The StringsIter is used to create iterator of data that derived from "strings".
 */
public class Strings {
    /**
     * Returns an iterator hashing the extracted strings from the specified input stream
     *
     * @param messageDigest the message digest object will be used to generate hash.
     * @param inputStream the source input stream. It's the caller's responsibility to close the input stream.
     * @return the returned iterator will throw {@link java.io.UncheckedIOException} when the underlying input stream throws {@link java.io.IOException}
     */
    public static @NotNull Iterator<byte[]> createHashIterator(@NotNull MessageDigest messageDigest, @NotNull InputStream inputStream){
        return new HashIteratorImpl(messageDigest, new PrintableSplitInputStream(new BufferedInputStream(inputStream)));
    }

    /**
     * Returns an iterator extracts strings from the specified input stream
     * @param inputStream the source input stream. It's the caller's responsibility to close the input stream.
     * @return the returned iterator will throw {@link java.io.UncheckedIOException} when the underlying input stream throws {@link java.io.IOException}
     */
    public static @NotNull Iterator<byte[]> createByteArrayIterator(@NotNull InputStream inputStream){
        return new BytesIteratorImpl(new PrintableSplitInputStream(inputStream));
    }

}
