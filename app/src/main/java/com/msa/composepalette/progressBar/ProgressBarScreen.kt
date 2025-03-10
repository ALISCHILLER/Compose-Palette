package com.msa.composepalette.progressBar

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ProgressBarScreen() {
    var progress by remember { mutableStateOf(0.3f) }
    val infiniteTransition = rememberInfiniteTransition()
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // ProgressBar خطی
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ProgressBar دایرهای با انیمیشن
        CircularProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier.size(100.dp),
            strokeWidth = 8.dp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ProgressBar سفارشی با Canvas
        CustomCircularProgress(progress = progress)

        Spacer(modifier = Modifier.height(20.dp))

        // دکمه برای تغییر پیشرفت
        Button(onClick = { progress = (progress + 0.1f).coerceIn(0f, 1f) }) {
            Text("افزایش پیشرفت")
        }
    }
}

@Composable
fun CustomCircularProgress(
    progress: Float,
    strokeWidth: Dp = 12.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary
) {
    val stroke = with(LocalDensity.current) { strokeWidth.toPx() }
    Canvas(modifier = Modifier.size(150.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = (size.minDimension - stroke) / 2

        // دایره پسزمینه
        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = center,
            style = Stroke(stroke, cap = StrokeCap.Round)
        )

        // دایره پیشرفت
        val sweepAngle = progress * 360f
        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(stroke, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )
    }
}

@Composable
fun LoadingOverlay(isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 4.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressBarScreenPreview() {
    MaterialTheme {
        ProgressBarScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun CustomCircularProgressPreview() {
    MaterialTheme {
        CustomCircularProgress(progress = 0.6f)
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingOverlayPreview() {
    MaterialTheme {
        LoadingOverlay(isLoading = true)
    }
}