package com.kaufmanneng.stashguard.presentation.productcategorymanagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaufmanneng.stashguard.R
import com.kaufmanneng.stashguard.domain.model.ProductCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCategoryManagementScreen(
    state: ProductCategoryManagementState,
    onAction: (ProductCategoryManagementAction) -> Unit,
    onNavigateBack: () -> Unit,
) {
    var categoryToEdit by remember { mutableStateOf<ProductCategory?>(null) }
    var categoryToDelete by remember { mutableStateOf<ProductCategory?>(null) }
    var isAddingNew by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { isAddingNew = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = state.customCategories,
                key = { it.id }
            ) { category ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = category.name, modifier = Modifier.weight(1f))
                    Row {
                        IconButton(onClick = { categoryToEdit = category }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Category")
                        }
                        IconButton(onClick = { categoryToDelete = category }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Category")
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
    if (isAddingNew) {
        AddEditCategoryDialog(
            onDismiss = { isAddingNew = false },
            onConfirm = { name ->
                onAction(ProductCategoryManagementAction.OnAddCategoryClicked(name))
                isAddingNew = false
            }
        )
    }

    categoryToEdit?.let { category ->
        AddEditCategoryDialog(
            onDismiss = { categoryToEdit = null },
            onConfirm = { newName ->
                onAction(ProductCategoryManagementAction.OnUpdateCategoryClicked(category.copy(name = newName)))
                categoryToEdit = null
            },
            initialValue = category.name
        )
    }

    categoryToDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this category ${category.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(ProductCategoryManagementAction.OnDeleteCategoryClicked(category))
                        categoryToDelete = null
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AddEditCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    initialValue: String = "",
) {
    var text by rememberSaveable { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialValue.isEmpty()) "Add Category" else "Edit Category") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Category Name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}