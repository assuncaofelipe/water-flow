package home.felipe.water.pocket.analysis.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import home.felipe.water.pocket.analysis.home.HomeScreen
import home.felipe.water.pocket.analysis.results.ResultsScreen
import home.felipe.water.pocket.analysis.results.ResultsViewModel
import home.felipe.water.pocket.analysis.shared.SharedRecordsViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val sharedRecordsViewModel: SharedRecordsViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = NavDestinations.HOME,
        modifier = modifier
    ) {
        composable(NavDestinations.HOME) {
            HomeScreen(
                sharedRecordsViewModel = sharedRecordsViewModel,
                onNavigateToResults = {target ->
                    navController.navigate(NavDestinations.resultsRoute(target))
                }
            )
        }

        composable(NavDestinations.RESULTS) { backStackEntry ->
            // VM específico da Results
            val resultsViewModel: ResultsViewModel = hiltViewModel(backStackEntry)

            val target: String = backStackEntry.arguments?.getString("target") ?: "DO"
            ResultsScreen(
                target = target,
                sharedRecordsViewModel = sharedRecordsViewModel,
                resultsViewModel = resultsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}