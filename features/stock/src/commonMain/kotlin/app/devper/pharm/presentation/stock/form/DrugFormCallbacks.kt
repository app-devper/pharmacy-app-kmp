package app.devper.pharm.presentation.stock.form

data class DrugFormCallbacks(

    val onSubmit: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onDismissError: () -> Unit = {},
    val onOpenLots: (drugId: String, drugName: String) -> Unit = { _, _ -> },
    val onOpenAdjustments: (drugId: String, drugName: String) -> Unit = { _, _ -> },

    val onName: (String) -> Unit = {},
    val onGenericName: (String) -> Unit = {},
    val onType: (String) -> Unit = {},
    val onStrength: (String) -> Unit = {},
    val onUnit: (String) -> Unit = {},
    val onBarcode: (String) -> Unit = {},
    val onRegNo: (String) -> Unit = {},

    val onSellPrice: (String) -> Unit = {},
    val onCostPrice: (String) -> Unit = {},
    val onTierRetail: (String) -> Unit = {},
    val onTierRegular: (String) -> Unit = {},
    val onTierWholesale: (String) -> Unit = {},
    val onMinStock: (String) -> Unit = {},

    val onAddAltUnit: () -> Unit = {},
    val onRemoveAltUnit: (Int) -> Unit = {},
    val onAltUnitName: (Int, String) -> Unit = { _, _ -> },
    val onAltUnitFactor: (Int, String) -> Unit = { _, _ -> },
    val onAltUnitSellPrice: (Int, String) -> Unit = { _, _ -> },
    val onAltUnitBarcode: (Int, String) -> Unit = { _, _ -> },
    val onAltUnitHidden: (Int, Boolean) -> Unit = { _, _ -> },

    val onToggleReportType: (String) -> Unit = {},
    val onInitialStock: (String) -> Unit = {},
    val onLotNumber: (String) -> Unit = {},
    val onLotExpiry: (String) -> Unit = {},
    val onLotQty: (String) -> Unit = {},
    val onLotCostPrice: (String) -> Unit = {},
    val onLotSellPrice: (String) -> Unit = {},
)
