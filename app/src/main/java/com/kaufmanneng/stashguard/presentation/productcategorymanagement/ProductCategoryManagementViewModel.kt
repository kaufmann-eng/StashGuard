package com.kaufmanneng.stashguard.presentation.productcategorymanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaufmanneng.stashguard.domain.repository.ProductCategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductCategoryManagementViewModel(
    private val categoryRepository: ProductCategoryRepository
) : ViewModel() {

    val state = categoryRepository.getAllCategories()
        .map { allCategories ->
            ProductCategoryManagementState(
                customCategories = allCategories.filter { !it.isDefault },
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProductCategoryManagementState()
        )

    fun onAction(action: ProductCategoryManagementAction) {
        when (action) {
            is ProductCategoryManagementAction.OnAddCategoryClicked -> {
                viewModelScope.launch {
                    categoryRepository.addCategory(action.name)
                }
            }
            is ProductCategoryManagementAction.OnUpdateCategoryClicked -> {
                viewModelScope.launch {
                    categoryRepository.updateCategory(action.category)
                }
            }
            is ProductCategoryManagementAction.OnDeleteCategoryClicked -> {
                viewModelScope.launch {
                    categoryRepository.deleteCategory(action.category)
                }
            }
        }
    }
}