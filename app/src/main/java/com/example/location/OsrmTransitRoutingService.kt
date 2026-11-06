package com.example.location

import com.example.data.TokyoTransitData
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.*

object OsrmTransitRoutingService {

    private const val OSRM_BASE_URL = "https://router.project-osrm.org/route/v1/walking"
    private const val TIMEOUT_MS = 3000

    /**
     * Calculates the best route based on user distance:
     * - Near (< 1.2km): Walking route rendered with walking route dots
     * - Far (1.2km - 2.6km): Walking route rendered with solid navigation line
     * - More Far (> 2.6km): Transit connecting route with colored train lines (e.g. JR green + Metro blue)
     */
    suspend fun calculateRoute(
        startLat: Double,
        startLng: Double,
        destination: Station,
        allStations: List<Station> = TokyoTransitData.allStations
    ): WalkingRoute = withContext(Dispatchers.IO) {
        val directDistKm = LocationService.distanceBetween(startLat, startLng, destination.latitude, destination.longitude)
        val directDistMeters = (directDistKm * 1000).toInt()

        when {
            // Tier 1: Near (< 1.2 km) -> Walking route dots
            directDistMeters <= 1200 -> {
                buildNearWalkingDotsRoute(startLat, startLng, destination)
            }

            // Tier 2: Far (1.2 km - 2.6 km) -> Solid walking line
            directDistMeters <= 2600 -> {
                buildFarWalkingLineRoute(startLat, startLng, destination)
            }

            // Tier 3: More Far (> 2.6 km) -> Multi-modal train connecting route (e.g. JR green + Metro blue)
            else -> {
                buildConnectingTransitRoute(startLat, startLng, destination, allStations)
            }
        }
    }

    /**
     * Tier 1: Near Walking Route (< 1.2km)
     * Displayed with walking route dots along the street grid.
     */
    private fun buildNearWalkingDotsRoute(
        startLat: Double,
        startLng: Double,
        destination: Station
    ): WalkingRoute {
        val osrmResult = queryOsrmWalking(startLat, startLng, destination.latitude, destination.longitude)
        val points = osrmResult?.first ?: LocationService.generateWalkingRoute(startLat, startLng, destination).polylinePoints
        val steps = osrmResult?.second ?: LocationService.generateWalkingRoute(startLat, startLng, destination).steps
        val totalDistance = osrmResult?.third ?: (LocationService.distanceBetween(startLat, startLng, destination.latitude, destination.longitude) * 1000 * 1.2).toInt()
        val durationMin = max(1, (totalDistance / 75.0).roundToInt())

        val segment = RouteSegment(
            id = "seg_near_walk",
            type = SegmentVisualType.WALK_DOTS,
            title = "Walk to ${destination.nameEn} Station",
            subtitle = "Follow blue walking route dots • $totalDistance m ($durationMin min)",
            colorHex = "#1A73E8",
            points = points,
            distanceMeters = totalDistance,
            durationMinutes = durationMin
        )

        return WalkingRoute(
            destinationStationId = destination.id,
            destinationName = destination.nameEn,
            totalDistanceMeters = totalDistance,
            durationMinutes = durationMin,
            polylinePoints = points,
            steps = steps,
            visualType = RouteVisualType.NEAR_WALKING_DOTS,
            segments = listOf(segment),
            isTransitConnecting = false,
            transitSummary = "Walking route dots • $totalDistance m ($durationMin min)",
            icFareYen = 0
        )
    }

    /**
     * Tier 2: Far Walking Route (1.2km - 2.6km)
     * Displayed with a solid navigation line.
     */
    private fun buildFarWalkingLineRoute(
        startLat: Double,
        startLng: Double,
        destination: Station
    ): WalkingRoute {
        val osrmResult = queryOsrmWalking(startLat, startLng, destination.latitude, destination.longitude)
        val points = osrmResult?.first ?: LocationService.generateWalkingRoute(startLat, startLng, destination).polylinePoints
        val steps = osrmResult?.second ?: LocationService.generateWalkingRoute(startLat, startLng, destination).steps
        val totalDistance = osrmResult?.third ?: (LocationService.distanceBetween(startLat, startLng, destination.latitude, destination.longitude) * 1000 * 1.25).toInt()
        val durationMin = max(1, (totalDistance / 75.0).roundToInt())

        val segment = RouteSegment(
            id = "seg_far_walk",
            type = SegmentVisualType.WALK_LINE,
            title = "Direct Walk to ${destination.nameEn}",
            subtitle = "Follow solid navigation line • ${(totalDistance / 1000.0).roundTo(1)} km ($durationMin min)",
            colorHex = "#1A73E8",
            points = points,
            distanceMeters = totalDistance,
            durationMinutes = durationMin
        )

        return WalkingRoute(
            destinationStationId = destination.id,
            destinationName = destination.nameEn,
            totalDistanceMeters = totalDistance,
            durationMinutes = durationMin,
            polylinePoints = points,
            steps = steps,
            visualType = RouteVisualType.FAR_WALKING_LINE,
            segments = listOf(segment),
            isTransitConnecting = false,
            transitSummary = "Solid walking navigation line • ${(totalDistance / 1000.0).roundTo(1)} km ($durationMin min)",
            icFareYen = 0
        )
    }

    /**
     * Tier 3: More Far (> 2.6km) -> Multi-modal Train Connecting Route
     * Connecting train lines with authentic line colors:
     * e.g. JR Train Green (#00B04F) + Tokyo Metro Line Blue (#009BBF)
     */
    private fun buildConnectingTransitRoute(
        startLat: Double,
        startLng: Double,
        destination: Station,
        allStations: List<Station>
    ): WalkingRoute {
        // 1. Find nearest origin station from user coordinates
        val originStation = allStations
            .filter { it.id != destination.id }
            .minByOrNull { LocationService.distanceBetween(startLat, startLng, it.latitude, it.longitude) }
            ?: TokyoTransitData.stationShibuya

        // 2. Leg 1: First-Mile Walk from user location to origin station (rendered as walking dots)
        val firstWalkPoints = LocationService.generateWalkingRoute(startLat, startLng, originStation).polylinePoints
        val firstWalkDist = (LocationService.distanceBetween(startLat, startLng, originStation.latitude, originStation.longitude) * 1000 * 1.2).toInt().coerceAtLeast(80)
        val firstWalkMin = max(1, (firstWalkDist / 75.0).roundToInt())

        val leg1Walk = RouteSegment(
            id = "seg_first_walk",
            type = SegmentVisualType.WALK_DOTS,
            title = "Walk to ${originStation.nameEn} Station",
            subtitle = "Follow blue walking dots to ${originStation.exits.firstOrNull()?.nameEn ?: "Entrance"} • $firstWalkDist m ($firstWalkMin min)",
            colorHex = "#1A73E8",
            points = firstWalkPoints,
            distanceMeters = firstWalkDist,
            durationMinutes = firstWalkMin
        )

        // 3. Leg 2: Train Connecting Line(s)
        // Multi-operator connection: JR train green (#00B04F) + Tokyo Metro line blue (#009BBF)
        val trainSegments = mutableListOf<RouteSegment>()
        val totalCombinedPoints = mutableListOf<Pair<Double, Double>>()
        totalCombinedPoints.addAll(firstWalkPoints)

        // Check if there is an intermediate transfer station (e.g. Shinjuku, Kanda, or Akihabara)
        val isDirectSingleLine = (originStation.id == TokyoTransitData.stationShibuya.id && destination.id == TokyoTransitData.stationHarajuku.id) ||
                (originStation.id == TokyoTransitData.stationShibuya.id && destination.id == TokyoTransitData.stationShinagawa.id)

        var totalTrainDurationMin = 0
        var totalTrainDistanceMeters = 0
        var transitSummaryText = ""
        val transferStationName: String?

        if (!isDirectSingleLine) {
            // Multi-line connecting route: JR Train (Green) ➔ Tokyo Metro (Blue)
            val transferStation = if (originStation.id == TokyoTransitData.stationShinjuku.id) {
                TokyoTransitData.stationAkihabara
            } else {
                TokyoTransitData.stationShinjuku
            }
            transferStationName = transferStation.nameEn

            // 2A: JR East Yamanote Line (Iconic JR Green #00B04F)
            val jrPoints = generateRailTrackPoints(originStation.latitude, originStation.longitude, transferStation.latitude, transferStation.longitude, arcCurve = 0.003)
            val jrDistMeters = (LocationService.distanceBetween(originStation.latitude, originStation.longitude, transferStation.latitude, transferStation.longitude) * 1000).toInt()
            val jrMin = max(4, (jrDistMeters / 600.0).roundToInt())

            val legJr = RouteSegment(
                id = "seg_jr_green",
                type = SegmentVisualType.TRAIN_LINE,
                title = "JR Yamanote Line (山手線)",
                subtitle = "${originStation.nameEn} ➔ ${transferStation.nameEn} • Platform 2 (Inner Loop)",
                colorHex = "#00B04F", // Authentic JR East Green
                lineCode = "JY",
                operator = RailOperator.JR_EAST,
                points = jrPoints,
                distanceMeters = jrDistMeters,
                durationMinutes = jrMin,
                stopsCount = 3,
                transferStationName = transferStation.nameEn
            )
            trainSegments.add(legJr)
            totalCombinedPoints.addAll(jrPoints)

            // 2B: Tokyo Metro Line (Tokyo Metro Sky Blue #009BBF)
            val metroPoints = generateRailTrackPoints(transferStation.latitude, transferStation.longitude, destination.latitude, destination.longitude, arcCurve = -0.003)
            val metroDistMeters = (LocationService.distanceBetween(transferStation.latitude, transferStation.longitude, destination.latitude, destination.longitude) * 1000).toInt()
            val metroMin = max(4, (metroDistMeters / 650.0).roundToInt())

            val legMetro = RouteSegment(
                id = "seg_metro_blue",
                type = SegmentVisualType.TRAIN_LINE,
                title = "Tokyo Metro Line (東京メトロ)",
                subtitle = "${transferStation.nameEn} ➔ ${destination.nameEn} • Platform 1 (Blue Line)",
                colorHex = "#009BBF", // Tokyo Metro Blue
                lineCode = "M",
                operator = RailOperator.TOKYO_METRO,
                points = metroPoints,
                distanceMeters = metroDistMeters,
                durationMinutes = metroMin,
                stopsCount = 4
            )
            trainSegments.add(legMetro)
            totalCombinedPoints.addAll(metroPoints)

            totalTrainDurationMin = jrMin + metroMin + 3 // 3 min transfer buffer
            totalTrainDistanceMeters = jrDistMeters + metroDistMeters
            transitSummaryText = "JR Green (JY) ➔ Metro Blue (M) • Transfer at ${transferStation.nameEn}"
        } else {
            // Direct JR Yamanote Line (Green)
            transferStationName = null
            val jrPoints = generateRailTrackPoints(originStation.latitude, originStation.longitude, destination.latitude, destination.longitude, arcCurve = 0.002)
            val jrDistMeters = (LocationService.distanceBetween(originStation.latitude, originStation.longitude, destination.latitude, destination.longitude) * 1000).toInt()
            val jrMin = max(3, (jrDistMeters / 600.0).roundToInt())

            val legJr = RouteSegment(
                id = "seg_jr_direct",
                type = SegmentVisualType.TRAIN_LINE,
                title = "JR Yamanote Line (山手線)",
                subtitle = "${originStation.nameEn} ➔ ${destination.nameEn} • Direct (No Transfer)",
                colorHex = "#00B04F",
                lineCode = "JY",
                operator = RailOperator.JR_EAST,
                points = jrPoints,
                distanceMeters = jrDistMeters,
                durationMinutes = jrMin,
                stopsCount = 2
            )
            trainSegments.add(legJr)
            totalCombinedPoints.addAll(jrPoints)

            totalTrainDurationMin = jrMin
            totalTrainDistanceMeters = jrDistMeters
            transitSummaryText = "Direct JR Yamanote Line (Green) • No Transfer"
        }

        // 4. Leg 3: Last-Mile Walk (Destination Station Platform to Exit, rendered as walking dots)
        val lastWalkDist = 140
        val lastWalkMin = 2
        val lastWalkPoints = listOf(
            destination.latitude to destination.longitude,
            destination.latitude + 0.0006 to destination.longitude + 0.0004,
            destination.latitude + 0.0012 to destination.longitude + 0.0008
        )
        totalCombinedPoints.addAll(lastWalkPoints)

        val legLastWalk = RouteSegment(
            id = "seg_last_walk",
            type = SegmentVisualType.WALK_DOTS,
            title = "Arrive at ${destination.nameEn}",
            subtitle = "Follow blue walking dots through IC Gates to Main Exit • $lastWalkDist m ($lastWalkMin min)",
            colorHex = "#1A73E8",
            points = lastWalkPoints,
            distanceMeters = lastWalkDist,
            durationMinutes = lastWalkMin
        )

        val allSegments = listOf(leg1Walk) + trainSegments + listOf(legLastWalk)
        val totalDistance = firstWalkDist + totalTrainDistanceMeters + lastWalkDist
        val totalDuration = firstWalkMin + totalTrainDurationMin + lastWalkMin

        // Step-by-step guidance instructions
        val steps = mutableListOf<WalkingStep>()
        steps.add(WalkingStep("Walk to ${originStation.nameEn} Station following the blue walking dots", firstWalkDist, false, "straight"))
        steps.add(WalkingStep("Enter ${originStation.nameEn} Station and touch IC Card at Ticket Gates (改札口)", 40, false, "enter"))
        if (!isDirectSingleLine && transferStationName != null) {
            steps.add(WalkingStep("Board JR Yamanote Line (Green Line #00B04F) towards ${transferStationName}", trainSegments[0].distanceMeters, true, "enter"))
            steps.add(WalkingStep("Transfer at ${transferStationName} Station to Tokyo Metro Line (Blue Line #009BBF)", 150, true, "straight"))
            steps.add(WalkingStep("Board Tokyo Metro Line towards ${destination.nameEn}", trainSegments[1].distanceMeters, true, "enter"))
        } else {
            steps.add(WalkingStep("Board JR Yamanote Line (Green Line #00B04F) direct to ${destination.nameEn}", trainSegments[0].distanceMeters, true, "enter"))
        }
        steps.add(WalkingStep("Alight at ${destination.nameEn} Station and touch IC Card at Exit Gates", lastWalkDist, false, "enter"))

        return WalkingRoute(
            destinationStationId = destination.id,
            destinationName = destination.nameEn,
            totalDistanceMeters = totalDistance,
            durationMinutes = totalDuration,
            polylinePoints = totalCombinedPoints,
            steps = steps,
            visualType = RouteVisualType.TRANSIT_CONNECTING,
            segments = allSegments,
            isTransitConnecting = true,
            transitSummary = transitSummaryText,
            icFareYen = destination.baseIcFareYen,
            originStationName = originStation.nameEn,
            transferStationName = transferStationName
        )
    }

    /**
     * Queries the public OSRM walking router API for true pedestrian street geometry.
     * Returns Triple(coordinates, steps, totalDistanceMeters) or null if network/OSRM is unavailable.
     */
    private fun queryOsrmWalking(
        startLat: Double,
        startLng: Double,
        destLat: Double,
        destLng: Double
    ): Triple<List<Pair<Double, Double>>, List<WalkingStep>, Int>? {
        return try {
            val urlString = "$OSRM_BASE_URL/$startLng,$startLat;$destLng,$destLat?overview=full&geometries=geojson&steps=true"
            val url = URL(urlString)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                setRequestProperty("User-Agent", "JapanTransitNav/1.0 (Android)")
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val responseText = reader.use { it.readText() }
                conn.disconnect()

                val json = JSONObject(responseText)
                if (json.optString("code") == "Ok") {
                    val routes = json.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val firstRoute = routes.getJSONObject(0)
                        val distanceMeters = firstRoute.optDouble("distance", 0.0).toInt()
                        val geometry = firstRoute.getJSONObject("geometry")
                        val coordinatesArray = geometry.getJSONArray("coordinates")

                        val pointList = mutableListOf<Pair<Double, Double>>()
                        for (i in 0 until coordinatesArray.length()) {
                            val coord = coordinatesArray.getJSONArray(i)
                            val lng = coord.getDouble(0)
                            val lat = coord.getDouble(1)
                            pointList.add(lat to lng)
                        }

                        // Parse steps
                        val stepsList = mutableListOf<WalkingStep>()
                        val legs = firstRoute.optJSONArray("legs")
                        if (legs != null && legs.length() > 0) {
                            val firstLeg = legs.getJSONObject(0)
                            val stepsArray = firstLeg.optJSONArray("steps")
                            if (stepsArray != null) {
                                for (s in 0 until stepsArray.length()) {
                                    val stepObj = stepsArray.getJSONObject(s)
                                    val maneuver = stepObj.optJSONObject("maneuver")
                                    val type = maneuver?.optString("type") ?: "turn"
                                    val modifier = maneuver?.optString("modifier") ?: "straight"
                                    val name = stepObj.optString("name", "pedestrian sidewalk")
                                    val stepDist = stepObj.optDouble("distance", 0.0).toInt()

                                    val instruction = when (type) {
                                        "depart" -> "Depart and head $modifier on $name (歩道)"
                                        "arrive" -> "Arrive at destination entrance (到着)"
                                        "turn" -> "Turn $modifier onto $name"
                                        "new name" -> "Continue onto $name"
                                        else -> "Follow $name $modifier"
                                    }
                                    stepsList.add(WalkingStep(instruction, stepDist, type == "turn", modifier))
                                }
                            }
                        }

                        if (pointList.isNotEmpty()) {
                            return Triple(pointList, stepsList, distanceMeters)
                        }
                    }
                }
            }
            null
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * Generates intermediate rail track curve coordinates along Tokyo's railway corridors.
     */
    private fun generateRailTrackPoints(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double,
        arcCurve: Double = 0.002
    ): List<Pair<Double, Double>> {
        val points = mutableListOf<Pair<Double, Double>>()
        val count = 12
        for (i in 0..count) {
            val fraction = i.toDouble() / count
            // Linear base
            val baseLat = lat1 + (lat2 - lat1) * fraction
            val baseLng = lng1 + (lng2 - lng1) * fraction
            // Smooth natural rail arc (sin curve)
            val arc = sin(fraction * Math.PI) * arcCurve
            val ptLat = baseLat + arc
            val ptLng = baseLng - (arc * 0.4)
            points.add(ptLat to ptLng)
        }
        return points
    }

    private fun Double.roundTo(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}
