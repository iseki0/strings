package space.iseki.strings

import java.nio.channels.Channels
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ByteChannelSplitterTest {


    @Test
    fun test() {
        val data = this::class.java.classLoader.getResourceAsStream("test-data").use { it.readBytes() }
        val ch = Channels.newChannel(data.inputStream())
        val splitter = ByteChannelSplitter.of(ch, 100, 4, 8192)
        repeat(4) {
            splitter.printableBytes!!.decodeToString().also(::println)
        }
    }

    @Test
    fun test2() {
        val data = this::class.java.classLoader.getResourceAsStream("gradle-wrapper-strings")
            .use { it.reader().readLines() }
        Files.newByteChannel(Path.of("./gradle/wrapper/gradle-wrapper.jar"), StandardOpenOption.READ).use { chan ->
            val splitter = ByteChannelSplitter.of(chan)
            val list = buildList {
                while (true) {
                    add(splitter.printableBytes?.toString(StandardCharsets.ISO_8859_1) ?: break)
                }
            }
            assertContentEquals(data, list)
            println(list)
        }
    }
}