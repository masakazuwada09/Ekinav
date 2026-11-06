package com.example.model

import androidx.compose.ui.graphics.Color

enum class RailOperator(
    val code: String,
    val displayNameEn: String,
    val displayNameJa: String,
    val brandColorHex: Long,
    val shortLogo: String = code
) {
    JR_EAST("JR", "JR East", "JR東日本", 0xFF00B04F, "JR"),
    TOKYO_METRO("METRO", "Tokyo Metro", "東京メトロ", 0xFF009BBF, "M"),
    TOEI_SUBWAY("TOEI", "Toei Subway", "都営地下鉄", 0xFF6CBB5A, "都営"),
    SHINKANSEN("SHINKANSEN", "Shinkansen Bullet Train", "新幹線", 0xFF003366, "新幹線"),
    TOKYU("TOKYU", "Tokyu Railway", "東急電鉄", 0xFFDA0442, "TY"),
    ODAKYU("ODAKYU", "Odakyu Railway", "小田急電鉄", 0xFF1D429A, "OH"),
    KEIO("KEIO", "Keio Railway", "京王電鉄", 0xFFDD0077, "KO"),
    KEISEI("KEISEI", "Keisei Skyliner", "京成電鉄", 0xFF004898, "KS"),
    TOBU("TOBU", "Tobu Railway", "東武鉄道", 0xFF0F3B84, "TB"),
    SEIBU("SEIBU", "Seibu Railway", "西武鉄道", 0xFF0067AC, "SI"),
    YURIKAMOME("YURIKAMOME", "Yurikamome Line", "ゆりかもめ", 0xFF0084B4, "ゆ"),
    MONORAIL("MONORAIL", "Tokyo Monorail", "東京モノレール", 0xFF0096D6, "MO"),
    TSUKUBA("TX", "Tsukuba Express", "つくばエクスプレス", 0xFFC8102E, "TX")
}

enum class MapTilerStyle(
    val idName: String,
    val displayName: String,
    val iconEmoji: String,
    val isSatellite: Boolean = false
) {
    STREETS("streets-v2", "Streets", "🗺️"),
    SATELLITE("satellite-v2", "Satellite", "🛰️", true),
    DARK("dataviz-dark", "Dark", "🌃"),
    CANVAS("canvas-only", "Vector Only", "🎨")
}

data class TransitLine(
    val id: String,
    val code: String, // e.g., "JY", "M", "G", "E", "OH", "KO"
    val nameEn: String,
    val nameJa: String,
    val operator: RailOperator,
    val color: Color,
    val isCircular: Boolean = false
)

enum class AmenityType(
    val displayName: String,
    val displayNameJa: String,
    val category: String
) {
    COIN_LOCKERS("Luggage Lockers", "コインロッカー", "Luggage"),
    ELEVATOR("Elevator / Wheelchair", "エレベーター・車椅子対応", "Accessibility"),
    FREE_WIFI("Free Wi-Fi", "無料Wi-Fi", "Services"),
    TICKET_OFFICE("Ticket Office (Midori)", "みどりの窓口", "Tickets"),
    RESTROOM("Multi-Toilet", "多機能トイレ", "Facilities"),
    CONVENIENCE("Convenience Store", "駅ナカ売店・コンビニ", "Shopping"),
    CURRENCY_EXCHANGE("IC Card / Exchange", "両替・外貨両替", "Services")
}

data class StationAmenity(
    val type: AmenityType,
    val detailEn: String,
    val detailJa: String,
    val isAvailable: Boolean = true
)

data class PlatformInfo(
    val platformNumber: String,
    val lineCode: String,
    val lineName: String,
    val directionEn: String,
    val directionJa: String,
    val isAccessible: Boolean = true
)

data class ExitInfo(
    val nameEn: String,
    val nameJa: String,
    val descriptionEn: String,
    val landmarks: List<String>
)

data class DepartureInfo(
    val lineCode: String,
    val lineName: String,
    val destinationEn: String,
    val destinationJa: String,
    val minutesAway: Int,
    val platformNumber: String,
    val status: String = "On Time", // "On Time", "Delayed 2m", "Rapid"
    val isRapid: Boolean = false
)

data class Station(
    val id: String,
    val nameEn: String,
    val nameJa: String,
    val kana: String,
    val latitude: Double,
    val longitude: Double,
    val lines: List<TransitLine>,
    val operators: Set<RailOperator>,
    val amenities: List<StationAmenity>,
    val platforms: List<PlatformInfo>,
    val exits: List<ExitInfo>,
    val baseIcFareYen: Int,
    val baseTicketFareYen: Int,
    val upcomingDepartures: List<DepartureInfo>,
    val addressJa: String,
    val isMajorHub: Boolean = false
)

data class WalkingStep(
    val instruction: String,
    val distanceMeters: Int,
    val isTurn: Boolean = false,
    val turnDirection: String = "" // "left", "right", "straight", "enter"
)

enum class RouteVisualType {
    NEAR_WALKING_DOTS,   // Near (< 1.2km): walking route dots
    FAR_WALKING_LINE,    // Far (1.2km - 2.5km): solid walking line
    TRANSIT_CONNECTING   // More far (> 2.5km): train connecting line with colors (JR green, Metro blue)
}

enum class SegmentVisualType {
    WALK_DOTS,
    WALK_LINE,
    TRAIN_LINE
}

data class RouteSegment(
    val id: String,
    val type: SegmentVisualType,
    val title: String,
    val subtitle: String,
    val colorHex: String,
    val lineCode: String? = null,
    val operator: RailOperator? = null,
    val points: List<Pair<Double, Double>>,
    val distanceMeters: Int,
    val durationMinutes: Int,
    val stopsCount: Int = 0,
    val transferStationName: String? = null
)

data class WalkingRoute(
    val destinationStationId: String,
    val destinationName: String,
    val totalDistanceMeters: Int,
    val durationMinutes: Int,
    val polylinePoints: List<Pair<Double, Double>>,
    val steps: List<WalkingStep>,
    val visualType: RouteVisualType = RouteVisualType.NEAR_WALKING_DOTS,
    val segments: List<RouteSegment> = emptyList(),
    val isTransitConnecting: Boolean = false,
    val transitSummary: String = "",
    val icFareYen: Int = 0,
    val originStationName: String? = null,
    val transferStationName: String? = null
)

data class TransitRouteSegment(
    val mode: String, // "WALK" or "TRAIN"
    val title: String,
    val subtitle: String,
    val durationMin: Int,
    val lineCode: String? = null,
    val lineColor: Color? = null,
    val departureTime: String,
    val arrivalTime: String,
    val platform: String? = null,
    val stopsCount: Int = 0
)

data class TransitRouteOption(
    val id: String,
    val originStationName: String,
    val destinationStationName: String,
    val totalTimeMin: Int,
    val walkingTimeMin: Int,
    val icFareYen: Int,
    val ticketFareYen: Int,
    val transfersCount: Int,
    val isFastest: Boolean = false,
    val isCheapest: Boolean = false,
    val segments: List<TransitRouteSegment>
)

data class AiTransitRecommendation(
    val query: String,
    val parsedIntent: String,
    val matchedStations: List<Station>,
    val explanationEn: String,
    val transitTipsEn: String,
    val highlightedAmenity: AmenityType? = null,
    val suggestedFilterLine: String? = null
)
