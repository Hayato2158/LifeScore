package io.github.hayato2158.lifescore.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import io.github.hayato2158.lifescore.data.ScoreRecord
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.hayato2158.lifescore.R
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyScoreChartScreen(
    scores: List<ScoreRecord>,
    yearMonth: YearMonth,
    formattedYearMonth: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = formattedYearMonth) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                R.string.back_button_description
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (scores.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "この月のデータはありません。")
            }
        } else {
            val parsed = remember(scores) {
                scores.map { LocalDate.parse(it.date) to it.score }
                    .sortedBy { it.first }
            }
            val daysInMonth = yearMonth.lengthOfMonth().coerceAtLeast(1)

            val primaryColor = MaterialTheme.colorScheme.primary
            val axisColor = Color.Gray
            val labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            val density = LocalDensity.current

            val textPaint = remember {
                android.graphics.Paint().apply {
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                    color = android.graphics.Color.argb(
                        (labelColor.alpha * 255).roundToInt(),
                        (labelColor.red * 255).roundToInt(),
                        (labelColor.green * 255).roundToInt(),
                        (labelColor.blue * 255).roundToInt(),
                    )
                }
            }

            Canvas(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                val width = size.width
                val height = size.height

                val verticalPadding = with(density) { 20.dp.toPx() }
                val horizontalPadding = with(density) { 30.dp.toPx() }
                val tickLength = with(density) { 6.dp.toPx() }
                textPaint.textSize = with(density) { 9.dp.toPx() }

                val graphLeft = horizontalPadding
                val graphTop = verticalPadding
                val graphRight = width - horizontalPadding / 2
                val graphBottom = height - verticalPadding
                val graphWidth = graphRight - graphLeft
                val graphHeight = graphBottom - graphTop

                // Y軸の描画
                drawLine(
                    color = axisColor,
                    start = Offset(graphLeft, graphTop),
                    end = Offset(graphLeft, graphBottom)
                )
                //X軸の描画
                drawLine(
                    color = axisColor,
                    start = Offset(graphLeft, graphBottom),
                    end = Offset(graphRight, graphBottom)
                )

                //Y軸のラベル
                val minScore = 1
                val maxScore = 5
                for (scoreValue in minScore..maxScore) {

                    val yPos = graphBottom - ((scoreValue - minScore) / (maxScore - minScore).toFloat()) * graphHeight

                    drawLine(
                        color = axisColor,
                        start = Offset(graphLeft - tickLength, yPos),
                        end = Offset(graphLeft, yPos),
                        strokeWidth = 1.dp.toPx()
                    )
                    //ラベル
                    drawContext.canvas.nativeCanvas.drawText(
                        scoreValue.toString(),
                        graphLeft - tickLength - (textPaint.textSize / 1.5f),
                        yPos + (textPaint.textSize / 3f),
                        textPaint
                    )
                }

                //X軸
                val dayFormatter = DateTimeFormatter.ofPattern("d")
                if (parsed.isNotEmpty()) {

                    parsed.forEach { (date, _) ->
                        val dayRatio =
                            (date.dayOfMonth - 1).toFloat() / (daysInMonth - 1).coerceAtLeast(1)
                        val xPos = graphLeft + dayRatio * graphWidth

                        drawLine(
                            color = axisColor,
                            start = Offset(xPos, graphBottom),
                            end = Offset(xPos, graphBottom + tickLength),
                            strokeWidth = 1.dp.toPx()
                        )

                        drawContext.canvas.nativeCanvas.drawText(
                            date.format(dayFormatter),
                            xPos,
                            graphBottom + tickLength + textPaint.textSize,
                            textPaint
                        )
                    }

                    // Scoreデータをプロット
                    val path = Path()
                    if (parsed.isNotEmpty()) {
                        parsed.forEachIndexed { index, (date, score) ->
                            val dayRatio =
                                (date.dayOfMonth - 1).toFloat() / (daysInMonth - 1).coerceAtLeast(
                                    1
                                )
                            val x = graphLeft + dayRatio * graphWidth
                            val y =
                                graphBottom - (score - minScore) / (maxScore - minScore).toFloat() * graphHeight

                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                            drawCircle(
                                color = primaryColor,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                        drawPath(
                            path = path,
                            color = primaryColor,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, name = "MonthlyScoreChart (with data)")
@Composable
fun PreviewMonthlyScoreChart_WithData() {
    // サンプル月（2025年08月）
    val ym = YearMonth.of(2025, 8)
    val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    // 適当に散らしたダミーデータ（1..5）
    val scores = listOf(
        ScoreRecord(ym.atDay(1).format(fmt), 3, null),
        ScoreRecord(ym.atDay(2).format(fmt), 4, null),
        ScoreRecord(ym.atDay(3).format(fmt), 1, null),
        ScoreRecord(ym.atDay(4).format(fmt), 2, null),
        ScoreRecord(ym.atDay(5).format(fmt), 5, null),
        ScoreRecord(ym.atDay(6).format(fmt), 2, "少し疲れた"),
        ScoreRecord(ym.atDay(10).format(fmt), 5, "最高！"),
        ScoreRecord(ym.atDay(11).format(fmt), 5, "最高！"),
        ScoreRecord(ym.atDay(15).format(fmt), 1, "体調いまいち"),
        ScoreRecord(ym.atDay(20).format(fmt), 4, null),
        ScoreRecord(ym.atDay(21).format(fmt), 4, null),
        ScoreRecord(ym.atDay(22).format(fmt), 4, null),
        ScoreRecord(ym.atDay(23).format(fmt), 4, null),
        ScoreRecord(ym.atDay(24).format(fmt), 4, null),
        ScoreRecord(ym.atDay(28).format(fmt), 3, null),
    )

    // テーマ名はあなたのプロジェクトに合わせて（例: LifeScoreTheme）
    MaterialTheme {
        MonthlyScoreChartScreen(
            scores = scores,
            yearMonth = ym,
            formattedYearMonth = "2025年08月",
            onBack = {},
        )
    }
}
