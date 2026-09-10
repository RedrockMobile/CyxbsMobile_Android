package com.cyxbs.pages.sport.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyxbs.components.config.compose.theme.LocalAppColors
import com.cyxbs.components.config.res.ConfigRes
import com.cyxbs.components.navigation.AppNav
import com.cyxbs.components.navigation.AppNavEntry
import com.cyxbs.components.navigation.NAV_SPORT
import com.cyxbs.components.utils.compose.clickableNoIndicator
import com.cyxbs.components.utils.compose.dark
import com.cyxbs.components.utils.compose.getWindowScreenSize
import com.cyxbs.pages.sport.api.SportNavArgument
import com.cyxbs.pages.sport.viewModel.SportViewModel
import com.cyxbs.pages.sport.widget.RefreshHeader
import com.cyxbs.pages.sport.widget.RefreshNestedScrollConnection
import com.cyxbs.pages.sport.widget.RefreshState
import com.cyxbs.pages.sport.widget.SportDetailUiState
import com.cyxbs.pages.sport.widget.SportRecordUi
import com.cyxbs.pages.sport.widget.currentTermText
import cyxbsmobile.cyxbs_pages.sport.generated.resources.Res
import cyxbsmobile.cyxbs_pages.sport.generated.resources.sport_ic_award
import cyxbsmobile.cyxbs_pages.sport.generated.resources.sport_ic_holiday
import cyxbsmobile.cyxbs_pages.sport.generated.resources.sport_ic_no_data
import cyxbsmobile.cyxbs_pages.sport.generated.resources.sport_ic_not_valid
import cyxbsmobile.cyxbs_pages.sport.generated.resources.sport_ic_other
import cyxbsmobile.cyxbs_pages.sport.generated.resources.sport_ic_run
import cyxbsmobile.cyxbs_pages.sport.generated.resources.sport_ic_shoes
import cyxbsmobile.cyxbs_pages.sport.generated.resources.sport_ic_spot
import cyxbsmobile.cyxbs_pages.sport.generated.resources.sport_ic_time
import cyxbsmobile.cyxbs_pages.sport.generated.resources.sport_ic_valid
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@AppNav(route = NAV_SPORT)
class SportNavEntry : AppNavEntry<SportNavArgument>() {

    override fun isNeedLogin(argument: SportNavArgument): Boolean {
        return true
    }

    @Composable
    override fun Content(argument: SportNavArgument) {
        viewModel { SportViewModel() }
        SportPage(argument)
    }
}

@Composable
fun SportPage(argument: SportNavArgument) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
            .background(LocalAppColors.current.bottomBg)
            .systemBarsPadding(),
        constraintSet = createConstraintSet()
    ) {
        val viewModel: SportViewModel = viewModel()
        val state = viewModel.uiState.collectAsStateWithLifecycle().value
        TopBarCompose(modifier = Modifier.layoutId(SportElement.TopBar), argument)
        DetailTotalTitle(modifier = Modifier.layoutId(SportElement.DetailTotalTitle))
        DetailTotal(modifier = Modifier.layoutId(SportElement.DetailTotal))
        SportImage(modifier = Modifier.layoutId(SportElement.SportImage))
        SportDetailRun(modifier = Modifier.layoutId(SportElement.SportDetailRun))
        SportRecord(
            modifier = Modifier.layoutId(SportElement.SportRecord),
            state = state
        )
    }
}

@Composable
private fun createConstraintSet(): ConstraintSet {
    val windowSize = getWindowScreenSize()
    return ConstraintSet {
        SportConstraintSet(
            scope = this,
            windowSize = windowSize
        ).createConstrain()
    }
}

@Composable
private fun TopBarCompose(
    modifier: Modifier = Modifier,
    argument: SportNavArgument
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(start = 15.dp)
                .size(16.dp)
                .clickableNoIndicator {
                    argument.popBackStack()
                },
            painter = painterResource(ConfigRes.configIcBack()),
            contentDescription = "back",
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .padding(start = 20.dp)
                .align(Alignment.CenterVertically),
            text = "体育打卡",
            fontWeight = FontWeight.Bold,
            color = LocalAppColors.current.tvLv3,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier
                .padding(end = 15.dp)
                .align(Alignment.CenterVertically),
            text = currentTermText(),
            color = 0xFF697c9b.dark(0xFF606061),
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DetailTotalTitle(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .padding(start = 15.dp),
        text = "总计：",
        color = 0xFF15315b.dark(0xFFFFFFFF),
        fontSize = 16.sp
    )
}

@Composable
private fun DetailTotal(
    modifier: Modifier = Modifier,
) {
    val impactFontFamily = ConfigRes.impactFontFamily()
    val impactMinFontFamily = ConfigRes.impactMinFontFamily()
    val viewmodel: SportViewModel = viewModel()
    val sportUiState by viewmodel.uiState.collectAsStateWithLifecycle()
    val textDone = when (val state = sportUiState) {
        SportDetailUiState.Error, SportDetailUiState.Loading -> "null"
        is SportDetailUiState.Empty -> state.summary.totalDone
        is SportDetailUiState.Holiday -> state.summary.totalDone
        is SportDetailUiState.Content -> state.summary.totalDone
    }
    val textNeed = when (val state = sportUiState) {
        SportDetailUiState.Error, SportDetailUiState.Loading -> ""
        is SportDetailUiState.Empty -> state.summary.totalNeed
        is SportDetailUiState.Holiday -> state.summary.totalNeed
        is SportDetailUiState.Content -> state.summary.totalNeed
    }
    Row(
        modifier = modifier
            .padding(top = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(start = 50.dp, bottom = 15.dp),
            text = textDone,
            color = Color(0xFF4A44E4),
            fontSize = 45.sp,
            fontFamily = impactFontFamily,
            style = TextStyle(fontWeight = if (impactFontFamily == null) FontWeight.Bold else null)
        )
        Text(
            modifier = Modifier
                .padding(start = 5.dp, top = 10.dp, bottom = 15.dp),
            text = textNeed,
            color = Color(0xFF4A44E4),
            fontSize = 25.sp,
            fontFamily = impactMinFontFamily,
            style = TextStyle(fontWeight = if (impactMinFontFamily == null) FontWeight.Bold else null)
        )
    }
}

@Composable
private fun SportImage(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier
            .padding(start = 45.dp, top = 5.dp),
        painter = painterResource(Res.drawable.sport_ic_shoes),
        contentDescription = null,
    )
}

@Composable
private fun SportDetailRun(
    modifier: Modifier = Modifier,
) {
    val viewmodel: SportViewModel = viewModel()
    val sportUiState by viewmodel.uiState.collectAsStateWithLifecycle()
    Row(
        modifier = modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SportDetailItem(
            modifier = Modifier
                .padding(start = 19.dp),
            title = "跑步:",
            done = when (val state = sportUiState) {
                SportDetailUiState.Error, SportDetailUiState.Loading -> "null"
                is SportDetailUiState.Empty -> state.summary.runDone
                is SportDetailUiState.Holiday -> state.summary.runDone
                is SportDetailUiState.Content -> state.summary.runDone
            },
            need = when (val state = sportUiState) {
                SportDetailUiState.Error, SportDetailUiState.Loading -> ""
                is SportDetailUiState.Empty -> state.summary.runNeed
                is SportDetailUiState.Holiday -> state.summary.runNeed
                is SportDetailUiState.Content -> state.summary.runNeed
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        SportDetailItem(
            modifier = Modifier
                .padding(start = 35.dp),
            title = "其他:",
            done = when (val state = sportUiState) {
                SportDetailUiState.Error, SportDetailUiState.Loading -> "null"
                is SportDetailUiState.Empty -> state.summary.otherDone
                is SportDetailUiState.Holiday -> state.summary.otherDone
                is SportDetailUiState.Content -> state.summary.otherDone
            },
            need = when (val state = sportUiState) {
                SportDetailUiState.Error, SportDetailUiState.Loading -> ""
                is SportDetailUiState.Empty -> state.summary.otherNeed
                is SportDetailUiState.Holiday -> state.summary.otherNeed
                is SportDetailUiState.Content -> state.summary.otherNeed
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        SportDetailItem(
            modifier = Modifier
                .padding(start = 35.dp, end = 10.dp),
            title = "奖励:",
            done = when (val state = sportUiState) {
                SportDetailUiState.Error, SportDetailUiState.Loading -> "null"
                is SportDetailUiState.Empty -> state.summary.award
                is SportDetailUiState.Holiday -> state.summary.award
                is SportDetailUiState.Content -> state.summary.award
            },
            need = ""
        )
    }
}

@Composable
private fun SportDetailItem(
    modifier: Modifier = Modifier,
    title: String,
    done: String,
    need: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier,
            text = title,
            color = 0xFF15315b.dark(0xFFFFFFFF),
            fontSize = 16.sp,
        )
        Text(
            modifier = Modifier
                .padding(start = 2.dp),
            text = done,
            color = 0xFF15315b.dark(0xFFFFFFFF),
            fontSize = 16.sp,
        )
        Text(
            modifier = Modifier
                .padding(2.dp),
            text = need,
            color = 0xFF697c9b.dark(0xFF606061),
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun SportRecord(
    modifier: Modifier = Modifier,
    state: SportDetailUiState
) {
    val viewModel: SportViewModel = viewModel()
    val listState = rememberLazyListState()
    val triggerOffset = with(LocalDensity.current) {
        70.dp.toPx()
    }
    val refreshState = remember {
        RefreshState(triggerOffset = triggerOffset)
    }

    Box(
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(0xFFFBFCFF.dark(0xFF1D1D1D))
    ) {
        PullToRefresh(
            state = refreshState,
            canPull = {
                listState.firstVisibleItemIndex == 0 &&
                        listState.firstVisibleItemScrollOffset == 0
            },
            onRefresh = {
                viewModel.refresh(isFirstLoading = false)
            },
            modifier = Modifier,
        ) {
            LazyColumn(
                state = listState,
                // 自定义刷新负责边界拖动，避免 iOS 弹性回滚先消费下拉距离。
                overscrollEffect = null,
                userScrollEnabled = !refreshState.isRefreshing,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 15.dp, end = 15.dp),
                contentPadding = PaddingValues(vertical = 5.dp)
            ) {
                if (state is SportDetailUiState.Content) {
                    items(state.records.size) { index ->
                        ContentItem(
                            modifier = Modifier,
                            record = state.records[index]
                        )
                    }
                } else {
                    if (state !is SportDetailUiState.Loading) {
                        item {
                            DetailHint(modifier = Modifier.fillParentMaxSize(), state = state)
                        }
                    }
                }
            }
        }
    }

    val vmRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    LaunchedEffect(vmRefreshing) {
        if (refreshState.isRefreshing && !vmRefreshing) {
            refreshState.finishRefresh()
        }
    }
}

@Composable
private fun PullToRefresh(
    state: RefreshState,
    canPull: () -> Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier,
    header: @Composable (RefreshState) -> Unit = {
        RefreshHeader(state = it)
    },
    content: @Composable BoxScope.() -> Unit,
) {
    val connection = remember(state) {
        RefreshNestedScrollConnection(
            state = state,
            canPull = canPull,
            onRelease = {
                if (state.release()) {
                    onRefresh()
                }
            }
        )
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    translationY = state.pullOffset - state.triggerOffset
                }
        ) {
            header(state)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = state.pullOffset
                }
                .nestedScroll(connection),
            content = content
        )
    }
}

@Composable
private fun ContentItem(
    modifier: Modifier = Modifier,
    record: SportRecordUi
) {
    Box(
        modifier = modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(0xFFFFFFFF.dark(0xFF1D1D1D)),
    ) {
        Column {
            LabelItem(
                modifier = Modifier.padding(top = 8.dp, bottom = 5.dp),
                date = record.date,
                isValid = record.isValid,
                isAward = record.isAward
            )
            Info(
                modifier = Modifier.padding(bottom = 8.dp),
                time = record.time,
                location = record.spot,
                type = record.type
            )
        }
    }
}

@Composable
private fun LabelItem(
    modifier: Modifier = Modifier,
    date: String,
    isValid: Boolean,
    isAward: Boolean
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(start = 10.dp),
            text = date,
            color = 0xFF15315b.dark(0xFFFFFFFF),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
        )
        if (isAward) {
            Image(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .height(18.dp),
                painter = painterResource(Res.drawable.sport_ic_award),
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (isValid) {
            Image(
                modifier = Modifier
                    .padding(end = 15.dp),
                painter = painterResource(Res.drawable.sport_ic_valid),
                contentDescription = null,
            )
        } else {
            Image(
                modifier = Modifier
                    .padding(end = 15.dp),
                painter = painterResource(Res.drawable.sport_ic_not_valid),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun Info(
    modifier: Modifier = Modifier,
    time: String,
    location: String,
    type: String
) {
    Row(
        modifier = modifier
            .padding(top = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InfoItem(
            modifier = Modifier,
            content = time,
            drawableResource = Res.drawable.sport_ic_time
        )
        Spacer(modifier = Modifier.weight(1f))
        InfoItem(
            modifier = Modifier,
            content = location,
            drawableResource = Res.drawable.sport_ic_spot
        )
        Spacer(modifier = Modifier.weight(1f))
        InfoItem(
            modifier = Modifier,
            content = type,
            drawableResource = if (type == "跑步") {
                Res.drawable.sport_ic_run
            } else {
                Res.drawable.sport_ic_other
            }
        )
    }
}

@Composable
private fun InfoItem(
    modifier: Modifier = Modifier,
    drawableResource: DrawableResource,
    content: String
) {
    Row(
        modifier = modifier
            .padding(end = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .padding(start = 10.dp),
            painter = painterResource(drawableResource),
            contentDescription = null,
        )
        Text(
            modifier = Modifier
                .padding(start = 5.dp),
            text = content,
            fontSize = 14.sp,
            color = 0xFF697C9B.dark(0xFF606061)
        )
    }
}

@Composable
private fun DetailHint(
    state: SportDetailUiState,
    modifier: Modifier = Modifier,
) {
    HintItem(
        modifier = modifier.fillMaxSize(),
        drawableResource = when (state) {
            is SportDetailUiState.Holiday -> Res.drawable.sport_ic_holiday
            is SportDetailUiState.Empty -> Res.drawable.sport_ic_no_data
            else -> ConfigRes.configIc404()
        },
        content = when (state) {
            is SportDetailUiState.Holiday -> "大家都放假了，好好度假吧"
            is SportDetailUiState.Empty -> "暂时还没有记录哦~"
            else -> "数据错误"
        }
    )
}

@Composable
private fun HintItem(
    modifier: Modifier = Modifier,
    drawableResource: DrawableResource,
    content: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(bottom = 10.dp),
            painter = painterResource(drawableResource),
            contentDescription = null
        )
        Text(
            modifier = Modifier,
            text = content,
            fontSize = 14.sp
        )
    }
}
