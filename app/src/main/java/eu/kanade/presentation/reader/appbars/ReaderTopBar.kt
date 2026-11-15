package eu.kanade.presentation.reader.appbars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarActions
import eu.kanade.presentation.reader.components.readerBarSurface
import mihon.icons.materialsymbols.MaterialSymbols
import mihon.icons.materialsymbols.automirroredrounded.ArrowBack
import mihon.icons.materialsymbols.rounded.Bookmark
import mihon.icons.materialsymbols.roundedfilled.Bookmark
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ReaderTopBar(
    mangaTitle: String?,
    chapterTitle: String?,
    navigateUp: () -> Unit,
    onClickTitle: () -> Unit,
    bookmarked: Boolean,
    onToggleBookmarked: () -> Unit,
    onOpenInWebView: (() -> Unit)?,
    onOpenInBrowser: (() -> Unit)?,
    onShare: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .readerBarSurface(RoundedCornerShape(50))
            .pointerInput(Unit) {}
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = navigateUp,
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = MaterialSymbols.AutoMirroredRounded.ArrowBack,
                contentDescription = stringResource(MR.strings.action_bar_up_description),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClickTitle)
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            if (mangaTitle != null) {
                Text(
                    text = mangaTitle,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (chapterTitle != null) {
                Text(
                    text = chapterTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        AppBarActions(
            actions = buildList {
                add(
                    AppBar.Action(
                        title = stringResource(
                            if (bookmarked) {
                                MR.strings.action_remove_bookmark
                            } else {
                                MR.strings.action_bookmark
                            },
                        ),
                        icon = if (bookmarked) {
                            MaterialSymbols.RoundedFilled.Bookmark
                        } else {
                            MaterialSymbols.Rounded.Bookmark
                        },
                        onClick = onToggleBookmarked,
                    ),
                )
                onOpenInWebView?.let {
                    add(
                        AppBar.OverflowAction(
                            title = stringResource(MR.strings.action_open_in_web_view),
                            onClick = it,
                        ),
                    )
                }
                onOpenInBrowser?.let {
                    add(
                        AppBar.OverflowAction(
                            title = stringResource(MR.strings.action_open_in_browser),
                            onClick = it,
                        ),
                    )
                }
                onShare?.let {
                    add(
                        AppBar.OverflowAction(
                            title = stringResource(MR.strings.action_share),
                            onClick = it,
                        ),
                    )
                }
            },
        )
    }
}
