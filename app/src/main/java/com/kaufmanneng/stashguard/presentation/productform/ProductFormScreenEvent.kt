package com.kaufmanneng.stashguard.presentation.productform

sealed interface ProductFormScreenEvent {
    data object ErrorNotAllFieldsFilled : ProductFormScreenEvent
}