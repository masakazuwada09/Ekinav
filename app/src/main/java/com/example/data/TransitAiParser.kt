package com.example.data

import com.example.model.*
import java.util.Locale

object TransitAiParser {

    fun parseQuery(
        rawQuery: String,
        userLat: Double,
        userLng: Double,
        allStations: List<Station> = TokyoTransitData.allStations
    ): AiTransitRecommendation {
        val query = rawQuery.trim().lowercase(Locale.ROOT)

        // 1. Luggage Lockers / Baggage Intent
        if (query.contains("locker") || query.contains("luggage") || query.contains("baggage") ||
            query.contains("suitcase") || query.contains("coin locker") || query.contains("ロッカー") || query.contains("荷物")
        ) {
            val matchingStations = allStations.filter { station ->
                station.amenities.any { it.type == AmenityType.COIN_LOCKERS && it.isAvailable }
            }.sortedBy { distanceKm(userLat, userLng, it.latitude, it.longitude) }

            val nearest = matchingStations.firstOrNull() ?: allStations.first()
            return AiTransitRecommendation(
                query = rawQuery,
                parsedIntent = "Search: Luggage & Coin Lockers",
                matchedStations = matchingStations,
                explanationEn = "Found ${matchingStations.size} stations equipped with multi-size electronic coin lockers. Nearest is ${nearest.nameEn} (${distanceFormatted(userLat, userLng, nearest.latitude, nearest.longitude)}).",
                transitTipsEn = "💡 Pro Tip: Stations like Tokyo and Shinjuku feature Suica/Pasmo IC card lockers with real-time screen indicators. Extra-large sizes fit 90L suitcases (typically ¥700–¥900/day).",
                highlightedAmenity = AmenityType.COIN_LOCKERS
            )
        }

        // 2. Yamanote Line Stations Intent
        if (query.contains("yamanote") || query.contains("jy") || query.contains("green line") ||
            query.contains("loop") || query.contains("山手線")
        ) {
            val matchingStations = allStations.filter { station ->
                station.lines.any { it.code == "JY" }
            }.sortedBy { distanceKm(userLat, userLng, it.latitude, it.longitude) }

            val nearest = matchingStations.firstOrNull() ?: allStations.first()
            return AiTransitRecommendation(
                query = rawQuery,
                parsedIntent = "Filter: JR Yamanote Line (JY)",
                matchedStations = matchingStations,
                explanationEn = "Displaying JR Yamanote circular loop stations. Nearest station is ${nearest.nameEn} (${distanceFormatted(userLat, userLng, nearest.latitude, nearest.longitude)}).",
                transitTipsEn = "💡 Pro Tip: Yamanote trains run every 2–4 minutes during peak hours. Outer loop (Sotomawari) runs clockwise; Inner loop (Uchimawari) runs counter-clockwise.",
                suggestedFilterLine = "JY"
            )
        }

        // 3. Elevator / Barrier-Free / Wheelchair Intent
        if (query.contains("elevator") || query.contains("wheelchair") || query.contains("stroller") ||
            query.contains("barrier-free") || query.contains("accessible") || query.contains("エレベーター")
        ) {
            val matchingStations = allStations.filter { station ->
                station.amenities.any { it.type == AmenityType.ELEVATOR && it.isAvailable }
            }.sortedBy { distanceKm(userLat, userLng, it.latitude, it.longitude) }

            val nearest = matchingStations.firstOrNull() ?: allStations.first()
            return AiTransitRecommendation(
                query = rawQuery,
                parsedIntent = "Accessibility: Barrier-Free & Elevators",
                matchedStations = matchingStations,
                explanationEn = "Showing ${matchingStations.size} accessible stations with direct street-to-platform elevator access.",
                transitTipsEn = "💡 Pro Tip: Station attendants (Ekicho) provide ramp boarding assistance. Push the blue call button beside ticket gates or priority elevators for dedicated escort.",
                highlightedAmenity = AmenityType.ELEVATOR
            )
        }

        // 4. Haneda / Narita / Airport Transfers
        if (query.contains("airport") || query.contains("haneda") || query.contains("narita") ||
            query.contains("羽田") || query.contains("成田")
        ) {
            val airportStations = allStations.filter {
                it.id == "sta_shinagawa" || it.id == "sta_shimbashi" || it.id == "sta_asakusa" || it.id == "sta_tokyo"
            }
            return AiTransitRecommendation(
                query = rawQuery,
                parsedIntent = "Airport Transit Gateways",
                matchedStations = airportStations,
                explanationEn = "Shinagawa and Shimbashi offer direct rapid rail connections to Haneda Airport (11 mins via Keikyu or Asakusa Rapid). Tokyo Station hosts Narita Express (N'EX).",
                transitTipsEn = "💡 Pro Tip: Tap your mobile Suica/Pasmo at the gate for seamless contactless fare deduction. Haneda Airport Keikyu line fare from Shinagawa is only ¥330.",
                suggestedFilterLine = "A"
            )
        }

        // 5. Tokyo Metro Lines Intent
        if (query.contains("metro") || query.contains("subway") || query.contains("ginza") ||
            query.contains("marunouchi") || query.contains("地下鉄") || query.contains("メトロ")
        ) {
            val matchingStations = allStations.filter { station ->
                station.operators.contains(RailOperator.TOKYO_METRO)
            }.sortedBy { distanceKm(userLat, userLng, it.latitude, it.longitude) }

            return AiTransitRecommendation(
                query = rawQuery,
                parsedIntent = "Operator: Tokyo Metro Network",
                matchedStations = matchingStations,
                explanationEn = "Showing ${matchingStations.size} Tokyo Metro stations with high-frequency underground connections across central Tokyo.",
                transitTipsEn = "💡 Pro Tip: Consider the Tokyo Subway 24/48/72-hour Tourist Ticket (¥800/¥1200/¥1500) for unlimited rides across both Tokyo Metro and Toei Subway lines.",
                suggestedFilterLine = "G"
            )
        }

        // 6. JR Pass / Ticket Office (Midori no Madoguchi)
        if (query.contains("ticket office") || query.contains("jr pass") || query.contains("pass") ||
            query.contains("midori") || query.contains("shinkansen") || query.contains("みどりの窓口")
        ) {
            val matchingStations = allStations.filter { station ->
                station.amenities.any { it.type == AmenityType.TICKET_OFFICE }
            }.sortedBy { distanceKm(userLat, userLng, it.latitude, it.longitude) }

            return AiTransitRecommendation(
                query = rawQuery,
                parsedIntent = "Facilities: Midori no Madoguchi / Ticket Office",
                matchedStations = matchingStations,
                explanationEn = "Found ${matchingStations.size} major hub stations with staffed JR Travel Service Centers for JR Pass exchange and Shinkansen reserved seats.",
                transitTipsEn = "💡 Pro Tip: Tokyo Station and Shinjuku have dedicated foreign tourist travel counters with English, Chinese, and Korean speaking staff.",
                highlightedAmenity = AmenityType.TICKET_OFFICE
            )
        }

        // 7. Free Wi-Fi / Digital Nomad
        if (query.contains("wifi") || query.contains("wi-fi") || query.contains("internet") || query.contains("ワイファイ")) {
            val matchingStations = allStations.filter { station ->
                station.amenities.any { it.type == AmenityType.FREE_WIFI }
            }
            return AiTransitRecommendation(
                query = rawQuery,
                parsedIntent = "Amenities: High-Speed Free Station Wi-Fi",
                matchedStations = matchingStations,
                explanationEn = "All major Tokyo stations support free high-speed public Wi-Fi ('JR-EAST_FREE_Wi-Fi' & 'Metro_Free_Wi-Fi').",
                transitTipsEn = "💡 Pro Tip: Download the 'Japan Connected-free Wi-Fi' app once to auto-authenticate across all Tokyo train operators without re-entering email credentials.",
                highlightedAmenity = AmenityType.FREE_WIFI
            )
        }

        // 8. General Name / Landmark / Fallback Keyword Search
        val matchedByName = allStations.filter {
            it.nameEn.lowercase(Locale.ROOT).contains(query) ||
            it.nameJa.contains(query) ||
            it.kana.contains(query) ||
            it.exits.any { exit -> exit.landmarks.any { l -> l.lowercase(Locale.ROOT).contains(query) } }
        }

        if (matchedByName.isNotEmpty()) {
            return AiTransitRecommendation(
                query = rawQuery,
                parsedIntent = "Search: Specific Stations & Landmarks",
                matchedStations = matchedByName,
                explanationEn = "Matched ${matchedByName.size} station(s) matching '${rawQuery}'.",
                transitTipsEn = "💡 Pro Tip: Check the Exit Guide in the bottom sheet to select the closest exit for your destination and avoid crossing massive multi-block streets."
            )
        }

        // Default: Nearest Stations overview
        val sortedByDist = allStations.sortedBy { distanceKm(userLat, userLng, it.latitude, it.longitude) }
        return AiTransitRecommendation(
            query = rawQuery,
            parsedIntent = "Search: Nearby Transit Stations",
            matchedStations = sortedByDist.take(5),
            explanationEn = "Showing closest stations to your current coordinates in Tokyo. Nearest is ${sortedByDist.first().nameEn}.",
            transitTipsEn = "💡 Pro Tip: Tap any station marker on the map or bottom sheet to see live departures, platform numbers, and walking directions."
        )
    }

    fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    fun distanceFormatted(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
        val km = distanceKm(lat1, lon1, lat2, lon2)
        return if (km < 1.0) {
            "${(km * 1000).toInt()}m"
        } else {
            String.format(Locale.US, "%.1f km", km)
        }
    }
}
