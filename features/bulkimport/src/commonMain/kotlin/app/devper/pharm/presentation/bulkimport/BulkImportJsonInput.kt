package app.devper.pharm.presentation.bulkimport

import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings

internal const val BULK_IMPORT_SAMPLE_JSON: String = """[
  {
    "name": "ตัวอย่าง พาราเซตามอล 500mg",
    "generic_name": "Paracetamol",
    "type": "ยาสามัญ",
    "strength": "500mg",
    "unit": "เม็ด",
    "sell_price": 2,
    "cost_price": 1,
    "min_stock": 10,
    "barcode": "8851111222333",
    "report_types": []
  }
]"""

@Composable
internal fun BulkImportJsonInput(
    value: String,
    onValueChange: (String) -> Unit,
    parseError: String?,
    modifier: Modifier = Modifier,
) {
    FormField(
        label = pharmStrings.bulkImportPasteHere,
        hint = pharmStrings.bulkImportPasteHint,
        error = parseError,
        modifier = modifier,
    ) {
        PharmTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = BULK_IMPORT_SAMPLE_JSON,
            singleLine = false,
            isError = parseError != null,
            modifier = Modifier.heightIn(min = 180.dp, max = 280.dp),
        )
    }
}
