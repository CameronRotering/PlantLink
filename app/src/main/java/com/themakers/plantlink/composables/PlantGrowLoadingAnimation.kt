package com.themakers.plantlink.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.themakers.plantlink.R

@Composable
fun PlantGrowLoadingAnimation(
    modifier: Modifier = Modifier,
    visibility: Boolean = false,
    potColor: Color = Color(0xFFE2725B) // Terracotta color
) {
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxWidth(),
        visible = visibility
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "plantGrowth")

        // Animate scale from 0f to 1f
        val scale by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000),
                repeatMode = RepeatMode.Restart
            ),
            label = "scale"
        )

        // Animate opacity along with scale to make it appear/disappear smoothly at restart
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000),
                repeatMode = RepeatMode.Restart
            ),
            label = "alpha"
        )

        Box(
            modifier = modifier
                .size(100.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Plant Icon (Growing)
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.sharp_psychiatry_24),
                contentDescription = "Growing Plant",
                tint = Color(0xFF3DA82C), // Green color matching xml
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                        translationY = -30.dp.toPx() // Move up to sit in the pot
                        transformOrigin = TransformOrigin(0.5f, 1f) // Grow from bottom
                    }
            )

            // Clay Pot (Static)
            Canvas(modifier = Modifier
                .size(width = 50.dp, height = 40.dp)
                .align(Alignment.BottomCenter)
            ) {
                val potPath = Path().apply {
                    // Draw a simple pot shape
                    moveTo(size.width * 0.2f, size.height) // Bottom left
                    lineTo(size.width * 0.8f, size.height) // Bottom right
                    lineTo(size.width, 0f) // Top right
                    lineTo(0f, 0f) // Top left
                    close()
                }
                drawPath(path = potPath, color = potColor)

                // Draw pot rim
                drawRect(
                    color = potColor.copy(alpha = 0.9f), // Slightly darker for rim
                    topLeft = Offset(-size.width * 0.1f, -size.height * 0.2f),
                    size = Size(size.width * 1.2f, size.height * 0.2f)
                )
            }
        }
    }
}

@Preview
@Composable
fun GrowLoadingAnimationPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PlantGrowLoadingAnimation(visibility = true)
    }
}