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
                        vm.onImportCsvUris(uris) { sid ->
                            Timber.i("Navigate -> Result sid=$sid")
                            nav.navigate(Route.Result.build(sid))
                        }
                    },
                    onOpenRecent = { rf ->
                        vm.onOpenRecent(rf) { sid ->
                            Timber.i("Open recent -> Result sid=$sid")
                            nav.navigate(Route.Result.build(sid))
                        }
                    }
                )
            }

            // PREVIEW
            composable(Route.Preview.withArg) { backStack ->
                val sid = backStack.arguments?.getString(Route.Preview.ARG_SID) ?: return@composable
                Timber.i("Preview route sid=$sid")

                val vm: PreviewModel = hiltViewModel()
                val state by vm.ui.collectAsStateWithLifecycle()

                LaunchedEffect(sid) { vm.load(sid) }

                PreviewScreen(
                    state = state,
                    onBack = { nav.popBackStack() },
                    onGenerateResults = {
                        Timber.i("Preview -> Results sid=$sid")
                        nav.navigate(Route.Result.build(sid))
                    }
                )
            }

            // RESULTS
            composable(Route.Result.withArg) { backStack ->
                val sid = backStack.arguments?.getString(Route.Result.ARG_SID) ?: return@composable
                Timber.i("Results route sid=$sid")

                val vm: ResultsViewModel = hiltViewModel()
                val state by vm.ui.collectAsStateWithLifecycle()

                LaunchedEffect(sid) { vm.runAll(sid) }

                ResultsScreen(
                    state = state,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}
