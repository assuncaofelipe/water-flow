package home.felipe.domain.util

import java.text.Normalizer
import java.util.Locale

object HeaderMatcher {

    fun buildMapping(features: List<String>, csvHeaders: List<String>): Map<String, String> {
        val out = mutableMapOf<String, String>()
        for (canonical in features) out[canonical] = matchCanonical(canonical, csvHeaders) ?: ""
        return out
    }

    private fun matchCanonical(canonical: String, csvHeaders: List<String>): String? {
        val c = tight(canonical)
        for (header in csvHeaders) {
            val raw = rawLower(header)
            val t = tight(header)

            val hit = when (c) {
                // Oxigênio dissolvido (mg/L) vs %saturação
                "do" -> (t.contains("dissolvedoxygen") && !raw.contains("%") && !raw.contains("saturation")) ||
                  (t.contains("oxigeniodissolvido") && !raw.contains("%") && !raw.contains("saturacao"))

                "dosat", "do_sat" -> (raw.contains("dissolved oxygen") && (raw.contains("%") || raw.contains(
                    "saturation"
                ))) ||
                  (raw.contains("oxigenio dissolvido") && (raw.contains("%") || raw.contains("saturacao")))

                // E. coli ~ coliformes fecais
                "ecoli", "e_coli" -> t.contains("ecoli") ||
                  t.contains("faecalcoliform") || t.contains("fecalcoliform") ||
                  t.contains("coliformesfecais")

                // Condutividade / Turbidez / pH
                "conductivity" -> t.contains("conductivity") || t.contains("condutividade")
                "turbidity" -> t.contains("turbidity") || t.contains("turbidez")
                "ph" -> t == "ph" || t.startsWith("ph")

                // Nutrientes e abreviações comuns
                "tkn" -> t.contains("totalkjeldahlnitrogen") || t == "tkn"
                "tp" -> t.contains("totalphosphorus") || t == "tp"
                "toc" -> t.contains("totalorganiccarbon") || t == "toc"
                "orthop" -> t.contains("orthophosphate") || t.contains("solublereactivephosphorus") || t.startsWith(
                    "orthop"
                )

                // Matéria orgânica
                "bod5" -> t.contains("biochemicaloxygendemand") || t.startsWith("bod")
                "cod" -> t.contains("chemicaloxygendemand") || t == "cod"

                // Sólidos
                "suspended_solids" -> t.contains("suspendedsolids") || t == "tss"

                // Óleos e graxas
                "oil_grease" -> raw.contains("oil") && raw.contains("grease")

                // Metais e afins (nome direto costuma existir no header)
                else -> (t == c) || t.startsWith(c) || t.contains(c) // fallback mais permissivo
            }

            if (hit) return header
        }
        return null
    }

    /** minúsculas + sem acento (mantém parênteses para detectar %) */
    private fun rawLower(s: String): String {
        var x = s.lowercase(Locale.ROOT)
        x = Normalizer.normalize(x, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        return x
    }

    /** remove parênteses/símbolos para comparação apertada */
    private fun tight(s: String): String {
        var x = rawLower(s)
        x = x.replace("μ", "u")
        x = x.replace("\\(.*?\\)".toRegex(), "")
        x = x.replace("[^a-z0-9%]+".toRegex(), "")
        return x
    }
}
