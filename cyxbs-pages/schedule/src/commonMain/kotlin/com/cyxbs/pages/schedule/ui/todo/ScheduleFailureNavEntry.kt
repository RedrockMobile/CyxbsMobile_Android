package com.cyxbs.pages.schedule.ui.todo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyxbs.components.config.compose.theme.LocalAppColors
import com.cyxbs.components.config.res.ConfigRes
import com.cyxbs.components.config.sp.accountSettings
import com.cyxbs.components.navigation.AppNav
import com.cyxbs.components.navigation.AppNavArgument
import com.cyxbs.components.navigation.AppNavEntry
import com.cyxbs.components.utils.compose.clickableNoIndicator
import com.cyxbs.components.utils.extensions.toast
import com.cyxbs.components.view.ui.Window
import com.cyxbs.pages.schedule.data.failure.ScheduleFailureOperation
import com.cyxbs.pages.schedule.data.failure.ScheduleFailureRecord
import com.cyxbs.pages.schedule.data.failure.ScheduleFailureRecords
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleInput
import com.cyxbs.pages.schedule.data.remote.v3.TimingKind
import com.cyxbs.pages.schedule.data.repository.v3.ScheduleV2SnapshotProjector
import com.cyxbs.pages.schedule.ui.edit.EditScheduleDialog
import com.cyxbs.pages.schedule.ui.edit.EditScope
import com.cyxbs.pages.schedule.viewmodel.ScheduleMainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Instant

/** 清单页的确定性同步失败记录入口。 */
@Serializable
object ScheduleFailureNavArgument : AppNavArgument

/** 失败记录独立页面；只读列表与编辑弹窗都复用当前账号的 Schedule Repository。 */
@AppNav(route = "schedule/failures")
class ScheduleFailureNavEntry : AppNavEntry<ScheduleFailureNavArgument>() {
  override fun isNeedLogin(argument: ScheduleFailureNavArgument): Boolean = true

  @Composable
  override fun Content(argument: ScheduleFailureNavArgument) {
    val viewModel = viewModel { ScheduleMainViewModel() }
    ScheduleFailurePage(
      viewModel = viewModel,
      onBack = argument::popBackStack,
    )
  }
}

/**
 * 展示当前账号所有可修复失败。
 *
 * 点击记录只打开已有 [EditScheduleDialog]；提交仍走正常 local-first Repository，成功响应会由数据层移除记录。
 */
@Composable
private fun ScheduleFailurePage(
  viewModel: ScheduleMainViewModel,
  onBack: () -> Unit,
) {
  val colors = LocalAppColors.current
  val accountId = accountSettings.stuNum
  val recordsFlow = remember(accountId) {
    accountId?.let(ScheduleFailureRecords::observe) ?: MutableStateFlow(emptyList())
  }
  val records by recordsFlow.collectAsState()
  val timeZone = remember { TimeZone.currentSystemDefault() }
  val projector = remember { ScheduleV2SnapshotProjector() }
  var editingRecord by remember { mutableStateOf<ScheduleFailureRecord?>(null) }
  val editableSchedule = remember(editingRecord, timeZone) {
    editingRecord?.let { projector.projectFailureSource(it.sourceSchedule, timeZone) }
  }

  LaunchedEffect(Unit) {
    viewModel.initialize()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(colors.bottomBg)
      .navigationBarsPadding()
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      FailurePageHeader(onBack)
      if (records.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text(
            text = "暂无失败记录",
            color = colors.tvLv2,
            fontSize = 15.sp,
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          item {
            Text(
              text = "以下数据仍保留在本地。修改并成功同步后，对应记录会自动移除。",
              color = colors.tvLv2,
              fontSize = 13.sp,
              lineHeight = 20.sp,
            )
          }
          items(records, key = ScheduleFailureRecord::scheduleId) { record ->
            FailureRecordCard(
              record = record,
              timeZone = timeZone,
              onClick = {
                val projected = projector.projectFailureSource(record.sourceSchedule, timeZone)
                if (projected == null) toast("该记录暂时无法打开，请保留记录并反馈")
                else editingRecord = record
              },
            )
          }
        }
      }
    }
  }

  editingRecord?.let { record ->
    val schedule = editableSchedule
    if (schedule != null) {
      Window(dismissOnBackPress = null) {
        Box(modifier = Modifier.fillMaxSize()) {
          EditScheduleDialog(
            show = true,
            editSchedule = schedule,
            categoryRepository = viewModel.repository,
            showCourseRelation = true,
            onDismiss = { editingRecord = null },
            onConfirm = { state, _, newCategory ->
              viewModel.saveSchedule(
                state = state,
                scope = EditScope.ALL,
                recurrenceId = null,
                newCategory = newCategory,
              )
              editingRecord = null
            },
            onDelete = {
              viewModel.deleteScheduleScoped(schedule.id, EditScope.ALL, null)
              editingRecord = null
            },
            onToggleCompleted = { completed ->
              viewModel.completeSchedule(schedule.id, null, completed)
            },
          )
        }
      }
    } else {
      LaunchedEffect(record.scheduleId) {
        toast("该记录暂时无法打开，请保留记录并反馈")
        editingRecord = null
      }
    }
  }
}

/** 与清单页保持相同安全区、返回图标和分隔线，只把标题改为失败记录。 */
@Composable
private fun FailurePageHeader(onBack: () -> Unit) {
  val colors = LocalAppColors.current
  Surface(
    color = colors.bottomBg,
    modifier = Modifier.statusBarsPadding(),
  ) {
    Column(modifier = Modifier.padding(top = 13.dp)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(37.dp)
          .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
          modifier = Modifier
            .width(22.dp)
            .height(37.dp)
            .clickableNoIndicator(onClick = onBack),
          contentAlignment = Alignment.CenterStart,
        ) {
          Icon(
            painter = painterResource(ConfigRes.configIcBack()),
            contentDescription = "返回",
            tint = colors.tvLv1,
            modifier = Modifier.width(9.dp).height(19.dp),
          )
        }
        Text(
          text = "失败记录",
          color = colors.tvLv1,
          fontSize = 21.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.weight(1f),
        )
      }
      Surface(
        color = colors.tvLv4.copy(alpha = 0.1f),
        modifier = Modifier.fillMaxWidth().height(1.dp),
      ) {}
    }
  }
}

/** 单条失败记录摘要；完整源批次只用于编辑与诊断，不直接铺在页面上。 */
@Composable
private fun FailureRecordCard(
  record: ScheduleFailureRecord,
  timeZone: TimeZone,
  onClick: () -> Unit,
) {
  val colors = LocalAppColors.current
  Surface(
    color = colors.middleBg,
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, colors.tvLv4.copy(alpha = 0.12f)),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = record.sourceSchedule.title.data.ifBlank { "未命名日程" },
          color = colors.tvLv1,
          fontSize = 16.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f),
        )
        Text(
          text = record.operation.label(),
          color = MaterialTheme.colors.error,
          fontSize = 12.sp,
        )
      }
      Text(
        text = record.sourceSchedule.timeSummary(timeZone),
        color = colors.tvLv2,
        fontSize = 13.sp,
      )
      Text(
        text = record.message.ifBlank { record.reasonCode },
        color = colors.tvLv2,
        fontSize = 13.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = "失败于 ${formatFailureTime(record.failedAt, timeZone)} · ${record.reasonCode}",
        color = colors.tvLv3,
        fontSize = 12.sp,
      )
    }
  }
}

private fun ScheduleFailureOperation.label(): String = when (this) {
  ScheduleFailureOperation.CREATE -> "新增失败"
  ScheduleFailureOperation.UPDATE -> "修改失败"
  ScheduleFailureOperation.DELETE -> "删除失败"
  ScheduleFailureOperation.SYNC -> "同步失败"
}

/** 使用失败请求中的绝对毫秒生成简短时间摘要，不依赖当前 Room 是否仍有该资源。 */
private fun ScheduleInput.timeSummary(timeZone: TimeZone): String = when (timing.data.kind) {
  TimingKind.TIMED -> {
    val start = timing.data.startAt?.let { formatFailureTime(it, timeZone) } ?: "?"
    val end = timing.data.endAt?.let { formatFailureTime(it, timeZone) } ?: "?"
    "$start — $end"
  }

  TimingKind.DEADLINE -> timing.data.dueAt?.let { formatFailureTime(it, timeZone) } ?: "时间点未知"
  TimingKind.ALL_DAY -> {
    val start = timing.data.startAt?.let { formatFailureTime(it, TimeZone.UTC) } ?: "?"
    "$start 全天"
  }

  TimingKind.UNSCHEDULED -> "未设置时间"
}

private fun formatFailureTime(epochMillis: Long, timeZone: TimeZone): String {
  val value = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(timeZone)
  return "${value.year}-${value.month.number.pad2()}-${value.day.pad2()} " +
      "${value.hour.pad2()}:${value.minute.pad2()}"
}

private fun Int.pad2(): String = toString().padStart(2, '0')
