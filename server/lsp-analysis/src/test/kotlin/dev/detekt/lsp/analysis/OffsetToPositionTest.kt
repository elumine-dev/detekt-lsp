package dev.detekt.lsp.analysis

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class OffsetToPositionTest {

    @Test
    fun `offset 0 maps to first line first column`() {
        val pos = "hello\nworld".offsetToPosition(0)
        pos.line shouldBe 0
        pos.character shouldBe 0
    }

    @Test
    fun `offset at newline character belongs to the line ending there`() {
        val pos = "hello\nworld".offsetToPosition(5)
        pos.line shouldBe 0
        pos.character shouldBe 5
    }

    @Test
    fun `offset just past newline starts the next line`() {
        val pos = "hello\nworld".offsetToPosition(6)
        pos.line shouldBe 1
        pos.character shouldBe 0
    }

    @Test
    fun `offset past end of string is clamped to the last position`() {
        val pos = "abc".offsetToPosition(100)
        pos.line shouldBe 0
        pos.character shouldBe 3
    }

    @Test
    fun `multiple newlines accumulate line count`() {
        val pos = "a\nb\nc\nd".offsetToPosition(6)
        pos.line shouldBe 3
        pos.character shouldBe 0
    }
}
