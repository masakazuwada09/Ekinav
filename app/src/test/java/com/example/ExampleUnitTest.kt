package com.example

import com.example.data.TokyoTransitData
import com.example.data.TransitAiParser
import com.example.location.LocationService
import com.example.model.AmenityType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `transit dataset contains required major hub stations`() {
        val stations = TokyoTransitData.allStations
        assertTrue(stations.isNotEmpty())

        val shinjuku = stations.find { it.id == "sta_shinjuku" }
        assertNotNull(shinjuku)
        assertTrue(shinjuku!!.lines.any { it.code == "JY" })
        assertTrue(shinjuku.amenities.any { it.type == AmenityType.COIN_LOCKERS })

        val shibuya = stations.find { it.id == "sta_shibuya" }
        assertNotNull(shibuya)
        assertEquals("Shibuya", shibuya!!.nameEn)
    }

    @Test
    fun `ai parser correctly identifies luggage locker intent`() {
        val rec = TransitAiParser.parseQuery(
            rawQuery = "Find stations near me with luggage lockers",
            userLat = 35.6595,
            userLng = 139.7005
        )

        assertEquals("Search: Luggage & Coin Lockers", rec.parsedIntent)
        assertTrue(rec.matchedStations.isNotEmpty())
        assertEquals(AmenityType.COIN_LOCKERS, rec.highlightedAmenity)
    }

    @Test
    fun `ai parser correctly identifies Yamanote line filter`() {
        val rec = TransitAiParser.parseQuery(
            rawQuery = "Show JR Yamanote line stations nearby",
            userLat = 35.6595,
            userLng = 139.7005
        )

        assertEquals("Filter: JR Yamanote Line (JY)", rec.parsedIntent)
        assertEquals("JY", rec.suggestedFilterLine)
        assertTrue(rec.matchedStations.all { st -> st.lines.any { it.code == "JY" } })
    }

    @Test
    fun `walking route generates proper distance and steps`() {
        val shibuya = TokyoTransitData.stationShibuya
        val route = LocationService.generateWalkingRoute(
            startLat = 35.6590,
            startLng = 139.7000,
            station = shibuya
        )

        assertTrue(route.totalDistanceMeters > 0)
        assertTrue(route.durationMinutes >= 1)
        assertTrue(route.steps.isNotEmpty())
        assertEquals(shibuya.nameEn, route.destinationName)
    }
}
