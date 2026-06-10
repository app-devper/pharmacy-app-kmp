package app.devper.pharm.ui.i18n

import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.domain.model.MovementType

fun MovementType.localizedLabel(s: PharmStrings): String = when (this) {
    MovementType.Import -> s.movementTypeImport
    MovementType.Sale -> s.movementTypeSale
    MovementType.Return -> s.movementTypeReturn
    MovementType.Adjustment -> s.movementTypeAdjustment
    MovementType.Writeoff -> s.movementTypeWriteoff
}

fun KyFormType.navTitle(s: PharmStrings): String = when (this) {
    KyFormType.Ky9 -> s.kyNavTitle9
    KyFormType.Ky10 -> s.kyNavTitle10
    KyFormType.Ky11 -> s.kyNavTitle11
    KyFormType.Ky12 -> s.kyNavTitle12
}

fun AdjustmentReason.label(s: PharmStrings): String = when (this) {
    AdjustmentReason.Recount -> s.stockReasonRecount
    AdjustmentReason.Damaged -> s.stockReasonDamaged
    AdjustmentReason.Expired -> s.stockReasonExpired
    AdjustmentReason.Lost -> s.stockReasonLost
    AdjustmentReason.Other -> s.stockReasonOther
}
