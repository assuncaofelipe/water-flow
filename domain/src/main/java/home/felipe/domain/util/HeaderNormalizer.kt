package home.felipe.domain.util

import java.text.Normalizer
import kotlin.text.iterator

object HeaderNormalizer {
    fun norm(input: String): String {
        val nfkd = Normalizer.normalize(input, Normalizer.Form.NFKD)
        val ascii = buildString {
            for (ch in nfkd) {
                if (ch.isLetterOrDigit()) append(ch.lowercaseChar()) else append(' ')
            }
        }
        return ascii.trim().replace(Regex("\\s+"), "_")
    }

    fun normSpaces(input: String): String {
        val nfkd = Normalizer.normalize(input, Normalizer.Form.NFKD)
        val ascii = buildString {
            for (ch in nfkd) {
                if (ch.isLetterOrDigit() || ch == '.' || ch == ' ') append(ch.lowercaseChar())
                else append(' ')
            }
        }
        return ascii.trim().replace(Regex("\\s+"), " ")
    }
}
