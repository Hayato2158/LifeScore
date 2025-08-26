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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            Canvas(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val width = size.width
                val height = size.height

                // Axes
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, height),
                    end = Offset(width, height)
                )
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(0f, height)
                )

                val path = Path()
                parsed.forEachIndexed { index, (date, score) ->
                    val x = width * (date.dayOfMonth - 1) / (daysInMonth - 1).coerceAtLeast(1)
                    val y = height - ((score - 1) / 4f) * height
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
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}