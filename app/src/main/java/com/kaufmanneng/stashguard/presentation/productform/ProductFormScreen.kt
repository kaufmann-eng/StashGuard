package com.kaufmanneng.stashguard.presentation.productform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    state: ProductFormState,
    event: SharedFlow<ProductFormScreenEvent>,
    onAction: (ProductFormAction) -> Unit
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        event.collect { event ->
            when (event) {
                ProductFormScreenEvent.ErrorNotAllFieldsFilled -> {
                    snackbarHostState.showSnackbar("Please fill in all fields")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Produkt bearbeiten" else "Neues Produkt") },
                navigationIcon = {
                    IconButton(onClick = { onAction(ProductFormAction.OnBackNavigationClicked) }) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (!state.isSaving) {
                        onAction(ProductFormAction.OnSaveClicked)
                    }
                }
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator()
                } else {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = {
                    onAction(ProductFormAction.OnNameChanged(it))
                },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    platformImeOptions = null,
                    showKeyboardOnFocus = true,
                    hintLocales = null
                )
            )

            OutlinedTextField(
                value = state.category,
                onValueChange = {
                    onAction(ProductFormAction.OnCategoryChanged(it))
                },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    platformImeOptions = null,
                    showKeyboardOnFocus = true,
                    hintLocales = null
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus() // Hide the keyboard
                        showDatePicker = true
                    }
                )
            )

            Button(
                onClick = { showDatePicker = true },
                enabled = !state.isSaving
            ) {
                Text(
                    text = state.expirationDate?.let {
                        val javaLocalDate = LocalDate.of(it.year, it.month, it.dayOfMonth)
                        javaLocalDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                    } ?: "Select Date"
                )
            }

            QuantityStepper(
                quantity = state.quantity,
                onIncrement = { onAction(ProductFormAction.OnIncrementQuantity) },
                onDecrement = { onAction(ProductFormAction.OnDecrementQuantity) },
                enabled = !state.isSaving
            )
        }
    }
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.UTC).date
                            onAction(ProductFormAction.OnDateSelected(selectedDate))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun QuantityStepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onDecrement,
            enabled = quantity > 1 && enabled
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Menge verringern")
        }
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        OutlinedButton(
            onClick = onIncrement,
            enabled = enabled
        ) {
            Icon(Icons.Default.Add, contentDescription = "Menge erhöhen")
        }
    }
}