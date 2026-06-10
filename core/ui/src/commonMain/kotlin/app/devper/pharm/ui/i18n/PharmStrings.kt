package app.devper.pharm.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import app.devper.pharm.ui.i18n.groups.LocaleStrings
import app.devper.pharm.ui.i18n.groups.LocaleStringsTh
import app.devper.pharm.ui.i18n.groups.LocaleStringsEn
import app.devper.pharm.ui.i18n.groups.SalesHistoryStrings
import app.devper.pharm.ui.i18n.groups.SalesHistoryStringsTh
import app.devper.pharm.ui.i18n.groups.SalesHistoryStringsEn
import app.devper.pharm.ui.i18n.groups.OfflineSyncStrings
import app.devper.pharm.ui.i18n.groups.OfflineSyncStringsTh
import app.devper.pharm.ui.i18n.groups.OfflineSyncStringsEn
import app.devper.pharm.ui.i18n.groups.StockCountStrings
import app.devper.pharm.ui.i18n.groups.StockCountStringsTh
import app.devper.pharm.ui.i18n.groups.StockCountStringsEn
import app.devper.pharm.ui.i18n.groups.BulkImportStrings
import app.devper.pharm.ui.i18n.groups.BulkImportStringsTh
import app.devper.pharm.ui.i18n.groups.BulkImportStringsEn
import app.devper.pharm.ui.i18n.groups.CustomersStrings
import app.devper.pharm.ui.i18n.groups.CustomersStringsTh
import app.devper.pharm.ui.i18n.groups.CustomersStringsEn
import app.devper.pharm.ui.i18n.groups.SuppliersStrings
import app.devper.pharm.ui.i18n.groups.SuppliersStringsTh
import app.devper.pharm.ui.i18n.groups.SuppliersStringsEn
import app.devper.pharm.ui.i18n.groups.MovementsStrings
import app.devper.pharm.ui.i18n.groups.MovementsStringsTh
import app.devper.pharm.ui.i18n.groups.MovementsStringsEn
import app.devper.pharm.ui.i18n.groups.PlanningStrings
import app.devper.pharm.ui.i18n.groups.PlanningStringsTh
import app.devper.pharm.ui.i18n.groups.PlanningStringsEn
import app.devper.pharm.ui.i18n.groups.ImportsStrings
import app.devper.pharm.ui.i18n.groups.ImportsStringsTh
import app.devper.pharm.ui.i18n.groups.ImportsStringsEn
import app.devper.pharm.ui.i18n.groups.ReportsStrings
import app.devper.pharm.ui.i18n.groups.ReportsStringsTh
import app.devper.pharm.ui.i18n.groups.ReportsStringsEn
import app.devper.pharm.ui.i18n.groups.ExpiryStrings
import app.devper.pharm.ui.i18n.groups.ExpiryStringsTh
import app.devper.pharm.ui.i18n.groups.ExpiryStringsEn
import app.devper.pharm.ui.i18n.groups.LabelsStrings
import app.devper.pharm.ui.i18n.groups.LabelsStringsTh
import app.devper.pharm.ui.i18n.groups.LabelsStringsEn
import app.devper.pharm.ui.i18n.groups.CommonStrings
import app.devper.pharm.ui.i18n.groups.ValidationStrings
import app.devper.pharm.ui.i18n.groups.ValidationStringsEn
import app.devper.pharm.ui.i18n.groups.ValidationStringsTh
import app.devper.pharm.ui.i18n.groups.CommonStringsTh
import app.devper.pharm.ui.i18n.groups.CommonStringsEn
import app.devper.pharm.ui.i18n.groups.LoginStrings
import app.devper.pharm.ui.i18n.groups.LoginStringsTh
import app.devper.pharm.ui.i18n.groups.LoginStringsEn
import app.devper.pharm.ui.i18n.groups.ProfileStrings
import app.devper.pharm.ui.i18n.groups.ProfileStringsTh
import app.devper.pharm.ui.i18n.groups.ProfileStringsEn
import app.devper.pharm.ui.i18n.groups.SettingsStrings
import app.devper.pharm.ui.i18n.groups.SettingsStringsTh
import app.devper.pharm.ui.i18n.groups.SettingsStringsEn
import app.devper.pharm.ui.i18n.groups.UsersStrings
import app.devper.pharm.ui.i18n.groups.UsersStringsTh
import app.devper.pharm.ui.i18n.groups.UsersStringsEn
import app.devper.pharm.ui.i18n.groups.HelpStrings
import app.devper.pharm.ui.i18n.groups.HelpStringsTh
import app.devper.pharm.ui.i18n.groups.HelpStringsEn
import app.devper.pharm.ui.i18n.groups.StockStrings
import app.devper.pharm.ui.i18n.groups.StockStringsTh
import app.devper.pharm.ui.i18n.groups.StockStringsEn
import app.devper.pharm.ui.i18n.groups.SellStrings
import app.devper.pharm.ui.i18n.groups.SellStringsTh
import app.devper.pharm.ui.i18n.groups.SellStringsEn
import app.devper.pharm.ui.i18n.groups.KyStrings
import app.devper.pharm.ui.i18n.groups.KyStringsTh
import app.devper.pharm.ui.i18n.groups.KyStringsEn
import app.devper.pharm.ui.i18n.groups.NavStrings
import app.devper.pharm.ui.i18n.groups.NavStringsTh
import app.devper.pharm.ui.i18n.groups.NavStringsEn

interface PharmStrings :
    LocaleStrings,
    SalesHistoryStrings,
    OfflineSyncStrings,
    StockCountStrings,
    BulkImportStrings,
    CustomersStrings,
    SuppliersStrings,
    MovementsStrings,
    PlanningStrings,
    ImportsStrings,
    ReportsStrings,
    ExpiryStrings,
    LabelsStrings,
    CommonStrings,
    ValidationStrings,
    LoginStrings,
    ProfileStrings,
    SettingsStrings,
    UsersStrings,
    HelpStrings,
    StockStrings,
    SellStrings,
    KyStrings,
    NavStrings

object PharmStringsTh :
    PharmStrings,
    LocaleStrings by LocaleStringsTh,
    SalesHistoryStrings by SalesHistoryStringsTh,
    OfflineSyncStrings by OfflineSyncStringsTh,
    StockCountStrings by StockCountStringsTh,
    BulkImportStrings by BulkImportStringsTh,
    CustomersStrings by CustomersStringsTh,
    SuppliersStrings by SuppliersStringsTh,
    MovementsStrings by MovementsStringsTh,
    PlanningStrings by PlanningStringsTh,
    ImportsStrings by ImportsStringsTh,
    ReportsStrings by ReportsStringsTh,
    ExpiryStrings by ExpiryStringsTh,
    LabelsStrings by LabelsStringsTh,
    CommonStrings by CommonStringsTh,
    ValidationStrings by ValidationStringsTh,
    LoginStrings by LoginStringsTh,
    ProfileStrings by ProfileStringsTh,
    SettingsStrings by SettingsStringsTh,
    UsersStrings by UsersStringsTh,
    HelpStrings by HelpStringsTh,
    StockStrings by StockStringsTh,
    SellStrings by SellStringsTh,
    KyStrings by KyStringsTh,
    NavStrings by NavStringsTh

object PharmStringsEn :
    PharmStrings,
    LocaleStrings by LocaleStringsEn,
    SalesHistoryStrings by SalesHistoryStringsEn,
    OfflineSyncStrings by OfflineSyncStringsEn,
    StockCountStrings by StockCountStringsEn,
    BulkImportStrings by BulkImportStringsEn,
    CustomersStrings by CustomersStringsEn,
    SuppliersStrings by SuppliersStringsEn,
    MovementsStrings by MovementsStringsEn,
    PlanningStrings by PlanningStringsEn,
    ImportsStrings by ImportsStringsEn,
    ReportsStrings by ReportsStringsEn,
    ExpiryStrings by ExpiryStringsEn,
    LabelsStrings by LabelsStringsEn,
    CommonStrings by CommonStringsEn,
    ValidationStrings by ValidationStringsEn,
    LoginStrings by LoginStringsEn,
    ProfileStrings by ProfileStringsEn,
    SettingsStrings by SettingsStringsEn,
    UsersStrings by UsersStringsEn,
    HelpStrings by HelpStringsEn,
    StockStrings by StockStringsEn,
    SellStrings by SellStringsEn,
    KyStrings by KyStringsEn,
    NavStrings by NavStringsEn

val LocalPharmStrings = staticCompositionLocalOf<PharmStrings> { PharmStringsTh }

val pharmStrings: PharmStrings
    @Composable
    @ReadOnlyComposable
    get() = LocalPharmStrings.current
