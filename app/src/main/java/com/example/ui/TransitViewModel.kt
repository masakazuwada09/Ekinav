package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TokyoTransitData
import com.example.data.TransitAiParser
import com.example.location.LocationService
import com.example.location.UserLocationState
import com.example.model.AiTransitRecommendation
import com.example.model.RailOperator
import com.example.model.Station
import com.example.model.WalkingRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TransitUiState(
    val userLocation: UserLocationState = UserLocationState(),
    val stations: List<Station> = TokyoTransitData.allStations,
    val selectedStation: Station? = null,
    val isBottomSheetVisible: Boolean = false,
    val activeRecommendation: AiTransitRecommendation? = null,
    val highlightedStationIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val selectedOperators: Set<RailOperator> = emptySet(),
    val activeWalkingRoute: WalkingRoute? = null,
    val showRouteDialog: Boolean = false,
    val isDarkTheme: Boolean = true,
    val maptilerStyle: com.example.model.MapTilerStyle = com.example.model.MapTilerStyle.STREETS,
    val recenterTrigger: Long = 0L
)

class TransitViewModel(application: Application) : AndroidViewModel(application) {

    private val locationService = LocationService(application)

    private val _uiState = MutableStateFlow(TransitUiState())
    val uiState: StateFlow<TransitUiState> = _uiState.asStateFlow()

    init {
        // Collect location updates
        viewModelScope.launch {
            locationService.locationState.collect { loc ->
                _uiState.value = _uiState.value.copy(userLocation = loc)
            }
        }
        locationService.startLocationUpdates()
    }

    fun onQueryChange(text: String) {
        _uiState.value = _uiState.value.copy(searchQuery = text)
    }

    fun submitAiSearch(query: String) {
        val userLoc = _uiState.value.userLocation
        val recommendation = TransitAiParser.parseQuery(
            rawQuery = query,
            userLat = userLoc.latitude,
            userLng = userLoc.longitude,
            allStations = TokyoTransitData.allStations
        )

        val highlightedIds = recommendation.matchedStations.map { it.id }.toSet()

        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            activeRecommendation = recommendation,
            highlightedStationIds = highlightedIds
        )

        // If single station matched or top recommendation exists, focus it
        if (recommendation.matchedStations.isNotEmpty()) {
            selectStation(recommendation.matchedStations.first())
        }
    }

    fun clearRecommendation() {
        _uiState.value = _uiState.value.copy(
            activeRecommendation = null,
            highlightedStationIds = emptySet()
        )
    }

    fun selectStation(station: Station) {
        _uiState.value = _uiState.value.copy(
            selectedStation = station,
            isBottomSheetVisible = true
        )
    }

    fun dismissStationDetail() {
        _uiState.value = _uiState.value.copy(
            isBottomSheetVisible = false
        )
    }

    fun requestDirectionsTo(station: Station) {
        val loc = _uiState.value.userLocation
        viewModelScope.launch {
            val route = com.example.location.OsrmTransitRoutingService.calculateRoute(
                startLat = loc.latitude,
                startLng = loc.longitude,
                destination = station,
                allStations = _uiState.value.stations
            )
            _uiState.value = _uiState.value.copy(
                activeWalkingRoute = route,
                showRouteDialog = true,
                isBottomSheetVisible = false
            )
        }
    }

    fun dismissRouteDialog() {
        _uiState.value = _uiState.value.copy(showRouteDialog = false)
    }

    fun clearActiveRoute() {
        _uiState.value = _uiState.value.copy(
            activeWalkingRoute = null,
            showRouteDialog = false
        )
    }

    fun toggleOperator(operator: RailOperator) {
        val current = _uiState.value.selectedOperators.toMutableSet()
        if (current.contains(operator)) {
            current.remove(operator)
        } else {
            current.add(operator)
        }
        _uiState.value = _uiState.value.copy(selectedOperators = current)
    }

    fun toggleTheme() {
        val newDark = !_uiState.value.isDarkTheme
        val currentStyle = _uiState.value.maptilerStyle
        val newStyle = if (newDark) {
            if (currentStyle == com.example.model.MapTilerStyle.STREETS) com.example.model.MapTilerStyle.DARK else currentStyle
        } else {
            if (currentStyle == com.example.model.MapTilerStyle.DARK) com.example.model.MapTilerStyle.STREETS else currentStyle
        }
        _uiState.value = _uiState.value.copy(
            isDarkTheme = newDark,
            maptilerStyle = newStyle
        )
    }

    fun cycleMaptilerStyle() {
        val current = _uiState.value.maptilerStyle
        val all = com.example.model.MapTilerStyle.entries
        val nextIdx = (all.indexOf(current) + 1) % all.size
        val nextStyle = all[nextIdx]
        val updatedDark = when (nextStyle) {
            com.example.model.MapTilerStyle.DARK -> true
            com.example.model.MapTilerStyle.STREETS -> false
            else -> _uiState.value.isDarkTheme
        }
        _uiState.value = _uiState.value.copy(
            maptilerStyle = nextStyle,
            isDarkTheme = updatedDark
        )
    }

    fun setMaptilerStyle(style: com.example.model.MapTilerStyle) {
        _uiState.value = _uiState.value.copy(maptilerStyle = style)
    }

    fun recenterOnUser() {
        _uiState.value = _uiState.value.copy(
            selectedStation = null,
            recenterTrigger = System.currentTimeMillis()
        )
    }

    fun simulateStepWalk() {
        val route = _uiState.value.activeWalkingRoute ?: return
        val targetStation = _uiState.value.stations.find { it.id == route.destinationStationId } ?: return

        locationService.stepTowards(targetStation.latitude, targetStation.longitude, 0.28)

        viewModelScope.launch {
            val newLoc = locationService.locationState.value
            val updatedRoute = com.example.location.OsrmTransitRoutingService.calculateRoute(
                startLat = newLoc.latitude,
                startLng = newLoc.longitude,
                destination = targetStation,
                allStations = _uiState.value.stations
            )
            _uiState.value = _uiState.value.copy(activeWalkingRoute = updatedRoute)
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationService.stopLocationUpdates()
    }
}
