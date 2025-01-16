package org.company.app.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetLayout(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = androidx.compose.material3.rememberModalBottomSheetState(),
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sheetContentColor: Color = MaterialTheme.colorScheme.onSurface,
    dragHandleIconColor: Color = MaterialTheme.colorScheme.onSurface,
    scrimColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    tonalElevation: Dp = 0.dp,
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    content: @Composable () -> Unit
) {
    val showBottomSheet by remember {
        derivedStateOf {
            if (sheetState.targetValue != SheetValue.Hidden) true
            else sheetState.isVisible
        }
    }

    Box(Modifier.fillMaxSize()) {
        content()
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { },
                modifier = modifier,
                sheetState = sheetState,
                sheetMaxWidth = sheetMaxWidth,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                ),
                containerColor = sheetBackgroundColor,
                contentColor = sheetContentColor,
                tonalElevation = tonalElevation,
                scrimColor = scrimColor,
                dragHandle = { BottomSheetDefaults.DragHandle(color = dragHandleIconColor) },
                contentWindowInsets = contentWindowInsets,
                properties = properties,
                content = sheetContent,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleModalBottomSheetLayout(
    vararg multipleModalStates: MultipleModalState,
    modifier: Modifier = Modifier,
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sheetContentColor: Color = MaterialTheme.colorScheme.onBackground,
    dragHandleIconColor: Color = MaterialTheme.colorScheme.onBackground,
    scrimColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    tonalElevation: Dp = 0.dp,
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    content: @Composable () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        content()
        multipleModalStates.forEach { item ->
            ModalBottomSheetLayout(
                sheetContent = item.content,
                modifier = modifier,
                sheetState = item.sheetState,
                sheetBackgroundColor = sheetBackgroundColor,
                sheetContentColor = sheetContentColor,
                dragHandleIconColor = dragHandleIconColor,
                scrimColor = scrimColor,
                sheetMaxWidth = sheetMaxWidth,
                tonalElevation = tonalElevation,
                contentWindowInsets = contentWindowInsets,
                properties = properties,
                content = {},
            )
        }
    }
}

data class MultipleModalState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val sheetState: SheetState,
    val content: @Composable ColumnScope.() -> Unit
)