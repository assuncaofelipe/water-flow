package home.felipe.domain.util

import java.util.Locale

object HeaderMatcher {

    /** Normalização pública para reutilizar nas VMs. */
    fun normalizeHeader(s: String): String {
        var x = s.lowercase(Locale.ROOT)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        x = x.replace("μ", "u").replace("µ", "u")
        x = x.replace("(", "").replace(")", "")
        x = x.replace("-", "")
        x = x.replace("[^a-z0-9%.]".toRegex(), "")
        return x
    }

    /** Constrói um mapa canônico -> header CSV usando heurísticas. */
    fun buildMapping(features: List<String>, csvHeaders: List<String>): Map<String, String> {
        val out = mutableMapOf<String, String>()
        for (canon in features) out[canon] = matchSingle(canon, csvHeaders) ?: ""
        return out
    }

    /** Tenta casar um único canônico com a melhor coluna do CSV. */
    fun matchSingle(canonical: String, csvHeaders: List<String>): String? {
        val c = normalizeHeader(canonical)
        val predictions = predicatesFor(c)

        for (h in csvHeaders) {
            val n = normalizeHeader(h)
            if (predictions.any { it(n) } || n == c) return h
        }
        for (h in csvHeaders) {
            val n = normalizeHeader(h)
            if (n.startsWith(c) || c.startsWith(n)) return h
        }
        return null
    }

    private fun predicatesFor(c: String): List<(String) -> Boolean> = when (c) {
        "do" -> listOf { h -> h.contains("dissolvedoxygen") && !h.contains("%") }
        "dosat", "dosat", "do_sat" -> listOf { h ->
            h.contains("dissolvedoxygen") && (h.contains("%") || h.contains(
                "saturation"
            ))
        }

        "ecoli", "e_coli" -> listOf { h ->
            h.contains("e.coli") || h.contains("ecoli") || h.contains(
                "faecalcoliform"
            ) || h.contains("fecalcoliform")
        }

        "conductivity" -> listOf { h -> h.contains("conductivity") || h.contains("condutividade") }
        "turbidity" -> listOf { h -> h.contains("turbidity") || h.contains("turbidez") }
        "ph" -> listOf { h -> h == "ph" || h.startsWith("ph") }

        "bod5" -> listOf { h ->
            h.contains("5daybiochemicaloxygendemand") || h.contains("biochemicaloxygendemand") || h.contains(
                "bod"
            )
        }

        "ammonia_n" -> listOf { h ->
            h.contains("ammonianitrogen") || h.contains("ammonianitrogen") || h.contains(
                "amonia"
            )
        }

        "nitrate_n" -> listOf { h -> h.contains("nitratenitrogen") }
        "nitrite_n" -> listOf { h -> h.contains("nitritenitrogen") }
        "orthop", "orthophosphate" -> listOf { h -> h.contains("orthophosphate") }
        "silica" -> listOf { h -> h.contains("silica") }
        "suspended_solids" -> listOf { h -> h.contains("suspendedsolids") }
        "tkn" -> listOf { h -> h.contains("totalkjeldahlnitrogen") }
        "toc" -> listOf { h -> h.contains("totalorganiccarbon") }
        "tp" -> listOf { h -> h.contains("totalphosphorus") }
        "total_solids" -> listOf { h -> h.contains("totalsolids") }
        "tvs" -> listOf { h -> h.contains("totalvolatilesolids") }
        "water_temp", "watertemp" -> listOf { h -> h.contains("watertemperature") || h.contains("temperaturewater") }
        "flow" -> listOf { h -> h.startsWith("flow") }
        "cod" -> listOf { h -> h.contains("chemicaloxygendemand") || h.contains("cod") }
        "oil_grease" -> listOf { h -> h.contains("oilandgrease") }
        // metais (genéricos)
        "copper" -> listOf { h -> h.startsWith("copper") }
        "iron" -> listOf { h -> h.startsWith("iron") }
        "lead" -> listOf { h -> h.startsWith("lead") }
        "manganese" -> listOf { h -> h.startsWith("manganese") }
        "mercury" -> listOf { h -> h.startsWith("mercury") }
        "chromium" -> listOf { h -> h.startsWith("chromium") }
        "aluminium" -> listOf { h -> h.startsWith("aluminium") || h.startsWith("aluminum") }
        "arsenic" -> listOf { h -> h.startsWith("arsenic") }
        "boron" -> listOf { h -> h.startsWith("boron") }
        "cadmium" -> listOf { h -> h.startsWith("cadmium") }
        "cyanide" -> listOf { h -> h.startsWith("cyanide") }
        "fluoride" -> listOf { h -> h.startsWith("fluoride") }
        else -> emptyList()
    }
}