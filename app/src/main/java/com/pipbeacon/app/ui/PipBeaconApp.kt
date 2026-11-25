package com.pipbeacon.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.pipbeacon.app.ui.theme.PipBeaconTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pipbeacon.app.data.MarketRepository
import com.pipbeacon.app.data.PermittedProviderMode
import com.pipbeacon.app.data.SettingsRepository
import com.pipbeacon.app.data.SignalRepository
import com.pipbeacon.app.data.lessons.LessonRepository
import com.pipbeacon.app.data.local.db.PipBeaconDatabase
import com.pipbeacon.app.data.news.NewsRepository
import com.pipbeacon.app.domain.CalculateTechnicalAnalysisUseCase
import com.pipbeacon.app.domain.ComputeForexSignalsUseCase
import com.pipbeacon.app.domain.GetCryptoMarketsUseCase
import com.pipbeacon.app.domain.GetForexMarketsUseCase
import com.pipbeacon.app.domain.GetMarketSessionsUseCase
import com.pipbeacon.app.domain.GetTradingSignalsUseCase
import com.pipbeacon.app.domain.PipCalculatorUseCase
import com.pipbeacon.app.notifications.NotificationHelper
import com.pipbeacon.app.ui.components.NavigationDestination
import com.pipbeacon.app.ui.components.PipBeaconBottomNav
import com.pipbeacon.app.ui.components.PipBeaconTopBar
import com.pipbeacon.app.ui.screens.AiToolsScreen
import com.pipbeacon.app.ui.screens.CryptoAcademyScreen
import com.pipbeacon.app.ui.screens.CryptoMarketsScreen
import com.pipbeacon.app.ui.screens.ForexAcademyScreen
import com.pipbeacon.app.ui.screens.ForexMarketsScreen
import com.pipbeacon.app.ui.screens.ForexSignalEngineScreen
import com.pipbeacon.app.ui.screens.HomeScreen
import com.pipbeacon.app.ui.screens.LiveRatesScreen
import com.pipbeacon.app.ui.screens.MarketNewsScreen
import com.pipbeacon.app.ui.screens.OnboardingScreen
import com.pipbeacon.app.ui.screens.ProfileScreen
import com.pipbeacon.app.ui.screens.SettingsScreen
import com.pipbeacon.app.ui.screens.SplashScreen
import com.pipbeacon.app.ui.screens.TechnicalAnalysisScreen
import com.pipbeacon.app.ui.screens.TradingCalculatorsScreen
import com.pipbeacon.app.ui.screens.TradingJournalScreen
import com.pipbeacon.app.ui.screens.TradingLessonsScreen
import com.pipbeacon.app.ui.screens.TradingSignalsScreen
import com.pipbeacon.app.ui.theme.NavyBackground
import com.pipbeacon.app.viewmodel.AcademyViewModel
import com.pipbeacon.app.viewmodel.ForexSignalEngineViewModel
import com.pipbeacon.app.viewmodel.MarketViewModel
import com.pipbeacon.app.viewmodel.NewsViewModel
import com.pipbeacon.app.viewmodel.SettingsViewModel
import com.pipbeacon.app.viewmodel.SignalViewModel
import com.pipbeacon.app.viewmodel.TechnicalAnalysisViewModel
import com.pipbeacon.app.work.WorkScheduler

@Composable
fun PipBeaconApp(
    settingsRepository: SettingsRepository = remember { SettingsRepository() }
) {
    val context = LocalContext.current
    val database = remember { PipBeaconDatabase.getInstance(context) }
    val marketRepository = remember { MarketRepository(settingsRepository, database = database) }
    val signalRepository = remember { SignalRepository(settingsRepository, database = database) }
    val newsRepository = remember { NewsRepository() }
    val lessonRepository = remember { LessonRepository() }

    // Init notification channel and schedule WorkManager
    LaunchedEffect(Unit) {
        NotificationHelper.initNotificationChannel(context)
        val config = settingsRepository.getConfig()
        WorkScheduler.schedulePeriodicRefresh(context, config.backgroundRefreshIntervalMinutes)
        if (config.autoSync) {
            marketRepository.refreshPrices()
        }
    }

    // ViewModel setup via remember
    val getForexMarketsUseCase = remember { GetForexMarketsUseCase(marketRepository) }
    val getCryptoMarketsUseCase = remember { GetCryptoMarketsUseCase(marketRepository) }
    val getMarketSessionsUseCase = remember { GetMarketSessionsUseCase(marketRepository) }
    val getTradingSignalsUseCase = remember { GetTradingSignalsUseCase(signalRepository) }
    val calculateTechnicalAnalysisUseCase = remember { CalculateTechnicalAnalysisUseCase(marketRepository) }
    val pipCalculatorUseCase = remember { PipCalculatorUseCase() }

    val marketViewModel: MarketViewModel = viewModel {
        MarketViewModel(
            marketRepository = marketRepository,
            getForexMarketsUseCase = getForexMarketsUseCase,
            getCryptoMarketsUseCase = getCryptoMarketsUseCase,
            getMarketSessionsUseCase = getMarketSessionsUseCase
        )
    }

    val signalViewModel: SignalViewModel = viewModel {
        SignalViewModel(getTradingSignalsUseCase = getTradingSignalsUseCase)
    }

    val technicalAnalysisViewModel: TechnicalAnalysisViewModel = viewModel {
        TechnicalAnalysisViewModel(
            calculateTechnicalAnalysisUseCase = calculateTechnicalAnalysisUseCase,
            pipCalculatorUseCase = pipCalculatorUseCase
        )
    }

    val settingsViewModel: SettingsViewModel = viewModel {
        SettingsViewModel(
            settingsRepository = settingsRepository,
            marketRepository = marketRepository
        )
    }

    val newsViewModel: NewsViewModel = viewModel {
        NewsViewModel(newsRepository = newsRepository)
    }

    val academyViewModel: AcademyViewModel = viewModel {
        AcademyViewModel(lessonRepository = lessonRepository)
    }

    val computeForexSignalsUseCase = remember { ComputeForexSignalsUseCase(marketRepository) }
    val forexSignalEngineViewModel: ForexSignalEngineViewModel = viewModel {
        ForexSignalEngineViewModel(computeForexSignalsUseCase = computeForexSignalsUseCase)
    }

    var showSplash by remember { mutableStateOf(true) }
    var currentRoute by remember { mutableStateOf(NavigationDestination.HOME.route) }

    val marketUiState by marketViewModel.uiState.collectAsStateWithLifecycle()
    val forexList by marketViewModel.forexList.collectAsStateWithLifecycle()
    val cryptoList by marketViewModel.cryptoList.collectAsStateWithLifecycle()
    val liveRatesList by marketViewModel.liveRatesList.collectAsStateWithLifecycle()
    val marketSessions by marketViewModel.marketSessions.collectAsStateWithLifecycle()
    val providerStatus by marketViewModel.providerStatus.collectAsStateWithLifecycle()
    val lastRefreshTime by marketViewModel.lastRefreshTime.collectAsStateWithLifecycle()

    val signalUiState by signalViewModel.uiState.collectAsStateWithLifecycle()
    val signalsList by signalViewModel.filteredSignals.collectAsStateWithLifecycle()
    val historicalSignals by signalViewModel.historicalSignals.collectAsStateWithLifecycle()

    val techUiState by technicalAnalysisViewModel.uiState.collectAsStateWithLifecycle()

    val settingsConfig by settingsViewModel.config.collectAsStateWithLifecycle()
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val candidateConfigs by settingsViewModel.candidateConfigs.collectAsStateWithLifecycle()
    val providerStatuses by settingsViewModel.providerStatuses.collectAsStateWithLifecycle()
    val academyUiState by academyViewModel.uiState.collectAsStateWithLifecycle()

    // Intercept system back button to return cleanly to previous hub or Home screen
    BackHandler(enabled = !showSplash && settingsConfig.hasCompletedOnboarding && currentRoute != NavigationDestination.HOME.route) {
        currentRoute = when (currentRoute) {
            NavigationDestination.SETTINGS.route,
            NavigationDestination.JOURNAL.route -> NavigationDestination.PROFILE.route
            else -> NavigationDestination.HOME.route
        }
    }

    val systemIsDark = isSystemInDarkTheme()
    val isDarkTheme = when (settingsConfig.themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> systemIsDark
    }

    val onToggleTheme: () -> Unit = {
        val nextMode = if (isDarkTheme) "LIGHT" else "DARK"
        settingsViewModel.setThemeMode(nextMode)
    }

    val screenTitle = when (currentRoute) {
        NavigationDestination.HOME.route -> "PipBeacon: Market Pulse"
        NavigationDestination.RATES.route -> "Markets & Live Rates"
        NavigationDestination.SIGNALS.route -> "Smart AI Signals"
        NavigationDestination.NEWS.route -> "Forex & Crypto News"
        NavigationDestination.PROFILE.route -> "Trader Profile"
        NavigationDestination.FOREX_ACADEMY.route -> "Forex Academy"
        NavigationDestination.CRYPTO_ACADEMY.route -> "Crypto Academy"
        NavigationDestination.AI_TOOLS.route -> "AI Trading Tools"
        NavigationDestination.CALCULATORS.route -> "Trading Calculators"
        NavigationDestination.LESSONS.route -> "Trading Lessons Academy"
        NavigationDestination.FOREX.route -> "Forex Markets"
        NavigationDestination.CRYPTO.route -> "Crypto Markets"
        NavigationDestination.ANALYSIS.route -> "Technical Analysis"
        NavigationDestination.SIGNAL_ENGINE.route -> "Forex Signal Engine"
        NavigationDestination.SETTINGS.route -> "Settings & Data"
        NavigationDestination.JOURNAL.route -> "Trading Journal"
        NavigationDestination.CALENDAR.route -> "Economic Calendar"
        else -> "PipBeacon"
    }

    val selfHeaderRoutes = setOf(
        NavigationDestination.HOME.route,
        NavigationDestination.JOURNAL.route,
        NavigationDestination.FOREX_ACADEMY.route,
        NavigationDestination.CRYPTO_ACADEMY.route,
        NavigationDestination.CALCULATORS.route,
        NavigationDestination.LESSONS.route,
        NavigationDestination.SIGNAL_ENGINE.route
    )

    PipBeaconTheme(darkTheme = isDarkTheme) {
        Crossfade(
            targetState = when {
                showSplash -> "SPLASH"
                !settingsConfig.hasCompletedOnboarding -> "ONBOARDING"
                else -> "MAIN"
            },
            animationSpec = tween(durationMillis = 420),
            label = "app_entry_crossfade"
        ) { entryStage ->
            when (entryStage) {
                "SPLASH" -> {
                    SplashScreen(
                        onSplashComplete = { showSplash = false }
                    )
                }
                "ONBOARDING" -> {
                    OnboardingScreen(
                        onFinishOnboarding = { settingsViewModel.completeOnboarding() }
                    )
                }
                else -> {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (isDarkTheme) NavyBackground else Color(0xFFF1F5F9)),
                        topBar = {
                            if (currentRoute !in selfHeaderRoutes) {
                                PipBeaconTopBar(
                                    title = screenTitle,
                                    providerStatus = providerStatus,
                                    onRefreshClick = { marketViewModel.refresh() },
                                    onCalculatorClick = { currentRoute = NavigationDestination.CALCULATORS.route },
                                    isDarkTheme = isDarkTheme,
                                    onToggleTheme = onToggleTheme
                                )
                            }
                        },
                        bottomBar = {
                            if (currentRoute != NavigationDestination.HOME.route) {
                                PipBeaconBottomNav(
                                    currentRoute = if (currentRoute == NavigationDestination.CALENDAR.route) {
                                        NavigationDestination.NEWS.route
                                    } else {
                                        currentRoute
                                    },
                                    onNavigate = { route -> currentRoute = route }
                                )
                            }
                        }
                    ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentRoute) {
                    NavigationDestination.HOME.route -> {
                        HomeScreen(
                            forexItems = forexList,
                            cryptoItems = cryptoList,
                            signals = signalsList,
                            sessions = marketSessions,
                            providerStatus = providerStatus,
                            lastRefreshTime = lastRefreshTime,
                            isRefreshing = marketUiState.isRefreshing,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = onToggleTheme,
                            onRefresh = { marketViewModel.refresh() },
                            onNavigateToForex = { currentRoute = NavigationDestination.FOREX.route },
                            onNavigateToCrypto = { currentRoute = NavigationDestination.CRYPTO.route },
                            onNavigateToSignals = { currentRoute = NavigationDestination.SIGNALS.route },
                            onNavigateToSettings = { currentRoute = NavigationDestination.SETTINGS.route },
                            onOpenCalculator = { currentRoute = NavigationDestination.CALCULATORS.route },
                            onNavigateToAiTutor = { currentRoute = NavigationDestination.AI_TOOLS.route },
                            onNavigateToAiNews = { currentRoute = NavigationDestination.NEWS.route },
                            onNavigateToCalculators = { currentRoute = NavigationDestination.CALCULATORS.route },
                            onNavigateToLessons = { currentRoute = NavigationDestination.LESSONS.route },
                            onNavigateToLiveRates = { currentRoute = NavigationDestination.RATES.route },
                            onSelectItem = { item ->
                                technicalAnalysisViewModel.selectSymbol(item.symbol)
                                currentRoute = NavigationDestination.ANALYSIS.route
                            },
                            onAnalyzeSymbol = { symbol ->
                                technicalAnalysisViewModel.selectSymbol(symbol)
                                currentRoute = NavigationDestination.ANALYSIS.route
                            },
                            onNavigateToSignalEngine = {
                                currentRoute = NavigationDestination.SIGNAL_ENGINE.route
                            },
                            onNavigateToProfile = {
                                currentRoute = NavigationDestination.PROFILE.route
                            },
                            onNavigateToForexAcademy = {
                                currentRoute = NavigationDestination.FOREX_ACADEMY.route
                            },
                            onNavigateToCryptoAcademy = {
                                currentRoute = NavigationDestination.CRYPTO_ACADEMY.route
                            },
                            onNavigateToJournal = {
                                currentRoute = NavigationDestination.JOURNAL.route
                            },
                            onNavigateToCalendar = {
                                currentRoute = NavigationDestination.CALENDAR.route
                            }
                        )
                    }
                NavigationDestination.RATES.route -> {
                    LiveRatesScreen(
                        uiState = marketUiState,
                        liveRates = liveRatesList,
                        providerStatus = providerStatus,
                        lastRefreshTime = lastRefreshTime,
                        onSearchChanged = { marketViewModel.onSearchQueryChanged(it) },
                        onTabSelected = { marketViewModel.setLiveRateTab(it) },
                        onSortSelected = { marketViewModel.setRateSortOption(it) },
                        onToggleAutoRefresh = { marketViewModel.toggleAutoRefresh() },
                        onRefresh = { marketViewModel.refresh() },
                        onNavigateToAnalysis = { symbol ->
                            technicalAnalysisViewModel.selectSymbol(symbol)
                            currentRoute = NavigationDestination.ANALYSIS.route
                        },
                        onNavigateToCalculator = { symbol ->
                            currentRoute = NavigationDestination.CALCULATORS.route
                        }
                    )
                }
                NavigationDestination.FOREX.route -> {
                    ForexMarketsScreen(
                        items = forexList,
                        searchQuery = marketUiState.searchQuery,
                        categoryFilter = marketUiState.forexCategoryFilter,
                        providerStatus = providerStatus,
                        selectedItem = marketUiState.selectedItem,
                        onSearchChange = { marketViewModel.onSearchQueryChanged(it) },
                        onCategoryChange = { marketViewModel.setForexCategoryFilter(it) },
                        onSelectItem = { marketViewModel.selectItem(it) },
                        onNavigateToSettings = { currentRoute = NavigationDestination.SETTINGS.route },
                        onAnalyzeItem = { symbol ->
                            technicalAnalysisViewModel.selectSymbol(symbol)
                            currentRoute = NavigationDestination.ANALYSIS.route
                        }
                    )
                }
                NavigationDestination.CRYPTO.route -> {
                    CryptoMarketsScreen(
                        items = cryptoList,
                        searchQuery = marketUiState.searchQuery,
                        categoryFilter = marketUiState.cryptoCategoryFilter,
                        providerStatus = providerStatus,
                        selectedItem = marketUiState.selectedItem,
                        onSearchChange = { marketViewModel.onSearchQueryChanged(it) },
                        onCategoryChange = { marketViewModel.setCryptoCategoryFilter(it) },
                        onSelectItem = { marketViewModel.selectItem(it) },
                        onNavigateToSettings = { currentRoute = NavigationDestination.SETTINGS.route },
                        onAnalyzeItem = { symbol ->
                            technicalAnalysisViewModel.selectSymbol(symbol)
                            currentRoute = NavigationDestination.ANALYSIS.route
                        }
                    )
                }
                NavigationDestination.SIGNALS.route -> {
                    TradingSignalsScreen(
                        signals = signalsList,
                        historicalSignals = historicalSignals,
                        performanceStats = signalViewModel.getPerformanceStats(),
                        showHistoryTab = signalUiState.showHistoryTab,
                        filterType = signalUiState.filterType,
                        onlyHighConfidence = signalUiState.onlyHighConfidence,
                        selectedSignal = signalUiState.selectedSignal,
                        onToggleTab = { signalViewModel.setShowHistoryTab(it) },
                        onFilterChange = { signalViewModel.setFilter(it) },
                        onToggleHighConfidence = { signalViewModel.toggleHighConfidence(it) },
                        onSelectSignal = { signalViewModel.selectSignal(it) },
                        onAnalyzeSymbol = { symbol ->
                            technicalAnalysisViewModel.selectSymbol(symbol)
                            currentRoute = NavigationDestination.ANALYSIS.route
                        },
                        onNavigateToSignalEngine = {
                            currentRoute = NavigationDestination.SIGNAL_ENGINE.route
                        }
                    )
                }
                NavigationDestination.ANALYSIS.route -> {
                    TechnicalAnalysisScreen(
                        selectedSymbol = techUiState.selectedSymbol,
                        selectedTimeFrame = techUiState.selectedTimeFrame,
                        isCandleChart = techUiState.isCandleChart,
                        showSmaOverlay = techUiState.showSmaOverlay,
                        showEmaOverlay = techUiState.showEmaOverlay,
                        showBollingerOverlay = techUiState.showBollingerOverlay,
                        candles = techUiState.candles,
                        report = techUiState.report,
                        showPipCalculator = techUiState.showPipCalculatorDialog,
                        calcEntry = techUiState.calcEntryPrice,
                        calcSl = techUiState.calcStopLoss,
                        calcTp = techUiState.calcTakeProfit,
                        calcBalance = techUiState.calcAccountBalance,
                        calcRisk = techUiState.calcRiskPercent,
                        calcLots = techUiState.calcLotSize,
                        calcResult = techUiState.calcResult,
                        onSelectSymbol = { technicalAnalysisViewModel.selectSymbol(it) },
                        onSelectTimeFrame = { technicalAnalysisViewModel.selectTimeFrame(it) },
                        onToggleChartType = { technicalAnalysisViewModel.toggleChartType() },
                        onToggleSma = { technicalAnalysisViewModel.toggleSmaOverlay() },
                        onToggleEma = { technicalAnalysisViewModel.toggleEmaOverlay() },
                        onToggleBollinger = { technicalAnalysisViewModel.toggleBollingerOverlay() },
                        onOpenCalculator = { technicalAnalysisViewModel.openPipCalculator(it) },
                        onUpdateCalcEntry = { technicalAnalysisViewModel.updateCalcEntryPrice(it) },
                        onUpdateCalcSl = { technicalAnalysisViewModel.updateCalcStopLoss(it) },
                        onUpdateCalcTp = { technicalAnalysisViewModel.updateCalcTakeProfit(it) },
                        onUpdateCalcBalance = { technicalAnalysisViewModel.updateCalcAccountBalance(it) },
                        onUpdateCalcRisk = { technicalAnalysisViewModel.updateCalcRiskPercent(it) },
                        onUpdateCalcLots = { technicalAnalysisViewModel.updateCalcLotSize(it) }
                    )
                }
                NavigationDestination.SETTINGS.route -> {
                    SettingsScreen(
                        config = settingsConfig,
                        candidateConfigs = candidateConfigs,
                        providerStatuses = providerStatuses,
                        diagnosticMessage = settingsUiState.diagnosticMessage,
                        isTesting = settingsUiState.isTestingConnection,
                        onModeChange = { settingsViewModel.setProviderMode(it) },
                        onCustomUrlChange = { settingsViewModel.setCustomUrl(it) },
                        onRefreshIntervalChange = { settingsViewModel.setRefreshInterval(it) },
                        onAutoSyncChange = { settingsViewModel.setAutoSync(it) },
                        onRiskToleranceChange = { settingsViewModel.setRiskTolerance(it) },
                        onToggleCandidate = { id, enabled -> settingsViewModel.toggleCandidateProvider(id, enabled) },
                        onVerifyCandidate = { id, verified -> settingsViewModel.verifyCandidateProvider(id, verified) },
                        onTestConnection = { settingsViewModel.testConnection() },
                        onResetDefaults = { settingsViewModel.resetToDefaults() },
                        onThemeModeChange = { settingsViewModel.setThemeMode(it) },
                        onNotificationsEnabledChange = { settingsViewModel.setNotificationsEnabled(it) },
                        onBackgroundRefreshIntervalChange = { interval ->
                            settingsViewModel.setBackgroundRefreshInterval(interval)
                            WorkScheduler.schedulePeriodicRefresh(context, interval)
                        }
                    )
                }
                NavigationDestination.NEWS.route -> {
                    MarketNewsScreen(
                        viewModel = newsViewModel,
                        onNavigateToAnalysis = { symbol ->
                            technicalAnalysisViewModel.selectSymbol(symbol)
                            currentRoute = NavigationDestination.ANALYSIS.route
                        },
                        initialTab = "NEWS"
                    )
                }
                NavigationDestination.CALENDAR.route -> {
                    MarketNewsScreen(
                        viewModel = newsViewModel,
                        onNavigateToAnalysis = { symbol ->
                            technicalAnalysisViewModel.selectSymbol(symbol)
                            currentRoute = NavigationDestination.ANALYSIS.route
                        },
                        initialTab = "CALENDAR"
                    )
                }
                NavigationDestination.AI_TOOLS.route -> {
                    AiToolsScreen(onBack = { currentRoute = NavigationDestination.HOME.route })
                }
                NavigationDestination.CALCULATORS.route -> {
                    TradingCalculatorsScreen(onBack = { currentRoute = NavigationDestination.HOME.route })
                }
                NavigationDestination.LESSONS.route -> {
                    TradingLessonsScreen(onBack = { currentRoute = NavigationDestination.HOME.route })
                }
                NavigationDestination.SIGNAL_ENGINE.route -> {
                    ForexSignalEngineScreen(
                        viewModel = forexSignalEngineViewModel,
                        onBack = { currentRoute = NavigationDestination.HOME.route },
                        onOpenChart = { symbol ->
                            technicalAnalysisViewModel.selectSymbol(symbol)
                            currentRoute = NavigationDestination.ANALYSIS.route
                        }
                    )
                }
                NavigationDestination.FOREX_ACADEMY.route -> {
                    ForexAcademyScreen(
                        uiState = academyUiState,
                        onSelectLevel = { academyViewModel.setLevelFilter(it) },
                        onToggleCompletion = { academyViewModel.toggleLessonCompletion(it) },
                        onSwitchToCryptoAcademy = { currentRoute = NavigationDestination.CRYPTO_ACADEMY.route },
                        onBack = { currentRoute = NavigationDestination.HOME.route }
                    )
                }
                NavigationDestination.CRYPTO_ACADEMY.route -> {
                    CryptoAcademyScreen(
                        uiState = academyUiState,
                        onSelectLevel = { academyViewModel.setLevelFilter(it) },
                        onToggleCompletion = { academyViewModel.toggleLessonCompletion(it) },
                        onSwitchToForexAcademy = { currentRoute = NavigationDestination.FOREX_ACADEMY.route },
                        onBack = { currentRoute = NavigationDestination.HOME.route }
                    )
                }
                NavigationDestination.JOURNAL.route -> {
                    TradingJournalScreen(
                        onBack = { currentRoute = NavigationDestination.PROFILE.route }
                    )
                }
                NavigationDestination.PROFILE.route -> {
                    ProfileScreen(
                        academyUiState = academyUiState,
                        config = settingsConfig,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleTheme,
                        onRiskToleranceChange = { settingsViewModel.setRiskTolerance(it) },
                        onOfflineDemoToggle = { offline ->
                            settingsViewModel.setProviderMode(
                                if (offline) PermittedProviderMode.UNCONFIGURED else PermittedProviderMode.FREE_PUBLIC_RATES
                            )
                        },
                        onOpenForexAcademy = { currentRoute = NavigationDestination.FOREX_ACADEMY.route },
                        onOpenCryptoAcademy = { currentRoute = NavigationDestination.CRYPTO_ACADEMY.route },
                        onOpenSettings = { currentRoute = NavigationDestination.SETTINGS.route },
                        onOpenJournal = { currentRoute = NavigationDestination.JOURNAL.route },
                        onReplayOnboarding = { settingsViewModel.resetOnboarding() }
                    )
                }
            }
        }
                    }
                }
            }
        }
    }
}
