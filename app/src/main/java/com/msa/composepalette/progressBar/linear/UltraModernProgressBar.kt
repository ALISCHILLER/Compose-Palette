@file:OptIn(ExperimentalComposeUiApi::class)

package com.msa.composepalette.progressBar.linear



// در ابتدای فایل:
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.ExperimentalComposeUiApi
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat

@Composable
fun UltraModernProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary,
    cornerRadius: Dp = 24.dp
) {
    // انیمیشن موج
    val waveAnimation = rememberInfiniteTransition()
    val wavePhase by waveAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing))
    )

    // انیمیشن پیشرفت
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500, easing = FastOutSlowInEasing)
    )

    // ذرات
    val particles = remember { mutableListOf<Particlee>() }
    val context = LocalContext.current
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        // دسترسی به constraints
        val maxWidth = constraints.maxWidth.toFloat()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { event ->
                    val touchProgress = event.x / maxWidth
                    if (touchProgress in 0f..1f) {
                        // ارتعاش
                        val vibrator = ContextCompat.getSystemService<Vibrator>(
                            context,
                            Vibrator::class.java
                        )
                        vibrator?.vibrate(
                            VibrationEffect.createOneShot(
                                50,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                        // اضافه کردن ذرات
                        particles.add(
                            Particlee(
                                position = Offset(event.x, with(density) { 24.dp.toPx() }),
                                color = primaryColor,
                                velocity = Offset(0f, -10f),
                                radius = 8f,
                                alpha = 1f
                            )
                        )
                    }
                    true
                }
        ) {
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, secondaryColor),
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    tileMode = TileMode.Clamp
                ),
                topLeft = Offset(0f, size.height * (1 - animatedProgress)),
                size = Size(size.width * animatedProgress, size.height * animatedProgress),
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )

            // رسم ذرات
            particles.forEachIndexed { index, particle ->
                drawCircle(
                    color = particle.color.copy(alpha = particle.alpha),
                    radius = particle.radius,
                    center = particle.position
                )
                particle.update()
                if (particle.radius <= 0) particles.removeAt(index)
            }
        }

        // متن پیشرفت
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp
        )
    }
}

// کلاس ذرات
data class Particlee(
    var position: Offset,
    val color: Color,
    val velocity: Offset,
    var radius: Float,
    var alpha: Float
) {
    fun update() {
        position += velocity
        radius *= 0.9f
        alpha *= 0.95f
    }
}


@Preview(showBackground = true)
@Composable
fun UltraModernPreview() {
    var progress by remember { mutableStateOf(0.7f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UltraModernProgressBar(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            primaryColor = Color(0xFF6200EE),
            secondaryColor = Color(0xFF03DAC5)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Slider(
            value = progress,
            onValueChange = { progress = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}