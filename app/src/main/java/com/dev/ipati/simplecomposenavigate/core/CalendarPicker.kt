package com.dev.ipati.simplecomposenavigate.core

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPositionInLayout
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil

private const val BUDDHISM_YEAR_OFFSET = 543

interface CalendarPickerCallBack {
    fun getStartCalendar(): Calendar?
    fun onDateSelectedCallBack(calendar: Calendar)
}

class CalendarPicker : DialogFragment() {

    private val listener: CalendarPickerCallBack? get() = parentFragment as? CalendarPickerCallBack

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            setLayout(width, height)
            setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ModalBottomCalendar("ระบุวันเกิด",
                    inputCalendar = listener?.getStartCalendar() ?: Calendar.getInstance(),
                    onDismissCallBack = {
                        dismiss()
                    },
                    onDateSelectedCallBack = {
                        listener?.onDateSelectedCallBack(it)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModalBottomCalendarPreview() {
    ModalBottomCalendar("ระบุวันเกิด")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomCalendar(
    title: String? = null,
    inputCalendar: Calendar = Calendar.getInstance(),
    onDismissCallBack: (() -> Unit) = {},
    onDateSelectedCallBack: ((Calendar) -> Unit) = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded,
        skipHiddenState = false,
    )
    inputCalendar.apply {
        set(Calendar.HOUR, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
    val isShowMonthState = remember {
        mutableStateOf(false)
    }
    val selectedDateState = remember {
        mutableStateOf(inputCalendar.apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        })
    }

    LaunchedEffect(Unit) {
        snapshotFlow { scaffoldState.bottomSheetState.currentValue }
            .collect {
                if (it == SheetValue.Hidden) {
                    onDismissCallBack()
                }
            }
    }
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContainerColor = Color.White,
        containerColor = Color.Transparent,
        sheetDragHandle = null,
        sheetShadowElevation = 16.dp,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(top = 24.dp, bottom = 64.dp, start = 4.dp, end = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    CalendarHeader(title = title, onHide = {
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.hide()
                        }
                    }, onAccepted = {
                        coroutineScope.launch {
                            if (isShowMonthState.value) {
                                isShowMonthState.value = !isShowMonthState.value
                            } else {
                                onDateSelectedCallBack(selectedDateState.value)
                                scaffoldState.bottomSheetState.hide()
                            }
                        }
                    })
                    CalendarView(
                        isShowMonthState = isShowMonthState,
                        inputCalendar = inputCalendar,
                        selectedDateState = selectedDateState,
                    )
                }
            }
        },
    ) {
        Box(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .background(Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    scaffoldState.bottomSheetState.hide()
                }
            })
    }
}

@Composable
private fun CalendarHeader(
    title: String?,
    onHide: (() -> Unit)? = null,
    onAccepted: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title.orEmpty(),
            style = AmazeTypography.Body1,
            textAlign = TextAlign.Center,
        )
        Row {
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            color = Color(CalendarColor.Primary400),
                        )
                    ) {
                        onHide?.invoke()
                    }
                    .padding(horizontal = 12.dp),
                text = CalendarMessage.CANCEL,
                color = Color(CalendarColor.Primary400),
                style = AmazeTypography.Body1,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            color = Color(CalendarColor.Primary400),
                        )
                    ) {
                        onAccepted?.invoke()
                    }
                    .padding(horizontal = 12.dp),
                text = CalendarMessage.OK,
                color = Color(CalendarColor.Primary400),
                style = AmazeTypography.Body1,
            )
        }
    }
}

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarView(
    inputCalendar: Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    },
    selectedDateState: MutableState<Calendar> = remember {
        mutableStateOf(inputCalendar.apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        })
    },
    isShowMonthState: MutableState<Boolean> = remember {
        mutableStateOf(false)
    },
) {
    val maxValue = Int.MAX_VALUE
    val startPage = maxValue / 2
    val month = inputCalendar.get(Calendar.MONTH)
    val year = inputCalendar.get(Calendar.YEAR)
    val monthPickerIndex = remember {
        mutableIntStateOf(inputCalendar.get(Calendar.MONTH))
    }
    val yearPickerIndex = remember {
        mutableIntStateOf(
            if (isThaiCalendar()) {
                inputCalendar.get(Calendar.YEAR) + BUDDHISM_YEAR_OFFSET
            } else {
                inputCalendar.get(Calendar.YEAR)
            } - 1
        )
    }
    val pagerState = rememberPagerState(
        initialPage = startPage,
        initialPageOffsetFraction = 0f
    ) {
        startPage.plus(1)
    }
    val coroutineScope = rememberCoroutineScope()
    var selectedDate by selectedDateState
    var isShowMonthPicker by isShowMonthState

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                val pageIndex = page - startPage
                val pageCalendar = getCalendar(1, month, year, pageIndex)
                monthPickerIndex.intValue = pageCalendar.get(Calendar.MONTH)
                yearPickerIndex.intValue =
                    pageCalendar.get(Calendar.YEAR) - 1 + if (isThaiCalendar()) BUDDHISM_YEAR_OFFSET else 0
            }
    }

    LaunchedEffect(Unit) {
        merge(
            snapshotFlow { monthPickerIndex.intValue },
            snapshotFlow { yearPickerIndex.intValue },
        )
            .collect {
                val wheelMonth = monthPickerIndex.intValue
                val wheelYear =
                    yearPickerIndex.intValue + 1 - if (isThaiCalendar()) BUDDHISM_YEAR_OFFSET else 0
                val wheelPageIndex = (wheelYear - year) * 12 + (wheelMonth - month)
                pagerState.scrollToPage(startPage + wheelPageIndex)
            }
    }

    Box(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShowSelectMonth(
                    monthPickerIndex = monthPickerIndex,
                    yearPickerIndex = yearPickerIndex,
                    onShowPickerDate = {
                        isShowMonthPicker = !isShowMonthPicker
                    }
                )

                CalendarDropDown(isShowMonthPicker = isShowMonthPicker) {
                    coroutineScope.launch {
                        isShowMonthPicker = !isShowMonthPicker
                    }
                }

                CalendarController(isShowMonthPicker, onNext = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1, 0f)
                    }
                }, onPrevious = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1, 0f)
                    }
                })
            }
            // section calendar
            Header(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )

            if (!isShowMonthPicker) {
                CalendarHorizontalPager(
                    pagerState,
                    startPage,
                    month,
                    year,
                    selectedDate,
                ) { itemSelected ->
                    selectedDate = itemSelected
                }
            } else {
                ShowMonthPicker(
                    monthPickerIndex = monthPickerIndex,
                    yearPickerIndex = yearPickerIndex
                )
            }
        }
    }
}

@Composable
private fun ShowSelectMonth(
    monthPickerIndex: MutableIntState,
    yearPickerIndex: MutableIntState,
    onShowPickerDate: (() -> Unit)? = null
) {
    val monthList = CalendarMessage.monthList
    val yearList = MutableList(4000) { (it + 1).toString() }
    val y = yearList[yearPickerIndex.intValue]
    val m = monthList[monthPickerIndex.intValue]
    Text(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onShowPickerDate?.invoke()
            }
            .padding(8.dp),
        text = "$m $y",
        style = AmazeTypography.H4,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun CalendarDropDown(
    isShowMonthPicker: Boolean,
    onShowPickerDate: (() -> Unit)? = null
) {
    val angle: Float by animateFloatAsState(
        targetValue = if (isShowMonthPicker) 90f else 0f,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing
        )
    )
    Icon(
        modifier = Modifier
            .rotate(angle)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onShowPickerDate?.invoke()
            }
            .size(16.dp),
        imageVector = Icons.Default.KeyboardArrowRight,
        tint = Color(CalendarColor.Primary400),
        contentDescription = "",
    )
}

@Composable
private fun CalendarController(
    isShowMonthPicker: Boolean,
    onNext: (() -> Unit)? = null,
    onPrevious: (() -> Unit)? = null
) {
    Row {
        Spacer(Modifier.weight(1f))
        Icon(
            modifier = Modifier
                .alpha(if (isShowMonthPicker) 0f else 1f)
                .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                .clickable(
                    enabled = !isShowMonthPicker,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = false,
                        color = Color(CalendarColor.Primary400),
                    ),
                ) {
                    onPrevious?.invoke()
                }
                .clipToBounds()
                .rotate(180f)
                .padding(all = 8.dp)
                .size(24.dp),
            imageVector = Icons.Default.KeyboardArrowRight,
            tint = Color(CalendarColor.Primary400),
            contentDescription = "",
        )
        Icon(
            modifier = Modifier
                .alpha(if (isShowMonthPicker) 0f else 1f)
                .padding(top = 8.dp, bottom = 8.dp)
                .clickable(
                    enabled = !isShowMonthPicker,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = false,
                        color = Color(CalendarColor.Primary400)
                    ),
                ) {
                    onNext?.invoke()
                }
                .padding(all = 8.dp)
                .size(24.dp),
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(CalendarColor.Primary400)
        )
    }
}

@ExperimentalFoundationApi
@Composable
private fun CalendarHorizontalPager(
    pagerState: PagerState,
    startPage: Int,
    month: Int,
    year: Int,
    selectedDate: Calendar,
    onSelectDate: ((itemSelected: Calendar) -> Unit)? = null
) {
    Box(
        modifier = Modifier,
    ) {
        HorizontalPager(
            modifier = Modifier,
            state = pagerState,
            pageSpacing = 0.dp,
            userScrollEnabled = true,
            reverseLayout = false,
            contentPadding = PaddingValues(0.dp),
            beyondBoundsPageCount = 0,
            pageSize = PageSize.Fill,
            flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
            key = null,
            pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
                Orientation.Horizontal
            ),
            pageContent = {
                val pageEachIndex = it - startPage
                val calendarEach = getCalendar(1, month, year, pageEachIndex)
                val maxDayOfMonth = calendarEach.getActualMaximum(Calendar.DAY_OF_MONTH)
                val maxDayOfWeek = calendarEach.getActualMaximum(Calendar.DAY_OF_WEEK)
                val dayWeek = calendarEach.get(Calendar.DAY_OF_WEEK)
                val selectedMonth = selectedDate.get(Calendar.MONTH)
                val currentMonth = calendarEach.get(Calendar.MONTH)
                val selectedYear = selectedDate.get(Calendar.YEAR)
                val currentYear = calendarEach.get(Calendar.YEAR)

                val dayWeekIndex = dayWeek - 2
                val offsetDay = maxDayOfMonth + dayWeek - 1
                val maxRow = ceil(offsetDay / 7.0).toInt()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(maxDayOfWeek),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items((maxRow + maxDayOfWeek) + maxDayOfMonth) { index ->
                        val day =
                            (index - dayWeekIndex).takeIf { it in 1..maxDayOfMonth }

                        val isMatch =
                            selectedDate.get(Calendar.DAY_OF_MONTH) == day
                                    && currentMonth == selectedMonth
                                    && currentYear == selectedYear

                        Box(
                            Modifier.wrapContentSize()
                        ) {
                            var selectModify = Modifier
                                .size(30.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                            if (isMatch) {
                                selectModify =
                                    selectModify.background(Color(CalendarColor.ColorSecond))
                            }
                            Box(modifier = selectModify)
                            Text(
                                text = day?.let { text -> "$text" } ?: "",
                                color = if (isMatch) Color(CalendarColor.Primary400) else Color.Black,
                                textAlign = TextAlign.Center,
                                style = AmazeTypography.Body1,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) {
                                        day?.let {
                                            val itemCalendar = getCalendar(
                                                it,
                                                calendarEach.get(Calendar.MONTH),
                                                calendarEach.get(Calendar.YEAR),
                                                0,
                                            )
                                            onSelectDate?.invoke(itemCalendar)
                                        }
                                    },
                            )
                        }
                    }
                }
            })
    }
}

@Composable
private fun ShowMonthPicker(
    monthPickerIndex: MutableState<Int>,
    yearPickerIndex: MutableIntState,
) {
    val yearList = MutableList(4000) { (it + 1).toString() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row {
            Box(
                modifier = Modifier
                    .weight(1f),
            ) {
                CircularWheelPicker(CalendarMessage.monthList, monthPickerIndex)
            }
            Box(
                modifier = Modifier
                    .weight(1f),
            ) {
                CircularWheelPicker(
                    yearList, yearPickerIndex
                )
            }
        }
    }
}

private fun isThaiCalendar() = Locale.getDefault().language.lowercase() == "th"

private fun getCalendar(day: Int, month: Int, year: Int, pageIndex: Int) =
    Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.MONTH, month)
        set(Calendar.YEAR, year)
        set(Calendar.HOUR, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.MONTH, pageIndex)
    }

@Composable
private fun Header(modifier: Modifier) {
    Row(
        modifier = modifier,
    ) {
        CalendarMessage.dayList.forEach {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = it,
                    color = Color(CalendarColor.Natural200),
                    textAlign = TextAlign.Center,
                    style = AmazeTypography.Body1,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

const
val isLoopEnabled = false

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CircularWheelPicker(
    items: List<String>,
    stateIndex: MutableState<Int>,
) {
    val alphaColor = Color.White
    var maxSize = items.size
    var startPoint = stateIndex.value
    val boxGap = 4.dp
    if (isLoopEnabled) {
        maxSize = Int.MAX_VALUE
        startPoint = maxSize / 2 + stateIndex.value
    }

    val localDensity = LocalDensity.current
    var columnHeightDp by remember {
        mutableStateOf(0f.dp)
    }
    var contentHeightDp by remember {
        mutableStateOf(0f.dp)
    }
    var isReady by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState(startPoint)
    val position = remember {
        derivedStateOf {
            if (isLoopEnabled) {
                var i = (scrollState.firstVisibleItemIndex - (maxSize / 2)) % items.size
                if (i < 0) {
                    i += items.size
                }
                i
            } else {
                scrollState.firstVisibleItemIndex
            }
        }
    }
    if (!isReady && scrollState.isScrollInProgress) {
        isReady = true
    }

    if (isReady && !scrollState.isScrollInProgress && columnHeightDp != 0.dp && contentHeightDp != 0.dp && stateIndex.value != position.value) {
        stateIndex.value = position.value
    }

    LaunchedEffect(Unit) {
        scrollState.scrollToItem(startPoint)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .onGloballyPositioned { coordinates ->
                columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
            }
    ) {
        val offset = columnHeightDp / 2 - contentHeightDp / 2
        SnapPositionInLayout
        val positionInLayout: Density.(layoutSize: Int, s: Int, itemSize: Int) -> Int =
            { _, _, _ -> offset.toPx().toInt() }

        val snappingLayout = remember(key1 = scrollState) {
            SnapLayoutInfoProvider(
                lazyListState = scrollState,
                positionInLayout = positionInLayout
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            flingBehavior = rememberSnapFlingBehavior(snappingLayout),
            contentPadding = PaddingValues(top = offset, bottom = offset)
        ) {
            items(maxSize, itemContent = { position ->
                val index = if (isLoopEnabled) {
                    var i = (position - (maxSize / 2)) % items.size
                    if (i < 0) {
                        i += items.size
                    }
                    i
                } else {
                    position
                }


                var itemYPosition by remember {
                    mutableFloatStateOf(0f)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .onGloballyPositioned { coordinates ->
                            itemYPosition = coordinates.positionInParent().y
                            contentHeightDp =
                                with(localDensity) { coordinates.size.height.toDp() }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .graphicsLayer {
                                if (offset != 0.dp) {
                                    rotationX =
                                        (offset.toPx() - itemYPosition) / offset.toPx() * 60
                                }
                            },
                        text = items[index],
                        style = AmazeTypography.H3,
                        textAlign = TextAlign.Center,
                    )
                }
            })
        }
        var alphaSize = columnHeightDp / 2 - contentHeightDp / 2 - boxGap
        if (alphaSize < 0.dp) {
            alphaSize = 0.dp
        }

        val alpha = 0.7f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(alphaSize)
                .alpha(alpha)
                .background(alphaColor),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(alphaSize)
                .align(Alignment.BottomCenter)
                .alpha(alpha)
                .background(alphaColor),
        )
    }
}

object AmazeTypography {
    val H3
        @Composable get() = BaseTypographyBold().copy(
            lineHeight = 29.dp.toSp(),
            fontSize = 24.dp.toSp(),
        )

    val H4
        @Composable get() = BaseTypographyBold().copy(
            lineHeight = 25.dp.toSp(),
            fontSize = 20.dp.toSp(),
        )

    val Body1
        @Composable get() = BaseTypographyRegular().copy(
            lineHeight = 23.dp.toSp(),
            fontSize = 18.dp.toSp(),
        )


    @Composable
    private fun BaseTypographyRegular() = TextStyle(
        color = Color(CalendarColor.Natural400)
    )

    @Composable
    private fun BaseTypographyBold() = TextStyle(
        color = Color(CalendarColor.Natural400)
    )
}

@Composable
fun Dp.toSp(): TextUnit = with(LocalDensity.current) { toSp() }

object CalendarColor {
    val Primary400: Int = "#2E6CF7".toColorInt()
    val ColorSecond: Int = "#DAECFE".toColorInt()
    val Natural200: Int = "#A7A7A7".toColorInt()
    val Natural400: Int = "#444444".toColorInt()
}

object CalendarMessage {
    const val OK = "Ok"
    const val CANCEL = "Cancel"

    val dayList = listOf(
        "อา.",
        "จ.",
        "อ.",
        "พ.",
        "พฤ.",
        "ศ.",
        "ส."
    )

    val monthList = listOf(
        "มกราคม",
        "กุมภาพันธ์",
        "มีนาคม",
        "เมษายน",
        "พฤษภาคม",
        "มิถุนายน",
        "กรกฎาคม",
        "สิงหาคม",
        "กันยายน",
        "ตุลาคม",
        "พฤศจิกายน",
        "ธันวาคม"
    )
}

