package space.iseki.strings

import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.security.MessageDigest
import java.util.HexFormat
import kotlin.io.path.inputStream

class StringsTest {

    @Test
    fun test1() {
        Path.of("./gradle/wrapper/gradle-wrapper.jar").inputStream().use {
            Strings.createHashIterator(MessageDigest.getInstance("MD5"), it)
                .forEach { println(HexFormat.of().formatHex(it)) }
        }
    }

    @Test
    fun test2() {
        Path.of("./gradle/wrapper/gradle-wrapper.jar").inputStream().use {
            Strings.createByteArrayIterator(it).forEach { println(it.toString(StandardCharsets.ISO_8859_1)) }
        }
    }
}