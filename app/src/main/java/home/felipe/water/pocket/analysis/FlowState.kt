package home.felipe.water.pocket.analysis

import home.felipe.domain.vo.WaterRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlowState @Inject constructor() {

    var fileName: String? = null

    var records: List<WaterRecord> = emptyList()
    var dates: List<String?> = emptyList() // mapeado de records.map { it.date }

    var targetsAvailable: List<String> = emptyList()
    var selectedTargets: List<String> = emptyList()

    val headerMapsByTarget: MutableMap<String, Map<String, String>> = mutableMapOf()

    fun clearAll() {
        fileName = null
        records = emptyList()
        dates = emptyList()
        targetsAvailable = emptyList()
        selectedTargets = emptyList()
        headerMapsByTarget.clear()
    }
}