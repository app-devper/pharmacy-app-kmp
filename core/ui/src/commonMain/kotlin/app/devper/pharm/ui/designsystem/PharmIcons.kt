package app.devper.pharm.ui.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

private fun strokeIcon(name: String, vararg svgPaths: String): ImageVector {
    val builder = ImageVector.Builder(
        name = "Pharm$name",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
    for (svg in svgPaths) {
        builder.addPath(
            pathData = PathParser().parsePathString(svg).toNodes(),
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.75f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero,
        )
    }
    return builder.build()
}

private fun filledIcon(name: String, vararg svgPaths: String): ImageVector {
    val builder = ImageVector.Builder(
        name = "Pharm$name",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
    for (svg in svgPaths) {
        builder.addPath(
            pathData = PathParser().parsePathString(svg).toNodes(),
            fill = SolidColor(Color.Black),
            pathFillType = PathFillType.NonZero,
        )
    }
    return builder.build()
}

private fun mixedIcon(
    name: String,
    strokePaths: List<String>,
    filledPaths: List<String>,
): ImageVector {
    val builder = ImageVector.Builder(
        name = "Pharm$name",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
    for (svg in strokePaths) {
        builder.addPath(
            pathData = PathParser().parsePathString(svg).toNodes(),
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.75f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero,
        )
    }
    for (svg in filledPaths) {
        builder.addPath(
            pathData = PathParser().parsePathString(svg).toNodes(),
            fill = SolidColor(Color.Black),
            pathFillType = PathFillType.NonZero,
        )
    }
    return builder.build()
}

private fun circle(cx: Float, cy: Float, r: Float): String =
    "M${cx - r} ${cy} a${r} ${r} 0 1 0 ${r * 2} 0 a${r} ${r} 0 1 0 ${-r * 2} 0 Z"

private fun rect(x: Float, y: Float, w: Float, h: Float, rx: Float = 0f): String =
    if (rx <= 0f) "M$x $y h$w v$h h${-w} Z"
    else "M${x + rx} $y h${w - 2 * rx} a$rx $rx 0 0 1 $rx $rx v${h - 2 * rx} a$rx $rx 0 0 1 ${-rx} $rx h${-(w - 2 * rx)} a$rx $rx 0 0 1 ${-rx} ${-rx} v${-(h - 2 * rx)} a$rx $rx 0 0 1 $rx ${-rx} Z"

object PharmIcons {

    val Pill: ImageVector = strokeIcon(
        name = "Pill",
        "M8.5 3.5 l12 12 a4.95 4.95 0 0 1 -7 7 l-12 -12 a4.95 4.95 0 0 1 7 -7 Z",
        "M8.5 3.5 l7 7 l-7 7",
    )

    val Sell: ImageVector = strokeIcon(
        name = "Sell",
        "M3 4 h2 l2.4 11.2 a2 2 0 0 0 2 1.6 h7.7 a2 2 0 0 0 2 -1.5 L21 8 H6",
        circle(9.5f, 20f, 1.3f),
        circle(17f, 20f, 1.3f),
    )

    val SalesHistory: ImageVector = strokeIcon(
        name = "SalesHistory",
        "M6 3 h12 v18 l-2 -1.5 l-2 1.5 l-2 -1.5 l-2 1.5 l-2 -1.5 L6 21 Z",
        "M9 8 h6 M9 12 h6 M9 16 h4",
    )

    val Stock: ImageVector = strokeIcon(
        name = "Stock",
        "M3 7.5 L12 3 l9 4.5 v9 L12 21 l-9 -4.5 Z",
        "M3 7.5 L12 12 l9 -4.5 M12 12 v9",
    )

    val StockCount: ImageVector = strokeIcon(
        name = "StockCount",
        rect(4f, 3f, 16f, 18f, rx = 2f),
        "M8 7 h8 M8 11 h2 M12 11 h2 M16 11 h0 M8 15 h2 M12 15 h2 M16 15 h0 M8 19 h2 M12 19 h6",
    )

    val Expiry: ImageVector = strokeIcon(
        name = "Expiry",
        circle(12f, 12f, 9f),
        "M12 7 v5 l3 2",
        "M19.5 4.5 L21 6 M4.5 4.5 L3 6",
    )

    val Movements: ImageVector = mixedIcon(
        name = "Movements",
        strokePaths = listOf("M4 6 h16 M4 12 h16 M4 18 h10"),
        filledPaths = listOf(
            circle(4f, 6f, 0.6f),
            circle(4f, 12f, 0.6f),
            circle(4f, 18f, 0.6f),
        ),
    )

    val OfflineSync: ImageVector = strokeIcon(
        name = "OfflineSync",
        "M3.5 12 a8.5 8.5 0 0 1 14.5 -6 L21 9 M20.5 12 a8.5 8.5 0 0 1 -14.5 6 L3 15",
        "M21 4 v5 h-5 M3 20 v-5 h5",
    )

    val Imports: ImageVector = strokeIcon(
        name = "Imports",
        "M12 3 v12",
        "M8 11 l4 4 l4 -4",
        "M4 17 v2 a2 2 0 0 0 2 2 h12 a2 2 0 0 0 2 -2 v-2",
    )

    val Suppliers: ImageVector = strokeIcon(
        name = "Suppliers",
        "M3 21 V8 l6 -3 v4 l5 -2 v6 l7 -2 v10 Z",
        "M3 21 h18",
        "M8 17 h2 M14 17 h2",
    )

    val Customers: ImageVector = strokeIcon(
        name = "Customers",
        circle(9f, 8f, 3.2f),
        circle(17f, 9f, 2.4f),
        "M3.5 20 c0.5 -3.3 3 -5 5.5 -5 s5 1.7 5.5 5",
        "M14.5 20 c0.3 -2.4 1.8 -4 3.5 -4 s2.7 1 3 3",
    )

    val Reports: ImageVector = strokeIcon(
        name = "Reports",
        "M3 21 h18",
        rect(5f, 11f, 3.5f, 9f, rx = 0.6f),
        rect(10.25f, 7f, 3.5f, 13f, rx = 0.6f),
        rect(15.5f, 14f, 3.5f, 6f, rx = 0.6f),
    )

    val Profit: ImageVector = strokeIcon(
        name = "Profit",
        "M4 17 l5 -5 l3 3 l7 -7",
        "M14 8 h5 v5",
    )

    val KyForms: ImageVector = strokeIcon(
        name = "KyForms",
        "M9 4 h6 a1 1 0 0 1 1 1 v1 h2 a1 1 0 0 1 1 1 v13 a1 1 0 0 1 -1 1 H6 a1 1 0 0 1 -1 -1 V7 a1 1 0 0 1 1 -1 h2 V5 a1 1 0 0 1 1 -1 Z",
        "M9 4 v2 h6 V4",
        "M9 11 h6 M9 15 h6 M9 19 h3",
    )

    val Users: ImageVector = strokeIcon(
        name = "Users",
        "M12 3 L4 6 v5 c0 5 3.5 9 8 10 c4.5 -1 8 -5 8 -10 V6 Z",
        "M9.5 12.5 L11 14 l3.5 -3.5",
    )

    val Settings: ImageVector = strokeIcon(
        name = "Settings",
        circle(12f, 12f, 3f),
        "M19.4 15 a1.7 1.7 0 0 0 0.3 1.8 l0.1 0.1 a2 2 0 1 1 -2.8 2.8 l-0.1 -0.1 a1.7 1.7 0 0 0 -1.8 -0.3 a1.7 1.7 0 0 0 -1 1.5 V21 a2 2 0 0 1 -4 0 v-0.1 a1.7 1.7 0 0 0 -1 -1.5 a1.7 1.7 0 0 0 -1.8 0.3 l-0.1 0.1 a2 2 0 1 1 -2.8 -2.8 l0.1 -0.1 a1.7 1.7 0 0 0 0.3 -1.8 a1.7 1.7 0 0 0 -1.5 -1 H3 a2 2 0 0 1 0 -4 h0.1 a1.7 1.7 0 0 0 1.5 -1 a1.7 1.7 0 0 0 -0.3 -1.8 l-0.1 -0.1 a2 2 0 1 1 2.8 -2.8 l0.1 0.1 a1.7 1.7 0 0 0 1.8 0.3 H9 a1.7 1.7 0 0 0 1 -1.5 V3 a2 2 0 0 1 4 0 v0.1 a1.7 1.7 0 0 0 1 1.5 a1.7 1.7 0 0 0 1.8 -0.3 l0.1 -0.1 a2 2 0 1 1 2.8 2.8 l-0.1 0.1 a1.7 1.7 0 0 0 -0.3 1.8 V9 a1.7 1.7 0 0 0 1.5 1 H21 a2 2 0 0 1 0 4 h-0.1 a1.7 1.7 0 0 0 -1.5 1 Z",
    )

    val Help: ImageVector = strokeIcon(
        name = "Help",
        "M4 4.5 A2.5 2.5 0 0 1 6.5 2 H19 a1 1 0 0 1 1 1 v17 a1 1 0 0 1 -1 1 H6.5 A2.5 2.5 0 0 1 4 18.5 Z",
        "M4 18.5 A2.5 2.5 0 0 1 6.5 16 H20",
        "M8 7 h7 M8 11 h5",
    )

    val Logout: ImageVector = strokeIcon(
        name = "Logout",
        "M15 3 h4 a2 2 0 0 1 2 2 v14 a2 2 0 0 1 -2 2 h-4",
        "M10 17 l5 -5 l-5 -5",
        "M15 12 H3",
    )

    val Hamburger: ImageVector = strokeIcon(
        name = "Hamburger",
        "M3 6 h18 M3 12 h18 M3 18 h18",
    )

    val Sun: ImageVector = strokeIcon(
        name = "Sun",
        circle(12f, 12f, 4f),
        "M12 2 v2 M12 20 v2 M2 12 h2 M20 12 h2",
        "M4.93 4.93 l1.41 1.41 M17.66 17.66 l1.41 1.41 M4.93 19.07 l1.41 -1.41 M17.66 6.34 l1.41 -1.41",
    )

    val Moon: ImageVector = strokeIcon(
        name = "Moon",
        "M21 12.79 A9 9 0 1 1 11.21 3 A7 7 0 0 0 21 12.79 Z",
    )

    val Search: ImageVector = strokeIcon(
        name = "Search",
        circle(11f, 11f, 7f),
        "M20 20 l-3.5 -3.5",
    )

    val Scan: ImageVector = strokeIcon(
        name = "Scan",
        "M4 7 V5 a1 1 0 0 1 1 -1 h2 M17 4 h2 a1 1 0 0 1 1 1 v2 M20 17 v2 a1 1 0 0 1 -1 1 h-2 M7 20 H5 a1 1 0 0 1 -1 -1 v-2",
        "M4 12 h16",
    )

    val More: ImageVector = filledIcon(
        name = "More",
        circle(12f, 5f, 1.1f),
        circle(12f, 12f, 1.1f),
        circle(12f, 19f, 1.1f),
    )

    val Person: ImageVector = strokeIcon(
        name = "Person",
        circle(12f, 8f, 3.5f),
        "M5 21 v-1.5 A5 5 0 0 1 10 14.5 h4 a5 5 0 0 1 5 5 V21",
    )

    val Pause: ImageVector = strokeIcon(
        name = "Pause",
        rect(6f, 4f, 4f, 16f, rx = 1f),
        rect(14f, 4f, 4f, 16f, rx = 1f),
    )

    val Plus: ImageVector = strokeIcon(
        name = "Plus",
        "M12 5 v14 M5 12 h14",
    )

    val Minus: ImageVector = strokeIcon(
        name = "Minus",
        "M5 12 h14",
    )

    val Close: ImageVector = strokeIcon(
        name = "Close",
        "M6 6 l12 12 M18 6 L6 18",
    )

    val Backspace: ImageVector = strokeIcon(
        name = "Backspace",
        "M20 6 H10 L4 12 L10 18 H20 Z",
        "M16 9.5 L12 14.5 M12 9.5 L16 14.5",
    )

    val Check: ImageVector = strokeIcon(
        name = "Check",
        "M5 12 l5 5 L20 7",
    )

    val Warning: ImageVector = strokeIcon(
        name = "Warning",
        "M10.3 3.7 L1.8 18 a2 2 0 0 0 1.7 3 h17 a2 2 0 0 0 1.7 -3 L13.7 3.7 a2 2 0 0 0 -3.4 0 Z",
        "M12 9 v4 M12 17 h0.01",
    )

    val Info: ImageVector = strokeIcon(
        name = "Info",
        circle(12f, 12f, 9f),
        "M12 11 v5 M12 8 h0.01",
    )

    val Print: ImageVector = strokeIcon(
        name = "Print",
        "M7 9 V3 h10 v6",
        rect(4f, 9f, 16f, 8f, rx = 1.5f),
        "M7 21 v-4 h10 v4 Z",
        circle(17f, 13f, 0.5f),
    )

    val Excel: ImageVector = strokeIcon(
        name = "Excel",
        "M14 3 H7 a2 2 0 0 0 -2 2 v14 a2 2 0 0 0 2 2 h10 a2 2 0 0 0 2 -2 V8 Z",
        "M14 3 v5 h5",
        "M9 13 l4 4 M13 13 l-4 4",
    )

    val FilePdf: ImageVector = strokeIcon(
        name = "FilePdf",
        "M14 3 H7 a2 2 0 0 0 -2 2 v14 a2 2 0 0 0 2 2 h10 a2 2 0 0 0 2 -2 V8 Z",
        "M14 3 v5 h5",
        "M8.5 17 v-3.2 h0.8 a1 1 0 0 1 0 2 h-0.8",
        "M12 13.8 v3.2 M12 13.8 h1.8 M12 15.4 h1.4",
        "M16 17 v-3.2 h1.6",
    )

    val ReturnArrow: ImageVector = strokeIcon(
        name = "ReturnArrow",
        "M9 14 L4 9 l5 -5",
        "M4 9 h11 a5 5 0 0 1 5 5 v6",
    )

    val ChevronLeft: ImageVector = strokeIcon(
        name = "ChevronLeft",
        "M15 6 L9 12 L15 18",
    )

    val ChevronRight: ImageVector = strokeIcon(
        name = "ChevronRight",
        "M9 6 L15 12 L9 18",
    )

    val Pencil: ImageVector = strokeIcon(
        name = "Pencil",
        "M16.5 3.5 L20.5 7.5 L8 20 l-5 1 l1 -5 Z",
        "M14 6 l4 4",
    )

    val Trash: ImageVector = strokeIcon(
        name = "Trash",
        "M4 6 h16",
        "M19 6 v14 a2 2 0 0 1 -2 2 H7 a2 2 0 0 1 -2 -2 V6",
        "M9 6 V4 a2 2 0 0 1 2 -2 h2 a2 2 0 0 1 2 2 v2",
        "M10 11 v6 M14 11 v6",
    )

    val Ban: ImageVector = strokeIcon(
        name = "Ban",
        circle(12f, 12f, 9f),
        "M5.5 5.5 l13 13",
    )

    val AlertCircle: ImageVector = strokeIcon(
        name = "AlertCircle",
        circle(12f, 12f, 9f),
        "M12 8 v5 M12 16 h0.01",
    )
}
