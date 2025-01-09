package org.company.app.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PeriodSelection(
    selectedPeriod: Period,
    modifier: Modifier = Modifier,
    onPeriodSelected: (Period) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Period.entries.forEach { period ->
            PeriodButton(
                period = period,
                isSelected = period == selectedPeriod,
                onClick = { onPeriodSelected(period) }
            )
        }
    }
}

@Composable
private fun PeriodButton(period: Period, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = period.title,
        style = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = if (isSelected) 1f else 0.5f
            )
        ),
        modifier = Modifier.clickable(onClick = onClick)
    )
}