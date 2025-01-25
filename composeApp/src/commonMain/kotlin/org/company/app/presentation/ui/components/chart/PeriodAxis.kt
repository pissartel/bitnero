package org.company.app.presentation.ui.components.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.company.app.domain.model.Period
import org.company.app.theme.convertMillisToReadableDate
import org.company.app.theme.generateRangeWithDivisions
import org.company.app.theme.toLocalDateTime

@Composable
internal fun PeriodAxis(period: Period, start: Long, end: Long) {
    val time = when (period) {
        Period.ONE_HOUR -> {
            val times = generateRangeWithDivisions(
                start,
                end,
                8
            )
            times.mapIndexed { index, time ->
                val previousHour = times.getOrNull(index - 1)?.toLocalDateTime()?.hour
                val hour = time.toLocalDateTime().hour
                if (previousHour != null && hour != previousHour)
                    time.convertMillisToReadableDate(
                        "HHhmm"
                    ) else time.convertMillisToReadableDate("mm")
            }
        }

        Period.ONE_DAY -> generateRangeWithDivisions(
            start,
            end,
            8
        ).map { it.convertMillisToReadableDate("HH") + "h" }

        Period.ONE_WEEK -> generateRangeWithDivisions(
            start,
            end,
            7
        ).map {
            it.toLocalDateTime().dayOfWeek.toString().take(3)
        }

        Period.ONE_MONTH -> generateRangeWithDivisions(
            start,
            end,
            4
        ).map { it.convertMillisToReadableDate("dd") }

        Period.ONE_YEAR -> generateRangeWithDivisions(
            start,
            end,
            12
        ).map { it.convertMillisToReadableDate("MM") }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        time.forEach {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                ),
            )
        }
    }
}
