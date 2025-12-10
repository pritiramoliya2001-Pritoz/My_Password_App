package com.example.myapplication.ui.theme.home

import PasswordEntity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.password.PasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordManagerApp(viewModel: PasswordViewModel) {
    val passwords by viewModel.passwordList.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("SecurePass") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(passwords) { item ->
                PasswordCard(item, viewModel)
            }
        }

        if (showAddDialog) {
            AddPasswordDialog(
                onDismiss = { showAddDialog = false },
                onSave = { account, user, pass ->
                    viewModel.addPassword(account, user, pass)
                    showAddDialog = false
                },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun PasswordCard(item: PasswordEntity, viewModel: PasswordViewModel) {
    var isVisible by remember { mutableStateOf(false) }
    val decryptedPass = remember(item.encryptedPassword) { viewModel.decryptPassword(item.encryptedPassword) }

    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.accountType, style = MaterialTheme.typography.titleMedium)
                    Text(text = item.username, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                IconButton(onClick = { viewModel.deletePassword(item) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Password Field within Card
            OutlinedTextField(
                value = decryptedPass,
                onValueChange = {},
                readOnly = true,
                label = { Text("Password") },
                visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isVisible = !isVisible }) {
                        Icon(if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Toggle")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AddPasswordDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit, viewModel: PasswordViewModel) {
    var account by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Bonus: Strength Meter State
    val strength = viewModel.calculateStrength(password)
    val strengthColor = if(strength < 0.3f) Color.Red else if(strength < 0.7f) Color.Yellow else Color.Green

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = account, onValueChange = { account = it }, label = { Text("Account (e.g. Gmail)") })
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })

                // Bonus: Generate Button
                Button(onClick = { password = viewModel.generatePassword() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Generate Strong Password")
                }

                // Bonus: Strength Meter UI
                Text("Password Strength", style = MaterialTheme.typography.labelSmall)
                LinearProgressIndicator(
                    progress = strength,
                    color = strengthColor,
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (account.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                    onSave(account, username, password)
                }
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}