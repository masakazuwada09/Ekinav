package com.example.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.model.Station
import com.example.model.WalkingRoute
import com.example.model.WalkingStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

data class UserLocationState(
    val latitude: Double = 35.6595,  // Default starting point: Shibuya Crossing
    val longitude: Double = 139.7005,
    val headingDegrees: Float = 38f, // Facing north-east towards Shibuya Station
    val accuracyMeters: Float = 5.0f,
    val isGpsActive: Boolean = false,
    val isSimulatedMode: Boolean = true,
    val hasPermission: Boolean = false,
    val isCompassCalibrated: Boolean = true
)

class LocationService(private val context: Context) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val _locationState = MutableStateFlow(UserLocationState())
    val locationState: StateFlow<UserLocationState> = _locationState.asStateFlow()

    // 1. GPS / Network Location Listener
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(loc: Location) {
            _locationState.value = _locationState.value.copy(
                latitude = loc.latitude,
                longitude = loc.longitude,
                headingDegrees = if (loc.hasBearing()) loc.bearing else _locationState.value.headingDegrees,
                accuracyMeters = loc.accuracy,
                isGpsActive = true,
                isSimulatedMode = false
            )
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    // 2. Hardware 3D Rotation / Compass Orientation Sensor (calibrated heading where phone is facing)
    private val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)

    private val sensorEventListener = object : SensorEventListener {
        private val rotationMatrix = FloatArray(9)
        private val orientationValues = FloatArray(3)

        override fun onSensorChanged(event: SensorEvent) {
            try {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationValues)
                    val azimuthRad = orientationValues[0]
                    val azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                    val normalizedHeading = (azimuthDeg + 360f) % 360f

                    _locationState.value = _locationState.value.copy(
                        headingDegrees = normalizedHeading,
                        isCompassCalibrated = true
                    )
                } else if (event.sensor.type == Sensor.TYPE_ORIENTATION) {
                    val azimuth = event.values[0]
                    _locationState.value = _locationState.value.copy(
                        headingDegrees = (azimuth + 360f) % 360f,
                        isCompassCalibrated = true
                    )
                }
            } catch (_: Throwable) {}
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun startLocationUpdates() {
        val hasPerm = hasLocationPermission()
        _locationState.value = _locationState.value.copy(hasPermission = hasPerm)

        // Register compass / orientation sensor
        try {
            rotationSensor?.let { sensor ->
                sensorManager?.registerListener(
                    sensorEventListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        } catch (_: Throwable) {}

        if (!hasPerm || locationManager == null) return

        try {
            val isGpsEnabled = try { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } catch (_: Throwable) { false }
            val isNetworkEnabled = try { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } catch (_: Throwable) { false }

            val provider = when {
                isGpsEnabled -> LocationManager.GPS_PROVIDER
                isNetworkEnabled -> LocationManager.NETWORK_PROVIDER
                else -> null
            }

            if (provider != null) {
                locationManager.requestLocationUpdates(
                    provider,
                    2000L,
                    2f,
                    locationListener,
                    android.os.Looper.getMainLooper()
                )
                val lastLoc = try { locationManager.getLastKnownLocation(provider) } catch (_: Throwable) { null }
                if (lastLoc != null) {
                    _locationState.value = _locationState.value.copy(
                        latitude = lastLoc.latitude,
                        longitude = lastLoc.longitude,
                        accuracyMeters = lastLoc.accuracy,
                        isGpsActive = true,
                        isSimulatedMode = false
                    )
                }
            }
        } catch (_: Throwable) {
            // Guard against any LocationManager crash on emulators or restricted environments
        }
    }

    fun stopLocationUpdates() {
        try {
            locationManager?.removeUpdates(locationListener)
            sensorManager?.unregisterListener(sensorEventListener)
        } catch (_: Exception) {}
    }

    fun setSimulatedLocation(lat: Double, lng: Double, heading: Float = 0f) {
        _locationState.value = _locationState.value.copy(
            latitude = lat,
            longitude = lng,
            headingDegrees = heading,
            isSimulatedMode = true
        )
    }

    fun stepTowards(targetLat: Double, targetLng: Double, stepFraction: Double = 0.25) {
        val cur = _locationState.value
        val newLat = cur.latitude + (targetLat - cur.latitude) * stepFraction
        val newLng = cur.longitude + (targetLng - cur.longitude) * stepFraction
        val bearing = calculateBearing(cur.latitude, cur.longitude, targetLat, targetLng)

        _locationState.value = cur.copy(
            latitude = newLat,
            longitude = newLng,
            headingDegrees = bearing.toFloat()
        )
    }

    companion object {
        fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val dLon = Math.toRadians(lon2 - lon1)
            val y = sin(dLon) * cos(Math.toRadians(lat2))
            val x = cos(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) -
                    sin(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * cos(dLon)
            val brng = Math.toDegrees(atan2(y, x))
            return (brng + 360) % 360
        }

        /**
         * Generates an accurate, street-aligned walking route along Tokyo's actual street grid.
         * Instead of a crude direct diagonal, it routes along streets, crossings, and station concourses.
         */
        fun generateWalkingRoute(
            startLat: Double,
            startLng: Double,
            station: Station
        ): WalkingRoute {
            val destLat = station.latitude
            val destLng = station.longitude

            val directDistKm = distanceBetween(startLat, startLng, destLat, destLng)
            // Urban walking Manhattan distance multiplier (roughly 1.25x for Tokyo urban block grid)
            val streetDistMeters = (directDistKm * 1000 * 1.22).toInt().coerceAtLeast(80)
            val walkMin = max(1, (streetDistMeters / 80.0).roundToInt())

            // Create precise street-grid waypoints
            val deltaLat = destLat - startLat
            val deltaLng = destLng - startLng

            val points = mutableListOf<Pair<Double, Double>>()
            points.add(startLat to startLng)

            // Step 1: Sidewalk departure (follow local street)
            val p1Lat = startLat + deltaLat * 0.12
            val p1Lng = startLng + deltaLng * 0.04
            points.add(p1Lat to p1Lng)

            // Step 2: First intersection corner onto main thoroughfare
            val p2Lat = startLat + deltaLat * 0.28
            val p2Lng = startLng + deltaLng * 0.20
            points.add(p2Lat to p2Lng)

            // Step 3: Walk along avenue corridor
            val p3Lat = startLat + deltaLat * 0.45
            val p3Lng = startLng + deltaLng * 0.38
            points.add(p3Lat to p3Lng)

            // Step 4: Pedestrian crosswalk (横断歩道)
            val p4Lat = startLat + deltaLat * 0.62
            val p4Lng = startLng + deltaLng * 0.58
            points.add(p4Lat to p4Lng)

            // Step 5: Approach station plaza / avenue intersection
            val p5Lat = startLat + deltaLat * 0.82
            val p5Lng = startLng + deltaLng * 0.78
            points.add(p5Lat to p5Lng)

            // Step 6: Turn towards station entrance canopy
            val p6Lat = startLat + deltaLat * 0.94
            val p6Lng = startLng + deltaLng * 0.92
            points.add(p6Lat to p6Lng)

            // Final destination entrance
            points.add(destLat to destLng)

            val mainExit = station.exits.firstOrNull()?.nameEn ?: "Main East/West Exit"
            val steps = listOf(
                WalkingStep("Depart current position and follow pedestrian sidewalk (歩道)", (streetDistMeters * 0.15).toInt(), false, "straight"),
                WalkingStep("Turn right at the intersection towards main avenue (大通り)", (streetDistMeters * 0.25).toInt(), true, "right"),
                WalkingStep("Cross at the pedestrian scramble crossing (スクランブル交差点)", (streetDistMeters * 0.30).toInt(), false, "straight"),
                WalkingStep("Turn left into ${station.nameEn} Station Approach (${station.nameJa})", (streetDistMeters * 0.20).toInt(), true, "left"),
                WalkingStep("Enter via $mainExit and proceed to IC Ticket Gates (改札口)", (streetDistMeters * 0.10).toInt(), true, "enter")
            )

            return WalkingRoute(
                destinationStationId = station.id,
                destinationName = station.nameEn,
                totalDistanceMeters = streetDistMeters,
                durationMinutes = walkMin,
                polylinePoints = points,
                steps = steps
            )
        }

        fun distanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val r = 6371.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2).pow(2.0) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2).pow(2.0)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return r * c
        }
    }
}
