import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import org.company.app.Platform
import org.company.app.getPlatform
import kotlin.math.abs

@Composable
fun InteractiveGraph(
    modifier: Modifier = Modifier,
    timeData: List<Long>,
    yData: List<Double>,
    graphColor: Color = Color.Blue,
    selectedPointIndex: Int?,
    unselectedGraphColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
    convertXCallback: (Long) -> String,
    axeColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
    animationEnabled: Boolean = true,
    pointSize: Dp = 6.dp,
    onPointSelected: (Int?) -> Unit
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current


    val pointRadius = pointSize.toPx(density)
    require(timeData.size == yData.size) { "timeData and yData must have the same size" }

    // Animation progress from 0f to 1f
    val animationProgress = remember { Animatable(0f) }
    var previousClosestPointIndexFound by remember { mutableStateOf<Int?>(null) }
    val animationFinished by remember(animationProgress) {
        derivedStateOf {
            animationProgress.value == 1f
        }
    } // State to track animation completion

    val textMeasurer = TextMeasurer(LocalFontFamilyResolver.current, density, LayoutDirection.Ltr)

    // Launch the animation when the composable is displayed
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = ANIMATION_DURATION,
                easing = LinearOutSlowInEasing
            )
        )
    }

    Box(
        modifier = modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    // Wait for a pointer event
                    val event = awaitPointerEvent()
                    val position = event.changes.first().position

                    when (event.type) {
                        PointerEventType.Exit -> {
                            onPointSelected(null)
                        }

                        // unset selected point on release on mobiles
                        PointerEventType.Release -> {
                            when (getPlatform()) {
                                Platform.ANDROID,
                                Platform.IOS -> onPointSelected(null)

                                Platform.JVM,
                                Platform.JS -> {
                                    // nothing to do
                                }
                            }
                        }

                        PointerEventType.Press -> {
                            if (!animationEnabled || animationFinished) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                // Handle taps: Select the closest point
                                val closestPointIndexFound = findClosestPointIndex(
                                    position,
                                    timeData,
                                    yData,
                                    density,
                                    size.toSize()
                                )

                                previousClosestPointIndexFound = closestPointIndexFound

                                onPointSelected(
                                    closestPointIndexFound

                                )
                            }
                        }

                        PointerEventType.Move -> {
                            if (!animationEnabled || animationFinished) {
                                // Handle drags: Update to the closest point while dragging
                                val closestPointIndexFound = findClosestPointIndex(
                                    position,
                                    timeData,
                                    yData,
                                    density,
                                    size.toSize()
                                )
                                if (closestPointIndexFound != null
                                    && closestPointIndexFound != previousClosestPointIndexFound
                                ) {
                                    previousClosestPointIndexFound = closestPointIndexFound
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onPointSelected(closestPointIndexFound)
                            }
                        }

                        else -> {
                            // Consume the event
                            event.changes.forEach { it.consume() }
                        }
                    }
                }
            }
        }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val canvasWidth = size.width
            val canvasHeight = size.height

            val minTime = timeData.minOrNull() ?: 0L
            val maxTime = timeData.maxOrNull() ?: 1L
            val minY = yData.minOrNull() ?: 0.0
            val maxY = yData.maxOrNull() ?: 1.0

            val timeRange = (maxTime - minTime).toFloat().takeIf { it > 0 } ?: 1f
            val yRange = (maxY - minY).toFloat().takeIf { it > 0 } ?: 1f

            val points = timeData.zip(yData).map { (time, yValue) ->
                val x = ((time - minTime) / timeRange) * canvasWidth
                val y = canvasHeight - ((yValue - minY) / yRange) * canvasHeight
                Offset(x, y.toFloat())
            }

            // Draw the animated graph path
            val animatedPointsCount = (points.size * animationProgress.value).toInt()

            // Draw the graph path
            val path = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach { point ->
                        lineTo(point.x, point.y)
                    }
                }
            }

            val colorPath = Path().apply {
                if (!animationEnabled || animationFinished) {
                    val pointToDrawInColor =
                        selectedPointIndex?.let { points.subList(0, it + 1) } ?: points
                    if (pointToDrawInColor.isNotEmpty()) {
                        moveTo(pointToDrawInColor.first().x, pointToDrawInColor.first().y)
                        pointToDrawInColor.drop(1).forEach { point ->
                            lineTo(point.x, point.y)
                        }
                    }
                } else if (!animationFinished) {
                    if (animatedPointsCount > 1) {
                        moveTo(points.first().x, points.first().y)
                        points.take(animatedPointsCount).drop(1).forEach { point ->
                            lineTo(point.x, point.y)
                        }
                    }
                }
            }

            if (animationEnabled && animationFinished) {
                drawPath(
                    path = path,
                    color = unselectedGraphColor,
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            drawPath(
                path = colorPath,
                color = graphColor,
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )


            // Draw the selected point and its label
            selectedPointIndex?.let { index ->
                val point = points[index]

                // Draw the point
                drawCircle(
                    color = graphColor,
                    radius = pointRadius,
                    center = point
                )
            }


            // Dessiner les axes et les labels pour le point sélectionné
            selectedPointIndex?.let { index ->
                if (index in points.indices) {
                    val selectedPoint = points[index]
                    val selectedX = timeData[index]
                    val selectedY = yData[index]

                    // Dessiner le label pour l'axe X
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            textMeasurer,
                            convertXCallback(selectedX),
                            Offset(
                                selectedPoint.x,
                                10.dp.toPx()
                            ),
                            TextStyle(color = axeColor)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Find the closest point index based on the user's tap or drag position.
 */
private fun findClosestPointIndex(
    position: Offset,
    timeData: List<Long>,
    yData: List<Double>,
    density: Density,
    canvasSize: androidx.compose.ui.geometry.Size
): Int? {
    val canvasWidth = canvasSize.width
    val canvasHeight = canvasSize.height

    val minTime = timeData.minOrNull() ?: 0L
    val maxTime = timeData.maxOrNull() ?: 1L
    val minY = yData.minOrNull() ?: 0.0
    val maxY = yData.maxOrNull() ?: 1.0

    val timeRange = (maxTime - minTime).toFloat().takeIf { it > 0 } ?: 1f
    val yRange = (maxY - minY).toFloat().takeIf { it > 0 } ?: 1f

    val points = timeData.zip(yData).map { (time, yValue) ->
        val x = ((time - minTime) / timeRange) * canvasWidth
        val y = canvasHeight - ((yValue - minY) / yRange) * canvasHeight
        Offset(x, y.toFloat())
    }

    // Find the closest point to the current position
    return points.indexOfFirst { point ->
        abs(position.x - point.x) < 10.dp.toPx(density = density)
        //  && abs(position.y - point.y) < 10.dp.toPx(density)
    }.takeIf { it != -1 }
}

private const val ANIMATION_DURATION: Int = 2000 // Duration in milliseconds
private fun Dp.toPx(density: Density): Float = value * density.density
