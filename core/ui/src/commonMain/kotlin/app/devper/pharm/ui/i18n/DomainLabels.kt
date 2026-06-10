package app.devper.pharm.ui.i18n

import app.devper.pharm.domain.model.MovementType

fun MovementType.localizedLabel(s: PharmStrings): String = when (this) {
    MovementType.Import -> s.movementTypeImport
    MovementType.Sale -> s.movementTypeSale
    MovementType.Return -> s.movementTypeReturn
    MovementType.Adjustment -> s.movementTypeAdjustment
    MovementType.Writeoff -> s.movementTypeWriteoff
}
