package common.test

import java.io.ByteArrayInputStream
import java.io.OutputStream
import java.io.PrintStream

fun remapStdIn(input: String, output: OutputStream) {
    System.setIn(ByteArrayInputStream(input.toByteArray()))
    System.setOut(PrintStream(output))
}
