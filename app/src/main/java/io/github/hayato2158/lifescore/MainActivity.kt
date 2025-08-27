package io.github.hayato2158.lifescore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import io.github.hayato2158.lifescore.data.MonthlySummary // Preview用にMonthlySummaryをインポート
import io.github.hayato2158.lifescore.data.ScoreRecord
import io.github.hayato2158.lifescore.ui.MonthlyScoreChartScreen
import io.github.hayato2158.lifescore.ui.ScoreHomeScreen
import io.github.hayato2158.lifescore.ui.ScoresViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vm: ScoresViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // ViewModelから各Stateを収集
            val allScores by vm.allScores.collectAsState()
            val currentMonthScores by vm.currentMonthScores.collectAsState()
            val formattedYearMonth by vm.formattedYearMonth.collectAsState()
            val monthlySummary by vm.monthlySummary.collectAsState()
            val currentMemo by vm.currentMemo.collectAsState()
            val currentYearMonth by vm.currentYearMonth.collectAsState()


            MaterialTheme {
                Surface {
                    var showChart by rememberSaveable { mutableStateOf(false) }

                    if (showChart){
                        BackHandler(enabled = showChart) {
                            showChart = false
                        }
                        MonthlyScoreChartScreen(
                            scores = currentMonthScores,
                            yearMonth = currentYearMonth,
                            formattedYearMonth = formattedYearMonth,
                            onBack = { showChart = false }
                        )
                    }
                    else{
                        ScoreHomeScreen(
                            currentMonthScores = currentMonthScores,
                            formattedYearMonth = formattedYearMonth,
                            monthlySummary = monthlySummary,
                            onSave = { score, date -> vm.save(score,date) },
                            onPreviousMonth = { vm.changeMonth(-1) },
                            onNextMonth = { vm.changeMonth(1) },
                            onRecordMemoChange = vm::updateRecordMemo,
                            onShowChart = { showChart = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, name = "ScoreHome Preview")
fun PreviewScoreHome() {
    val fakeAllScores = listOf(
        ScoreRecord("2025-08-21", 5),
        ScoreRecord("2025-08-20", 3),
        ScoreRecord("2025-08-19", 4),
    )
    // PreviewではcurrentMonthScoresもallScoresと同じで良いかもしれませんし、フィルタリングを模倣しても良いです。
    // ここでは当月のデータだけが含まれるようにしてみます。
    val fakeCurrentMonthScores = fakeAllScores.filter { it.date.startsWith("2025-08") }

    MaterialTheme {
        ScoreHomeScreen(
            currentMonthScores = fakeCurrentMonthScores,
            formattedYearMonth = "2025年08月",
            monthlySummary = MonthlySummary(totalScore = 8, averageScore = 4.0, recordCount = 2),
            onSave = {_, _ ->},
            onPreviousMonth = {},
            onNextMonth = {},
            onRecordMemoChange = { _, _ -> },
            onShowChart = {}
        )
    }
}
