package io.github.hayato2158.lifescore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.hayato2158.lifescore.R
import io.github.hayato2158.lifescore.data.MonthlySummary
import io.github.hayato2158.lifescore.data.ScoreRecord
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreHomeScreen(
    currentMonthScores: List<ScoreRecord>,
    formattedYearMonth: String,
    monthlySummary: MonthlySummary?,
    onSave: (Int, LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
    onRecordMemoChange: (ScoreRecord, String) -> Unit,
    onShowChart: () -> Unit
) {
    var editingRecord by remember { mutableStateOf<ScoreRecord?>(null) }
    var editingMemo by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("LifeScore") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = onShowChart) {
                        Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Show Chart")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp) // 左右のパディングをColumnに適用
                .fillMaxSize() // Columnが全画面を埋めるように
        ) {
            // 月選択とサマリ表示エリア
            MonthNavigationAndSummary(
                formattedYearMonth = formattedYearMonth,
                summary = monthlySummary,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp) // 上下に少しパディング
            )

            // 日付選択とスコア入力ボタンエリア
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("日付: ${selectedDate.format(dateFormatter)}")
                Button(onClick = { showDatePicker = true }) { Text("日付を選択") }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            }
                            showDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("キャンセル") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            ScoreInputButtons(
                onScoreSelected = { score -> onSave(score, selectedDate) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))


            // スコアリスト (現在の月のスコアのみ表示)
            if (currentMonthScores.isEmpty()) {
                Text(
                    text = "この月の記録はありません。",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f) // 残りのスペースを埋めるように
                ) {
                    items(currentMonthScores, key = { it.date }) { record ->
                        ScoreRecordItem(record = record, onClick = {
                            editingRecord = record
                            editingMemo = record.memo.orEmpty()
                        })
                    }
                }
            }
            if (editingRecord != null) {
                AlertDialog(
                    onDismissRequest = { editingRecord = null },
                    title = { Text("メモ編集") },
                    text = {
                        OutlinedTextField(
                            value = editingMemo,
                            onValueChange = { editingMemo = it },
                            label = { Text("メモ") }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            editingRecord?.let { onRecordMemoChange(it, editingMemo) }
                            editingRecord = null
                        }) { Text("保存") }
                    },
                    dismissButton = {
                        TextButton(onClick = { editingRecord = null }) { Text("キャンセル") }
                    }
                )
            }
        }
    }
}

@Composable
fun ScoreInputButtons(
    onScoreSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("今日の気分は？", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly // ボタンを均等に配置
        ) {
            (1..5).forEach { score ->
                Button(
                    onClick = { onScoreSelected(score) },
                    modifier = Modifier.padding(horizontal = 2.dp) // ボタン間のスペースを少し
                ) {
                    Text(text = "$score")
                }
            }
        }
    }
}


@Composable
fun MonthNavigationAndSummary(
    formattedYearMonth: String,
    summary: MonthlySummary?,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.previous_month_button_description))
            }
            Text(text = formattedYearMonth, style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = onNextMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.next_month_button_description))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (summary != null) {
            Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally)
                { // 中央揃え
                    Text("合計スコア:  ${summary.totalScore}", style = MaterialTheme.typography.bodyLarge)
                    Text("平均スコア: ${String.format(Locale.getDefault(), "%.2f",summary.averageScore)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text("記録日数: ${summary.recordCount}日", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) { // サマリがない場合もCardで囲む
                Text(
                    "この月のデータはありません。",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp).fillMaxWidth(), // 中央揃えとパディング
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun ScoreRecordItem(record: ScoreRecord, onClick: (ScoreRecord) -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth().clickable{ onClick(record) },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = record.date, style = MaterialTheme.typography.bodyLarge)
                Text(text = "${record.score}", style = MaterialTheme.typography.headlineSmall)
            }
            if (!record.memo.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = record.memo ?: "", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
