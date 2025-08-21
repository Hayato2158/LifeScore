package io.github.hayato2158.lifescore.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // getValueのインポートを追加
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.hayato2158.lifescore.data.ScoreRecord

@Composable
fun ScoreHomeRoute(
    viewModel: ScoresViewModel,
) {
    // collectAsState()の戻り値を正しくデリゲートするためにgetValueをインポート
    val items by viewModel.items.collectAsState()
    ScoreHomeScreen(
        items = items,
        onSave = { viewModel.saveToday(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class) // TopAppBarなどの実験的なAPIを使用するために追加
@Composable
fun ScoreHomeScreen(
    items: List<ScoreRecord>,
    onSave: (Int) -> Unit // 末尾の余分なカンマを削除
) { // 関数の括弧を修正
    Scaffold(
        topBar = { TopAppBar(title = { Text("LifeScore") }) }
    ) { innerPadding -> // パラメータ名をinnerからinnerPaddingに変更（より分かりやすいため）
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // innerPaddingを使用
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("今日のスコアを記録", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { // spaceBy を spacedBy に修正
                (1..5).forEach { s ->
                    Button(onClick = { onSave(s) }) { Text("$s") }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("履歴(新規順)", style = MaterialTheme.typography.titleMedium)

            LazyColumn( // LazyColumnのインポートが必要
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items) { rec -> // items(items) のインポートが必要
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) { // ElevatedCardのインポートが必要
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(rec.date)
                            Text("★ ${rec.score}")
                        } // Rowの閉じ括弧が不足していたのを修正
                    } // ElevatedCardの閉じ括弧が不足していたのを修正
                } // itemsの閉じ括弧が不足していたのを修正
            } // LazyColumnの閉じ括弧が不足していたのを修正
        } // Columnの閉じ括弧が不足していたのを修正
    } // Scaffoldの閉じ括弧が不足していたのを修正
} // ScoreHomeScreenの閉じ括弧が不足していたのを修正