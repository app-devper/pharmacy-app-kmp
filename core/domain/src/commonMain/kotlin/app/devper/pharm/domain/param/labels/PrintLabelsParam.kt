package app.devper.pharm.domain.param.labels

import app.devper.pharm.domain.model.LabelLine
import app.devper.pharm.domain.model.LabelSize

data class PrintLabelsParam(
    val size: LabelSize,
    val lines: List<LabelLine>,
)
