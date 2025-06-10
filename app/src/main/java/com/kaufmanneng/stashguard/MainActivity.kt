package com.kaufmanneng.stashguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaufmanneng.stashguard.presentation.navigation.ProductForm
import com.kaufmanneng.stashguard.presentation.navigation.ProductList
import com.kaufmanneng.stashguard.presentation.productform.ProductFormNavigationEvent
import com.kaufmanneng.stashguard.presentation.productform.ProductFormScreen
import com.kaufmanneng.stashguard.presentation.productform.ProductFormViewModel
import com.kaufmanneng.stashguard.presentation.productlist.ProductListNavigationEvent
import com.kaufmanneng.stashguard.presentation.productlist.ProductListScreen
import com.kaufmanneng.stashguard.presentation.productlist.ProductListViewModel
import com.kaufmanneng.stashguard.ui.theme.StashGuardTheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinContext {
                StashGuardTheme {
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
                                    onAction = viewModel::onAction
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}