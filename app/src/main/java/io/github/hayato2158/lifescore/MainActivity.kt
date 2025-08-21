package io.github.hayato2158.lifescore

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import dagger.hilt.android.AndroidEntryPoint // Hiltのアノテーションをインポート
import io.github.hayato2158.lifescore.data.ScoreRecord
import io.github.hayato2158.lifescore.ui.ScoreHomeRoute
import io.github.hayato2158.lifescore.ui.ScoreHomeScreen
import io.github.hayato2158.lifescore.ui.ScoresViewModel
import io.github.hayato2158.lifescore.ui.theme.LifeScoreTheme
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint // Hiltが依存関係を注入できるようにするアノテーション
class MainActivity : ComponentActivity() {

    // Hiltを使用すると、ViewModelの取得方法も変わります。
    private val vm: ScoresViewModel by viewModels() // Hiltがファクトリを提供

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    ScoreHomeRoute(viewModel = vm)
                }
            }
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true, name = "ScoreHome Preview"
)
fun PreviewScoreHome() {
    val fake = listOf(
        ScoreRecord("2025-08-21", 5),
        ScoreRecord("2025-08-20", 3),
        ScoreRecord("2025-08-19", 4)
    )
    MaterialTheme {
        ScoreHomeScreen(items = fake, onSave = {})
    }
}
