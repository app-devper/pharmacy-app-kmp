package app.devper.pharm.presentation.movements

import androidx.compose.ui.graphics.vector.ImageVector
import app.devper.pharm.domain.model.MovementType
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmIcons

data class MovementsTypeSpec(
    val id: String,
    val icon: ImageVector,
    val tone: PharmBadgeTone,
    val type: MovementType? = null,
)

object MovementsTypeCatalog {

    val specs: List<MovementsTypeSpec> = listOf(
        MovementsTypeSpec(
            id = MovementType.Import.wire,
            icon = PharmIcons.Imports,
            tone = PharmBadgeTone.Green,
            type = MovementType.Import,
        ),
        MovementsTypeSpec(
            id = MovementType.Sale.wire,
            icon = PharmIcons.Sell,
            tone = PharmBadgeTone.Blue,
            type = MovementType.Sale,
        ),
        MovementsTypeSpec(
            id = MovementType.Return.wire,
            icon = PharmIcons.ReturnArrow,
            tone = PharmBadgeTone.Indigo,
            type = MovementType.Return,
        ),
        MovementsTypeSpec(
            id = MovementType.Adjustment.wire,
            icon = PharmIcons.Pencil,
            tone = PharmBadgeTone.Amber,
            type = MovementType.Adjustment,
        ),
        MovementsTypeSpec(
            id = MovementType.Writeoff.wire,
            icon = PharmIcons.Trash,
            tone = PharmBadgeTone.Red,
            type = MovementType.Writeoff,
        ),
        MovementsTypeSpec(
            id = "void",
            icon = PharmIcons.Ban,
            tone = PharmBadgeTone.Gray,
            type = null,
        ),
    )

    val allIds: Set<String> = specs.map { it.id }.toSet()

    val byId: Map<String, MovementsTypeSpec> = specs.associateBy { it.id }

    val byMovementType: Map<MovementType, MovementsTypeSpec> =
        specs.mapNotNull { spec -> spec.type?.let { it to spec } }.toMap()

    fun toEnumSet(ids: Set<String>): Set<MovementType> =
        ids.mapNotNull { byId[it]?.type }.toSet()
}
