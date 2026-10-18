package app.kupe.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import app.kupe.di.AppContainer
import app.kupe.ui.home.HomeScreen
import app.kupe.ui.login.LoginScreen
import app.kupe.ui.login.LoginViewModel
import app.kupe.ui.register.CountryScreen
import app.kupe.ui.register.CountrySearchViewModel
import app.kupe.ui.register.CredentialsScreen
import app.kupe.ui.register.InterestsScreen
import app.kupe.ui.register.LanguageSearchViewModel
import app.kupe.ui.register.LanguagesScreen
import app.kupe.ui.register.ProfilePhotoScreen
import app.kupe.ui.register.RegistrationViewModel
import app.kupe.ui.register.TravelStyleScreen
import app.kupe.ui.register.UsernameScreen
import app.kupe.ui.welcome.WelcomeScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val HOME = "home"

    const val REGISTER = "register"
    const val REG_CREDENTIALS = "register/credentials"
    const val REG_USERNAME = "register/username"
    const val REG_TRAVEL_STYLE = "register/travel_style"
    const val REG_INTERESTS = "register/interests"
    const val REG_COUNTRY = "register/country"
    const val REG_LANGUAGES = "register/languages"
    const val REG_PHOTO = "register/photo"
}

@Composable
fun KupeApp(container: AppContainer) {
    val navController = rememberNavController()
    val auth = container.authRepository
    val scope = rememberCoroutineScope()

    var startDestination by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        startDestination = if (auth.hasSession.first()) Routes.HOME else Routes.WELCOME
    }

    val hasSession by auth.hasSession.collectAsStateWithLifecycle(initialValue = null)
    LaunchedEffect(hasSession) {
        if (hasSession == false && navController.currentDestination?.route == Routes.HOME) {
            navController.navigate(Routes.WELCOME) { popUpTo(navController.graph.id) { inclusive = true } }
        }
    }

    val start = startDestination
    if (start == null) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        return
    }

    val goHome: () -> Unit = {
        navController.navigate(Routes.HOME) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(navController = navController, startDestination = start, modifier = Modifier.fillMaxSize()) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onLogin = { navController.navigate(Routes.LOGIN) },
                onRegister = { navController.navigate(Routes.REGISTER) },
            )
        }
        composable(Routes.LOGIN) {
            val vm: LoginViewModel = viewModel(factory = LoginViewModel.factory(container))
            LoginScreen(
                viewModel = vm,
                googleProvider = container.googleIdTokenProvider,
                onBack = { navController.popBackStack() },
                onLoggedIn = goHome,
            )        }
        composable(Routes.HOME) {
            HomeScreen(onLogout = { scope.launch { auth.logout() } })
        }

        navigation(startDestination = Routes.REG_CREDENTIALS, route = Routes.REGISTER) {
            composable(Routes.REG_CREDENTIALS) { entry ->
                val vm = entry.registrationViewModel(navController, container)
                CredentialsScreen(
                    viewModel = vm,
                    googleProvider = container.googleIdTokenProvider,
                    onBack = { navController.popBackStack() },
                    onContinue = { navController.navigate(Routes.REG_USERNAME) },
                    onAuthenticated = goHome,
                )
            }
            composable(Routes.REG_USERNAME) { entry ->
                val vm = entry.registrationViewModel(navController, container)
                UsernameScreen(
                    viewModel = vm,
                    onBack = { vm.discardPendingRegistration(); navController.popBackStack() },
                    onNext = { navController.navigate(Routes.REG_TRAVEL_STYLE) },
                )
            }
            composable(Routes.REG_TRAVEL_STYLE) {
                TravelStyleScreen(
                    onBack = { navController.popBackStack() },
                    onSkip = { navController.navigate(Routes.REG_INTERESTS) },
                    onNext = { navController.navigate(Routes.REG_INTERESTS) },
                )
            }
            composable(Routes.REG_INTERESTS) {
                InterestsScreen(
                    onBack = { navController.popBackStack() },
                    onSkip = { navController.navigate(Routes.REG_COUNTRY) },
                    onNext = { navController.navigate(Routes.REG_COUNTRY) },
                )
            }
            composable(Routes.REG_COUNTRY) { entry ->
                val vm = entry.registrationViewModel(navController, container)
                val search: CountrySearchViewModel = viewModel(factory = CountrySearchViewModel.factory(container))
                CountryScreen(
                    viewModel = vm,
                    searchViewModel = search,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.REG_LANGUAGES) },
                )
            }
            composable(Routes.REG_LANGUAGES) { entry ->
                val vm = entry.registrationViewModel(navController, container)
                val search: LanguageSearchViewModel = viewModel(factory = LanguageSearchViewModel.factory(container))
                LanguagesScreen(
                    viewModel = vm,
                    searchViewModel = search,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.REG_PHOTO) },
                )
            }
            composable(Routes.REG_PHOTO) { entry ->
                val vm = entry.registrationViewModel(navController, container)
                ProfilePhotoScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onRestart = { navController.popBackStack(Routes.REG_CREDENTIALS, inclusive = false) },
                    onUsernameRejected = { navController.popBackStack(Routes.REG_USERNAME, inclusive = false) },
                    onCompleted = goHome,
                )
            }
        }
    }
}

@Composable
private fun NavBackStackEntry.registrationViewModel(navController: NavController, container: AppContainer): RegistrationViewModel {
    val graphEntry = remember(this) { navController.getBackStackEntry(Routes.REGISTER) }
    return viewModel(graphEntry, factory = RegistrationViewModel.factory(container))
}
