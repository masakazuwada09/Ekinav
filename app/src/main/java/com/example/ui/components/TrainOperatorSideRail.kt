package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RailOperator
import com.example.ui.theme.TransitCyan

@Composable
fun TrainOperatorSideRail(
    selectedOperators: Set<RailOperator>,
    onToggleOperator: (RailOperator) -> Unit,
    onClearOperators: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Animated chevron rotation angle: 0° when collapsed, 180° when expanded
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "ChevronRotation"
    )

    Column(
        modifier = modifier
            .padding(start = 12.dp)
            .testTag("train_operator_side_rail"),
        horizontalAlignment = Alignment.Start
    ) {
        // Main Trigger Pill / Button: Train Icon + Animated Chevron
        Surface(
            onClick = { isExpanded = !isExpanded },
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            shadowElevation = 8.dp,
            border = BorderStroke(
                width = 1.5.dp,
                color = if (selectedOperators.isNotEmpty()) TransitCyan else MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.testTag("train_operator_dropdown_toggle")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Train Icon badge (or active count badge)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (selectedOperators.isNotEmpty()) TransitCyan else MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedOperators.isNotEmpty()) {
                        Text(
                            text = "${selectedOperators.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.DirectionsTransit,
                            contentDescription = "Train Categories",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Animated Dropdown Chevron
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse train categories" else "Expand train categories",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(chevronRotation)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown Panel: Expand animation revealing logo-only train operator buttons
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(animationSpec = tween(220)),
            exit = shrinkVertically(
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            ) + fadeOut(animationSpec = tween(180))
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 12.dp,
                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .width(62.dp)
                    .heightIn(max = 440.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 6.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. "ALL TRAINS" Logo Button (Resets filter, shows full Tokyo network)
                    val isAllSelected = selectedOperators.isEmpty()
                    Surface(
                        onClick = onClearOperators,
                        shape = CircleShape,
                        color = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isAllSelected) BorderStroke(2.5.dp, TransitCyan) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shadowElevation = if (isAllSelected) 6.dp else 1.dp,
                        modifier = Modifier
                            .size(46.dp)
                            .testTag("filter_all_trains_logo")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = "All Tokyo Train Lines",
                                tint = if (isAllSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Divider separating ALL from individual operators
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        thickness = 1.dp
                    )

                    // 2. Individual Train Operator Logos (Logo Only / Icons Only)
                    RailOperator.entries.forEach { op ->
                        val isSelected = selectedOperators.contains(op)
                        TrainOperatorLogoButton(
                            operator = op,
                            isSelected = isSelected,
                            onClick = { onToggleOperator(op) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Authentic, Logo-Only button for each Japanese rail operator.
 * Designed with precise brand colors, emblems, and typography.
 */
@Composable
private fun TrainOperatorLogoButton(
    operator: RailOperator,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val brandColor = Color(operator.brandColorHex)

    Box(
        modifier = Modifier
            .size(46.dp)
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(13.dp)
            )
            .clip(RoundedCornerShape(13.dp))
            .background(
                brush = if (isSelected) {
                    Brush.verticalGradient(
                        colors = listOf(brandColor, brandColor.copy(alpha = 0.85f))
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(brandColor.copy(alpha = 0.92f), brandColor)
                    )
                }
            )
            .border(
                border = if (isSelected) {
                    BorderStroke(2.8.dp, Color.White)
                } else {
                    BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                },
                shape = RoundedCornerShape(13.dp)
            )
            .clickable(onClick = onClick)
            .testTag("rail_logo_${operator.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        // Draw Authentic Operator Logo Emblem
        when (operator) {
            RailOperator.JR_EAST -> {
                // Official JR East green emblem with bold italic "JR"
                Text(
                    text = "JR",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White
                )
            }
            RailOperator.TOKYO_METRO -> {
                // Official Tokyo Metro cyan emblem with white "M"
                Text(
                    text = "M",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White
                )
            }
            RailOperator.TOEI_SUBWAY -> {
                // Official Toei Ginkgo Subway emblem
                Text(
                    text = "都営",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            RailOperator.SHINKANSEN -> {
                // Shinkansen Bullet Train emblem
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Shinkansen",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            RailOperator.TOKYU -> {
                Text(
                    text = "TY",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            RailOperator.ODAKYU -> {
                Text(
                    text = "OH",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            RailOperator.KEIO -> {
                Text(
                    text = "KO",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            RailOperator.KEISEI -> {
                Text(
                    text = "KS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            RailOperator.TOBU -> {
                Text(
                    text = "TB",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            RailOperator.SEIBU -> {
                Text(
                    text = "SI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            RailOperator.YURIKAMOME -> {
                Text(
                    text = "ゆ",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            RailOperator.MONORAIL -> {
                Text(
                    text = "MO",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            RailOperator.TSUKUBA -> {
                Text(
                    text = "TX",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        // Selected Indicator: Glowing white check badge in the bottom-right corner
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-2).dp, y = (-2).dp)
                    .size(10.dp)
                    .background(Color.White, CircleShape)
            )
        }
    }
}
