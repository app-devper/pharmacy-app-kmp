package app.devper.pharm.domain.model

import app.devper.pharm.domain.extension.atLeast

/**
 * What the signed-in role may do inside screens, matching pharmacy-api's route
 * permissions (ADR-0004). Screens hide actions the backend would refuse; the
 * backend still enforces every rule.
 */
data class RolePermissions(
    /** Create, edit, and bulk-import drugs, including selling prices (ADMIN+). */
    val canEditDrugs: Boolean,
    /** Adjust stock, add or delete lots, and open reorder suggestions (MANAGER+). */
    val canManageStock: Boolean,
    /** Void a whole bill (ADMIN+). */
    val canVoidSales: Boolean,
    /** Edit or delete customer profiles (MANAGER+). */
    val canEditCustomers: Boolean,
) {
    companion object {
        /** Everything allowed; the default outside the signed-in shell (previews, tests). */
        val Full = RolePermissions(canEditDrugs = true, canManageStock = true, canVoidSales = true, canEditCustomers = true)
    }
}

fun Role.permissions(): RolePermissions = RolePermissions(
    canEditDrugs = atLeast(Role.ADMIN),
    canManageStock = atLeast(Role.MANAGER),
    canVoidSales = atLeast(Role.ADMIN),
    canEditCustomers = atLeast(Role.MANAGER),
)
