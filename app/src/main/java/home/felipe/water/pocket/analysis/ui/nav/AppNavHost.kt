package home.felipe.water.pocket.analysis.ui.nav

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import home.felipe.water.pocket.analysis.ui.routes.Route
import home.felipe.water.pocket.analysis.ui.screens.home.HomeScreen
import home.felipe.water.pocket.analysis.ui.screens.home.HomeViewModel
import home.felipe.water.pocket.analysis.ui.screens.preview.PreviewModel
import home.felipe.water.pocket.analysis.ui.screens.preview.PreviewScreen
import home.felipe.water.pocket.analysis.ui.screens.results.ResultsScreen
import home.felipe.water.pocket.analysis.ui.screens.results.ResultsViewModel
import timber.log.Timber

@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    MaterialTheme {
        NavHost(navController = nav, startDestination = Route.Home.path) {

            // HOME
            composable(Route.Home.path) {
                val vm: HomeViewModel = hiltViewModel()
                val state by vm.ui.collectAsStateWithLifecycle()

                HomeScreen(
                    state = state,
                    onImportCsvUris = { uris ->
                        vm.onImportCsvUris(uris) {
                            Timber.i("Navigate -> Results")
                            nav.navigate(Route.Result.path)
                        }
                    },
                    onOpenRecent = { rf ->
                        vm.onOpenRecent(rf) {
                            Timber.i("Open recent -> Results")
                            nav.navigate(Route.Result.path)
                        }
                    }
                )
            }

            // PREVIEW (mantém sid)
            composable(Route.Preview.withArg) { backStack ->
                val sid = backStack.arguments?.getString(Route.Preview.ARG_SID) ?: return@composable
                Timber.i("Preview route sid=%s", sid)

                val vm: PreviewModel = hiltViewModel()
                val state by vm.ui.collectAsStateWithLifecycle()

                LaunchedEffect(sid) { vm.load(sid) }

                PreviewScreen(
                    state = state,
                    onBack = { nav.popBackStack() },
                    onGenerateResults = {
                        Timber.i("Preview -> Results sid=%s", sid)
                        nav.navigate(Route.Result.path)
                    }
                )
            }

            // RESULTS (sem sid; VM inicia sozinho)
            composable(Route.Result.path) {
                Timber.i("Results route")

                val vm: ResultsViewModel = hiltViewModel()
                val state by vm.ui.collectAsStateWithLifecycle()

                ResultsScreen(
                    state = state,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}