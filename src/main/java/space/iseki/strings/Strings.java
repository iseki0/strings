package space.iseki.strings;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Iterator;

/**
 * The StringsIter is used to create iterator of data that derived from "strings".
 */
public class Strings {
    /**
     * Returns an iterator hashing the extracted strings from the specified input stream with default option.
     *
     * @param messageDigest the message digest object will be used to generate hash.
     * @param inputStream   the source input stream. It's the caller's responsibility to close the input stream.
     * @return the returned iterator will throw {@link java.io.UncheckedIOException} when the underlying input stream throws {@link java.io.IOException}
     */
    public static @NotNull Iterator<byte[]> createHashIterator(@NotNull MessageDigest messageDigest, @NotNull InputStream inputStream) {
        return createHashIterator(messageDigest, inputStream, Option.DEFAULT);
    }

    /**
     * Returns an iterator hashing the extracted strings from the specified input stream.
     *
     * @param messageDigest the message digest object will be used to generate hash.
     * @param inputStream   the source input stream. It's the caller's responsibility to close the input stream.
     * @param option        the options, just like strings command.
     * @return the returned iterator will throw {@link java.io.UncheckedIOException} when the underlying input stream throws {@link java.io.IOException}
     */
    public static @NotNull Iterator<byte[]> createHashIterator(@NotNull MessageDigest messageDigest, @NotNull InputStream inputStream, @NotNull Option option) {
        return new HashIteratorImpl(messageDigest, create(inputStream, option));
    }

    /**
     * Returns an iterator extracts strings from the specified input stream with default option.
     *
     * @param inputStream the source input stream. It's the caller's responsibility to close the input stream.
     * @return the returned iterator will throw {@link java.io.UncheckedIOException} when the underlying input stream throws {@link java.io.IOException}
     */
    public static @NotNull Iterator<byte[]> createByteArrayIterator(@NotNull InputStream inputStream) {
        return createByteArrayIterator(inputStream, Option.DEFAULT);
    }

    /**
     * Returns an iterator extracts strings from the specified input stream.
     *
     * @param inputStream the source input stream. It's the caller's responsibility to close the input stream.
     * @param option      the options, just like strings command.
     * @return the returned iterator will throw {@link java.io.UncheckedIOException} when the underlying input stream throws {@link java.io.IOException}
     */
    public static @NotNull Iterator<byte[]> createByteArrayIterator(@NotNull InputStream inputStream, @NotNull Option option) {
        return new BytesIteratorImpl(create(inputStream, option));
    }

    private static PrintableSplitInputStream create(InputStream inputStream, Option option) {
        return new PrintableSplitInputStream(inputStream, option.min(), option.space());
    }

    /**
     * Options, just like strings command
     */
    public record Option(int min, boolean space) {
        static Option DEFAULT = new Option();

        /**
         * Options, just like strings command
         */
        public Option {
            if (min < 1) throw new IllegalArgumentException("min < 1");
        }

        /**
         * Options, just like strings command
         */
        public Option() {
            this(4, false);
        }

        /**
         * Options, just like strings command
         */
        public Option(int min) {
            this(min, false);
        }

        /**
         * Options, just like strings command
         */
        public Option(boolean space) {
            this(4, space);
        }
    }

}
