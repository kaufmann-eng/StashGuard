package com.kaufmanneng.stashguard.presentation.productform

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kaufmanneng.stashguard.domain.model.Product
import com.kaufmanneng.stashguard.domain.repository.ProductCategoryRepository
import com.kaufmanneng.stashguard.domain.repository.ProductRepository
import com.kaufmanneng.stashguard.presentation.navigation.ProductForm
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.util.UUID

class ProductFormViewModel(
    private val productRepository: ProductRepository,
    categoryRepository: ProductCategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _initialId: UUID? = savedStateHandle.toRoute<ProductForm>().productId?.let { stringId ->
        try {
            UUID.fromString(stringId)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Log.w("ProductFormViewModel", "Invalid product ID: $stringId: ${e.message}")
            null
        }
    }

    private val _categoriesFlow = categoryRepository.getAllCategories()

    private val _state = MutableStateFlow(ProductFormState())

    init {
        _initialId?.let {
            viewModelScope.launch {
                productRepository.getProductById(it)?.let { product ->
                    _state.update { currentState ->
                        currentState.copy(
                            name = product.name,
                            productCategory = product.productCategory,
                            quantity = product.quantity,
                            expirationDate = product.expirationDate,
                            isEditMode = true
                        )
                    }
                }
            }
        }
    }

    private val _navigationEvent = MutableSharedFlow<ProductFormNavigationEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val _screenEvent = MutableSharedFlow<ProductFormScreenEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val state = combine(
        flow = _state,
        flow2 = _categoriesFlow
    ) { formState, categories ->
        formState.copy(availableCategories = categories)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value
    )

    val navigationEvent = _navigationEvent.asSharedFlow()

    val screenEvent = _screenEvent.asSharedFlow()

    fun onAction(action: ProductFormAction) {
        when (action) {
            is ProductFormAction.OnNameChanged -> onNameChange(action.name)
            is ProductFormAction.OnDateSelected -> onDateSelected(action.date)
            ProductFormAction.OnIncrementQuantity -> {
                _state.update { it.copy(quantity = it.quantity + 1) }
            }
            ProductFormAction.OnDecrementQuantity -> {
                if (_state.value.quantity > 1) {
                    _state.update { it.copy(quantity = it.quantity - 1) }
                }
            }
            is ProductFormAction.OnSaveClicked ->  {
                viewModelScope.launch {
                    saveProduct { _navigationEvent.emit(ProductFormNavigationEvent.OnNavigateBack) }
                }
            }
            is ProductFormAction.OnBackNavigationClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(ProductFormNavigationEvent.OnNavigateBack)
                }
            }
            is ProductFormAction.OnCategorySelected -> {
                _state.update { it.copy(productCategory = action.productCategory) }
            }
        }
    }

    private fun onNameChange(newName: String) {
        _state.update { it.copy(name = newName) }
    }

    private fun onDateSelected(newDate: LocalDate) {
        _state.update { it.copy(expirationDate = newDate) }
    }

    private suspend fun saveProduct(onProductSaved: suspend () -> Unit) {
        val currentState = _state.value

        if (currentState.name.isBlank() || currentState.productCategory == null || currentState.expirationDate == null) {
            _screenEvent.emit(ProductFormScreenEvent.ErrorNotAllFieldsFilled)
            return
        }

        _state.update { it.copy(isSaving = true) }

        if (currentState.isEditMode) {
            val productToUpdate = Product(
                id = _initialId!!,
                name = currentState.name.trim(),
                productCategory = currentState.productCategory,
                expirationDate = currentState.expirationDate,
                quantity = currentState.quantity
            )
            productRepository.addOrUpdateProduct(productToUpdate)
        } else {
            val existingProduct = productRepository.findProductByDetails(
                name = currentState.name.trim(),
                productCategoryId = currentState.productCategory.id,
                expirationDate = currentState.expirationDate
            )

            if (existingProduct != null) {
                val updatedProduct = existingProduct.copy(
                    quantity = existingProduct.quantity + currentState.quantity
                )
                productRepository.addOrUpdateProduct(updatedProduct)
            } else {
                val newProduct = Product(
                    id = UUID.randomUUID(),
                    name = currentState.name.trim(),
                    productCategory = currentState.productCategory,
                    expirationDate = currentState.expirationDate,
                    quantity = currentState.quantity
                )
                productRepository.addOrUpdateProduct(newProduct)
            }
        }
        onProductSaved()
        _state.update { it.copy(isSaving = false) }
    }
}