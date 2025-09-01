package home.felipe.water.pocket.analysis.shared

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import home.felipe.domain.vo.WaterRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedRecordsViewModel @Inject constructor(
) : ViewModel() {

    private val _recordsState: MutableStateFlow<List<WaterRecord>> = MutableStateFlow(emptyList())
    val recordsState: StateFlow<List<WaterRecord>> = _recordsState

    fun setImportedRecords(records: List<WaterRecord>) {
        _recordsState.value = records
    }
}