# strings

`strings` implementation in Java.

```kotlin
import java.nio.file.Path
import java.security.MessageDigest
import java.util.HexFormat
import kotlin.io.path.inputStream

Path.of("/theFile").inputStream().buffered().use { input ->
    for (bytes in Strings.createByteArrayIterator(input)) {
        println(bytes.toString(Charsets.US_ASCII))
    }
}
Path.of("/theFile").inputStream().buffered().use { input ->
    for (hash in Strings.createHashIterator(MessageDigest.getInstance("MD5"), input)) {
        println(HexFormat.of().formatHex(hash)) // print MD5 for every string
    }
}
```
