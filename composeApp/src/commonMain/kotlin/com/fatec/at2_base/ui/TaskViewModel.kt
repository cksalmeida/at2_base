package com.fatec.at2_base.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatec.at2_base.model.NewTaskRequest
import com.fatec.at2_base.model.Task
import com.fatec.at2_base.network.TaskApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Endereço do backend.
 * - `10.0.2.2` é o alias do emulador Android para o `localhost` da máquina.
 * - Em um dispositivo físico, troque pelo IP da máquina na rede (ex.: http://192.168.0.10:8080).
 */
const val DEFAULT_BASE_URL = "http://10.0.2.2:8080"

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
)

class TaskViewModel(
    private val api: TaskApi = TaskApi(DEFAULT_BASE_URL),
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    /** Consome o endpoint GET /tasks e atualiza a lista exibida. */
    fun loadTasks() {
        _uiState.update { it.copy(isLoading = true, message = null, isError = false) }
        viewModelScope.launch {
            try {
                val tasks = api.getTasks()
                _uiState.update { it.copy(tasks = tasks, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        message = "Falha ao carregar tarefas: ${e.message}",
                    )
                }
            }
        }
    }

    /** Consome o endpoint POST /tasks e, em caso de sucesso, recarrega a lista. */
    fun addTask(title: String, description: String, onSuccess: () -> Unit = {}) {
        if (title.isBlank()) {
            _uiState.update { it.copy(isError = true, message = "Informe um título para a tarefa") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, message = null, isError = false) }
        viewModelScope.launch {
            try {
                val created = api.addTask(NewTaskRequest(title, description))
                _uiState.update {
                    it.copy(
                        tasks = it.tasks + created,
                        isSubmitting = false,
                        isError = false,
                        message = "Tarefa \"${created.title}\" cadastrada com sucesso!",
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isError = true,
                        message = "Falha ao cadastrar: ${e.message}",
                    )
                }
            }
        }
    }

    fun consumeMessage() {
        _uiState.update { it.copy(message = null) }
    }
}