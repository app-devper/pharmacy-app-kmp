package app.devper.pharm.domain.extension

import app.devper.pharm.domain.param.StockCountInputLine

fun parsePendingStockCounts(counts: Map<String, String>): List<Pair<String, Int>> =
    counts.mapNotNull { (id, raw) ->
        val parsed = raw.toIntOrNull() ?: return@mapNotNull null
        if (parsed < 0) null else id to parsed
    }

fun buildStockCountInput(counts: Map<String, String>): List<StockCountInputLine> =
    parsePendingStockCounts(counts).map { (id, counted) ->
        StockCountInputLine(drugId = id, counted = counted)
    }
