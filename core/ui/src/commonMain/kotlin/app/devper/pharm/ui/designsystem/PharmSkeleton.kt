package app.devper.pharm.ui.designsystem

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(6.dp),
) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    androidx.compose.foundation.layout.Box(
        modifier = modifier.background(
            color = pharmTokens.colors.borderSubtle.copy(alpha = alpha),
            shape = shape,
        ),
    )
}

@Composable
fun PharmListSkeleton(
    modifier: Modifier = Modifier,
    rows: Int = 6,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        repeat(rows) { SkeletonRow() }
    }
}

@Composable
private fun SkeletonRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PharmSkeleton(modifier = Modifier.size(40.dp), shape = CircleShape)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            PharmSkeleton(modifier = Modifier.fillMaxWidth(0.6f).height(12.dp))
            PharmSkeleton(modifier = Modifier.fillMaxWidth(0.9f).height(10.dp))
        }
    }
}
