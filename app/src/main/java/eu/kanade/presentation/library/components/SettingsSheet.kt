package eu.kanade.presentation.library.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.zacsweers.metrox.viewmodel.metroViewModel
import eu.kanade.tachiyomi.ui.category.CategoryScreen
import eu.kanade.tachiyomi.ui.download.DownloadQueueScreen
import eu.kanade.tachiyomi.ui.more.MoreViewModel
import eu.kanade.tachiyomi.ui.setting.SettingsScreen
import eu.kanade.tachiyomi.ui.stats.StatsScreen
import mihon.feature.support.SupportUsScreen
import mihon.icons.materialsymbols.MaterialSymbols
import mihon.icons.materialsymbols.automirroredrounded.Help
import mihon.icons.materialsymbols.automirroredrounded.Label
import mihon.icons.materialsymbols.rounded.CloudOff
import mihon.icons.materialsymbols.rounded.Download
import mihon.icons.materialsymbols.rounded.Eyeglasses2
import mihon.icons.materialsymbols.rounded.Info
import mihon.icons.materialsymbols.rounded.QueryStats
import mihon.icons.materialsymbols.rounded.Settings
import mihon.icons.materialsymbols.rounded.Storage
import mihon.icons.materialsymbols.roundedfilled.VolunteerActivism
import tachiyomi.core.common.Constants
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

private const val SheetAnimDuration = 600

@Composable
fun SettingsSheet(onDismiss: () -> Unit) {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel = metroViewModel<MoreViewModel>()
    val uriHandler = LocalUriHandler.current
    val maxHeight = (LocalConfiguration.current.screenHeightDp * 0.85f).dp

    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
    val dismiss = { visibleState.targetState = false }

    LaunchedEffect(visibleState.currentState, visibleState.targetState) {
        if (!visibleState.currentState && !visibleState.targetState) onDismiss()
    }

    if (!visibleState.currentState && !visibleState.targetState) return

    Dialog(
        onDismissRequest = { dismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn(tween(SheetAnimDuration)),
                exit = fadeOut(tween(SheetAnimDuration)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { dismiss() },
                )
            }
            AnimatedVisibility(
                visibleState = visibleState,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically(tween(SheetAnimDuration)) { it },
                exit = slideOutVertically(tween(SheetAnimDuration)) { it },
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {},
                ) {
                    Column(
                        modifier = Modifier
                            .heightIn(max = maxHeight)
                            .verticalScroll(rememberScrollState())
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(width = 40.dp, height = 5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.outlineVariant),
                        )
                        Text(
                            text = stringResource(MR.strings.label_settings),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp),
                        )

                        SheetRow(
                            icon = MaterialSymbols.Rounded.CloudOff,
                            title = stringResource(MR.strings.label_downloaded_only),
                            subtitle = stringResource(MR.strings.downloaded_only_summary),
                            onClick = { viewModel.downloadedOnly = !viewModel.downloadedOnly },
                            trailing = {
                                Switch(
                                    checked = viewModel.downloadedOnly,
                                    onCheckedChange = { viewModel.downloadedOnly = it },
                                )
                            },
                        )
                        SheetRow(
                            icon = MaterialSymbols.Rounded.Eyeglasses2,
                            title = stringResource(MR.strings.pref_incognito_mode),
                            subtitle = stringResource(MR.strings.pref_incognito_mode_summary),
                            onClick = { viewModel.incognitoMode = !viewModel.incognitoMode },
                            trailing = {
                                Switch(
                                    checked = viewModel.incognitoMode,
                                    onCheckedChange = { viewModel.incognitoMode = it },
                                )
                            },
                        )
                        SheetRow(
                            icon = MaterialSymbols.Rounded.Eyeglasses2,
                            title = "Liquid glass",
                            subtitle = "Glass navigation bar (Android 13+)",
                            onClick = { GlassSettings.set(context, !GlassSettings.enabled) },
                            trailing = {
                                Switch(
                                    checked = GlassSettings.enabled,
                                    onCheckedChange = { GlassSettings.set(context, it) },
                                )
                            },
                        )
                        SheetRow(
                            icon = MaterialSymbols.Rounded.Download,
                            title = stringResource(MR.strings.label_download_queue),
                            onClick = {
                                dismiss()
                                navigator.push(DownloadQueueScreen)
                            },
                        )
                        SheetRow(
                            icon = MaterialSymbols.AutoMirroredRounded.Label,
                            title = stringResource(MR.strings.categories),
                            onClick = {
                                dismiss()
                                navigator.push(CategoryScreen())
                            },
                        )
                        SheetRow(
                            icon = MaterialSymbols.Rounded.QueryStats,
                            title = stringResource(MR.strings.label_stats),
                            onClick = {
                                dismiss()
                                navigator.push(StatsScreen())
                            },
                        )
                        SheetRow(
                            icon = MaterialSymbols.Rounded.Storage,
                            title = stringResource(MR.strings.label_data_storage),
                            onClick = {
                                dismiss()
                                navigator.push(SettingsScreen(SettingsScreen.Destination.DataAndStorage))
                            },
                        )
                        SheetRow(
                            icon = MaterialSymbols.Rounded.Settings,
                            title = stringResource(MR.strings.label_settings),
                            onClick = {
                                dismiss()
                                navigator.push(SettingsScreen())
                            },
                        )
                        SheetRow(
                            icon = MaterialSymbols.RoundedFilled.VolunteerActivism,
                            title = stringResource(MR.strings.label_support_us),
                            onClick = {
                                dismiss()
                                navigator.push(SupportUsScreen())
                            },
                        )
                        SheetRow(
                            icon = MaterialSymbols.Rounded.Info,
                            title = stringResource(MR.strings.pref_category_about),
                            onClick = {
                                dismiss()
                                navigator.push(SettingsScreen(SettingsScreen.Destination.About))
                            },
                        )
                        SheetRow(
                            icon = MaterialSymbols.AutoMirroredRounded.Help,
                            title = stringResource(MR.strings.label_help),
                            onClick = { uriHandler.openUri(Constants.URL_HELP) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (trailing != null) {
                Spacer(modifier = Modifier.width(12.dp))
                trailing()
            }
        }
    }
}
