package com.example.ui.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun GoogleMapsLocationPuck(
    screenX: Float,
    screenY: Float,
    headingDegrees: Float,
    accuracyMeters: Float,
    pulseFraction: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val beamRadiusPx = with(density) { 68.dp.toPx() }
    val puckOuterRadiusPx = with(density) { 11.5.dp.toPx() }
    val puckInnerRadiusPx = with(density) { 8.5.dp.toPx() }
    val puckCenterDotRadiusPx = with(density) { 2.5.dp.toPx() }
    val pulseMaxRadiusPx = with(density) { 32.dp.toPx() }

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(screenX, screenY)

        // 1. Draw Google Maps Flashlight / Calibrated Heading Cone
        drawGoogleMapsHeadingCone(
            center = center,
            headingDegrees = headingDegrees,
            beamRadius = beamRadiusPx
        )

        // 2. Draw Pulsing Radar Aura
        val pulseRadius = puckOuterRadiusPx + (pulseMaxRadiusPx - puckOuterRadiusPx) * pulseFraction
        val pulseAlpha = (1f - pulseFraction) * 0.35f
        drawCircle(
            color = Color(0xFF4285F4).copy(alpha = pulseAlpha),
            radius = pulseRadius,
            center = center
        )

        // 3. Draw Google Maps Location Puck
        // Soft drop shadow
        drawCircle(
            color = Color(0x33000000),
            radius = puckOuterRadiusPx + 1.5f,
            center = Offset(center.x, center.y + 2f)
        )

        // Crisp white ring
        drawCircle(
            color = Color.White,
            radius = puckOuterRadiusPx,
            center = center
        )

        // Inner Vibrant Google Blue core (#1A73E8)
        drawCircle(
            color = Color(0xFF1A73E8),
            radius = puckInnerRadiusPx,
            center = center
        )

        // Center micro white highlight dot
        drawCircle(
            color = Color.White,
            radius = puckCenterDotRadiusPx,
            center = center
        )
    }
}

private fun DrawScope.drawGoogleMapsHeadingCone(
    center: Offset,
    headingDegrees: Float,
    beamRadius: Float
) {
    // Flashlight beam spread: 50 degrees total (±25 degrees from heading)
    val halfSpread = 25f
    // In Android canvas: 0 degrees is East (positive X), 90 is South (positive Y), 270 is North (negative Y).
    // Heading 0 is North. So canvas angle = (heading - 90).
    val startAngle = headingDegrees - 90f - halfSpread
    val sweepAngle = halfSpread * 2f

    val arcRect = androidx.compose.ui.geometry.Rect(
        left = center.x - beamRadius,
        top = center.y - beamRadius,
        right = center.x + beamRadius,
        bottom = center.y + beamRadius
    )

    val conePath = Path().apply {
        moveTo(center.x, center.y)
        arcTo(
            rect = arcRect,
            startAngleDegrees = startAngle,
            sweepAngleDegrees = sweepAngle,
            forceMoveTo = false
        )
        close()
    }

    // Radial gradient from bright translucent blue at center to completely transparent at edge
    val beamBrush = Brush.radialGradient(
        colors = listOf(
            Color(0x994285F4), // 60% opacity Google blue
            Color(0x444285F4),
            Color(0x154285F4),
            Color(0x004285F4)  // 0% opacity edge
        ),
        center = center,
        radius = beamRadius
    )

    drawPath(
        path = conePath,
        brush = beamBrush,
        style = Fill
    )
}
