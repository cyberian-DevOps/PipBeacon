package com.pipbeacon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.data.ai.AiTradingService
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCardElevated
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.TealAccent
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary

@Composable
fun AiToolsScreen(
    initialTab: String = "TUTOR", // TUTOR, NEWS
    onBack: (() -> Unit)? = null,
    aiService: AiTradingService = remember { AiTradingService() }
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    ShowcaseBackground(modifier = Modifier.testTag("ai_tools_screen")) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NavySurface)
                .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            AiTabItem(
                label = "AI Trading Tutor",
                icon = Icons.Default.AutoAwesome,
                isSelected = selectedTab == "TUTOR",
                onClick = { selectedTab = "TUTOR" },
                modifier = Modifier.weight(1f)
            )

            AiTabItem(
                label = "AI News Analyser",
                icon = Icons.Default.Newspaper,
                isSelected = selectedTab == "NEWS",
                onClick = { selectedTab = "NEWS" },
                modifier = Modifier.weight(1f)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                "TUTOR" -> AiTutorScreen(onBack = onBack, aiService = aiService)
                "NEWS" -> AiNewsAnalyzerScreen(onBack = onBack, aiService = aiService)
            }
        }
        }
    }
}

@Composable
private fun AiTabItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) NavyCardElevated else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) GoldAccentBright else TextMuted,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
