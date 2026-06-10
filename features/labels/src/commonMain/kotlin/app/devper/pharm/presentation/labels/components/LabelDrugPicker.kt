package app.devper.pharm.presentation.labels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.presentation.labels.LabelPrintUiState
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@Composable
internal fun LabelDrugPicker(
    state: LabelPrintUiState,
    onQueryChange: (String) -> Unit,
    onAddDrug: (Drug) -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            PharmTextField(
                value = state.query,
                onValueChange = onQueryChange,
                placeholder = pharmStrings.labelsSearchPlaceholder,
            )
        }
        if (state.loading) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                PharmCircularProgress()
            }
            return@Column
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.filteredDrugs, key = { it.id }) { drug ->
                LabelDrugRow(drug = drug, onAdd = { onAddDrug(drug) })
            }
        }
    }
}

@Composable
private fun LabelDrugRow(drug: Drug, onAdd: () -> Unit) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onAdd)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = drug.name,
            style = PharmText.body.copy(color = t.colors.fg1),
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        Text(
            text = pharmStrings.labelsAddCta,
            style = PharmText.micro.copy(color = t.colors.accent, fontWeight = FontWeight.SemiBold),
        )
    }
}
