package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.theme.SuicaGreen
import com.example.ui.theme.TransitCyan

@Composable
fun RouteGuideDialog(
    route: WalkingRoute,
    destinationStation: Station,
    onDismiss: () -> Unit,
    onStartNavigation: () -> Unit
) {
    val isTransit = route.visualType == RouteVisualType.TRANSIT_CONNECTING
    val isNearDots = route.visualType == RouteVisualType.NEAR_WALKING_DOTS

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("route_guide_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isTransit) Color(0xFF00B04F).copy(alpha = 0.2f) else TransitCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isTransit) Icons.Default.Train else Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    tint = if (isTransit) Color(0xFF00B04F) else TransitCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = when (route.visualType) {
                                    RouteVisualType.NEAR_WALKING_DOTS -> "Walking Route (Near)"
                                    RouteVisualType.FAR_WALKING_LINE -> "Walking Route (Far)"
                                    RouteVisualType.TRANSIT_CONNECTING -> "Transit Connecting Route"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "To ${destinationStation.nameEn} (${destinationStation.nameJa})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Routing Style Mode Indicator Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (route.visualType) {
                        RouteVisualType.NEAR_WALKING_DOTS -> Color(0xFF1A73E8).copy(alpha = 0.15f)
                        RouteVisualType.FAR_WALKING_LINE -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        RouteVisualType.TRANSIT_CONNECTING -> Color(0xFF00B04F).copy(alpha = 0.15f)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (route.visualType) {
                                RouteVisualType.NEAR_WALKING_DOTS -> "● ● ●  Walking Route Dots (< 1.2km Near)"
                                RouteVisualType.FAR_WALKING_LINE -> "―――  Solid Navigation Line (1.2–2.6km Far)"
                                RouteVisualType.TRANSIT_CONNECTING -> "🚆 Multi-Color Train Connecting Line (> 2.6km)"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (route.visualType) {
                                RouteVisualType.NEAR_WALKING_DOTS -> Color(0xFF1A73E8)
                                RouteVisualType.FAR_WALKING_LINE -> MaterialTheme.colorScheme.primary
                                RouteVisualType.TRANSIT_CONNECTING -> Color(0xFF00B04F)
                            }
                        )
                    }
                }

                // If Transit Connecting: Visual Multi-Color Train Journey Bar
                if (isTransit) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Leg 1: Walk dots
                            JourneyChip("🚶 Walk", Color(0xFF1A73E8), "Dots")
                            Text("➜", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)

                            // Leg 2: JR Green
                            JourneyChip("🟢 JR Train", Color(0xFF00B04F), "Green")
                            Text("➜", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)

                            // Leg 3: Metro Blue
                            JourneyChip("🔵 Metro", Color(0xFF009BBF), "Blue")
                            Text("➜", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)

                            // Leg 4: Arrive
                            JourneyChip("🏁 Arrive", Color(0xFF1A73E8), "Exit")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Banner (Duration & Distance & Fare)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isTransit) "Total Time" else "Est. Walk",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${route.durationMinutes} min",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column {
                            Text(
                                text = if (isTransit) "Transit Mode" else "Distance",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (isTransit) "Train + Walk" else "${route.totalDistanceMeters}m",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "IC Fare",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (route.icFareYen > 0) "¥${route.icFareYen}" else "Free",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = SuicaGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Guidance Breakdown Title
                Text(
                    text = if (isTransit) "Multi-modal Route Breakdown" else "Turn-by-turn Navigation Steps",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Step / Segment list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 190.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isTransit && route.segments.isNotEmpty()) {
                        itemsIndexed(route.segments) { idx, seg ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = try {
                                            Color(android.graphics.Color.parseColor(seg.colorHex))
                                        } catch (_: Throwable) {
                                            MaterialTheme.colorScheme.primary
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (seg.type == SegmentVisualType.TRAIN_LINE) Icons.Default.Train else Icons.Default.DirectionsWalk,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = seg.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = seg.subtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        itemsIndexed(route.steps) { idx, step ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isNearDots) Color(0xFF1A73E8).copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${idx + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isNearDots) Color(0xFF1A73E8) else MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = step.instruction,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${step.distanceMeters} meters",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Close")
                    }

                    Button(
                        onClick = {
                            onStartNavigation()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Follow on Map")
                    }
                }
            }
        }
    }
}

@Composable
private fun JourneyChip(label: String, color: Color, badge: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
