package app.devper.pharm.presentation.stock

import app.devper.pharm.ui.i18n.PharmStringsTh

import app.devper.pharm.ui.i18n.PharmStringsEn

import app.devper.pharm.presentation.stock.i18n.label

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StockTypeFilterTest {

    @Test
    fun all_matches_every_type_including_null_and_blank() {
        listOf(null, "", "  ", "herb", "supp", "cur", "ยาสมุนไพร", "อาหารเสริม", "ยาแผนปัจจุบัน", "xyz").forEach {
            assertTrue(StockTypeFilter.All.matches(it), "All should match '$it'")
        }
    }

    @Test
    fun herb_matches_english_and_thai_herb_tokens() {
        assertTrue(StockTypeFilter.Herb.matches("herb"))
        assertTrue(StockTypeFilter.Herb.matches("HERB"))
        assertTrue(StockTypeFilter.Herb.matches("  herb  "))
        assertTrue(StockTypeFilter.Herb.matches("herbal"))
        assertTrue(StockTypeFilter.Herb.matches("ยาสมุนไพร"))
    }

    @Test
    fun herb_does_not_match_supp_or_current() {
        assertFalse(StockTypeFilter.Herb.matches("supp"))
        assertFalse(StockTypeFilter.Herb.matches("อาหารเสริม"))
        assertFalse(StockTypeFilter.Herb.matches("cur"))
        assertFalse(StockTypeFilter.Herb.matches("ยาแผนปัจจุบัน"))
        assertFalse(StockTypeFilter.Herb.matches(null))
        assertFalse(StockTypeFilter.Herb.matches(""))
    }

    @Test
    fun supplement_matches_english_and_thai_supp_tokens() {
        assertTrue(StockTypeFilter.Supplement.matches("supp"))
        assertTrue(StockTypeFilter.Supplement.matches("supplement"))
        assertTrue(StockTypeFilter.Supplement.matches("SUPP"))
        assertTrue(StockTypeFilter.Supplement.matches("อาหารเสริม"))
    }

    @Test
    fun supplement_does_not_match_herb_or_current() {
        assertFalse(StockTypeFilter.Supplement.matches("herb"))
        assertFalse(StockTypeFilter.Supplement.matches("ยาสมุนไพร"))
        assertFalse(StockTypeFilter.Supplement.matches("cur"))
        assertFalse(StockTypeFilter.Supplement.matches("ยาแผนปัจจุบัน"))
        assertFalse(StockTypeFilter.Supplement.matches(null))
    }

    @Test
    fun current_matches_anything_that_is_not_herb_or_supp() {
        assertTrue(StockTypeFilter.Current.matches("cur"))
        assertTrue(StockTypeFilter.Current.matches("ยาแผนปัจจุบัน"))
        assertTrue(StockTypeFilter.Current.matches("modern"))
        assertTrue(StockTypeFilter.Current.matches(null))
        assertTrue(StockTypeFilter.Current.matches(""))
        assertTrue(StockTypeFilter.Current.matches("anything-without-keywords"))
    }

    @Test
    fun current_does_not_match_herb_or_supp_tokens() {
        assertFalse(StockTypeFilter.Current.matches("herb"))
        assertFalse(StockTypeFilter.Current.matches("ยาสมุนไพร"))
        assertFalse(StockTypeFilter.Current.matches("supp"))
        assertFalse(StockTypeFilter.Current.matches("อาหารเสริม"))
        assertFalse(StockTypeFilter.Current.matches("herbal supplement"))
    }

    @Test
    fun matches_is_case_insensitive_and_trims_whitespace() {
        assertTrue(StockTypeFilter.Herb.matches("  HERB  "))
        assertTrue(StockTypeFilter.Supplement.matches("  SUPP  "))
        assertTrue(StockTypeFilter.Current.matches("  cur  "))
    }

    @Test
    fun enum_has_expected_four_entries_with_labels() {
        assertEquals(4, StockTypeFilter.entries.size)
        StockTypeFilter.entries.forEach {
            assertTrue(it.label(PharmStringsTh).isNotBlank(), "${it.name} must have a non-blank label")
            assertTrue(it.label(PharmStringsEn).isNotBlank(), "${it.name} must have a non-blank en label")
        }
    }
}
