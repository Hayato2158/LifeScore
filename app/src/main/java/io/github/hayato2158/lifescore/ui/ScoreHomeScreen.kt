package io.github.hayato2158.lifescore.ui

import android.R.attr.fontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.layout.width // Rowの均等配置に使うかも
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.Add // FAB削除のため不要
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
// import androidx.compose.material3.FloatingActionButton // FAB削除のため不要
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
// import androidx.compose.runtime.collectAsState // このファイルでは不要
// import androidx.compose.runtime.getValue // このファイルでは不要
// import androidx.compose.runtime.mutableStateOf // showDialog削除のため不要
// import androidx.compose.runtime.remember // showDialog削除のため不要
// import androidx.compose.runtime.setValue // showDialog削除のため不要
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// import androidx.compose.ui.window.Dialog // ScoreInputDialog削除のため不要
import io.github.hayato2158.lifescore.data.MonthlySummary
import io.github.hayato2158.lifescore.data.ScoreRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreHomeScreen(
    allScores: List<ScoreRecord>,
    currentMonthScores: List<ScoreRecord>,
    formattedYearMonth: String,
    monthlySummary: MonthlySummary?,
    onSave: (Int) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    // var showDialog by remember { mutableStateOf(false) } // ダイアログ関連削除

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("LifeScore") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
        // floatingActionButton = { // FAB削除
        //     FloatingActionButton(onClick = { showDialog = true }) {
        //         Icon(Icons.Filled.Add, contentDescription = "Add Score")
        //     }
        // }
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

            // スコア入力ボタンエリア
            ScoreInputButtons(
                onScoreSelected = { score -> onSave(score) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp) // 上下に少しパディング
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
                    items(currentMonthScores, key = { it.date }) { record -> // it.id から it.date にキーを変更 (ScoreRecordの主キーはdate)
                        ScoreRecordItem(record)
                    }
                }
            }

            // if (showDialog) { // ダイアログ関連削除
            //     ScoreInputDialog(
            //         onDismiss = { showDialog = false },
            //         onSave = { score ->
            //             onSave(score)
            //             showDialog = false
            //         }
            //     )
            // }
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
                    modifier = Modifier.padding(horizontal = 4.dp) // ボタン間のスペースを少し
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
                Icon(Icons.Filled.ArrowBack, contentDescription = "Previous Month")
            }
            Text(text = formattedYearMonth, style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = onNextMonth) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (summary != null) {
            Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) { // 中央揃え
                    Text("合計スコア: ${summary.totalScore}", style = MaterialTheme.typography.bodyLarge)
                    Text("平均スコア: ${"%.2f".format(summary.averageScore)}", style = MaterialTheme.typography.bodyLarge)
                    Text("記録日数: ${summary.recordCount}日", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) { // サマリがない場合もCardで囲む
                Text(
                    "この月のサマリデータはありません。",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp).fillMaxWidth(), // 中央揃えとパディング
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun ScoreRecordItem(record: ScoreRecord, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = record.date, style = MaterialTheme.typography.bodyLarge)
            Text(text = "${record.score} 点", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

// ScoreInputDialog は不要になったので削除
// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun ScoreInputDialog(...) { ... }

