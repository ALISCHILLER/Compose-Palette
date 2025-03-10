package com.msa.composepalette.progressBar.circular

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FancyCircularProgressBar(
    progress: Float, // بین 0 تا 1
    primaryColor: Color = Color(0xFF00C853), // رنگ اصلی
    secondaryColor: Color = Color(0xFFBDBDBD), // رنگ ثانویه
    circleCount: Int = 10, // تعداد دایرهها
    modifier: Modifier = Modifier
) {
    // اضافه کردن انیمیشن برای نرمی حرکت
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = (size.minDimension - 16.dp.toPx()) / 2 // فاصله از مرکز

            for (i in 0 until circleCount) {
                val angle = 2 * PI * i / circleCount
                val x = centerX + radius * cos(angle).toFloat()
                val y = centerY + radius * sin(angle).toFloat()

                // محاسبه رنگ بر اساس پیشرفت
                val t = (i.toFloat() / circleCount).coerceIn(0f, 1f)
                val color = if (i < animatedProgress * circleCount) {
                    // گرادیان رنگی از روشن به تیره
                    Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.8f),
                            primaryColor.copy(alpha = 1f)
                        ),
                        center = Offset(x, y),
                        radius = 16.dp.toPx()
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(
                            secondaryColor.copy(alpha = 0.3f),
                            secondaryColor.copy(alpha = 0.5f)
                        ),
                        center = Offset(x, y),
                        radius = 16.dp.toPx()
                    )
                }

                // رسم دایره توپر با گرادیان
                drawCircle(
                    brush = color,
                    radius = 8.dp.toPx(),
                    center = Offset(x, y),
                    style = Fill
                )
            }
        }

        // متن درصد پیشرفت
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Black.copy(alpha = 0.8f)
        )
    }
}

// مثال استفاده
@Composable
fun ProgressBarDemo3() {
    var progress by remember { mutableStateOf(0f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        FancyCircularProgressBar(
            progress = progress,
            primaryColor = Color(0xFF00C853), // سبز مایل به آبی
            secondaryColor = Color(0xFFEEEEEE), // خاکستری روشن
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            progress += 0.1f
            if (progress > 1f) progress = 0f
        }) {
            Text("افزایش پیشرفت")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProgressBarDemo3() {
    ProgressBarDemo3()
}