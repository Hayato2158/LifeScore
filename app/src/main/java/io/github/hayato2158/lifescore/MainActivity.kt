package io.github.hayato2158.lifescore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import io.github.hayato2158.lifescore.data.MonthlySummary // Preview用にMonthlySummaryをインポート
import io.github.hayato2158.lifescore.data.ScoreRecord
import io.github.hayato2158.lifescore.ui.ScoreHomeScreen
import io.github.hayato2158.lifescore.ui.ScoresViewModel
import io.github.hayato2158.lifescore.ui.theme.LifeScoreTheme

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

            LifeScoreTheme {
                Surface {
                    ScoreHomeScreen(
                        allScores = allScores,
                        currentMonthScores = currentMonthScores,
                        formattedYearMonth = formattedYearMonth,
                        monthlySummary = monthlySummary,
                        currentMemo = currentMemo,
                        onMemoChange = vm::updateMemo,
                        onSave = { score -> vm.saveToday(score) },
                        onPreviousMonth = { vm.changeMonth(-1) },
                        onNextMonth = { vm.changeMonth(1) }
                    )
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
        ScoreRecord("2025-08-18", 5),
        ScoreRecord("2025-08-17", 3),
        ScoreRecord("2025-08-16", 4),
        ScoreRecord("2025-08-15", 5),
        ScoreRecord("2025-08-14", 3),
        ScoreRecord("2025-08-13", 4),
        ScoreRecord("2025-08-12", 5),

    )
    // PreviewではcurrentMonthScoresもallScoresと同じで良いかもしれませんし、フィルタリングを模倣しても良いです。
    // ここでは当月のデータだけが含まれるようにしてみます。
    val fakeCurrentMonthScores = fakeAllScores.filter { it.date.startsWith("2025-08") }

    LifeScoreTheme {
        ScoreHomeScreen(
            allScores = fakeAllScores,
            currentMonthScores = fakeCurrentMonthScores,
            formattedYearMonth = "2025年08月",
            monthlySummary = MonthlySummary(totalScore = 8, averageScore = 4.0, recordCount = 2), // fakeCurrentMonthScoresに合わせる (5+3=8, count=2)
            currentMemo = "",
            onMemoChange = {},
            onSave = {},
            onPreviousMonth = {},
            onNextMonth = {}
        )
    }
}
