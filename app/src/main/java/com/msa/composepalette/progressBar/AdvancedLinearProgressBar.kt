package com.msa.composepalette.progressBar

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AdvancedLinearProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColors: List<Color> = listOf(Color.Magenta, Color.Cyan),
    animationDuration: Int = 1500
) {
    // انیمیشن گرادیان متحرک
    val infiniteTransition = rememberInfiniteTransition()
    val translateAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // انیمیشن پیشرفت
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    BoxWithConstraints(
        modifier = modifier
            .height(12.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(backgroundColor)
    ) {
        // محاسبه عرض پیشرفت با استفاده از constraints.maxWidth
        val progressWidth = animatedProgress * constraints.maxWidth

        // بخش پیشرفت با گرادیان متحرک
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(with(LocalDensity.current) { progressWidth.toDp() })
                .background(
                    brush = Brush.linearGradient(
                        colors = progressColors,
                        start = Offset(-translateAnimation.value, 0f),
                        end = Offset(translateAnimation.value, 0f)
                    )
                )
                .clip(RoundedCornerShape(25.dp))
        )

        // متن درصد پیشرفت
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

// برای دریافت عرض Modifier
//@Composable
//private fun Modifier.calculateProgressWidth(): Float {
//    val constraints = BoxWithConstraints { constraints }
//    return if (this == Modifier.fillMaxWidth()) {
//        constraints.maxWidth.toFloat()
//    } else {
//        0f
//    }
//}

@Preview(showBackground = true)
@Composable
fun AdvancedProgressBarPreview() {
    var progress by remember { mutableStateOf(0.5f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AdvancedLinearProgressBar(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            progressColors = listOf(Color.Magenta, Color.Cyan)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Slider(
            value = progress,
            onValueChange = { progress = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

