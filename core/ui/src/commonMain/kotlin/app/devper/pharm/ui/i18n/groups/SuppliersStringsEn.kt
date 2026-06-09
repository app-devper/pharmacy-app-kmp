package app.devper.pharm.ui.i18n.groups

object SuppliersStringsEn : SuppliersStrings {
    override val suppliersListSubtitle = "Manage suppliers and partner companies"
    override val suppliersSearchPlaceholder = "Search name / contact / phone…"
    override val suppliersAddCta = "Add supplier"
    override val suppliersListNotFound = "No suppliers match the search"
    override val suppliersListEmpty = "No suppliers yet"
    override val suppliersHeaderName = "Company / Store"
    override val suppliersHeaderContact = "Contact"
    override val suppliersHeaderTaxId = "Tax ID"
    override val suppliersHeaderDetails = "Details"
    override val suppliersDeleteConfirmTitle = "Delete supplier?"
    override val suppliersDeleteConfirmMessage: (String) -> String = { name ->
        "Delete \"$name\" from the system? Existing purchase orders will keep this name."
    }
    override val suppliersFormAddTitle = "Add supplier"
    override val suppliersFormEditTitle = "Edit supplier"
    override val suppliersFormInfoSection = "Supplier info"
    override val suppliersFormCompanyName = "Company / supplier name"
    override val suppliersFormCompanyPlaceholder = "e.g. ABC Pharma Co., Ltd."
    override val suppliersFormContactName = "Sales contact"
    override val suppliersFormAddress = "Address"
    override val suppliersFormAddressPlaceholder = "Street / sub-district / district / province"
    override val suppliersFormTaxId = "Tax identification number"
    override val suppliersFormNotesPlaceholder = "Order terms / discounts / additional details"
    override val suppliersFormNotFound = "Supplier not found"
}
