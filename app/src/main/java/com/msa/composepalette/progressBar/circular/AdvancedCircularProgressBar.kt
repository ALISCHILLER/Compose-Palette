package com.msa.composepalette.progressBar.circular

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AdvancedCircularProgressBar(
    progress: Float = 0.7f, // بین 0 تا 1
    indeterminate: Boolean = false,
    trackColor: Color = Color(0xFFE0E0E0),
    progressColors: List<Color> = listOf(Color(0xFF00C853), Color(0xFF00E676)),
    strokeWidth: Dp = 12.dp,
    modifier: Modifier = Modifier
) {
    // انیمیشن برای حالت نامشخص
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerSweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // انیمیشن برای حالت مشخص
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = (kotlin.math.min(canvasWidth, canvasHeight) - strokeWidth.toPx()) / 2
            val center = Offset(canvasWidth / 2, canvasHeight / 2)

            // رسم مسیر پسزمینه
            drawCircle(
                color = trackColor.copy(alpha = 0.3f),
                radius = radius,
                center = center,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // رسم پیشرفت
            val sweepAngle = if (indeterminate)
                (shimmerSweep * 360f) % 360f
            else
                (animatedProgress * 360f).coerceIn(0f, 360f)

            val gradientBrush = Brush.sweepGradient(
                colors = progressColors,
                center = center,
//                radius = radius
            )

            drawArc(
                brush = gradientBrush,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // افکت Shimmer
            if (indeterminate) {
                val shimmerAngle = (shimmerSweep * 360f - 90f) % 360f
                val shimmerRadius = radius * 1.1f
                val shimmerX = center.x + shimmerRadius * cos(Math.toRadians(shimmerAngle.toDouble())).toFloat()
                val shimmerY = center.y + shimmerRadius * sin(Math.toRadians(shimmerAngle.toDouble())).toFloat()

                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = strokeWidth.toPx() * 2,
                    center = Offset(shimmerX, shimmerY),
//                    style = Fill
                )
            }
        }

        // متن درصد با افکت سایه
        Text(
            text = if (indeterminate) "..." else "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.2f),
                    blurRadius = 4f
                )
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        )
    }
}

// مثال استفاده با کنترل‌های تعاملی
@Composable
fun ProgressBarDemo4() {
    var progress by remember { mutableStateOf(0.5f) }
    var isIndeterminate by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        AdvancedCircularProgressBar(
            progress = progress,
            indeterminate = isIndeterminate,
            strokeWidth = 16.dp,
            modifier = Modifier.size(280.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("تنظیم پیشرفت:")
        Slider(
            value = progress,
            onValueChange = { progress = it },
            valueRange = 0f..1f
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("حالت نامشخص:")
            Switch(
                checked = isIndeterminate,
                onCheckedChange = { isIndeterminate = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProgressBarDemo4() {
    ProgressBarDemo4()
}