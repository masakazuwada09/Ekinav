package com.example.ui.map

import com.example.model.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.BuildConfig
import com.example.data.TokyoTransitData
import com.example.location.UserLocationState
import com.example.model.MapTilerStyle
import com.example.model.RailOperator
import com.example.model.Station
import com.example.model.WalkingRoute
import com.example.ui.theme.*
import kotlin.math.*

fun lon2tile(lon: Double, zoom: Int): Int =
    floor((lon + 180.0) / 360.0 * (1 shl zoom)).toInt()

fun lat2tile(lat: Double, zoom: Int): Int {
    val latRad = Math.toRadians(lat)
    val n = ln(tan(latRad) + 1.0 / cos(latRad))
    return floor((1.0 - n / Math.PI) / 2.0 * (1 shl zoom)).toInt()
}

fun tile2lon(x: Int, zoom: Int): Double =
    x.toDouble() / (1 shl zoom) * 360.0 - 180.0

fun tile2lat(y: Int, zoom: Int): Double {
    val n = Math.PI - 2.0 * Math.PI * y.toDouble() / (1 shl zoom)
    return Math.toDegrees(atan(sinh(n)))
}

@Composable
fun TransitCanvasMap(
    userLocation: UserLocationState,
    stations: List<Station>,
    selectedStation: Station?,
    highlightedStationIds: Set<String>,
    activeWalkingRoute: WalkingRoute?,
    filterOperators: Set<RailOperator>,
    isDarkTheme: Boolean = true,
    maptilerStyle: MapTilerStyle = MapTilerStyle.STREETS,
    onStationClick: (Station) -> Unit,
    modifier: Modifier = Modifier
) {
    // Center of Tokyo (Shinjuku-Shibuya-Tokyo triangle)
    var centerLat by remember { mutableDoubleStateOf(35.6760) }
    var centerLng by remember { mutableDoubleStateOf(139.7350) }
    var zoomScale by remember { mutableFloatStateOf(1.35f) }

    // Pulsing aura animation for user location puck
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseFraction"
    )

    // When selected station changes, gently center camera on it
    LaunchedEffect(selectedStation) {
        selectedStation?.let {
            centerLat = it.latitude
            centerLng = it.longitude
        }
    }

    val maptilerKey = BuildConfig.MAPTILER_API_KEY
    val hasValidMaptilerKey = maptilerKey.isNotBlank() && maptilerKey != "YOUR_MAPTILER_API_KEY"

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("transit_canvas_map")
            .background(if (isDarkTheme) Color(0xFF090E17) else Color(0xFFF1F5F9))
    ) {
        val hasValidBounds = constraints.hasBoundedWidth && constraints.hasBoundedHeight &&
                constraints.maxWidth > 10 && constraints.maxHeight > 10
        val canvasW = if (hasValidBounds) constraints.maxWidth.toFloat() else 1080f
        val canvasH = if (hasValidBounds) constraints.maxHeight.toFloat() else 1920f
        val density = LocalDensity.current


        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.6f, 4.5f)
                        // Adjust coordinate pan based on zoom level
                        val latDelta = (pan.y / (size.height * zoomScale * 12.0))
                        val lngDelta = (pan.x / (size.width * zoomScale * 12.0))
                        centerLat += latDelta
                        centerLng -= lngDelta
                    }
                }
                .pointerInput(stations, zoomScale, centerLat, centerLng) {
                    detectTapGestures { tapOffset ->
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        var closest: Station? = null
                        var minDistance = Float.MAX_VALUE

                        stations.forEach { station ->
                            val stationOffset = geoToScreen(
                                station.latitude,
                                station.longitude,
                                centerLat,
                                centerLng,
                                zoomScale,
                                w,
                                h
                            )
                            val dist = (stationOffset - tapOffset).getDistance()
                            // Tap radius of ~44dp in pixels
                            if (dist < 80f && dist < minDistance) {
                                minDistance = dist
                                closest = station
                            }
                        }

                        closest?.let { onStationClick(it) }
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            if (canvasW <= 10f || canvasH <= 10f) return@Canvas

            // 1. Draw Tokyo geography (only when MapTiler tile layer is not active)
            if (!hasValidMaptilerKey || maptilerStyle == MapTilerStyle.CANVAS) {
                drawTokyoGeography(centerLat, centerLng, zoomScale, canvasW, canvasH, isDarkTheme)
            }

            // 2. Draw Railway Track Lines
            drawTransitRailTracks(centerLat, centerLng, zoomScale, canvasW, canvasH, filterOperators, isDarkTheme)

            // 3. Draw Active Walking Route (if any)
            activeWalkingRoute?.let { route ->
                drawWalkingRoutePath(route, centerLat, centerLng, zoomScale, canvasW, canvasH)
            }

            // 4. Draw Stations
            stations.forEach { station ->
                // Check operator filter
                if (filterOperators.isEmpty() || station.operators.any { filterOperators.contains(it) }) {
                    val isSelected = station.id == selectedStation?.id
                    val isHighlighted = highlightedStationIds.contains(station.id)
                    drawStationMarker(
                        station = station,
                        centerLat = centerLat,
                        centerLng = centerLng,
                        zoom = zoomScale,
                        canvasW = canvasW,
                        canvasH = canvasH,
                        isSelected = isSelected,
                        isHighlighted = isHighlighted,
                        isDarkTheme = isDarkTheme
                    )
                }
            }

            // 5. Draw Precise User Location Puck
            drawUserLocationPuck(
                userLocation = userLocation,
                centerLat = centerLat,
                centerLng = centerLng,
                zoom = zoomScale,
                canvasW = canvasW,
                canvasH = canvasH,
                pulseFraction = pulseFraction
            )
        }
    }
}

// Convert Lat/Lng to Screen Canvas X/Y based on projection
fun geoToScreen(
    lat: Double,
    lng: Double,
    centerLat: Double,
    centerLng: Double,
    zoom: Float,
    canvasW: Float,
    canvasH: Float
): Offset {
    val scaleFactor = min(canvasW, canvasH) * 5500f * zoom
    // Mercator approximation for central Tokyo (lat ~ 35.68)
    val cosLat = cos(Math.toRadians(35.68))
    val x = (canvasW / 2f) + ((lng - centerLng) * cosLat * scaleFactor).toFloat()
    val y = (canvasH / 2f) - ((lat - centerLat) * scaleFactor).toFloat()
    return Offset(x, y)
}

private fun DrawScope.drawTokyoGeography(
    centerLat: Double,
    centerLng: Double,
    zoom: Float,
    w: Float,
    h: Float,
    isDarkTheme: Boolean
) {
    // Imperial Palace Green Zone (Kokyo) around 35.6852, 139.7528
    val imperialOffset = geoToScreen(35.6852, 139.7528, centerLat, centerLng, zoom, w, h)
    val imperialRadius = 45f * zoom
    drawCircle(
        color = if (isDarkTheme) Color(0xFF132B20) else Color(0xFFDCFCE7),
        radius = imperialRadius,
        center = imperialOffset
    )

    // Meiji Jingu / Yoyogi Park Green Zone around 35.6720, 139.6990
    val yoyogiOffset = geoToScreen(35.6720, 139.6990, centerLat, centerLng, zoom, w, h)
    drawCircle(
        color = if (isDarkTheme) Color(0xFF132B20) else Color(0xFFDCFCE7),
        radius = 35f * zoom,
        center = yoyogiOffset
    )

    // Sumida River blue ribbon
    val riverPoints = listOf(
        35.7350 to 139.8100,
        35.7180 to 139.8050,
        35.7118 to 139.7990,
        35.6980 to 139.7900,
        35.6850 to 139.7850,
        35.6600 to 139.7750,
        35.6400 to 139.7650
    )
    val riverPath = Path()
    riverPoints.forEachIndexed { idx, pt ->
        val offset = geoToScreen(pt.first, pt.second, centerLat, centerLng, zoom, w, h)
        if (idx == 0) riverPath.moveTo(offset.x, offset.y) else riverPath.lineTo(offset.x, offset.y)
    }
    drawPath(
        path = riverPath,
        color = if (isDarkTheme) Color(0xFF0D253D) else Color(0xFFBAE6FD),
        style = Stroke(width = 18f * zoom, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawTransitRailTracks(
    centerLat: Double,
    centerLng: Double,
    zoom: Float,
    w: Float,
    h: Float,
    filterOperators: Set<RailOperator>,
    isDarkTheme: Boolean
) {
    val trackWidth = (6f * zoom).coerceIn(3.5f, 14f)
    val railBgColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFCBD5E1)

    // Draw JR Yamanote Loop
    if (filterOperators.isEmpty() || filterOperators.contains(RailOperator.JR_EAST)) {
        val yamanotePath = Path()
        TokyoTransitData.yamanoteTrackPolyline.forEachIndexed { i, pt ->
            val off = geoToScreen(pt.first, pt.second, centerLat, centerLng, zoom, w, h)
            if (i == 0) yamanotePath.moveTo(off.x, off.y) else yamanotePath.lineTo(off.x, off.y)
        }
        // Background track bed
        drawPath(
            path = yamanotePath,
            color = railBgColor,
            style = Stroke(width = trackWidth + 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Foreground JR Yamanote signature green line
        drawPath(
            path = yamanotePath,
            color = JrYamanoteGreen,
            style = Stroke(width = trackWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw Chuo Rapid track
        val chuoPath = Path()
        TokyoTransitData.chuoTrackPolyline.forEachIndexed { i, pt ->
            val off = geoToScreen(pt.first, pt.second, centerLat, centerLng, zoom, w, h)
            if (i == 0) chuoPath.moveTo(off.x, off.y) else chuoPath.lineTo(off.x, off.y)
        }
        drawPath(
            path = chuoPath,
            color = JrChuoOrange,
            style = Stroke(width = trackWidth * 0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }

    // Tokyo Metro Lines
    if (filterOperators.isEmpty() || filterOperators.contains(RailOperator.TOKYO_METRO)) {
        // Metro Ginza Orange
        val ginzaPath = Path()
        TokyoTransitData.ginzaTrackPolyline.forEachIndexed { i, pt ->
            val off = geoToScreen(pt.first, pt.second, centerLat, centerLng, zoom, w, h)
            if (i == 0) ginzaPath.moveTo(off.x, off.y) else ginzaPath.lineTo(off.x, off.y)
        }
        drawPath(
            path = ginzaPath,
            color = MetroGinzaOrange,
            style = Stroke(width = trackWidth * 0.85f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Metro Marunouchi Red
        val marunouchiPath = Path()
        TokyoTransitData.marunouchiTrackPolyline.forEachIndexed { i, pt ->
            val off = geoToScreen(pt.first, pt.second, centerLat, centerLng, zoom, w, h)
            if (i == 0) marunouchiPath.moveTo(off.x, off.y) else marunouchiPath.lineTo(off.x, off.y)
        }
        drawPath(
            path = marunouchiPath,
            color = MetroMarunouchiRed,
            style = Stroke(width = trackWidth * 0.85f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

private fun DrawScope.drawWalkingRoutePath(
    route: WalkingRoute,
    centerLat: Double,
    centerLng: Double,
    zoom: Float,
    w: Float,
    h: Float
) {
    if (route.polylinePoints.isEmpty()) return

    if (route.visualType == RouteVisualType.NEAR_WALKING_DOTS) {
        // Draw Google Maps style walking dots
        route.polylinePoints.forEach { pt ->
            val off = geoToScreen(pt.first, pt.second, centerLat, centerLng, zoom, w, h)
            drawCircle(color = Color.White, radius = 5.5f * zoom, center = off)
            drawCircle(color = Color(0xFF1A73E8), radius = 3.8f * zoom, center = off)
        }
    } else {
        val routePath = Path()
        route.polylinePoints.forEachIndexed { i, pt ->
            val off = geoToScreen(pt.first, pt.second, centerLat, centerLng, zoom, w, h)
            if (i == 0) routePath.moveTo(off.x, off.y) else routePath.lineTo(off.x, off.y)
        }

        // Glow underlay
        drawPath(
            path = routePath,
            color = TransitCyan.copy(alpha = 0.35f),
            style = Stroke(width = 16f * zoom, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Solid line: Green for transit connecting line, Google blue for walking
        val lineColor = if (route.visualType == RouteVisualType.TRANSIT_CONNECTING) Color(0xFF00B04F) else Color(0xFF1A73E8)
        drawPath(
            path = routePath,
            color = lineColor,
            style = Stroke(width = 6f * zoom, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

private fun DrawScope.drawStationMarker(
    station: Station,
    centerLat: Double,
    centerLng: Double,
    zoom: Float,
    canvasW: Float,
    canvasH: Float,
    isSelected: Boolean,
    isHighlighted: Boolean,
    isDarkTheme: Boolean
) {
    val center = geoToScreen(station.latitude, station.longitude, centerLat, centerLng, zoom, canvasW, canvasH)

    val baseRadius = if (station.isMajorHub) (18f * zoom).coerceIn(14f, 26f) else (13f * zoom).coerceIn(9f, 18f)

    // Highlight / Selection Outer Ring
    if (isSelected || isHighlighted) {
        val haloRadius = baseRadius + 14f
        drawCircle(
            color = if (isSelected) TransitCyan.copy(alpha = 0.45f) else TransitAmber.copy(alpha = 0.45f),
            radius = haloRadius,
            center = center
        )
        drawCircle(
            color = if (isSelected) TransitCyan else TransitAmber,
            radius = haloRadius,
            center = center,
            style = Stroke(width = 3.5f)
        )
    }

    // Main station marker background
    val primaryColor = station.lines.firstOrNull()?.color ?: JrYamanoteGreen
    drawCircle(
        color = if (isDarkTheme) Color(0xFF0F172A) else Color.White,
        radius = baseRadius,
        center = center
    )
    drawCircle(
        color = primaryColor,
        radius = baseRadius - 2.5f,
        center = center,
        style = Stroke(width = 4.5f)
    )
    // Central core dot
    drawCircle(
        color = if (isSelected) TransitCyan else primaryColor,
        radius = baseRadius * 0.45f,
        center = center
    )

    // Station Name Badge (Kanji + English)
    val textOffsetY = center.y + baseRadius + 16f
    val badgeWidth = (station.nameEn.length * 14f + 36f).coerceAtLeast(100f)
    val badgeHeight = 36f
    val badgeRect = androidx.compose.ui.geometry.Rect(
        offset = Offset(center.x - badgeWidth / 2f, textOffsetY),
        size = Size(badgeWidth, badgeHeight)
    )

    // Badge container
    drawRoundRect(
        color = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.92f) else Color.White.copy(alpha = 0.95f),
        topLeft = badgeRect.topLeft,
        size = badgeRect.size,
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = if (isSelected) TransitCyan else primaryColor.copy(alpha = 0.6f),
        topLeft = badgeRect.topLeft,
        size = badgeRect.size,
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = if (isSelected) 2.5f else 1f)
    )

    // Line code badge dot on the left of station label
    drawCircle(
        color = primaryColor,
        radius = 5.5f,
        center = Offset(badgeRect.left + 14f, badgeRect.top + badgeHeight / 2f)
    )
}

private fun DrawScope.drawUserLocationPuck(
    userLocation: UserLocationState,
    centerLat: Double,
    centerLng: Double,
    zoom: Float,
    canvasW: Float,
    canvasH: Float,
    pulseFraction: Float
) {
    val center = geoToScreen(userLocation.latitude, userLocation.longitude, centerLat, centerLng, zoom, canvasW, canvasH)

    // Pulsing aura beacon
    val pulseMaxRadius = 48f * zoom
    val currentPulseRadius = 14f + pulseFraction * (pulseMaxRadius - 14f)
    val pulseAlpha = (1f - pulseFraction) * 0.45f
    drawCircle(
        color = TransitCyan.copy(alpha = pulseAlpha),
        radius = currentPulseRadius,
        center = center
    )

    // Heading cone indicator
    val headingRad = Math.toRadians((userLocation.headingDegrees - 90).toDouble())
    val coneDistance = 34f
    val coneAngleRad = Math.toRadians(35.0)

    val p1 = Offset(
        (center.x + cos(headingRad - coneAngleRad) * coneDistance).toFloat(),
        (center.y + sin(headingRad - coneAngleRad) * coneDistance).toFloat()
    )
    val p2 = Offset(
        (center.x + cos(headingRad + coneAngleRad) * coneDistance).toFloat(),
        (center.y + sin(headingRad + coneAngleRad) * coneDistance).toFloat()
    )

    val conePath = Path().apply {
        moveTo(center.x, center.y)
        lineTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        close()
    }
    drawPath(
        path = conePath,
        brush = Brush.radialGradient(
            colors = listOf(TransitCyan.copy(alpha = 0.65f), Color.Transparent),
            center = center,
            radius = coneDistance * 1.3f
        ),
        style = Fill
    )

    // Center puck dot
    drawCircle(
        color = Color.White,
        radius = 12f,
        center = center
    )
    drawCircle(
        color = TransitCyan,
        radius = 8.5f,
        center = center
    )
}
