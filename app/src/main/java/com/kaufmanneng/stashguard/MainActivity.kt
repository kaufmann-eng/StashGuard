package com.kaufmanneng.stashguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaufmanneng.stashguard.domain.model.ScreenTheme
import com.kaufmanneng.stashguard.presentation.main.MainViewModel
import com.kaufmanneng.stashguard.presentation.navigation.ProductCategoryManagement
import com.kaufmanneng.stashguard.presentation.navigation.ProductForm
import com.kaufmanneng.stashguard.presentation.navigation.ProductList
import com.kaufmanneng.stashguard.presentation.navigation.Settings
import com.kaufmanneng.stashguard.presentation.productcategorymanagement.ProductCategoryManagementScreen
import com.kaufmanneng.stashguard.presentation.productcategorymanagement.ProductCategoryManagementViewModel
import com.kaufmanneng.stashguard.presentation.productform.ProductFormNavigationEvent
import com.kaufmanneng.stashguard.presentation.productform.ProductFormScreen
import com.kaufmanneng.stashguard.presentation.productform.ProductFormViewModel
import com.kaufmanneng.stashguard.presentation.productlist.ProductListNavigationEvent
import com.kaufmanneng.stashguard.presentation.productlist.ProductListScreen
import com.kaufmanneng.stashguard.presentation.productlist.ProductListViewModel
import com.kaufmanneng.stashguard.presentation.settings.SettingsScreen
import com.kaufmanneng.stashguard.presentation.settings.SettingsViewModel
import com.kaufmanneng.stashguard.ui.theme.StashGuardTheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinContext {
                val mainViewModel = koinViewModel<MainViewModel>()
                val state by mainViewModel.state.collectAsStateWithLifecycle()
                StashGuardTheme(
                    darkTheme = when (state.settings.theme) {
                        ScreenTheme.LIGHT -> false
                        ScreenTheme.DARK -> true
                        ScreenTheme.SYSTEM -> isSystemInDarkTheme()
                    },
                    dynamicColor = state.settings.useDynamicColor
                ) {
                    if (state.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val navController = rememberNavController()

                            NavHost(
                                navController = navController,
                                startDestination = ProductList
                            ) {
                                composable<ProductList> {
                                    val viewModel = koinViewModel<ProductListViewModel>()
                                    LaunchedEffect(Unit) {
                                        viewModel.navigationEvent.collect { event ->
                                            when (event) {
                                                is ProductListNavigationEvent.OnNavigateToProductForm -> {
                                                    navController.navigate(ProductForm(event.productId?.toString()))
                                                }
                                                is ProductListNavigationEvent.OnNavigateToCategoryManagement -> {
                                                    navController.navigate(ProductCategoryManagement)
                                                }
                                                is ProductListNavigationEvent.OnNavigateToSettings -> {
                                                    navController.navigate(Settings)
                                                }
                                            }
                                        }
                                    }
                                    val state by viewModel.state.collectAsStateWithLifecycle()
                                    ProductListScreen(
                                        state = state,
                                        event = viewModel.screenEvent,
                                        onAction = viewModel::onAction
                                    )
                                }
                                composable<ProductForm> {
                                    val viewModel = koinViewModel<ProductFormViewModel>()
                                    LaunchedEffect(Unit) {
                                        viewModel.navigationEvent.collect { event ->
                                            when (event) {
                                                ProductFormNavigationEvent.OnNavigateBack -> {
                                                    navController.navigateUp()
                                                }
                                            }
                                        }
                                    }
                                    val state by viewModel.state.collectAsStateWithLifecycle()
                                    ProductFormScreen(
                                        state = state,
                                        event = viewModel.screenEvent,
                                        onAction = viewModel::onAction,
                                        onNavigateBack = { navController.navigateUp() }
                                    )
                                }
                                composable<ProductCategoryManagement> {
                                    val viewModel = koinViewModel<ProductCategoryManagementViewModel>()
                                    val state by viewModel.state.collectAsStateWithLifecycle()

                                    ProductCategoryManagementScreen(
                                        state = state,
                                        onAction = viewModel::onAction,
                                        onNavigateBack = { navController.navigateUp() }
                                    )
                                }
                                composable<Settings> {
                                    val viewModel = koinViewModel<SettingsViewModel>()
                                    val state by viewModel.state.collectAsStateWithLifecycle()
                                    SettingsScreen(
                                        state = state,
                                        onAction = viewModel::onAction,
                                        onNavigateBack = { navController.navigateUp() }
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