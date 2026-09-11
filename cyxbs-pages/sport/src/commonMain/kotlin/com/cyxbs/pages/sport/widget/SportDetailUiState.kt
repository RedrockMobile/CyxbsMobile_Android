package com.cyxbs.pages.sport.widget

import com.cyxbs.components.config.time.SchoolCalendar
import com.cyxbs.pages.sport.model.SportDetailBean
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * @Desc : 体育打卡详情页 UI 状态
 * @Author : xt
 */
sealed interface SportDetailUiState {
    data object Loading : SportDetailUiState
    data class Holiday(
        val summary: SummaryUi
    ) : SportDetailUiState

    data class Content(
        val summary: SummaryUi,
        val records: List<SportRecordUi>,
    ) : SportDetailUiState

    data class Empty(
        val summary: SummaryUi
    ) : SportDetailUiState

    data object Error : SportDetailUiState
}

// 供 Compose 列表展示的单条打卡记录
data class SportRecordUi(
    val date: String,
    val time: String,
    val spot: String,
    val type: String,
    val isAward: Boolean,
    val isValid: Boolean,
)

// 供 Compose 顶部统计区域展示的汇总数据
data class SummaryUi(
    val totalDone: String,
    val totalNeed: String,
    val runDone: String,
    val runNeed: String,
    val otherDone: String,
    val otherNeed: String,
    val award: String,
)

/**
 * 将网络层体育详情转换为页面可直接消费的 UI 状态
 * @return 根据学期、记录和请求结果生成的详情状态
 */
fun SportDetailBean.toDetailUiState(): SportDetailUiState {
    val summary = SummaryUi(
        totalDone = (runDone + otherDone).toString(),
        totalNeed = "/${runTotal + otherTotal}",
        runDone = runDone.toString(),
        runNeed = "/$runTotal",
        otherDone = otherDone.toString(),
        otherNeed = "/$otherTotal",
        award = award.toString(),
    )

    val records = item.orEmpty()
        .asReversed()
        .map { record ->
            SportRecordUi(
                date = record.date,
                time = record.time,
                spot = record.spot.normalizeSportSpot(),
                type = record.type,
                isAward = record.isAward,
                isValid = record.valid,
            )
        }

    val week = SchoolCalendar.getWeekOfTerm() ?: 22
    return if (week in 1..21) {
        if (records.isEmpty()) {
            SportDetailUiState.Empty(
                summary = summary,
            )
        } else {
            SportDetailUiState.Content(
                summary = summary,
                records = records,
            )
        }
    } else SportDetailUiState.Holiday(summary)
}

// 统一特殊场地名称，避免 UI 中出现冗余括号说明
private fun String.normalizeSportSpot() = when (this) {
    "风雨操场（羽毛球馆）",
    "风雨操场（乒乓球馆）" -> "风雨操场"

    else -> this
}

// 根据当前日期生成学期标题文本
fun currentTermText(): String {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val year = currentDate.year
    val month = currentDate.month.number
    val season = when (month) {
        1 -> "秋"
        in 2..7 -> "春"
        in 8..12 -> "秋"
        else -> ""
    }
    return "${year}年  $season"
}
