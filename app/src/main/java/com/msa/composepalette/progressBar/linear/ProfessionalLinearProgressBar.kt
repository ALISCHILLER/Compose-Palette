package com.msa.composepalette.progressBar.linear

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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun ProfessionalLinearProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColors: List<Color> = listOf(
        Color(0xFF6200EE),
        Color(0xFF03DAC5),
        Color(0xFFE91E63)
    ),
    animationDuration: Int = 1500
) {
    // انیمیشن گرادیان متحرک
    val infiniteTransition = rememberInfiniteTransition()
    val translateAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // انیمیشن پیشرفت با اسپرینگ
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    BoxWithConstraints(
        modifier = modifier
            .height(16.dp)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
    ) {
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
                .clip(RoundedCornerShape(50))
        )

        // متن پیشرفت با افکت برجستگی
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(4.dp)
                .graphicsLayer(
                    shadowElevation = with(LocalDensity.current) { 4.dp.toPx() }, // تبدیل dp به پیکسل
                    shape = RoundedCornerShape(8.dp),
                    clip = true
                )
                .clip(RoundedCornerShape(8.dp)),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.2f),
                    offset = Offset(2f, 2f),
                    blurRadius = 8f
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfessionalProgressBarPreview() {
    var progress by remember { mutableStateOf(0.5f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfessionalLinearProgressBar(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            progressColors = listOf(
                Color(0xFF6200EE),
                Color(0xFF03DAC5),
                Color(0xFFE91E63)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Slider(
            value = progress,
            onValueChange = { progress = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}