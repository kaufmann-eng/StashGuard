package com.kaufmanneng.stashguard.presentation.productlist

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.kaufmanneng.stashguard.domain.model.Product
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    state: ProductListState,
    event: SharedFlow<ProductListScreenEvent>,
    onAction: (ProductListAction) -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        var showRationaleDialog by rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (!isGranted) {
                    showRationaleDialog = true
                }
            }
        )

        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (showRationaleDialog) {
            AlertDialog(
                onDismissRequest = { showRationaleDialog = false },
                title = { Text("Permission Required") },
                text = { Text("This app requires notifications permission to notify you about expiring products.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showRationaleDialog = false
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", context.packageName, null)
                            intent.data = uri
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Einstellungen öffnen")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRationaleDialog = false }) {
                        Text("Abbrechen")
                    }
                }
            )
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        event.collect { event ->
            when (event) {
                is ProductListScreenEvent.ShowUndoDeleteSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "Product ${event.product.name} deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onAction(ProductListAction.OnUndoDeleteClicked(event.product))
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Product List") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(ProductListAction.OnAddProductClicked) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Product")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            ProductSearchBar(
                state = state,
                onAction = onAction,
                modifier = Modifier.fillMaxWidth()
            )
            if (!state.isSearchActive){
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    ProductListContent(
                        products = state.products,
                        onAction = onAction,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductSearchBar(
    state: ProductListState,
    onAction: (ProductListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.searchQuery,
                    onQueryChange = { onAction(ProductListAction.OnSearchQueryChanged(it)) },
                    onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                    expanded = state.isSearchActive,
                    onExpandedChange = { onAction(ProductListAction.OnSearchActiveChanged(it)) },
                    placeholder = { Text("Search products...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onAction(ProductListAction.OnSearchQueryChanged("")) }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear Search")
                            }
                        }
                    }
                )
            },
            expanded = state.isSearchActive,
            onExpandedChange = { onAction(ProductListAction.OnSearchActiveChanged(it)) }
        ) {
            ProductListContent(
                products = state.products,
                onAction = onAction,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ProductListContent(
    products: List<Product>,
    onAction: (ProductListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = products,
            key = { product -> product.id }
        ) { product ->
            val swipeState = rememberSwipeToDismissBoxState(
                confirmValueChange = { swipeValue -> true }
            )
            LaunchedEffect(swipeState.currentValue) {
                if (swipeState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                    onAction(ProductListAction.OnDeleteProduct(product))
                    swipeState.snapTo(SwipeToDismissBoxValue.Settled)
                }
            }
            SwipeToDismissBox(
                modifier = Modifier.animateItem(),
                state = swipeState,
                backgroundContent = {
                    val color = when (swipeState.dismissDirection) {
                        SwipeToDismissBoxValue.EndToStart -> Color.Red
                        else -> Color.Transparent
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium)
                            .background(color)
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Product",
                            tint = Color.White
                        )
                    }
                },
                enableDismissFromStartToEnd = false
            ) {
                ProductItem(
                    modifier = Modifier.fillMaxWidth(),
                    product = product,
                    onClick = { onAction(ProductListAction.OnProductClicked(product)) }
                )
            }
        }
    }
}

@Composable
private fun ProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: () -> Unit
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysUntilExpiry = today.daysUntil(product.expirationDate)

    OutlinedCard(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Category: ${product.category}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Quantity: ${product.quantity}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Expires in: $daysUntilExpiry days", style = MaterialTheme.typography.bodyMedium)
        }
    }
}