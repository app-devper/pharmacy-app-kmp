package app.devper.pharm.domain.parser

import app.devper.pharm.domain.param.StockCountInputLine

object StockCountInputBuilder {

    fun parsePending(counts: Map<String, String>): List<Pair<String, Int>> =
        counts.mapNotNull { (id, raw) ->
            val parsed = raw.toIntOrNull() ?: return@mapNotNull null
            if (parsed < 0) null else id to parsed
        }

    fun build(counts: Map<String, String>): List<StockCountInputLine> =
        parsePending(counts).map { (id, counted) ->
            StockCountInputLine(drugId = id, counted = counted)
        }
}
