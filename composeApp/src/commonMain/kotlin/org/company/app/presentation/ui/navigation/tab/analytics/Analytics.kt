package org.company.app.presentation.ui.navigation.tab.analytics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.company.app.presentation.ui.screens.analytics.AnalyticScreen

object Analytics : Tab {
    @Composable
    override fun Content() {
       Navigator(AnalyticScreen())
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Analytics)
            val title by remember { mutableStateOf("Analytics") }
            val index: UShort = 1u
            return TabOptions(index, title, icon)
        }
}