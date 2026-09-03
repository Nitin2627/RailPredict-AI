package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.AiAssistantSheet
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.util.*
import com.example.viewmodel.RailPredictViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val titleKey: (AppStrings) -> String, val icon: ImageVector) {
    object Landing : Screen("landing", { it.overview }, Icons.Default.Home)
    object Dashboard : Screen("dashboard", { it.dashboard }, Icons.Default.Dashboard)
    object LiveTracking : Screen("tracking", { it.liveTrains }, Icons.Default.DirectionsTransit)
    object TrainDetails : Screen("train_details/{trainNumber}", { "Train Profile" }, Icons.Default.Info) {
        fun createRoute(trainNumber: String) = "train_details/$trainNumber"
    }
    object AiPrediction : Screen("ai_prediction", { it.aiEngine }, Icons.Default.Psychology)
    object PassengerView : Screen("passenger", { it.passenger }, Icons.Default.Person)
    object Alerts : Screen("alerts", { "Alerts" }, Icons.Default.Notifications)
    object DemoMode : Screen("demo_mode", { "AI Demo" }, Icons.Default.PlayCircle)
    object LanguageSelection : Screen("language_selection", { it.chooseLanguage }, Icons.Default.Translate)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val languageManager = remember { LanguageManager(context) }
            val currentLanguage by languageManager.languageFlow.collectAsState(initial = Language.ENGLISH)
            val isFirstLaunch by languageManager.isFirstLaunchFlow.collectAsState(initial = null)

            val strings = when (currentLanguage) {
                Language.HINDI -> HindiStrings
                Language.HINGLISH -> HinglishStrings
                else -> EnglishStrings
            }

            CompositionLocalProvider(LocalStrings provides strings) {
                MyApplicationTheme {
                    if (isFirstLaunch != null) {
                        RailPredictApp(
                            startDestination = if (isFirstLaunch == true) Screen.LanguageSelection.route else Screen.Landing.route,
                            languageManager = languageManager
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RailPredictApp(
    startDestination: String,
    languageManager: LanguageManager
) {
    val navController = rememberNavController()
    val viewModel: RailPredictViewModel = viewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val strings = stringResource()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isSimRunning by viewModel.isSimulationRunning.collectAsState()
    val alerts by viewModel.alerts.collectAsState()

    var showAiAssistantSheet by remember { mutableStateOf(false) }

    val bottomNavItems = listOf(
        Screen.Landing,
        Screen.Dashboard,
        Screen.LiveTracking,
        Screen.AiPrediction,
        Screen.PassengerView
    )

    val drawerItems = listOf(
        Screen.Landing,
        Screen.Dashboard,
        Screen.LiveTracking,
        Screen.AiPrediction,
        Screen.PassengerView,
        Screen.Alerts,
        Screen.DemoMode
    )

    val showBars = currentRoute != Screen.LanguageSelection.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBars,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = RailNavy800,
                drawerContentColor = RailTextPrimary,
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Drawer Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = RailNavy900,
                        border = BorderStroke(1.5.dp, RailTealAccent),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DirectionsTransit,
                                contentDescription = null,
                                tint = RailTealAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = strings.appName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            ),
                            color = RailTextPrimary
                        )
                        Text(
                            text = "IR Hackathon 26028",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = RailBlue400
                        )
                    }
                }

                Divider(color = RailNavy600, modifier = Modifier.padding(vertical = 8.dp))

                drawerItems.forEach { item ->
                    val isSelected = currentRoute == item.route

                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.titleKey(strings),
                                tint = if (isSelected) RailTealAccent else RailTextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = item.titleKey(strings),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) RailTealAccent else RailTextPrimary
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                popUpTo(Screen.Landing.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = RailNavy700,
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }
                
                // Language Settings in Drawer
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Language, contentDescription = null, tint = RailTextSecondary) },
                    label = { Text(strings.language, color = RailTextPrimary) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.LanguageSelection.route)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (showBars) {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = strings.appName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    ),
                                    color = RailTextPrimary
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = RailBlue500.copy(alpha = 0.2f),
                                    border = BorderStroke(0.5.dp, RailBlue400)
                                ) {
                                    Text(
                                        text = "IR-26028",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = RailBlue400,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = RailTextPrimary)
                            }
                        },
                        actions = {
                            IconButton(onClick = { showAiAssistantSheet = true }) {
                                Icon(Icons.Default.Psychology, contentDescription = "AI", tint = RailPurpleAI)
                            }
                            IconButton(onClick = { navController.navigate(Screen.DemoMode.route) }) {
                                Icon(Icons.Default.PlayCircle, contentDescription = "Demo", tint = RailTealAccent)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = RailNavy900)
                    )
                }
            },
            bottomBar = {
                if (showBars) {
                    NavigationBar(containerColor = RailNavy800) {
                        bottomNavItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = screen.titleKey(strings)) },
                                label = {
                                    Text(
                                        text = screen.titleKey(strings),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Landing.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = RailTealAccent,
                                    selectedTextColor = RailTealAccent,
                                    unselectedIconColor = RailTextSecondary,
                                    unselectedTextColor = RailTextSecondary,
                                    indicatorColor = RailNavy700
                                )
                            )
                        }
                    }
                }
            },
            containerColor = RailNavy900
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (showBars) innerPadding else PaddingValues(0.dp))
            ) {
                composable(Screen.LanguageSelection.route) {
                    LanguageSelectionScreen(
                        onLanguageSelected = { language ->
                            scope.launch {
                                languageManager.saveLanguage(language)
                                navController.navigate(Screen.Landing.route) {
                                    popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(Screen.Landing.route) {
                    LandingScreen(
                        viewModel = viewModel,
                        onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) },
                        onNavigateToTracking = { navController.navigate(Screen.LiveTracking.route) },
                        onNavigateToPassenger = { navController.navigate(Screen.PassengerView.route) },
                        onNavigateToDemo = { navController.navigate(Screen.DemoMode.route) }
                    )
                }

                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToTrainDetails = { trainNumber ->
                            navController.navigate(Screen.TrainDetails.createRoute(trainNumber))
                        },
                        onNavigateToAiPrediction = { navController.navigate(Screen.AiPrediction.route) },
                        onNavigateToTracking = { navController.navigate(Screen.LiveTracking.route) }
                    )
                }

                composable(Screen.LiveTracking.route) {
                    LiveTrackingScreen(
                        viewModel = viewModel,
                        onNavigateToTrainDetails = { trainNumber ->
                            navController.navigate(Screen.TrainDetails.createRoute(trainNumber))
                        },
                        onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) }
                    )
                }

                composable(
                    route = Screen.TrainDetails.route,
                    arguments = listOf(navArgument("trainNumber") { type = NavType.StringType })
                ) { backStackEntry ->
                    val trainNumber = backStackEntry.arguments?.getString("trainNumber") ?: "12345"
                    TrainDetailsScreen(
                        viewModel = viewModel,
                        trainNumber = trainNumber,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.AiPrediction.route) {
                    AiPredictionScreen(viewModel = viewModel)
                }

                composable(Screen.PassengerView.route) {
                    PassengerViewScreen(viewModel = viewModel)
                }

                composable(Screen.Alerts.route) {
                    AlertsScreen(viewModel = viewModel)
                }

                composable(Screen.DemoMode.route) {
                    DemoModeScreen(viewModel = viewModel)
                }
            }
        }

        if (showAiAssistantSheet) {
            AiAssistantSheet(viewModel = viewModel, onDismiss = { showAiAssistantSheet = false })
        }
    }
}
