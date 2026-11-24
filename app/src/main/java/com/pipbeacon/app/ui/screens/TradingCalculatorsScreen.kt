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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pipbeacon.app.domain.PipValueResult
import com.pipbeacon.app.domain.PositionSizeResult
import com.pipbeacon.app.domain.RiskRewardResult
import com.pipbeacon.app.domain.TradingCalculatorsUseCase
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorSell
import com.pipbeacon.app.ui.theme.NavyBackground
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TealAccent
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary

@Composable
fun TradingCalculatorsScreen(
    onBack: (() -> Unit)? = null,
    calculatorUseCase: TradingCalculatorsUseCase = remember { TradingCalculatorsUseCase() }
) {
    var selectedCalculator by remember { mutableStateOf("POSITION_SIZE") } // POSITION_SIZE, PIP_VALUE, RISK_REWARD

    // Position Size State
    var accountBalance by remember { mutableStateOf("10000") }
    var riskPercentage by remember { mutableStateOf("1") }
    var stopLossPips by remember { mutableStateOf("50") }
    var pipValuePerLot by remember { mutableStateOf("10") }
    var positionSizeResult by remember { mutableStateOf<PositionSizeResult?>(null) }

    // Pip Value State
    var pipValLots by remember { mutableStateOf("1.0") }
    var pipValPerStandard by remember { mutableStateOf("10.0") }
    var pipMovement by remember { mutableStateOf("50") }
    var pipValueResult by remember { mutableStateOf<PipValueResult?>(null) }

    // Risk / Reward State
    var entryPrice by remember { mutableStateOf("1.0850") }
    var stopLossPrice by remember { mutableStateOf("1.0815") }
    var takeProfitPrice by remember { mutableStateOf("1.0930") }
    var riskRewardResult by remember { mutableStateOf<RiskRewardResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("trading_calculators_screen")
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
                text = "Trading Calculators",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector Chips: ◎ Position Size | $ Pip Value | % Risk/Reward
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcTypeChip(
                label = "◎ Position Size",
                isSelected = selectedCalculator == "POSITION_SIZE",
                onClick = { selectedCalculator = "POSITION_SIZE" },
                modifier = Modifier.weight(1.1f)
            )
            CalcTypeChip(
                label = "$ Pip Value",
                isSelected = selectedCalculator == "PIP_VALUE",
                onClick = { selectedCalculator = "PIP_VALUE" },
                modifier = Modifier.weight(0.9f)
            )
            CalcTypeChip(
                label = "% Risk/Reward",
                isSelected = selectedCalculator == "RISK_REWARD",
                onClick = { selectedCalculator = "RISK_REWARD" },
                modifier = Modifier.weight(1.0f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Calculator Form Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(NavyCard)
                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
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

                        Spacer(modifier = Modifier.height(16.dp))

                        CalculatorInputField(
                            label = "Account Balance",
                            suffix = "$",
                            value = accountBalance,
                            onValueChange = { accountBalance = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CalculatorInputField(
                            label = "Risk Percentage",
                            suffix = "%",
                            value = riskPercentage,
                            onValueChange = { riskPercentage = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CalculatorInputField(
                            label = "Stop Loss",
                            suffix = "pips",
                            value = stopLossPips,
                            onValueChange = { stopLossPips = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CalculatorInputField(
                            label = "Pip Value (per lot)",
                            suffix = "$",
                            value = pipValuePerLot,
                            onValueChange = { pipValuePerLot = it }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val bal = accountBalance.toDoubleOrNull() ?: 10000.0
                                val risk = riskPercentage.toDoubleOrNull() ?: 1.0
                                val sl = stopLossPips.toDoubleOrNull() ?: 50.0
                                val pipVal = pipValuePerLot.toDoubleOrNull() ?: 10.0
                                positionSizeResult = calculatorUseCase.calculatePositionSize(bal, risk, sl, pipVal)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("calculate_position_size_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccentBright),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(
                                text = "Calculate",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                "PIP_VALUE" -> {
                    Column {
                        Text(
                            text = "Pip Value Calculator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Find out how much each pip is worth on your open positions",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CalculatorInputField(
                            label = "Lot Size",
                            suffix = "lots",
                            value = pipValLots,
                            onValueChange = { pipValLots = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CalculatorInputField(
                            label = "Pip Value (Standard 1.0 Lot)",
                            suffix = "$",
                            value = pipValPerStandard,
                            onValueChange = { pipValPerStandard = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CalculatorInputField(
                            label = "Expected Pip Movement",
                            suffix = "pips",
                            value = pipMovement,
                            onValueChange = { pipMovement = it }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val lots = pipValLots.toDoubleOrNull() ?: 1.0
                                val std = pipValPerStandard.toDoubleOrNull() ?: 10.0
                                val move = pipMovement.toDoubleOrNull() ?: 50.0
                                pipValueResult = calculatorUseCase.calculatePipValue(lots, std, move)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("calculate_pip_value_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccentBright),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(
                                text = "Calculate",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
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

                        Spacer(modifier = Modifier.height(16.dp))

                        CalculatorInputField(
                            label = "Entry Price",
                            suffix = "price",
                            value = entryPrice,
                            onValueChange = { entryPrice = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CalculatorInputField(
                            label = "Stop Loss Price",
                            suffix = "price",
                            value = stopLossPrice,
                            onValueChange = { stopLossPrice = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CalculatorInputField(
                            label = "Take Profit Price",
                            suffix = "price",
                            value = takeProfitPrice,
                            onValueChange = { takeProfitPrice = it }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val entry = entryPrice.toDoubleOrNull() ?: 1.0850
                                val sl = stopLossPrice.toDoubleOrNull() ?: 1.0815
                                val tp = takeProfitPrice.toDoubleOrNull() ?: 1.0930
                                riskRewardResult = calculatorUseCase.calculateRiskReward(entry, sl, tp)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("calculate_rr_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccentBright),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(
                                text = "Calculate",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
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
                    ResultRow(label = "Optimal Position Size", value = "${res.lotSize} Lots", valueColor = IndicatorBuy)
                    ResultRow(label = "Total Risk Exposure", value = "$${String.format(java.util.Locale.US, "%.2f", res.riskAmount)}", valueColor = IndicatorSell)
                    ResultRow(label = "Pip Value for Position", value = "$${String.format(java.util.Locale.US, "%.2f", res.pipValueTotal)} / pip")
                    ResultRow(label = "Contract Volume", value = "${String.format(java.util.Locale.US, "%,d", res.units)} Units")
                }
            }
        }

        pipValueResult?.let { res ->
            if (selectedCalculator == "PIP_VALUE") {
                ResultCard(title = "Calculated Pip Value") {
                    ResultRow(label = "Value Per Pip", value = "$${res.valuePerPip}", valueColor = GoldAccentBright)
                    ResultRow(label = "Movement Projected", value = "${res.totalPipsForMove} Pips")
                    ResultRow(label = "Projected P&L", value = "$${res.estimatedProfitLoss}", valueColor = IndicatorBuy)
                }
            }
        }

        riskRewardResult?.let { res ->
            if (selectedCalculator == "RISK_REWARD") {
                ResultCard(title = "Risk to Reward Analysis") {
                    ResultRow(label = "Risk / Reward Ratio", value = res.riskRewardRatioText, valueColor = if (res.isFavorable) IndicatorBuy else IndicatorSell)
                    ResultRow(label = "Stop Loss Distance", value = "${res.riskPips} Pips", valueColor = IndicatorSell)
                    ResultRow(label = "Take Profit Distance", value = "${res.rewardPips} Pips", valueColor = IndicatorBuy)
                    ResultRow(label = "Breakeven Win Rate Needed", value = "${res.breakevenWinRatePercent}%", valueColor = GoldAccentBright)
                }
            }
        }
    }
}

@Composable
private fun CalcTypeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) GoldAccentBright else NavySurfaceVariant)
            .border(
                1.dp,
                if (isSelected) GoldAccentBright else NavyBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
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
            shape = RoundedCornerShape(10.dp),
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
            .clip(RoundedCornerShape(14.dp))
            .background(NavyCard)
            .border(1.dp, IndicatorBuy.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
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
        Text(text = value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
