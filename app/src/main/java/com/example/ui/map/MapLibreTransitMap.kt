package com.example.ui.map

import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.PointF
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.BuildConfig
import com.example.location.LocationService
import com.example.location.UserLocationState
import com.example.model.*
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.Polyline
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import kotlin.math.*

@Composable
fun MapLibreTransitMap(
    userLocation: UserLocationState,
    stations: List<Station>,
    selectedStation: Station?,
    highlightedStationIds: Set<String>,
    activeWalkingRoute: WalkingRoute?,
    filterOperators: Set<RailOperator>,
    isDarkTheme: Boolean = true,
    maptilerStyle: MapTilerStyle = MapTilerStyle.STREETS,
    recenterTrigger: Long = 0L,
    onStationClick: (Station) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val apiKey = BuildConfig.MAPTILER_API_KEY

    var maplibreMapInstance by remember { mutableStateOf<MapLibreMap?>(null) }
    var currentStyleUrl by remember { mutableStateOf("") }
    val currentMarkers = remember { mutableStateListOf<Marker>() }

    // Multi-segment Route Polylines (casing + primary lines for each train/walk leg)
    val currentRoutePolylines = remember { mutableStateListOf<Polyline>() }

    // Screen coordinates and camera bearing for Google Maps Location Puck
    var userScreenPos by remember { mutableStateOf<PointF?>(null) }
    var cameraBearing by remember { mutableFloatStateOf(0f) }

    // Walking Route Dots: Geo waypoints and dynamic 60fps screen projections
    var walkingDotGeoPoints by remember { mutableStateOf<List<Pair<Double, Double>>>(emptyList()) }
    var walkingDotsScreenOffsets by remember { mutableStateOf<List<PointF>>(emptyList()) }

    // Transfer station screen location (for multi-operator train connection, e.g. JR Green -> Metro Blue)
    var transferStationGeo by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var transferStationScreenPos by remember { mutableStateOf<PointF?>(null) }

    // Continuous radar pulse animation for Google Maps location puck
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseFraction"
    )

    // MapView instance created once per composition
    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
        }
    }

    // Forward Android lifecycle events to MapView
    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try {
                mapView.onDestroy()
            } catch (_: Throwable) {}
        }
    }

    // Determine MapTiler vector style URL based on active style selection and theme
    val targetStyleUrl = remember(maptilerStyle, isDarkTheme, apiKey) {
        val keyParam = if (apiKey.isNotBlank() && apiKey != "YOUR_MAPTILER_API_KEY") "?key=$apiKey" else ""
        when {
            maptilerStyle == MapTilerStyle.SATELLITE -> "https://api.maptiler.com/maps/satellite-v2/style.json$keyParam"
            maptilerStyle == MapTilerStyle.CANVAS -> "https://api.maptiler.com/maps/basic-v2/style.json$keyParam"
            maptilerStyle == MapTilerStyle.DARK || isDarkTheme -> "https://api.maptiler.com/maps/dataviz-dark/style.json$keyParam"
            else -> "https://api.maptiler.com/maps/streets-v2/style.json$keyParam"
        }
    }

    // Switch style when user toggles style FAB or theme changes
    LaunchedEffect(targetStyleUrl, maplibreMapInstance) {
        val map = maplibreMapInstance ?: return@LaunchedEffect
        if (targetStyleUrl != currentStyleUrl) {
            currentStyleUrl = targetStyleUrl
            map.setStyle(targetStyleUrl) {
                // Style loaded successfully
            }
        }
    }

    // Center camera when selected station changes
    LaunchedEffect(selectedStation, maplibreMapInstance) {
        val map = maplibreMapInstance ?: return@LaunchedEffect
        selectedStation?.let { station ->
            val target = LatLng(station.latitude, station.longitude)
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(target, 14.5),
                1000
            )
        }
    }

    // Animate camera to user location on recenter trigger
    LaunchedEffect(recenterTrigger, maplibreMapInstance) {
        if (recenterTrigger > 0L) {
            val map = maplibreMapInstance ?: return@LaunchedEffect
            val target = LatLng(userLocation.latitude, userLocation.longitude)
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(target, 15.0),
                900
            )
        }
    }

    // Update screen coordinates of user location whenever coordinates change
    LaunchedEffect(userLocation.latitude, userLocation.longitude, maplibreMapInstance) {
        val map = maplibreMapInstance ?: return@LaunchedEffect
        try {
            userScreenPos = map.projection.toScreenLocation(LatLng(userLocation.latitude, userLocation.longitude))
            cameraBearing = map.cameraPosition.bearing.toFloat()
        } catch (_: Throwable) {}
    }

    // Update Station Markers when stations or filter changes
    LaunchedEffect(stations, filterOperators, maplibreMapInstance) {
        val map = maplibreMapInstance ?: return@LaunchedEffect
        // Clear previous markers
        currentMarkers.forEach { marker ->
            try {
                map.removeMarker(marker)
            } catch (_: Throwable) {}
        }
        currentMarkers.clear()

        val filtered = if (filterOperators.isEmpty()) {
            stations
        } else {
            stations.filter { st -> st.operators.any { filterOperators.contains(it) } }
        }

        filtered.forEach { station ->
            val icon = StationMarkerIconHelper.getStationMarkerIcon(context, station)
            val opt = MarkerOptions()
                .position(LatLng(station.latitude, station.longitude))
                .title(station.nameJa)
                .snippet("${station.nameEn} • ${station.lines.joinToString { it.code }}")
                .setIcon(icon)
            try {
                val marker = map.addMarker(opt)
                if (marker != null) {
                    currentMarkers.add(marker)
                }
            } catch (_: Throwable) {}
        }
    }

    // Handle Active Route Rendering:
    // 1. Near: Walking Route Dots
    // 2. Far: Solid Walking Line
    // 3. More Far: Multi-modal Train Connecting Line (JR Train Green, Metro Line Blue) + Walking Dots
    LaunchedEffect(activeWalkingRoute, isDarkTheme, maplibreMapInstance) {
        val map = maplibreMapInstance ?: return@LaunchedEffect

        // Clean up previous polylines
        currentRoutePolylines.forEach { try { map.removePolyline(it) } catch (_: Throwable) {} }
        currentRoutePolylines.clear()
        walkingDotGeoPoints = emptyList()
        walkingDotsScreenOffsets = emptyList()
        transferStationGeo = null
        transferStationScreenPos = null

        val route = activeWalkingRoute ?: return@LaunchedEffect

        try {
            val casingColor = if (isDarkTheme) AndroidColor.parseColor("#0F172A") else AndroidColor.parseColor("#FFFFFF")

            // A. Collect Walking Dots points (for Near Walk or First/Last Mile of Train Connection)
            val walkSegments = route.segments.filter { it.type == SegmentVisualType.WALK_DOTS }
            if (route.visualType == RouteVisualType.NEAR_WALKING_DOTS || walkSegments.isNotEmpty()) {
                val rawWalkPoints = if (walkSegments.isNotEmpty()) {
                    walkSegments.flatMap { it.points }
                } else {
                    route.polylinePoints
                }
                // Interpolate walking points every ~15 meters for dense Google Maps walking dots
                walkingDotGeoPoints = interpolateWalkingDots(rawWalkPoints)
                walkingDotsScreenOffsets = walkingDotGeoPoints.map {
                    map.projection.toScreenLocation(LatLng(it.first, it.second))
                }
            }

            // B. Draw Far Solid Walking Line
            if (route.visualType == RouteVisualType.FAR_WALKING_LINE || route.segments.any { it.type == SegmentVisualType.WALK_LINE }) {
                val farSegments = route.segments.filter { it.type == SegmentVisualType.WALK_LINE }.ifEmpty {
                    listOf(RouteSegment("far", SegmentVisualType.WALK_LINE, "", "", "#1A73E8", points = route.polylinePoints, distanceMeters = route.totalDistanceMeters, durationMinutes = route.durationMinutes))
                }
                farSegments.forEach { seg ->
                    val pts = seg.points.map { LatLng(it.first, it.second) }
                    if (pts.size >= 2) {
                        // Casing
                        val casing = map.addPolyline(PolylineOptions().addAll(pts).color(casingColor).width(8.5f))
                        currentRoutePolylines.add(casing)
                        // Solid Blue Navigation Line
                        val line = map.addPolyline(PolylineOptions().addAll(pts).color(AndroidColor.parseColor("#1A73E8")).width(5.5f))
                        currentRoutePolylines.add(line)
                    }
                }
            }

            // C. Draw Connecting Train Lines with Authentic Colors (JR Green, Metro Blue)
            if (route.visualType == RouteVisualType.TRANSIT_CONNECTING || route.segments.any { it.type == SegmentVisualType.TRAIN_LINE }) {
                val trainSegments = route.segments.filter { it.type == SegmentVisualType.TRAIN_LINE }
                trainSegments.forEach { seg ->
                    val pts = seg.points.map { LatLng(it.first, it.second) }
                    if (pts.size >= 2) {
                        // Outer railway border casing
                        val casing = map.addPolyline(PolylineOptions().addAll(pts).color(casingColor).width(9.5f))
                        currentRoutePolylines.add(casing)

                        // Inner vibrant railway track in operator/line color (e.g. JR Green #00B04F, Metro Blue #009BBF)
                        val lineColor = try { AndroidColor.parseColor(seg.colorHex) } catch (_: Throwable) { AndroidColor.parseColor("#00B04F") }
                        val trainLine = map.addPolyline(PolylineOptions().addAll(pts).color(lineColor).width(6.5f))
                        currentRoutePolylines.add(trainLine)
                    }
                }

                // If transferring between JR and Metro, note transfer station geo
                val transferName = route.transferStationName
                if (transferName != null) {
                    val transferStation = stations.find { it.nameEn.contains(transferName, ignoreCase = true) }
                    if (transferStation != null) {
                        transferStationGeo = transferStation.latitude to transferStation.longitude
                        transferStationScreenPos = map.projection.toScreenLocation(LatLng(transferStation.latitude, transferStation.longitude))
                    }
                }
            }

            // D. Smoothly fit camera bounds to include full route and both endpoints
            val allPoints = route.polylinePoints.map { LatLng(it.first, it.second) }
            if (allPoints.size >= 2) {
                val boundsBuilder = LatLngBounds.Builder()
                allPoints.forEach { boundsBuilder.include(it) }
                val bounds = boundsBuilder.build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 130), 850)
            }
        } catch (_: Throwable) {}
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("maplibre_transit_map")
    ) {
        AndroidView(
            factory = {
                mapView.apply {
                    getMapAsync { maplibreMap ->
                        maplibreMapInstance = maplibreMap

                        // Initial Camera centered on Tokyo (Shinjuku - Tokyo Station corridor)
                        val initialPosition = CameraPosition.Builder()
                            .target(LatLng(35.6812, 139.7671)) // Tokyo Station
                            .zoom(13.0)
                            .tilt(15.0)
                            .build()
                        maplibreMap.cameraPosition = initialPosition

                        // Set initial style
                        currentStyleUrl = targetStyleUrl
                        maplibreMap.setStyle(targetStyleUrl) {
                            // UI settings
                            maplibreMap.uiSettings.isCompassEnabled = true
                            maplibreMap.uiSettings.isAttributionEnabled = true
                            maplibreMap.uiSettings.isLogoEnabled = false
                        }

                        // Continuously sync Google Maps puck & walking route dots during camera drag / zoom / rotate
                        maplibreMap.addOnCameraMoveListener {
                            try {
                                userScreenPos = maplibreMap.projection.toScreenLocation(
                                    LatLng(userLocation.latitude, userLocation.longitude)
                                )
                                cameraBearing = maplibreMap.cameraPosition.bearing.toFloat()

                                // Update projected screen points for walking dots
                                if (walkingDotGeoPoints.isNotEmpty()) {
                                    walkingDotsScreenOffsets = walkingDotGeoPoints.map {
                                        maplibreMap.projection.toScreenLocation(LatLng(it.first, it.second))
                                    }
                                }

                                // Update transfer station screen position
                                transferStationGeo?.let {
                                    transferStationScreenPos = maplibreMap.projection.toScreenLocation(LatLng(it.first, it.second))
                                }
                            } catch (_: Throwable) {}
                        }

                        // Handle station marker clicks
                        maplibreMap.setOnMarkerClickListener { marker ->
                            val clickedStation = stations.find {
                                it.nameJa == marker.title ||
                                        (abs(it.latitude - marker.position.latitude) < 0.001 &&
                                                abs(it.longitude - marker.position.longitude) < 0.001)
                            }
                            if (clickedStation != null) {
                                onStationClick(clickedStation)
                                true
                            } else {
                                false
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 1. Overlay Walking Route Dots (Google Maps / OSRM pedestrian style)
        if (walkingDotsScreenOffsets.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val dotHaloRadius = 4.8.dp.toPx()
                val dotInnerRadius = 3.2.dp.toPx()
                val screenW = size.width
                val screenH = size.height

                walkingDotsScreenOffsets.forEach { dot ->
                    if (dot.x in -30f..(screenW + 30f) && dot.y in -30f..(screenH + 30f)) {
                        // Outer crisp white halo
                        drawCircle(
                            color = Color.White,
                            radius = dotHaloRadius,
                            center = Offset(dot.x, dot.y)
                        )
                        // Inner vibrant navigation blue dot (#1A73E8)
                        drawCircle(
                            color = Color(0xFF1A73E8),
                            radius = dotInnerRadius,
                            center = Offset(dot.x, dot.y)
                        )
                    }
                }
            }
        }

        // 2. Transfer Node Badge (shows transfer point between JR Green and Metro Blue)
        transferStationScreenPos?.let { transferPos ->
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(transferPos.x, transferPos.y)
                // Outer glow
                drawCircle(
                    color = Color.White,
                    radius = 12.dp.toPx(),
                    center = center
                )
                // Transfer double ring: outer JR Green (#00B04F), inner Metro Blue (#009BBF)
                drawCircle(
                    color = Color(0xFF00B04F),
                    radius = 9.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = Color(0xFF009BBF),
                    radius = 5.dp.toPx(),
                    center = center
                )
            }
        }

        // 3. Overlay Google Maps Location Puck with Calibrated Heading Cone & Radar Pulse
        userScreenPos?.let { pos ->
            if (pos.x > -150f && pos.y > -150f) {
                GoogleMapsLocationPuck(
                    screenX = pos.x,
                    screenY = pos.y,
                    headingDegrees = userLocation.headingDegrees - cameraBearing,
                    accuracyMeters = userLocation.accuracyMeters,
                    pulseFraction = pulseFraction,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Interpolates geo points along walking paths to render continuous walking route dots
 * spaced every ~14 meters.
 */
private fun interpolateWalkingDots(points: List<Pair<Double, Double>>): List<Pair<Double, Double>> {
    if (points.size < 2) return points
    val result = mutableListOf<Pair<Double, Double>>()

    for (i in 0 until points.size - 1) {
        val p1 = points[i]
        val p2 = points[i + 1]
        val distKm = LocationService.distanceBetween(p1.first, p1.second, p2.first, p2.second)
        val distMeters = distKm * 1000.0
        // Place a walking dot every ~14 meters
        val steps = max(1, (distMeters / 14.0).roundToInt())

        for (s in 0 until steps) {
            val frac = s.toDouble() / steps
            val lat = p1.first + (p2.first - p1.first) * frac
            val lng = p1.second + (p2.second - p1.second) * frac
            result.add(lat to lng)
        }
    }
    result.add(points.last())
    return result
}
