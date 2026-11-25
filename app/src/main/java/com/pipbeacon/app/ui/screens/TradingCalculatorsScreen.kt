package com.pipbeacon.app.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.domain.PipValueResult
import com.pipbeacon.app.domain.PositionSizeResult
import com.pipbeacon.app.domain.RiskRewardResult
import com.pipbeacon.app.domain.TradingCalculatorsUseCase
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.components.ShowcaseGoldButton
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.JetBrainsMonoFont
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun TradingCalculatorsScreen(
    onBack: (() -> Unit)? = null,
    calculatorUseCase: TradingCalculatorsUseCase = remember { TradingCalculatorsUseCase() }
) {
    var selectedCalculator by remember { mutableStateOf("POSITION_SIZE") } // POSITION_SIZE, PIP_VALUE, RISK_REWARD, PROFIT

    // Position Size State
    var accountBalance by remember { mutableStateOf("10000") }
    var riskPercentage by remember { mutableStateOf("1") }
    var stopLossPips by remember { mutableStateOf("50") }
    var pipValuePerLot by remember { mutableStateOf("10") }
    var positionSizeResult by remember { mutableStateOf<PositionSizeResult?>(null) }

    // Pip Value & Profit Estimator State
    var pipValLots by remember { mutableStateOf("1.0") }
    var pipValPerStandard by remember { mutableStateOf("10.0") }
    var pipMovement by remember { mutableStateOf("50") }
    var pipValueResult by remember { mutableStateOf<PipValueResult?>(null) }

    // Risk / Reward State
    var entryPrice by remember { mutableStateOf("1.0850") }
    var stopLossPrice by remember { mutableStateOf("1.0815") }
    var takeProfitPrice by remember { mutableStateOf("1.0930") }
    var riskRewardResult by remember { mutableStateOf<RiskRewardResult?>(null) }

    ShowcaseBackground(modifier = Modifier.testTag("trading_calculators_screen")) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = "Calculators",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Showcase 4 Icon-Badged Calculator Menu Rows
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShowcaseCalculatorSelectorRow(
                    title = "Pip Calculator",
                    subtitle = "Value per pip by contract size",
                    icon = Icons.Default.Calculate,
                    iconTint = CyanAccent,
                    isSelected = selectedCalculator == "PIP_VALUE",
                    onClick = { selectedCalculator = "PIP_VALUE" }
                )
                ShowcaseCalculatorSelectorRow(
                    title = "Position Size Calculator",
                    subtitle = "Optimal lot sizing from account risk %",
                    icon = Icons.Default.Balance,
                    iconTint = GoldAccentBright,
                    isSelected = selectedCalculator == "POSITION_SIZE",
                    onClick = { selectedCalculator = "POSITION_SIZE" }
                )
                ShowcaseCalculatorSelectorRow(
                    title = "Risk Calculator",
                    subtitle = "Analyze Risk:Reward ratio & breakeven win rate",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    iconTint = IndicatorBuy,
                    isSelected = selectedCalculator == "RISK_REWARD",
                    onClick = { selectedCalculator = "RISK_REWARD" }
                )
                ShowcaseCalculatorSelectorRow(
                    title = "Profit Calculator",
                    subtitle = "Estimate projected P&L from target pips",
                    icon = Icons.Default.AttachMoney,
                    iconTint = IndicatorBuy,
                    isSelected = selectedCalculator == "PROFIT",
                    onClick = { selectedCalculator = "PROFIT" }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculator Form Box
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(18.dp)) {
                    when (selectedCalculator) {
                        "POSITION_SIZE" -> {
                            Column {
                                Text(
                                    text = "Position Size Calculator",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Calculate the optimal lot size based on your risk tolerance",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                CalculatorInputField("Account Balance", "$", accountBalance) { accountBalance = it }
                                Spacer(modifier = Modifier.height(10.dp))
                                CalculatorInputField("Risk Percentage", "%", riskPercentage) { riskPercentage = it }
                                Spacer(modifier = Modifier.height(10.dp))
                                CalculatorInputField("Stop Loss", "pips", stopLossPips) { stopLossPips = it }
                                Spacer(modifier = Modifier.height(10.dp))
                                CalculatorInputField("Pip Value (per lot)", "$", pipValuePerLot) { pipValuePerLot = it }

                                Spacer(modifier = Modifier.height(18.dp))

                                ShowcaseGoldButton(
                                    text = "Calculate",
                                    onClick = {
                                        val bal = accountBalance.toDoubleOrNull() ?: 10000.0
                                        val risk = riskPercentage.toDoubleOrNull() ?: 1.0
                                        val sl = stopLossPips.toDoubleOrNull() ?: 50.0
                                        val pipVal = pipValuePerLot.toDoubleOrNull() ?: 10.0
                                        positionSizeResult = calculatorUseCase.calculatePositionSize(bal, risk, sl, pipVal)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("calculate_position_size_button")
                                )
                            }
                        }

                        "PIP_VALUE", "PROFIT" -> {
                            val isProfitMode = selectedCalculator == "PROFIT"
                            Column {
                                Text(
                                    text = if (isProfitMode) "Profit Calculator" else "Pip Value Calculator",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isProfitMode) {
                                        "Project educational return across target pips and contract volume"
                                    } else {
                                        "Find out how much each pip is worth on your open positions"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                CalculatorInputField("Lot Size", "lots", pipValLots) { pipValLots = it }
                                Spacer(modifier = Modifier.height(10.dp))
                                CalculatorInputField("Pip Value (Standard 1.0 Lot)", "$", pipValPerStandard) { pipValPerStandard = it }
                                Spacer(modifier = Modifier.height(10.dp))
                                CalculatorInputField("Expected Pip Movement", "pips", pipMovement) { pipMovement = it }

                                Spacer(modifier = Modifier.height(18.dp))

                                ShowcaseGoldButton(
                                    text = "Calculate",
                                    onClick = {
                                        val lots = pipValLots.toDoubleOrNull() ?: 1.0
                                        val std = pipValPerStandard.toDoubleOrNull() ?: 10.0
                                        val move = pipMovement.toDoubleOrNull() ?: 50.0
                                        pipValueResult = calculatorUseCase.calculatePipValue(lots, std, move)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("calculate_pip_value_button")
                                )
                            }
                        }

                        "RISK_REWARD" -> {
                            Column {
                                Text(
                                    text = "Risk / Reward Calculator",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Analyze trade expectancy and required breakeven win rate",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                CalculatorInputField("Entry Price", "price", entryPrice) { entryPrice = it }
                                Spacer(modifier = Modifier.height(10.dp))
                                CalculatorInputField("Stop Loss Price", "price", stopLossPrice) { stopLossPrice = it }
                                Spacer(modifier = Modifier.height(10.dp))
                                CalculatorInputField("Take Profit Price", "price", takeProfitPrice) { takeProfitPrice = it }

                                Spacer(modifier = Modifier.height(18.dp))

                                ShowcaseGoldButton(
                                    text = "Calculate",
                                    onClick = {
                                        val entry = entryPrice.toDoubleOrNull() ?: 1.0850
                                        val sl = stopLossPrice.toDoubleOrNull() ?: 1.0815
                                        val tp = takeProfitPrice.toDoubleOrNull() ?: 1.0930
                                        riskRewardResult = calculatorUseCase.calculateRiskReward(entry, sl, tp)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("calculate_rr_button")
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Output Results Display
            positionSizeResult?.let { res ->
                if (selectedCalculator == "POSITION_SIZE") {
                    ResultCard(title = "Calculated Position Size") {
                        ResultRow("Optimal Position Size", "${res.lotSize} Lots", IndicatorBuy)
                        ResultRow("Total Risk Exposure", "$${String.format(Locale.US, "%.2f", res.riskAmount)}", IndicatorSell)
                        ResultRow("Pip Value for Position", "$${String.format(Locale.US, "%.2f", res.pipValueTotal)} / pip")
                        ResultRow("Contract Volume", "${String.format(Locale.US, "%,d", res.units)} Units")
                    }
                }
            }

            pipValueResult?.let { res ->
                if (selectedCalculator == "PIP_VALUE" || selectedCalculator == "PROFIT") {
                    ResultCard(title = if (selectedCalculator == "PROFIT") "Estimated Profit Projection" else "Calculated Pip Value") {
                        ResultRow("Value Per Pip", "$${res.valuePerPip}", GoldAccentBright)
                        ResultRow("Movement Projected", "${res.totalPipsForMove} Pips")
                        ResultRow("Projected P&L", "$${res.estimatedProfitLoss}", IndicatorBuy)
                    }
                }
            }

            riskRewardResult?.let { res ->
                if (selectedCalculator == "RISK_REWARD") {
                    ResultCard(title = "Risk to Reward Analysis") {
                        ResultRow("Risk / Reward Ratio", res.riskRewardRatioText, if (res.isFavorable) IndicatorBuy else IndicatorSell)
                        ResultRow("Stop Loss Distance", "${res.riskPips} Pips", IndicatorSell)
                        ResultRow("Take Profit Distance", "${res.rewardPips} Pips", IndicatorBuy)
                        ResultRow("Breakeven Win Rate Needed", "${res.breakevenWinRatePercent}%", GoldAccentBright)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowcaseCalculatorSelectorRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (isSelected) GoldAccentBright else NavyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconTint.copy(alpha = 0.14f))
                        .border(1.dp, iconTint.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(19.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = TextSecondary, fontSize = 11.sp)
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Select $title",
                tint = if (isSelected) GoldAccentBright else TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun CalculatorInputField(
    label: String,
    suffix: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            trailingIcon = {
                Text(
                    text = suffix,
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldAccentBright,
                unfocusedBorderColor = NavyBorder,
                focusedContainerColor = NavySurfaceVariant,
                unfocusedContainerColor = NavySurfaceVariant,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ResultCard(
    title: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NavyCard)
            .border(1.dp, IndicatorBuy.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                color = IndicatorBuy,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(
            text = value,
            color = valueColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            fontFamily = JetBrainsMonoFont
        )
    }
}
