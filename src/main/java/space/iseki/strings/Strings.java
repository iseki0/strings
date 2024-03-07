package space.iseki.strings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * The StringsIter is used to create iterator of data that derived from "strings".
 */
@SuppressWarnings("unused")
public class Strings {
    private static final byte[] DEFAULT_LINEBREAK = new byte[]{'\n'};

    /**
     * Returns an iterator hashing the extracted strings from the specified input stream with default option.
     *
     * @param messageDigest the message digest object will be used to generate hash.
     * @param inputStream   the source input stream. It's the caller's responsibility to close the input stream.
     * @return the returned iterator will throw {@link UncheckedIOException} when the underlying input stream throws {@link IOException}
     */
    public static @NotNull Iterator<byte @NotNull []> createHashIterator(@NotNull MessageDigest messageDigest, @NotNull InputStream inputStream) {
        return createHashIterator(messageDigest, inputStream, Option.DEFAULT);
    }

    /**
     * Returns an iterator hashing the extracted strings from the specified input stream.
     *
     * @param messageDigest the message digest object will be used to generate hash.
     * @param inputStream   the source input stream. It's the caller's responsibility to close the input stream.
     * @param option        the options, just like strings command.
     * @return the returned iterator will throw {@link UncheckedIOException} when the underlying input stream throws {@link IOException}
     */
    public static @NotNull Iterator<byte @NotNull []> createHashIterator(@NotNull MessageDigest messageDigest, @NotNull InputStream inputStream, @NotNull Option option) {
        return new HashIteratorImpl(messageDigest, create(inputStream, option));
    }

    /**
     * Returns an iterator extracts strings from the specified input stream with default option.
     *
     * @param inputStream the source input stream. It's the caller's responsibility to close the input stream.
     * @return the returned iterator will throw {@link UncheckedIOException} when the underlying input stream throws {@link IOException}
     */
    public static @NotNull Iterator<byte @NotNull []> createByteArrayIterator(@NotNull InputStream inputStream) {
        return createByteArrayIterator(inputStream, Option.DEFAULT);
    }

    /**
     * Returns an iterator extracts strings from the specified input stream.
     *
     * @param inputStream the source input stream. It's the caller's responsibility to close the input stream.
     * @param option      the options, just like strings command.
     * @return the returned iterator will throw {@link UncheckedIOException} when the underlying input stream throws {@link IOException}
     */
    public static @NotNull Iterator<byte @NotNull []> createByteArrayIterator(@NotNull InputStream inputStream, @NotNull Option option) {
        return new BytesIteratorImpl(create(inputStream, option));
    }

    /**
     * Returns an input stream that extracts strings from the specified input stream with default option.
     *
     * @param inputStream the source input stream. It's the caller's responsibility to close the input stream.
     */
    public static @NotNull InputStream of(@NotNull InputStream inputStream) {
        return of(inputStream, DEFAULT_LINEBREAK, Option.DEFAULT);
    }

    /**
     * Returns an input stream that extracts strings from the specified input stream with default option.
     *
     * @param inputStream the source input stream. It's the caller's responsibility to close the input stream.
     * @param option      the options, just like strings command.
     */
    public static @NotNull InputStream of(@NotNull InputStream inputStream, @NotNull Option option) {
        return of(inputStream, DEFAULT_LINEBREAK, option);
    }

    /**
     * Returns an input stream that extracts strings from the specified input stream with default option.
     *
     * @param inputStream the source input stream. It's the caller's responsibility to close the input stream.
     * @param separator   the separator used to split strings.
     */
    public static @NotNull InputStream of(@NotNull InputStream inputStream, byte @NotNull [] separator) {
        return of(inputStream, separator, Option.DEFAULT);
    }

    /**
     * Returns an input stream that extracts strings from the specified input stream with default option.
     *
     * @param inputStream the source input stream. It's the caller's responsibility to close the input stream.
     * @param separator   the separator used to split strings.
     * @param option      the options, just like strings command.
     */
    public static @NotNull InputStream of(@NotNull InputStream inputStream, byte @NotNull [] separator, @NotNull Option option) {
        return new RebuildStringsInputStream(create(inputStream, option), Arrays.copyOf(separator, separator.length));
    }

    private static PrintableSplitInputStream create(InputStream inputStream, Option option) {
        return new PrintableSplitInputStream(inputStream, option.min(), option.space());
    }

    /**
     * Options, just like strings command
     */
    @SuppressWarnings("unused")
    public static final class Option {
        public static final Option DEFAULT = new Builder().createOption();
        private final int min;
        private final boolean space;

        /**
         * Options, just like strings command
         */
        @SuppressWarnings("DeprecatedIsStillUsed")
        @Deprecated(forRemoval = true)
        public Option(int min, boolean space) {
            if (min < 1) throw new IllegalArgumentException("min < 1");
            this.min = min;
            this.space = space;
        }

        /**
         * Options, just like strings command
         */
        @Deprecated(forRemoval = true)
        public Option() {
            this(4, false);
        }

        /**
         * Options, just like strings command
         */
        @Deprecated(forRemoval = true)
        public Option(int min) {
            this(min, false);
        }

        /**
         * Options, just like strings command
         */
        @Deprecated(forRemoval = true)
        public Option(boolean space) {
            this(4, space);
        }

        public int min() {
            return min;
        }

        public boolean space() {
            return space;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Option) obj;
            return this.min == that.min &&
                    this.space == that.space;
        }

        @Override
        public int hashCode() {
            return Objects.hash(min, space);
        }

        @Override
        public String toString() {
            return "Option[" +
                    "min=" + min + ", " +
                    "space=" + space + ']';
        }

        public static class Builder {
            private int min = 4;
            private boolean space = false;

            public Builder setMin(int min) {
                this.min = min;
                return this;
            }

            public Builder setSpace(boolean space) {
                this.space = space;
                return this;
            }

            public Option createOption() {
                return new Option(min, space);
            }
        }

    }

}
