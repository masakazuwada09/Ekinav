package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RailOperator
import com.example.model.WalkingRoute
import com.example.ui.theme.TransitCyan

@Composable
fun MapControls(
    selectedOperators: Set<RailOperator>,
    onToggleOperator: (RailOperator) -> Unit,
    activeRoute: WalkingRoute?,
    onClearRoute: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    maptilerStyle: com.example.model.MapTilerStyle = com.example.model.MapTilerStyle.STREETS,
    onCycleMaptilerStyle: () -> Unit = {},
    isSimulatedLocation: Boolean,
    onStepSimulated: () -> Unit,
    onRecenterUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Top Row: Operator Filter Chips (JR, Metro, Toei, Private)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedOperators.isEmpty(),
                onClick = {
                    // Reset to show all
                },
                modifier = Modifier.height(42.dp),
                label = { Text("All Lines", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold) },
                leadingIcon = {
                    if (selectedOperators.isEmpty()) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            RailOperator.entries.forEach { op ->
                val isSelected = selectedOperators.contains(op)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggleOperator(op) },
                    modifier = Modifier.height(42.dp),
                    label = { 
                        Text(
                            text = "${op.code} ${op.displayNameEn}", 
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(op.brandColorHex), CircleShape)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(op.brandColorHex).copy(alpha = 0.25f),
                        selectedLabelColor = Color(op.brandColorHex)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Route Banner Pill (if walking route active)
        AnimatedVisibility(
            visible = activeRoute != null,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut()
        ) {
            activeRoute?.let { route ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsWalk,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${route.destinationName}: ${route.totalDistanceMeters}m (${route.durationMinutes} min)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onClearRoute,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel Route",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Buttons Stack
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            // MapTiler Layer Style Switcher
            SmallFloatingActionButton(
                onClick = onCycleMaptilerStyle,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(48.dp)
                    .shadow(6.dp, CircleShape)
                    .border(1.2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    .testTag("cycle_maptiler_style_fab")
            ) {
                Text(
                    text = maptilerStyle.iconEmoji,
                    fontSize = 20.sp
                )
            }

            // Theme toggle (Dark/Light)
            SmallFloatingActionButton(
                onClick = onToggleTheme,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(48.dp)
                    .shadow(6.dp, CircleShape)
                    .border(1.2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    .testTag("toggle_theme_fab")
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Map Theme",
                    tint = if (isDarkTheme) Color(0xFFFBBF24) else Color(0xFF0284C7),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Simulate step (walk towards selected station)
            if (activeRoute != null) {
                SmallFloatingActionButton(
                    onClick = onStepSimulated,
                    containerColor = TransitCyan,
                    contentColor = Color.Black,
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(6.dp, CircleShape)
                        .border(1.2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .testTag("step_walk_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = "Simulate Walk Step",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Recenter on user
            FloatingActionButton(
                onClick = onRecenterUser,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    .testTag("recenter_location_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Re-center location",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
