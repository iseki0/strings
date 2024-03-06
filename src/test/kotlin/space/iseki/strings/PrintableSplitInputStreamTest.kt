package space.iseki.strings

import java.nio.charset.StandardCharsets
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class PrintableSplitInputStreamTest {
    @Test
    fun test() {
        this::class.java.classLoader.getResourceAsStream("test-data").use { it.readBytes() }.inputStream()
            .let(::PrintableSplitInputStream).use {
                while (it.next()) {
                    val s = it.readBytes().toString(StandardCharsets.ISO_8859_1)
                    if (s.isEmpty()) break
                    println(">>>> $s")
                }
            }
    }

    @Test
    fun test2() {
        // strings gradle/wrapper/gradle-wrapper.jar > src/test/resources/gradle-wrapper-strings
        val data =
            this::class.java.classLoader.getResourceAsStream("gradle-wrapper-strings").use { it.reader().readLines() }
        Path.of("./gradle/wrapper/gradle-wrapper.jar").inputStream().buffered().let(::PrintableSplitInputStream)
            .use { splitter ->
                val list = buildList {
                    while (splitter.next()) {
                        val s = splitter.readBytes().toString(StandardCharsets.ISO_8859_1)
                        if (s.isEmpty()) break
                        add(s)
                    }
                }
                assertContentEquals(data, list)
            }
    }

    @Test
    fun test3() {
        // strings -wn 8 gradle/wrapper/gradle-wrapper.jar > src/test/resources/gradle-wrapper-strings-8s
        val data =
            this::class.java.classLoader.getResourceAsStream("gradle-wrapper-strings-8s").use { it.reader().readText() }
        val r = Path.of("./gradle/wrapper/gradle-wrapper.jar").inputStream().use { input ->
            val splitter = PrintableSplitInputStream(input.buffered(), 8, true)
            buildList {
                while (splitter.next()) {
                    val s = splitter.readBytes().toString(StandardCharsets.ISO_8859_1)
                    if (s.isEmpty()) break
                    add(s)
                }
            }.joinToString("\n", postfix = "\n")
        }
        assertEquals(data, r)
    }

    @Test
    fun test4() {
        val r = Path.of("./gradle/wrapper/gradle-wrapper.jar").inputStream().use { input ->
            val splitter = PrintableSplitInputStream(input.buffered(), 8, true)
            buildList {
                while (splitter.next()) {
                    val s = splitter.readBytes().toString(StandardCharsets.ISO_8859_1)
                    if (s.isEmpty()) break
                    add(s)
                }
            }.joinToString("\n", postfix = "\n")
        }
        val t = Strings.of(
            Path.of("./gradle/wrapper/gradle-wrapper.jar").inputStream(),
            byteArrayOf('\n'.code.toByte()),
            Strings.Option(8, true)
        ).use { it.reader().readText() }
        assertEquals(r, t)
        println(t)
    }

}
