package com.kaufmanneng.stashguard.presentation.productlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaufmanneng.stashguard.domain.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchQuery = savedStateHandle.getStateFlow(SEARCH_QUERY_KEY, "")
    private val _isSearchActive = savedStateHandle.getStateFlow(IS_SEARCH_ACTIVE_KEY, false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _productsFlow = _searchQuery.flatMapLatest { query ->
        productRepository.getProducts(query)
    }

    val state = combine(_productsFlow, _searchQuery, _isSearchActive) {
        products, query, isSearchActive ->

        val grouped = products.groupBy { it.productCategory }
            .toSortedMap(compareBy { it.name })

        ProductListState(
            groupedProducts = grouped,
            searchQuery = query,
            isSearchActive = isSearchActive,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductListState())



    private val _navigationEvent = MutableSharedFlow<ProductListNavigationEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _screenEvent = MutableSharedFlow<ProductListScreenEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val screenEvent = _screenEvent.asSharedFlow()

    fun onAction(action: ProductListAction) {
        when (action) {
            is ProductListAction.OnSearchQueryChanged -> {
                savedStateHandle[SEARCH_QUERY_KEY] = action.query
            }
            is ProductListAction.OnSearchActiveChanged -> {
                savedStateHandle[IS_SEARCH_ACTIVE_KEY] = action.isActive
            }
            is ProductListAction.OnAddProductClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(ProductListNavigationEvent.OnNavigateToProductForm(null))
                }
            }
            is ProductListAction.OnDeleteProduct -> {
                viewModelScope.launch {
                    productRepository.deleteProduct(action.product)
                    _screenEvent.emit(ProductListScreenEvent.ShowUndoDeleteSnackbar(action.product))
                }
            }
            is ProductListAction.OnUndoDeleteClicked -> {
                viewModelScope.launch {
                    productRepository.addOrUpdateProduct(action.product)
                }
            }
            is ProductListAction.OnProductClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(ProductListNavigationEvent.OnNavigateToProductForm(action.product.id))
                }
            }
            is ProductListAction.OnManageCategoriesClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(ProductListNavigationEvent.OnNavigateToCategoryManagement)
                }
            }
            is ProductListAction.OnSettingsClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(ProductListNavigationEvent.OnNavigateToSettings)
                }
            }
        }
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "searchQuery"
        private const val IS_SEARCH_ACTIVE_KEY = "isSearchActive"
    }
}