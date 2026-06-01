package com.fatec.at2_base

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fatec.at2_base.model.Task
import com.fatec.at2_base.ui.TaskViewModel

@Composable
fun App() {
    MaterialTheme {
        val viewModel: TaskViewModel = viewModel { TaskViewModel() }
        TaskListScreen(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Feedback visual: exibe a mensagem (sucesso ou erro) em um Snackbar.
    LaunchedEffect(state.message) {
        val message = state.message
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Lista de Tarefas") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            TaskForm(
                isSubmitting = state.isSubmitting,
                onSubmit = { title, description -> viewModel.addTask(title, description) },
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Tarefas (${state.tasks.size})",
                    style = MaterialTheme.typography.titleMedium,
                )
                TextButton(onClick = { viewModel.loadTasks() }) {
                    Text("Atualizar")
                }
            }

            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.tasks.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("Nenhuma tarefa cadastrada ainda.")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.tasks, key = { it.id }) { task ->
                            TaskItem(task)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskForm(
    isSubmitting: Boolean,
    onSubmit: (title: String, description: String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nova tarefa", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição (opcional)") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    onSubmit(title, description)
                    title = ""
                    description = ""
                },
                enabled = !isSubmitting && title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp))
                } else {
                    Text("Cadastrar")
                }
            }
        }
    }
}

@Composable
private fun TaskItem(task: Task) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
            )
            if (task.description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}