package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable
import app.devper.pharm.ui.theme.tabular
import androidx.compose.foundation.shape.CircleShape
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@Composable
fun PharmDatePicker(
    initialMillis: Long?,
    onPick: (Long?) -> Unit,
) {
    val s = pharmStrings
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Bangkok")).date }
    val initialDate = remember(initialMillis) { initialMillis?.let(::utcMillisToLocalDate) }
    var selected by remember(initialMillis) { mutableStateOf(initialDate) }
    var visibleMonth by remember(initialMillis) {
        mutableStateOf(CalendarMonth.of(initialDate ?: today))
    }

    PharmModal(
        open = true,
        onDismiss = { onPick(null) },
        size = PharmModalSize.Sm,
        footer = {
            DatePickerFooter(
                todayLabel = s.calendarToday,
                cancelLabel = s.commonCancel,
                confirmLabel = s.commonConfirm,
                confirmEnabled = selected != null,
                onToday = {
                    selected = today
                    visibleMonth = CalendarMonth.of(today)
                },
                onCancel = { onPick(null) },
                onConfirm = { onPick(selected?.toUtcStartOfDayMillis()) },
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CalendarHeader(
                title = visibleMonth.title(s),
                onPrev = { visibleMonth = visibleMonth.previous() },
                onNext = { visibleMonth = visibleMonth.next() },
                prevDesc = s.calendarPrevMonth,
                nextDesc = s.calendarNextMonth,
            )
            WeekdayHeaderRow(labels = weekdayHeaders(s))
            visibleMonth.weeks().forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        DayCell(
                            date = date,
                            isSelected = date != null && date == selected,
                            isToday = date != null && date == today,
                            onClick = { date?.let { selected = it } },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DatePickerFooter(
    todayLabel: String,
    cancelLabel: String,
    confirmLabel: String,
    confirmEnabled: Boolean,
    onToday: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (shouldStackDatePickerFooter(maxWidth)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PharmButton(
                    label = todayLabel,
                    onClick = onToday,
                    modifier = Modifier.fillMaxWidth(),
                    size = PharmButtonSize.Sm,
                    variant = PharmButtonVariant.Outline,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PharmButton(
                        label = cancelLabel,
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        size = PharmButtonSize.Sm,
                        variant = PharmButtonVariant.Ghost,
                    )
                    PharmButton(
                        label = confirmLabel,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        size = PharmButtonSize.Sm,
                        enabled = confirmEnabled,
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PharmButton(
                    label = todayLabel,
                    onClick = onToday,
                    size = PharmButtonSize.Sm,
                    variant = PharmButtonVariant.Outline,
                )
                Box(modifier = Modifier.weight(1f))
                PharmButton(
                    label = cancelLabel,
                    onClick = onCancel,
                    size = PharmButtonSize.Sm,
                    variant = PharmButtonVariant.Ghost,
                )
                PharmButton(
                    label = confirmLabel,
                    onClick = onConfirm,
                    size = PharmButtonSize.Sm,
                    enabled = confirmEnabled,
                )
            }
        }
    }
}

internal fun shouldStackDatePickerFooter(width: Dp): Boolean = width < 300.dp

@Composable
private fun CalendarHeader(
    title: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    prevDesc: String,
    nextDesc: String,
) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PharmIconButton(
            contentDescription = prevDesc,
            onClick = onPrev,
            modifier = Modifier.size(t.dimens.controlHeight),
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                contentDescription = null,
                tint = pharmTokens.colors.fg2,
                modifier = Modifier.size(22.dp),
            )
        }
        Text(
            text = title,
            style = PharmText.h3.copy(color = t.colors.fg1),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        PharmIconButton(
            contentDescription = nextDesc,
            onClick = onNext,
            modifier = Modifier.size(t.dimens.controlHeight),
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = pharmTokens.colors.fg2,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun WeekdayHeaderRow(labels: List<String>) {
    val t = pharmTokens
    Row(modifier = Modifier.fillMaxWidth()) {
        labels.forEach { label ->
            Text(
                text = label,
                style = PharmText.micro.copy(color = t.colors.fgMuted),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate?,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val clickModifier = if (date != null) {
        Modifier.pharmClickable(role = Role.Button, shape = CircleShape, onClick = onClick)
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .heightIn(min = 44.dp)
            .then(clickModifier)
            .padding(2.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (date != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isSelected -> t.colors.accent
                            isToday -> t.colors.accentBgSoft
                            else -> t.colors.surface
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${date.dayOfMonth}",
                    style = PharmText.bodySm.copy(
                        color = if (isSelected) t.colors.surface else t.colors.fg1,
                        fontWeight = if (isToday || isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    ).tabular(),
                )
            }
        } else {
            Box(modifier = Modifier.size(36.dp))
        }
    }
}

internal fun utcMillisToLocalDate(millis: Long): LocalDate =
    Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date

internal fun LocalDate.toUtcStartOfDayMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
