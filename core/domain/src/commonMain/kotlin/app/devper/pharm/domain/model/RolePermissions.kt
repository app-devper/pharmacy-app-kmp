package app.devper.pharm.domain.model

import app.devper.pharm.domain.extension.atLeast

data class RolePermissions(
    val canEditDrugs: Boolean,
    val canManageStock: Boolean,
    val canVoidSales: Boolean,
    val canEditCustomers: Boolean,
    val canResolvePendingSales: Boolean,
    val canSeeFinancialReports: Boolean,
) {
    companion object {
        val Full = RolePermissions(
            canEditDrugs = true,
            canManageStock = true,
            canVoidSales = true,
            canEditCustomers = true,
            canResolvePendingSales = true,
            canSeeFinancialReports = true,
        )
    }
}

fun Role.permissions(): RolePermissions = RolePermissions(
    canEditDrugs = atLeast(Role.ADMIN),
    canManageStock = atLeast(Role.MANAGER),
    canVoidSales = atLeast(Role.ADMIN),
    canEditCustomers = atLeast(Role.MANAGER),
    canResolvePendingSales = atLeast(Role.ADMIN),
    canSeeFinancialReports = atLeast(Role.ADMIN),
)
