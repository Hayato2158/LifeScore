package io.github.hayato2158.lifescore.data

data class MonthlySummary(
    val totalScore: Int,
    val averageScore: Double,
    val recordCount: Int // 平均計算の元になる記録数も入れておくと便利
)
