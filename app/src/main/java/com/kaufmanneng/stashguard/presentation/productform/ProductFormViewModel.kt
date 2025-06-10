package com.kaufmanneng.stashguard.presentation.productform

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kaufmanneng.stashguard.domain.model.Product
import com.kaufmanneng.stashguard.domain.repository.ProductRepository
import com.kaufmanneng.stashguard.presentation.navigation.ProductForm
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.util.UUID

class ProductFormViewModel(
    private val productRepository: ProductRepository,
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

    private val _state = MutableStateFlow(ProductFormState())
    val state = _state.asStateFlow()

    init {
        _initialId?.let {
            viewModelScope.launch {
                productRepository.getProductById(it)?.let { product ->
                    _state.update { currentState ->
                        currentState.copy(
                            name = product.name,
                            category = product.category,
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

    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _screenEvent = MutableSharedFlow<ProductFormScreenEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val screenEvent = _screenEvent.asSharedFlow()

    fun onAction(action: ProductFormAction) {
        when (action) {
            is ProductFormAction.OnNameChanged -> onNameChange(action.name)
            is ProductFormAction.OnCategoryChanged -> onCategoryChange(action.category)
            is ProductFormAction.OnDateSelected -> onDateSelected(action.date)
            ProductFormAction.OnIncrementQuantity -> {
                _state.update { it.copy(quantity = it.quantity + 1) }
            }
            ProductFormAction.OnDecrementQuantity -> {
                if (state.value.quantity > 1) {
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
        }
    }

    private fun onNameChange(newName: String) {
        _state.update { it.copy(name = newName) }
    }

    private fun onCategoryChange(newCategory: String) {
        _state.update { it.copy(category = newCategory) }
    }

    private fun onDateSelected(newDate: LocalDate) {
        _state.update { it.copy(expirationDate = newDate) }
    }

    private suspend fun saveProduct(onProductSaved: suspend () -> Unit) {
        val currentState = _state.value

        if (currentState.name.isBlank() || currentState.category.isBlank() || currentState.expirationDate == null) {
            _screenEvent.emit(ProductFormScreenEvent.ErrorNotAllFieldsFilled)
            return
        }

        _state.update { it.copy(isSaving = true) }

        if (currentState.isEditMode) {
            val productToUpdate = Product(
                id = _initialId!!,
                name = currentState.name.trim(),
                category = currentState.category.trim().ifBlank { "General" },
                expirationDate = currentState.expirationDate,
                quantity = currentState.quantity
            )
            productRepository.addOrUpdateProduct(productToUpdate)
        } else {
            val existingProduct = productRepository.findProductByDetails(
                name = currentState.name.trim(),
                category = currentState.category.trim().ifBlank { "General" },
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
                    category = currentState.category.trim().ifBlank { "General" },
                    expirationDate = currentState.expirationDate,
                    quantity = currentState.quantity
                )
                productRepository.addOrUpdateProduct(newProduct)
            }
        }
        _state.update { it.copy(isSaving = false) }
        onProductSaved()
    }
}