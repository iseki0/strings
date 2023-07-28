package space.iseki.strings;

import java.io.InputStream;

public class Strings {
    public static PrintableSplitInputStream of(InputStream inputStream) {
        return new PrintableSplitInputStream(inputStream);
    }
}



