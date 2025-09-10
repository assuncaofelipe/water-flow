package home.felipe.domain.vo

data class AnalysisSession(
    val id: String,
    val fileName: String,
    val records: List<WaterRecord>,
    val dates: List<String?>,
    val headersUnion: List<String> = emptyList(),
    val targetsAvailable: List<String> = emptyList(),
    val headerMapsByTarget: Map<String, Map<String, String>> = emptyMap()
)
