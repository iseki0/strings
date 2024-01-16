package space.iseki.strings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Masks:
 * <pre>
 *     #define L  (const unsigned short) (lo|is   |pr)	* lower case letter *
 *     #define XL (const unsigned short) (lo|is|xd|pr)	* lowercase hex digit *
 *     #define U  (const unsigned short) (up|is   |pr)	* upper case letter *
 *     #define XU (const unsigned short) (up|is|xd|pr)	* uppercase hex digit *
 *     #define D  (const unsigned short) (di   |xd|pr)	* decimal digit *
 *     #define P  (const unsigned short) (pn      |pr)	* punctuation *
 *     #define _  (const unsigned short) (pn|is   |pr)	* underscore *
 *
 *     #define C  (const unsigned short) (         cn)	* control character *
 *     #define Z  (const unsigned short) (nv      |cn)	* NUL *
 *     #define M  (const unsigned short) (nv|sp   |cn)	* cursor movement: \f \v *
 *     #define V  (const unsigned short) (vs|sp   |cn)	* vertical space: \r \n *
 *     #define T  (const unsigned short) (nv|sp|bl|cn)	* tab *
 *     #define S  (const unsigned short) (nv|sp|bl|pr)	* space *
 * </pre>
 * Code:
 * <pre>
 *       Z,  C,  C,  C,   C,  C,  C,  C,   | NUL SOH STX ETX  EOT ENQ ACK BEL |
 *       C,  T,  V,  M,   M,  V,  C,  C,   | BS  HT  LF  VT   FF  CR  SO  SI  |
 *       C,  C,  C,  C,   C,  C,  C,  C,   | DLE DC1 DC2 DC3  DC4 NAK SYN ETB |
 *       C,  C,  C,  C,   C,  C,  C,  C,   | CAN EM  SUB ESC  FS  GS  RS  US  |
 *       S,  P,  P,  P,   P,  P,  P,  P,   | SP  !   "   #    $   %   &   '   |
 *       P,  P,  P,  P,   P,  P,  P,  P,   | (   )   *   +    ,   -   .   /   |
 *       D,  D,  D,  D,   D,  D,  D,  D,   | 0   1   2   3    4   5   6   7   |
 *       D,  D,  P,  P,   P,  P,  P,  P,   | 8   9   :   ;    <   =   >   ?   |
 *       P, XU, XU, XU,  XU, XU, XU,  U,   | @   A   B   C    D   E   F   G   |
 *       U,  U,  U,  U,   U,  U,  U,  U,   | H   I   J   K    L   M   N   O   |
 *       U,  U,  U,  U,   U,  U,  U,  U,   | P   Q   R   S    T   U   V   W   |
 *       U,  U,  U,  P,   P,  P,  P,  _,   | X   Y   Z   [    \   ]   ^   _   |
 *       P, XL, XL, XL,  XL, XL, XL,  L,   | `   a   b   c    d   e   f   g   |
 *       L,  L,  L,  L,   L,  L,  L,  L,   | h   i   j   k    l   m   n   o   |
 *       L,  L,  L,  L,   L,  L,  L,  L,   | p   q   r   s    t   u   v   w   |
 *       L,  L,  L,  P,   P,  P,  P,  C,   | x   y   z   {    |   }   ~   DEL |
 * </pre>
 *
 * <pre>
 *     #define STRING_ISGRAPHIC(c) \
 *       (   (c) >= 0 \
 *        && (c) <= 255 \
 *        && ((c) == '\t' || ISPRINT (c) || (encoding == 'S' && (c) > 127) \
 * 	   || (include_all_whitespace && ISSPACE (c))) \
 *       )
 * </pre>
 * The implementation is not thread-safe.
 *
 * @see <a href="https://github.com/redox-os/binutils-gdb/blob/f35674005e609660f5f45005a9e095541ca4c5fe/binutils/strings.c#L79">strings.c</a>
 * @see <a href="https://github.com/gcc-mirror/gcc/blob/86d92c84762f8c805c4e3d87f394c095139c81f0/libiberty/safe-ctype.c#L126">safe-ctype.c</a>
 */
class PrintableSplitInputStream extends InputStream {
    private final InputStream inputStream;
    private final boolean space;
    private final byte[] buf;
    private int pos;
    private boolean acc = true;
    private boolean pause = false;

    PrintableSplitInputStream(@NotNull InputStream inputStream, int min) {
        this(inputStream, min, false);
    }

    PrintableSplitInputStream(@NotNull InputStream inputStream, int min, boolean space) {
        //noinspection ConstantValue
        if (inputStream == null) throw new NullPointerException("inputStream == null");
        if (min < 1) throw new IllegalArgumentException("min < 1");
        this.buf = new byte[min];
        this.inputStream = inputStream;
        this.space = space;
    }

    PrintableSplitInputStream(@NotNull InputStream inputStream) {
        this(inputStream, 4);
    }


    @Override
    public int read() throws IOException {
        while (true) {
            if (pause) return -1;
            if (acc) {
                var i = inputStream.read();
                if (i == -1) return -1;
                if (printable(i)) {
                    buf[pos++] = (byte) i;
                    if (pos == buf.length) {
                        acc = false;
                    }
                } else {
                    pos = 0;
                }
            } else {
                if (pos == 0) {
                    var i = inputStream.read();
                    if (i == -1) return -1;
                    if (!printable(i)) {
                        acc = true;
                    }
                    if (printable(i)) return i;
                    acc = true;
                    pause = true;
                } else {
                    return buf[buf.length - pos--] & 0xff;
                }
            }
        }
    }

    public void next() {
        pause = false;
    }

    private boolean printable(int ch) {
        return (ch >= 32 || ch == '\t' || (space && ch >= 10 && ch <= 13)) && ch < 127;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
