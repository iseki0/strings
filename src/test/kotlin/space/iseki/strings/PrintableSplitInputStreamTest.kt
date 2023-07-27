package space.iseki.strings

import java.nio.charset.StandardCharsets
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals

class PrintableSplitInputStreamTest {
    @Test
    fun test() {
        this::class.java.classLoader.getResourceAsStream("test-data").use { it.readBytes() }.inputStream()
            .let(::PrintableSplitInputStream).use {
            while (true) {
                val s = it.readBytes().toString(StandardCharsets.ISO_8859_1)
                if (s.isEmpty()) break
                println(">>>> $s")
                it.next()
            }
        }
    }

    @Test
    fun test2() {
        val data = this::class.java.classLoader.getResourceAsStream("gradle-wrapper-strings")
            .use { it.reader().readLines() }
        Path.of("./gradle/wrapper/gradle-wrapper.jar").inputStream()
            .let(::PrintableSplitInputStream).use { splitter ->
                val list = buildList {
                    while (true) {
                        val s = splitter.readBytes().toString(StandardCharsets.ISO_8859_1)
                        if (s.isEmpty()) break
                        add(s)
                        splitter.next()
                    }
                }
                assertContentEquals(data, list)
            }
    }
}
