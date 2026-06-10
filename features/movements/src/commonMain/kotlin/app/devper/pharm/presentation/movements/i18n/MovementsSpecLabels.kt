package app.devper.pharm.presentation.movements.i18n

import app.devper.pharm.domain.model.MovementType
import app.devper.pharm.presentation.movements.MovementsTypeSpec
import app.devper.pharm.ui.i18n.PharmStrings

fun MovementsTypeSpec.localizedLabel(s: PharmStrings): String = when (type) {
    MovementType.Import -> s.movementsSpecImport
    MovementType.Sale -> s.movementsSpecSale
    MovementType.Return -> s.movementsSpecReturn
    MovementType.Adjustment -> s.movementsSpecAdjustment
    MovementType.Writeoff -> s.movementsSpecWriteoff
    null -> s.movementsSpecVoided
}
