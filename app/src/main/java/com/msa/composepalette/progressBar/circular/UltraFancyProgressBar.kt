package com.msa.composepalette.progressBar.circular

import android.graphics.Camera
import android.graphics.Paint as AndroidPaint
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

@Composable
fun UltraFancyProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
    rotationSpeed: Float = 0.5f
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1500, easing = FastOutSlowInEasing)
    )

    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (2000 / rotationSpeed.coerceIn(0.1f, 2f)).toInt(), // ✅ نامگذاری صریح
                easing = LinearEasing // ✅ نامگذاری صریح
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }
    if (animatedProgress >= 1f) {
        LaunchedEffect(Unit) {
            particles = List(particleCount) {
                Particle(
                    color = Color.hsv(Random.nextInt(0, 360).toFloat(), 0.8f, 1f),
                    velocity = Offset(
                        x = Random.nextDouble(-3.0, 3.0).toFloat(),
                        y = Random.nextDouble(-3.0, 3.0).toFloat()
                    ),
                    size = Random.nextInt(4, 12).dp.value
                )
            }
            delay(1000)
            particles = emptyList()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val center = Offset(canvasWidth / 2, canvasHeight / 2)
            val radius = min(canvasWidth, canvasHeight) / 2 - 24.dp.value

            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = radius + 8.dp.value,
                center = center,
                style = Stroke(4.dp.value, cap = StrokeCap.Round)
            )

            rotate(rotationAngle) {
                drawContext.canvas.nativeCanvas.apply {
                    save()
                    val camera = Camera().apply {
                        rotateX((20f * sin(rotationAngle * 0.017f)).toFloat())
                        rotateY((15f * cos(rotationAngle * 0.017f)).toFloat())
                    }
                    camera.applyToCanvas(this)
                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        radius = radius,
                        center = center,
                        style = Fill
                    )
                    restore()
                }
            }

            val gradientColors = listOf(
                Color.Magenta,
                Color.Cyan,
                Color.Yellow,
                Color.Magenta
            ).map { it.copy(alpha = 0.8f) }

            val gradientBrush = Brush.sweepGradient(
                colors = gradientColors,
                center = center
            )

            drawArc(
                brush = gradientBrush,
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(16.dp.value, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )

            particles.forEach { particle ->
                drawCircle(
                    color = particle.color,
                    radius = particle.size,
                    center = Offset(
                        x = center.x + particle.velocity.x * (1000 - particle.lifeTime) / 10f,
                        y = center.y + particle.velocity.y * (1000 - particle.lifeTime) / 10f
                    ),
                    style = Fill
                )
            }

            drawContext.canvas.saveLayer(
                Rect(0f, 0f, canvasWidth, canvasHeight),
                Paint().apply {
                    colorFilter = ColorFilter.lighting(
                        multiply = Color.White,
                        add = Color.White.copy(alpha = 0.4f)
                    )
                }
            )

            drawIntoCanvas { canvas ->
                val textPaint = AndroidPaint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 48.sp.value
                    textAlign = AndroidPaint.Align.CENTER
                    typeface = Typeface.DEFAULT_BOLD
                }
                canvas.nativeCanvas.drawText(
                    "${(animatedProgress * 100).toInt()}%",
                    center.x,
                    center.y + 16.dp.value,
                    textPaint
                )
            }
            drawContext.canvas.restore()
        }
    }
}

data class Particle(
    val color: Color,
    val velocity: Offset,
    val size: Float,
    var lifeTime: Int = 1000
)

@Composable
fun ProgressBarDemo5() {
    var progress by remember { mutableStateOf(0f) }
    var particleCount by remember { mutableStateOf(50) }
    var rotationSpeed by remember { mutableStateOf(0.5f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        UltraFancyProgressBar(
            progress = progress,
            particleCount = particleCount,
            rotationSpeed = rotationSpeed,
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("تنظیم پیشرفت:")
        Slider(
            value = progress,
            onValueChange = { progress = it },
            valueRange = 0f..1f
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("تعداد ذرات:")
        Slider(
            value = particleCount.toFloat(),
            onValueChange = { particleCount = it.toInt() },
            valueRange = 10f..200f
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("سرعت چرخش:")
        Slider(
            value = rotationSpeed,
            onValueChange = { rotationSpeed = it },
            valueRange = 0.1f..2f
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProgressBarDemo5() {
    ProgressBarDemo5()
}