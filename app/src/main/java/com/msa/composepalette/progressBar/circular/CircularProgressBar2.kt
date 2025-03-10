package com.msa.composepalette.progressBar.circular

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CircularProgressBar2(
    progress: Float, // بین 0 تا 1 (مثلاً 0.75 برای 75%)
    modifier: Modifier = Modifier
) {
    val circleCount = 10 // تعداد دایره‌ها
    val circleRadius = 8.dp // شعاع هر دایره
    val spacing = 4.dp // فاصله بین دایره‌ها
    val strokeWidth = 2.dp // ضخامت خطوط

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // دایره‌های کوچک
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = (size.minDimension - circleRadius.toPx() * 2 - spacing.toPx() * (circleCount - 1)) / 2

            for (i in 0 until circleCount) {
                val angle = 2 * Math.PI * i / circleCount
                val x = centerX + radius * kotlin.math.cos(angle).toFloat()
                val y = centerY + radius * kotlin.math.sin(angle).toFloat()

                // تعیین رنگ بر اساس پیشرفت
                val color = if (i < progress * circleCount) {
                    Color.Green.copy(alpha = 0.2f + 0.8f * (i / circleCount.toFloat()))
                } else {
                    Color.Gray
                }

                drawCircle(
                    color = color,
                    radius = circleRadius.toPx(),
                    center = Offset(x, y),
                    style = Stroke(width = strokeWidth.toPx())
                )
            }
        }

        // متن درصد پیشرفت
        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }
}

// مثال استفاده
@Composable
fun ProgressBarDemo2() {
    var progress by remember { mutableStateOf(0f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressBar2(progress = progress, modifier = Modifier.size(200.dp))

        Spacer(modifier = Modifier.height(16.dp))

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
fun PreviewProgressBarDemo2() {
    ProgressBarDemo2()
}