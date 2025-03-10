package com.msa.composepalette.progressBar.circular

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import kotlin.math.roundToInt

@Composable
fun CircularProgressBar(progress: Float) {
    val totalSegments = 10
    val segmentAngle = 360f / totalSegments

    Box(
        modifier = Modifier
            .size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            for (i in 0 until totalSegments) {
                val angle = segmentAngle * i
                val isFilled = i < (progress * totalSegments).roundToInt()
                val color = when {
                    isFilled && i < 5 -> Color(0xFFBB86FC) // رنگ اول
                    isFilled -> Color(0xFF6200EE) // رنگ دوم
                    else -> Color(0xFFB0BEC5) // رنگ دایره‌های خالی
                }

                drawArc(
                    color = color,
                    startAngle = angle,
                    sweepAngle = segmentAngle,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx())
                )
            }
        }

        Text(
            text = "${(progress * 100).roundToInt()}%",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }
}

@Composable
fun ProgressBarDemo() {
    var progress by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition()
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing)
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressBar(progress = progress)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (progress < 1f) {
                progress += 0.1f
            }
        }) {
            Text("افزایش درصد")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // دکمه برای ریست کردن
        Button(onClick = {
            progress = 0f
        }) {
            Text("ریست")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProgressBarDemo() {
    ProgressBarDemo()
}