package io.github.hayato2158.lifescore

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.hayato2158.lifescore.data.ScoreRecord
import io.github.hayato2158.lifescore.ui.theme.LifeScoreTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val repo by lazy { (application as App).repository }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val itemsState = mutableStateOf<List<ScoreRecord>>(emptyList())
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repo.all().collect { list ->
                    Log.d("LifeScore", "items=${list.size} ${list}")
                    itemsState.value = list
                }
            }
        }

        setContent {
            MaterialTheme {
                Surface {
                    SmokeTestScreen(
                        items = itemsState.value,
                        onSave = { score ->
                            lifecycleScope.launch {
                                repo.saveToday(score)
                                Log.d("LifeScore", "saveToday($score) done")
                            }
                        }
                    )
                }
            }
        }
    }
}



@Composable
private fun SmokeTestScreen(
    items: List<ScoreRecord>,
    onSave: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("LifeScore", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // 1〜5を簡易ボタンで保存
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..5).forEach { s ->
                Button(onClick = { onSave(s) }) { Text("$s") }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Score Log (new → old)")

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items) { rec ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(rec.date)
                        Text("★ ${rec.score}")
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true, name = "SmokeTest Preview")
@Composable
fun PreviewSmokeTestScreen() {
    LifeScoreTheme {
        SmokeTestScreen(
            items = listOf(
                ScoreRecord(date = "2025-08-21", score = 5),
                ScoreRecord(date = "2025-08-20", score = 3),
                ScoreRecord(date = "2025-08-19", score = 4),
            ),
            onSave = {} // プレビューなので空でOK
        )
    }
}