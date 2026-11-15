package eu.kanade.presentation.reader.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalSlider
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import eu.kanade.presentation.util.isTabletUi
import mihon.icons.materialsymbols.MaterialSymbols
import mihon.icons.materialsymbols.rounded.SkipNext
import mihon.icons.materialsymbols.rounded.SkipPrevious
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import kotlin.math.roundToInt

enum class ChapterNavigatorType {
    HORIZONTAL_LTR,
    HORIZONTAL_RTL,
    VERTICAL_LEFT,
    VERTICAL_RIGHT,
    ;

    fun isHorizontal() = this in setOf(HORIZONTAL_LTR, HORIZONTAL_RTL)
}

@Composable
fun ChapterNavigator(
    type: ChapterNavigatorType,
    onNextChapter: () -> Unit,
    enabledNext: Boolean,
    onPreviousChapter: () -> Unit,
    enabledPrevious: Boolean,
    currentPage: Int,
    totalPages: Int,
    onPageIndexChange: (Int) -> Unit,
    onPageIndexChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current

    val state = if (totalPages > 1) {
        key(totalPages) {
            rememberSliderState(
                value = currentPage.toFloat(),
                steps = 0,
                trackRange = 1f..totalPages.toFloat(),
            )
        }
            .also {
                it.value = currentPage.toFloat()
            }
    } else {
        null
    }

    val interactionSource = remember { MutableInteractionSource() }
    val sliderDragged by interactionSource.collectIsDraggedAsState()
    LaunchedEffect(currentPage) {
        if (sliderDragged) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    val isTabletUi = isTabletUi()
    val mainAxisPadding = if (isTabletUi) 24.dp else 8.dp

    val onPageChange: (Int) -> Unit = { onPageIndexChange(it - 1) }

    if (type.isHorizontal()) {
        HorizontalChapterNavigator(
            isRtl = type == ChapterNavigatorType.HORIZONTAL_RTL,
            hasPages = state != null,
            onNextChapter = onNextChapter,
            enabledNext = enabledNext,
            onPreviousChapter = onPreviousChapter,
            enabledPrevious = enabledPrevious,
            currentPage = currentPage,
            totalPages = totalPages,
            onPageChange = onPageChange,
            onPageChangeFinished = onPageIndexChangeFinished,
            interactionSource = interactionSource,
            mainAxisPadding = mainAxisPadding,
            modifier = modifier,
        )
    } else {
        VerticalChapterNavigator(
            state = state,
            onNextChapter = onNextChapter,
            enabledNext = enabledNext,
            onPreviousChapter = onPreviousChapter,
            enabledPrevious = enabledPrevious,
            currentPage = currentPage,
            totalPages = totalPages,
            onPageChange = onPageChange,
            onPageChangeFinished = onPageIndexChangeFinished,
            interactionSource = interactionSource,
            mainAxisPadding = mainAxisPadding,
            modifier = modifier,
        )
    }
}

@Composable
private fun NavButton(
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(40.dp)
            .readerBarSurface(CircleShape),
        content = content,
    )
}

@Composable
private fun ThinPageSlider(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    onPageChangeFinished: () -> Unit,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val currentOnPageChange by rememberUpdatedState(onPageChange)
    val currentOnFinished by rememberUpdatedState(onPageChangeFinished)
    var dragging by remember { mutableStateOf(false) }
    var dragStart by remember { mutableStateOf<DragInteraction.Start?>(null) }

    val fraction = if (totalPages > 1) {
        ((currentPage - 1f) / (totalPages - 1f)).coerceIn(0f, 1f)
    } else {
        0f
    }
    val activeColor = MaterialTheme.colorScheme.onSurface
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
    val thumbSize by animateDpAsState(
        targetValue = if (dragging) 14.dp else 0.dp,
        label = "pageSliderThumb",
    )

    fun pageAt(x: Float, width: Float): Int {
        val raw = (x / width).coerceIn(0f, 1f)
        val f = if (isRtl) 1f - raw else raw
        return (f * (totalPages - 1)).roundToInt() + 1
    }

    fun stopDrag() {
        dragging = false
        dragStart?.let { interactionSource.tryEmit(DragInteraction.Stop(it)) }
        dragStart = null
        currentOnFinished()
    }

    Box(
        modifier = modifier
            .height(32.dp)
            .pointerInput(totalPages, isRtl) {
                detectTapGestures { offset ->
                    currentOnPageChange(pageAt(offset.x, size.width.toFloat()))
                    currentOnFinished()
                }
            }
            .pointerInput(totalPages, isRtl) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        dragging = true
                        val start = DragInteraction.Start()
                        dragStart = start
                        interactionSource.tryEmit(start)
                        currentOnPageChange(pageAt(offset.x, size.width.toFloat()))
                    },
                    onDragEnd = { stopDrag() },
                    onDragCancel = { stopDrag() },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        currentOnPageChange(pageAt(change.position.x, size.width.toFloat()))
                    },
                )
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(inactiveColor),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(activeColor),
        )
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.weight(fraction.coerceAtLeast(0.001f)))
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(activeColor),
            )
            Spacer(modifier = Modifier.weight((1f - fraction).coerceAtLeast(0.001f)))
        }
    }
}

@Composable
fun HorizontalChapterNavigator(
    isRtl: Boolean,
    hasPages: Boolean,
    onNextChapter: () -> Unit,
    enabledNext: Boolean,
    onPreviousChapter: () -> Unit,
    enabledPrevious: Boolean,
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    onPageChangeFinished: () -> Unit,
    interactionSource: MutableInteractionSource,
    mainAxisPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    // We explicitly handle direction based on the reader viewer rather than the system direction
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = mainAxisPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavButton(
                enabled = if (isRtl) enabledNext else enabledPrevious,
                onClick = if (isRtl) onNextChapter else onPreviousChapter,
            ) {
                Icon(
                    imageVector = MaterialSymbols.Rounded.SkipPrevious,
                    contentDescription = stringResource(
                        if (isRtl) MR.strings.action_next_chapter else MR.strings.action_previous_chapter,
                    ),
                )
            }

            if (hasPages) {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .readerBarSurface(RoundedCornerShape(50))
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(contentAlignment = Alignment.CenterEnd) {
                            Text(text = currentPage.toString(), style = MaterialTheme.typography.labelMedium)
                            // Taking up full length so the slider doesn't shift when 'currentPage' length changes
                            Text(
                                text = totalPages.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = androidx.compose.ui.graphics.Color.Transparent,
                            )
                        }

                        ThinPageSlider(
                            currentPage = currentPage,
                            totalPages = totalPages,
                            onPageChange = onPageChange,
                            onPageChangeFinished = onPageChangeFinished,
                            interactionSource = interactionSource,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                        )

                        Text(text = totalPages.toString(), style = MaterialTheme.typography.labelMedium)
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
            }

            NavButton(
                enabled = if (isRtl) enabledPrevious else enabledNext,
                onClick = if (isRtl) onPreviousChapter else onNextChapter,
            ) {
                Icon(
                    imageVector = MaterialSymbols.Rounded.SkipNext,
                    contentDescription = stringResource(
                        if (isRtl) MR.strings.action_previous_chapter else MR.strings.action_next_chapter,
                    ),
                )
            }
        }
    }
}

@Composable
fun VerticalChapterNavigator(
    state: SliderState?,
    onNextChapter: () -> Unit,
    enabledNext: Boolean,
    onPreviousChapter: () -> Unit,
    enabledPrevious: Boolean,
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    onPageChangeFinished: () -> Unit,
    interactionSource: MutableInteractionSource,
    mainAxisPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = mainAxisPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NavButton(
            enabled = enabledPrevious,
            onClick = onPreviousChapter,
        ) {
            Icon(
                imageVector = MaterialSymbols.Rounded.SkipPrevious,
                contentDescription = stringResource(MR.strings.action_previous_chapter),
                modifier = Modifier.rotate(90f),
            )
        }

        if (state != null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .readerBarSurface(RoundedCornerShape(24.dp))
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = currentPage.toString(), style = MaterialTheme.typography.labelMedium)

                VerticalSlider(
                    state = state,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    onValueChange = { onPageChange(it.roundToInt()) },
                    onValueChangeFinished = onPageChangeFinished,
                    interactionSource = interactionSource,
                )

                Text(text = totalPages.toString(), style = MaterialTheme.typography.labelMedium)
            }
        } else {
            Spacer(Modifier.weight(1f))
        }

        NavButton(
            enabled = enabledNext,
            onClick = onNextChapter,
        ) {
            Icon(
                imageVector = MaterialSymbols.Rounded.SkipNext,
                contentDescription = stringResource(MR.strings.action_next_chapter),
                modifier = Modifier.rotate(90f),
            )
        }
    }
}

@Preview
@Composable
private fun ChapterNavigatorPreview() {
    var currentPage by remember { mutableIntStateOf(1) }
    TachiyomiPreviewTheme {
        ChapterNavigator(
            type = ChapterNavigatorType.VERTICAL_RIGHT,
            onNextChapter = {},
            enabledNext = true,
            onPreviousChapter = {},
            enabledPrevious = true,
            currentPage = currentPage,
            totalPages = 10,
            onPageIndexChange = { currentPage = (it + 1) },
            onPageIndexChangeFinished = {},
        )
    }
}
