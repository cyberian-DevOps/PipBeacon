package com.pipbeacon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.pipbeacon.app.core.DataResult
import com.pipbeacon.app.data.MarketItem
import com.pipbeacon.app.ui.components.MarketItemCard
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.components.ShowcaseGoldButton
import com.pipbeacon.app.ui.components.SignalBadge
import com.pipbeacon.app.ui.components.WaitingForProviderCard
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorNeutral
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.JetBrainsMonoFont
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun ForexMarketsScreen(
    items: List<MarketItem>,
    searchQuery: String,
    categoryFilter: String,
    providerStatus: DataResult<Unit>,
    selectedItem: MarketItem?,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSelectItem: (MarketItem?) -> Unit,
    onNavigateToSettings: () -> Unit,
    onAnalyzeItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Major", "Minor")

    ShowcaseBackground(modifier = modifier.testTag("forex_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search Forex pairs (e.g. EUR/USD)", fontSize = 13.sp, color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = CyanAccent) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forex_search_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccentBright,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = NavyCard,
                        unfocusedContainerColor = NavyCard
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = categoryFilter == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategoryChange(cat) },
                            label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccentBright,
                                selectedLabelColor = Color(0xFF060D1A),
                                containerColor = NavyCard,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) GoldAccentBright else NavyBorder
                            )
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (providerStatus !is DataResult.Success) {
                    item {
                        WaitingForProviderCard(onConfigureClick = onNavigateToSettings)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                if (items.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No forex pairs match your search.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                        }
                    }
                } else {
                    items(items) { item ->
                        MarketItemCard(
                            item = item,
                            onClick = { onSelectItem(item) }
                        )
                    }
                }
            }
        }
    }

    selectedItem?.let { item ->
        MarketDetailDialog(
            item = item,
            onDismiss = { onSelectItem(null) },
            onAnalyze = {
                onSelectItem(null)
                onAnalyzeItem(item.symbol)
            }
        )
    }
}

@Composable
fun MarketDetailDialog(
    item: MarketItem,
    onDismiss: () -> Unit,
    onAnalyze: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(NavySurface)
                .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = item.symbol,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavyCard)
                        .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Category:", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(item.category, style = MaterialTheme.typography.labelSmall, color = CyanAccent)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Market Status:", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(
                                if (item.isProviderLive) "Feed Active" else "Offline Demo Mode",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (item.isProviderLive) IndicatorBuy else IndicatorNeutral
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Current Price:", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(
                                if (item.currentPrice != null) String.format(Locale.US, "%.4f", item.currentPrice) else "Unavailable",
                                fontFamily = JetBrainsMonoFont,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Technical Bias:", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            SignalBadge(signal = item.signalSummary)
                        }
                        if (item.providerName != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Data Source:", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text(item.providerName, style = MaterialTheme.typography.labelSmall, color = CyanAccent)
                            }
                        }
                        if (item.quoteTimestamp != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Quote Status:", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text(
                                    item.statusText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (item.isStale) IndicatorSell else IndicatorBuy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ShowcaseGoldButton(
                    text = "Open Chart Analysis",
                    onClick = onAnalyze,
                    trailingArrow = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("analyze_pair_button")
                )
            }
        }
    }
}
