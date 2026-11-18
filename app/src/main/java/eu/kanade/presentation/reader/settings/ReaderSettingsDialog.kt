package eu.kanade.presentation.reader.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import eu.kanade.presentation.components.AdaptiveSheet
import eu.kanade.tachiyomi.ui.reader.setting.ReaderSettingsViewModel
import kotlinx.coroutines.launch
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ReaderSettingsDialog(
    onDismissRequest: () -> Unit,
    onShowMenus: () -> Unit,
    onHideMenus: () -> Unit,
    viewModel: ReaderSettingsViewModel,
) {
    val tabTitles = listOf(
        stringResource(MR.strings.pref_category_reading_mode),
        stringResource(MR.strings.pref_category_general),
        stringResource(MR.strings.custom_filter),
    )
    val pagerState = rememberPagerState { tabTitles.size }
    val scope = rememberCoroutineScope()

    BoxWithConstraints {
        AdaptiveSheet(
            modifier = Modifier.heightIn(max = maxHeight * 0.75f),
            onDismissRequest = {
                onDismissRequest()
                onShowMenus()
            },
        ) {
            val window = (LocalView.current.parent as? DialogWindowProvider)?.window

            LaunchedEffect(pagerState.currentPage) {
                if (pagerState.currentPage == 2) {
                    window?.setDimAmount(0f)
                    onHideMenus()
                } else {
                    window?.setDimAmount(0.5f)
                    onShowMenus()
                }
            }

            Column {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)),
                )

                PillTabs(
                    titles = tabTitles,
                    selected = pagerState.currentPage,
                    onSelect = { scope.launch { pagerState.animateScrollToPage(it) } },
                )

                HorizontalPager(
                    modifier = Modifier.animateContentSize(),
                    state = pagerState,
                    verticalAlignment = Alignment.Top,
                ) { page ->
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 12.dp),
                    ) {
                        when (page) {
                            0 -> ReadingModePage(viewModel)
                            1 -> GeneralPage(viewModel)
                            2 -> ColorFilterPage(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PillTabs(
    titles: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            .padding(4.dp),
    ) {
        titles.forEachIndexed { index, title ->
            val isSelected = index == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape)
                    .then(
                        if (isSelected) {
                            Modifier.background(SettingsAccentBrush)
                        } else {
                            Modifier
                        },
                    )
                    .clickable { onSelect(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
