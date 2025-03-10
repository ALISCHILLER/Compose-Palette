package com.msa.composepalette.progressBar.circular

import android.graphics.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

// بعد از اصلاح
import android.graphics.Paint // ✅
import androidx.compose.ui.graphics.BlendMode

@Composable
fun HyperDriveProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    particleDensity: Int = 100,
    rotationSpeed: Float = 0.8f,
    enableHapticFeedback: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        )
    )
    // رسم گرادیان پیشرفت
    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(Color(0xFFBB86FC), Color(0xFF03DAC6), Color(0xFF6200EE))
    } else {
        listOf(Color(0xFF6200EE), Color(0xFF03DAC6), Color(0xFFBB86FC))
    }
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1800 / rotationSpeed.coerceIn(0.2f, 3f)).toInt(),
                easing = LinearEasing
            )
        )
    )

    var particles by remember { mutableStateOf<List<AdvancedParticle>>(emptyList()) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(animatedProgress) {
        if (animatedProgress >= 1f && enableHapticFeedback) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size
            val center = Offset(canvasSize.width / 2, canvasSize.height / 2)
            val radius = min(canvasSize.width, canvasSize.height) / 2 - 32.dp.value

            // رسم میدان انرژی
            drawContext.canvas.saveLayer(
                Rect(0f, 0f, canvasSize.width, canvasSize.height),
                androidx.compose.ui.graphics.Paint().apply {
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        color = Color.White.copy(alpha = 0.1f),
                        blendMode = BlendMode.SrcOver
                    )
                }
            )

            // رسم ذرات پویا
            particles.forEach { particle ->
                drawCircle(
                    color = particle.color,
                    radius = particle.size * particle.lifeCycle,
                    center = Offset(
                        center.x + particle.velocity.x * particle.lifeCycle * 4f,
                        center.y + particle.velocity.y * particle.lifeCycle * 4f
                    ),
                    style = Fill
                )
            }

            // رسم چرخش 3D
            rotate(rotationAngle) {
                drawContext.canvas.nativeCanvas.apply {
                    save()
                    val camera = Camera().apply {
                        rotateX(15f * sin(rotationAngle * 0.017f))
                        rotateY(10f * cos(rotationAngle * 0.017f))
                    }
                    camera.applyToCanvas(this)
                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        radius = radius * 1.1f,
                        center = center,
                        style = Fill
                    )
                    restore()
                }
            }



            drawArc(
                brush = Brush.sweepGradient(gradientColors, center),
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(18.dp.value, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )

            // رسم انعکاس نور
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = radius * 0.9f,
                center = center,
                style = Stroke(4.dp.value, cap = StrokeCap.Round)
            )

            // رسم متن با افکت نوری
            drawIntoCanvas { canvas ->
                val textPaint = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 48.sp.value
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
                    maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
                }
                canvas.nativeCanvas.drawText(
                    "${(animatedProgress * 100).toInt()}%",
                    center.x,
                    center.y + 8.dp.value,
                    textPaint
                )
            }
        }
    }

    // سیستم ذرات هوشمند
    LaunchedEffect(animatedProgress) {
        if (animatedProgress >= 1f) {
            particles = List(particleDensity) {
                AdvancedParticle(
                    color = Color.hsv(
                        hue = Random.nextInt(0, 360).toFloat(),
                        saturation = 0.7f,
                        value = 1f
                    ),
                    velocity = Offset(
                        x = Random.nextDouble(-5.0, 5.0).toFloat(),
                        y = Random.nextDouble(-5.0, 5.0).toFloat()
                    ),
                    size = Random.nextInt(4, 16).dp.value,
                    lifeCycle = Random.nextFloat() * 0.5f + 0.5f
                )
            }
            delay(800)
            particles = emptyList()
        }
    }
}

data class AdvancedParticle(
    val color: Color,
    val velocity: Offset,
    val size: Float,
    val lifeCycle: Float = 1f
)

@Composable
fun HyperDriveDemo() {
    var progress by remember { mutableStateOf(0f) }
    var particleDensity by remember { mutableStateOf(100) }
    var rotationSpeed by remember { mutableStateOf(0.8f) }
    var enableHaptic by remember { mutableStateOf(true) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        HyperDriveProgressBar(
            progress = progress,
            particleDensity = particleDensity,
            rotationSpeed = rotationSpeed,
            enableHapticFeedback = enableHaptic,
            modifier = Modifier.size(320.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("تنظیمات پیشرفته:", style = MaterialTheme.typography.titleMedium)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("پیشرفت:")
            Slider(
                value = progress,
                onValueChange = { progress = it },
                valueRange = 0f..1f
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("تراکم ذرات:")
            Slider(
                value = particleDensity.toFloat(),
                onValueChange = { particleDensity = it.toInt() },
                valueRange = 50f..200f
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("سرعت چرخش:")
            Slider(
                value = rotationSpeed,
                onValueChange = { rotationSpeed = it },
                valueRange = 0.2f..3f
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("فیدبک لمسی:")
            Switch(
                checked = enableHaptic,
                onCheckedChange = { enableHaptic = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHyperDrive() {
    HyperDriveDemo()
}