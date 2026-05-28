package dev.detekt.lsp.document

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.com.intellij.psi.PsiErrorElement
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KotlinParserTest {

    private val parser = KotlinParser()

    @AfterAll
    fun closeParser() {
        parser.close()
    }

    @Test
    fun `parses a valid Kotlin file with zero PsiErrorElements`() {
        val ktFile = parser.parse("Foo.kt", "fun foo() = 42\n")
        val errors = PsiTreeUtil.findChildrenOfType(ktFile, PsiErrorElement::class.java)
        errors.shouldBeEmpty()
    }

    @Test
    fun `parses a broken Kotlin file and surfaces at least one PsiErrorElement`() {
        val ktFile = parser.parse("Broken.kt", "fun foo() {\n")
        val errors = PsiTreeUtil.findChildrenOfType(ktFile, PsiErrorElement::class.java)
        errors.isNotEmpty() shouldBe true
    }
}
