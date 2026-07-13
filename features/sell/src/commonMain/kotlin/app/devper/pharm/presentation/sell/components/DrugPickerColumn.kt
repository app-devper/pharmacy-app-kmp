package app.devper.pharm.presentation.sell.components

import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.extension.SearchSubmitAction
import app.devper.pharm.domain.extension.nextLotDaysLeft
import app.devper.pharm.domain.extension.resolvePrice
import app.devper.pharm.domain.extension.resolveSearchSubmit
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import app.devper.pharm.ui.designsystem.DrugCard
import app.devper.pharm.ui.designsystem.DrugCardType
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.designsystem.PharmCircularProgress

private val DRUG_CARD_HEIGHT = 148.dp

@Composable
fun DrugPickerColumn(
    query: String,
    onQueryChange: (String) -> Unit,
    drugs: List<Drug>,
    visible: List<Drug>,
    loading: Boolean,
    activeTier: String,
    onAdd: (Drug) -> Unit,
    modifier: Modifier = Modifier,
    searchFocusRequester: FocusRequester? = null,
) {
    val t = pharmTokens
    val searchFocus = searchFocusRequester ?: remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { searchFocus.requestFocus() } }
    val focusManager = LocalFocusManager.current
    val gridState = rememberLazyGridState()
    LaunchedEffect(gridState.isScrollInProgress) {
        if (gridState.isScrollInProgress) focusManager.clearFocus()
    }
    var armedDrugId by remember(query) { mutableStateOf<String?>(null) }
    var addedDrugName by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(addedDrugName) {
        if (addedDrugName != null) {
            delay(2000)
            addedDrugName = null
        }
    }
    val addAndClear = { drug: Drug ->
        onAdd(drug)
        addedDrugName = drug.name
        onQueryChange("")
    }
    val onSubmitSearch = {
        when (val action = visible.resolveSearchSubmit(query)) {
            is SearchSubmitAction.AddNow -> addAndClear(action.drug)
            is SearchSubmitAction.Confirm ->
                if (armedDrugId == action.drug.id) addAndClear(action.drug) else armedDrugId = action.drug.id
            SearchSubmitAction.None -> Unit
        }
    }
    Column(modifier = modifier.fillMaxSize().background(t.colors.bgPage)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(t.colors.surface)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PharmTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = pharmStrings.sellSearchPlaceholder,
                modifier = Modifier.weight(1f),
                imeAction = ImeAction.Search,
                onImeAction = onSubmitSearch,
                focusRequester = searchFocus,
                leadingSlot = null,
                trailingSlot = null,
            )
            ScannerActivePill()
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

        ResultLine(query, total = drugs.size, visibleCount = visible.size)

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

        addedDrugName?.let { name ->
            Text(
                text = pharmStrings.sellPickerAdded(name),
                style = PharmText.micro.copy(color = t.colors.successFg),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(t.colors.successBg)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
        }

        visible.firstOrNull { it.id == armedDrugId }?.let { armed ->
            Text(
                text = pharmStrings.sellPickerConfirmAdd(armed.name),
                style = PharmText.micro.copy(color = t.colors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(t.colors.accentBgSoft)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
        }

        when {
            loading && drugs.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { PharmCircularProgress(color = t.colors.accent) }

            visible.isEmpty() && !loading -> EmptyState(searching = query.isNotBlank())

            else -> {
                val today = remember { Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Bangkok")).date }
                BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
            ) {

                val columns = when {
                    maxWidth >= PharmBreakpoint.GridWide -> 4
                    maxWidth >= PharmBreakpoint.Medium  -> 3
                    maxWidth >= PharmBreakpoint.Stack  -> 2
                    else                -> 1
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    state = gridState,
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(visible, key = { it.id }) { drug ->
                        DrugCard(
                            modifier = Modifier.height(DRUG_CARD_HEIGHT),
                            name = drug.name,
                            generic = drug.genericName,
                            price = resolvePrice(drug.sellPrice, drug.prices, activeTier).amount,
                            stock = drug.stock.value,
                            unit = drug.unit ?: pharmStrings.commonUnitDefault,
                            type = inferType(drug),
                            altUnitCount = drug.altUnits.count { !it.hidden },
                            kyForm = inferKyForm(drug),
                            highlighted = drug.id == armedDrugId,
                            expiryDaysLeft = drug.nextLotDaysLeft(today),
                            lowStockThreshold = drug.minStock.value.coerceAtLeast(20),
                            onClick = { onAdd(drug) },
                        )
                    }
                }
                }
            }
        }
    }
}

@Composable
private fun ScannerActivePill() {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .height(t.dimens.controlHeight)
            .clip(t.shapes.md)
            .background(t.colors.accentBgSoft)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(t.shapes.pill)
                .background(t.colors.successFg),
        )
        Icon(
            imageVector = PharmIcons.Scan,
            contentDescription = pharmStrings.sellScannerOn,
            tint = t.colors.accent,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun ResultLine(query: String, total: Int, visibleCount: Int) {
    val text = if (query.isBlank()) pharmStrings.sellPickerCountAll(total) else pharmStrings.sellPickerCountFound(visibleCount, total)
    Text(
        text = text,
        style = PharmText.micro,
        modifier = Modifier
            .fillMaxWidth()
            .background(pharmTokens.colors.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun EmptyState(searching: Boolean) {
    PharmEmptyState(
        icon = if (searching) PharmIcons.Search else PharmIcons.Imports,
        title = if (searching) pharmStrings.sellNoResults else pharmStrings.sellPickerEmptyStock,
    )
}

private fun inferType(drug: Drug): DrugCardType {
    val t = drug.type?.trim()?.lowercase().orEmpty()
    return when {
        t.contains("herb")    || t.contains("สมุนไพร")  -> DrugCardType.Herb
        t.contains("supp")    || t.contains("อาหารเสริม") -> DrugCardType.Supplement
        else                                              -> DrugCardType.Rx
    }
}

private fun inferKyForm(drug: Drug): Int? {

    val rts = drug.reportTypes.map { it.lowercase() }
    return when {
        "ky12" in rts -> 12
        "ky11" in rts -> 11
        "ky10" in rts -> 10
        else -> null
    }
}
