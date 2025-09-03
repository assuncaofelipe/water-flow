package home.felipe.water.pocket.analysis.ui.shared

import home.felipe.domain.vo.PredictionResult
import home.felipe.water.pocket.analysis.ui.models.PredictionRow
import home.felipe.water.pocket.analysis.ui.components.QualityStatus
import home.felipe.water.pocket.analysis.ui.models.ResultData

fun PredictionResult.toResultsAllCard(
    dates: List<String?> = emptyList()
): ResultData {
    val unit = unitFor(target)
    val pts = predictions.mapIndexed { i, v -> i to v }
    val tbl = predictions.mapIndexed { i, v ->
        PredictionRow(
            date = dates.getOrNull(i),
            station = null,
            value = v
        )
    }
    val q = qualityFor(target, stats.mean)

    return ResultData(
        target = target,
        mean = stats.mean,
        min = stats.min,
        max = stats.max,
        unit = unit,
        points = pts,
        table = tbl,
        quality = q
    )
}

// Helpers podem continuar privados ao arquivo
private fun unitFor(target: String): String? = when (target.lowercase()) {
    "do" -> "mg/L"
    "do_sat" -> "% sat"
    "ph" -> null
    "turbidity" -> "NTU"
    "conductivity" -> "µS/cm"
    "e_coli", "e-coli" -> "counts/100mL"
    else -> null
}

private fun qualityFor(target: String, mean: Float): QualityStatus = when (target.lowercase()) {
    "do" -> when {
        mean >= 7.0f -> QualityStatus.OK
        mean >= 5.0f -> QualityStatus.WARNING
        else -> QualityStatus.CRITICAL
    }

    "ph" -> when {
        mean in 6.5f..8.5f -> QualityStatus.OK
        mean in 6.0f..9.0f -> QualityStatus.WARNING
        else -> QualityStatus.CRITICAL
    }

    "turbidity" -> when {
        mean <= 5f -> QualityStatus.OK
        mean <= 50f -> QualityStatus.WARNING
        else -> QualityStatus.CRITICAL
    }

    else -> QualityStatus.OK
}