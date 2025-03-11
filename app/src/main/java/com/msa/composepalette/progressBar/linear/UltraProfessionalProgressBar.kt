package com.msa.composepalette.progressBar.linear

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.material3.Slider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun UltraProfessionalProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
    progressColors: List<Color> = listOf(
        Color(0xFF6200EE),
        Color(0xFF03DAC5),
        Color(0xFFE91E63)
    ),
    cornerRadius: Dp = 16.dp,
    animationDuration: Int = 1500
) {
    // انیمیشن گرادیان متحرک
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerTranslate by infiniteTransition.animateFloat(
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
            .height(24.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        val progressWidth = animatedProgress * constraints.maxWidth

        // بخش پیشرفت با گرادیان و ذرات
        Canvas(modifier = Modifier.fillMaxSize()) {
            // گرادیان اصلی
            val brush = Brush.linearGradient(
                colors = progressColors,
                start = Offset(-shimmerTranslate, 0f),
                end = Offset(shimmerTranslate, 0f)
            )

            // رسم پیشرفت
            drawRoundRect(
                brush = brush,
                topLeft = Offset.Zero,
                size = Size(progressWidth, size.height),
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )

            // ذرات نورانی
            val particleCount = 10
            val particleRadius = 4.dp.toPx()
            for (i in 0 until particleCount) {
                val angle = (i * 36).toFloat()
                val x = (progressWidth * sin(angle * PI / 180)).toFloat()
                val y = (size.height * cos(angle * PI / 180)).toFloat()
                drawCircle(
                    color = progressColors[i % progressColors.size].copy(alpha = 0.5f),
                    radius = particleRadius,
                    center = Offset(x, y)
                )
            }
        }

        // گلو برجسته (اصلاحشده)
        Box(
            modifier = Modifier
                .offset(
                    x = with(LocalDensity.current) {
                        (progressWidth - 24.dp.toPx()).toDp()
                    }
                )
                .size(24.dp)
                .blur(16.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        // متن پیشرفت با جلوه سایه
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    shadowElevation = 8.dp.toPx()
                    shape = RoundedCornerShape(8.dp)
                    clip = true
                }
                .padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = Offset(2f, 2f),
                    blurRadius = 8f
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UltraProgressBarPreview() {
    var progress by remember { mutableStateOf(0.7f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UltraProfessionalProgressBar(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            progressColors = listOf(
                Color(0xFF6200EE),
                Color(0xFF03DAC5),
                Color(0xFFE91E63)
            ),
            cornerRadius = 24.dp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Slider(
            value = progress,
            onValueChange = { progress = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}