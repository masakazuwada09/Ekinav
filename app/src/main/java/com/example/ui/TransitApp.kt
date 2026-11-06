package com.example.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.TransitAiParser
import com.example.model.MapTilerStyle
import com.example.ui.components.*
import com.example.ui.map.MapLibreTransitMap
import com.example.ui.map.TransitCanvasMap
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SuicaGreen
import com.example.ui.theme.TransitCyan

@Composable
fun TransitApp(
    viewModel: TransitViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Location Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Updates handled inside LocationService via state flow
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    MyApplicationTheme(darkTheme = uiState.isDarkTheme) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("transit_app_root")
            ) {
        // 1. Interactive Vector Transit Map (MapLibre Native with MapTiler or Canvas Mode)
        if (uiState.maptilerStyle != MapTilerStyle.CANVAS && com.example.BuildConfig.MAPTILER_API_KEY.isNotBlank() && com.example.BuildConfig.MAPTILER_API_KEY != "YOUR_MAPTILER_API_KEY") {
            MapLibreTransitMap(
                userLocation = uiState.userLocation,
                stations = uiState.stations,
                selectedStation = uiState.selectedStation,
                highlightedStationIds = uiState.highlightedStationIds,
                activeWalkingRoute = uiState.activeWalkingRoute,
                filterOperators = uiState.selectedOperators,
                isDarkTheme = uiState.isDarkTheme,
                maptilerStyle = uiState.maptilerStyle,
                recenterTrigger = uiState.recenterTrigger,
                onStationClick = { station -> viewModel.selectStation(station) },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            TransitCanvasMap(
                userLocation = uiState.userLocation,
                stations = uiState.stations,
                selectedStation = uiState.selectedStation,
                highlightedStationIds = uiState.highlightedStationIds,
                activeWalkingRoute = uiState.activeWalkingRoute,
                filterOperators = uiState.selectedOperators,
                isDarkTheme = uiState.isDarkTheme,
                maptilerStyle = uiState.maptilerStyle,
                onStationClick = { station -> viewModel.selectStation(station) },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. Map Controls & Filter Chips (Top edge-to-edge with status bar padding)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(vertical = 4.dp)
        ) {
            MapControls(
                selectedOperators = uiState.selectedOperators,
                onToggleOperator = { op -> viewModel.toggleOperator(op) },
                activeRoute = uiState.activeWalkingRoute,
                onClearRoute = { viewModel.clearActiveRoute() },
                isDarkTheme = uiState.isDarkTheme,
                onToggleTheme = { viewModel.toggleTheme() },
                maptilerStyle = uiState.maptilerStyle,
                onCycleMaptilerStyle = { viewModel.cycleMaptilerStyle() },
                isSimulatedLocation = uiState.userLocation.isSimulatedMode,
                onStepSimulated = { viewModel.simulateStepWalk() },
                onRecenterUser = { viewModel.recenterOnUser() }
            )
        }

        // 3. Floating Bottom AI Natural Language Search & Query Interface
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            AiSearchSheet(
                queryText = uiState.searchQuery,
                onQueryChange = { q -> viewModel.onQueryChange(q) },
                onSearchSubmit = { q -> viewModel.submitAiSearch(q) },
                activeRecommendation = uiState.activeRecommendation,
                onClearRecommendation = { viewModel.clearRecommendation() },
                onSelectRecommendedStation = { st -> viewModel.selectStation(st) }
            )
        }

        // 4. Modal Station Detail Bottom Sheet
        if (uiState.isBottomSheetVisible && uiState.selectedStation != null) {
            val station = uiState.selectedStation!!
            val distFormatted = TransitAiParser.distanceFormatted(
                uiState.userLocation.latitude,
                uiState.userLocation.longitude,
                station.latitude,
                station.longitude
            )
            StationDetailBottomSheet(
                station = station,
                userDistanceFormatted = distFormatted,
                onDismiss = { viewModel.dismissStationDetail() },
                onGetDirections = { st -> viewModel.requestDirectionsTo(st) }
            )
        }

        // 5. Walking Route Guide Dialog
        if (uiState.showRouteDialog && uiState.activeWalkingRoute != null && uiState.selectedStation != null) {
            RouteGuideDialog(
                route = uiState.activeWalkingRoute!!,
                destinationStation = uiState.selectedStation!!,
                onDismiss = { viewModel.dismissRouteDialog() },
                onStartNavigation = {
                    // The polyline is drawn on map and user can tap "Step Walk"
                }
            )
        }
            }
        }
    }
}
