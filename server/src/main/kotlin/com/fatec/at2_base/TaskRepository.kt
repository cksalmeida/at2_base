package com.fatec.at2_base

import com.fatec.at2_base.model.NewTaskRequest
import com.fatec.at2_base.model.Task
import java.util.concurrent.atomic.AtomicInteger

/**
 * Armazenamento em memória das tarefas (não há banco de dados).
 * As operações são sincronizadas para suportar requisições concorrentes.
 */
object TaskRepository {

    private val tasks = mutableListOf<Task>()
    private val idSequence = AtomicInteger(0)

    init {
        // Alguns dados de exemplo para o app já abrir com conteúdo.
        add(NewTaskRequest("Estudar Ktor", "Criar endpoints GET e POST"))
        add(NewTaskRequest("Estudar Jetpack Compose", "Montar a tela de lista e o formulário"))
    }

    fun all(): List<Task> = synchronized(tasks) { tasks.toList() }

    fun add(request: NewTaskRequest): Task = synchronized(tasks) {
        val task = Task(
            id = idSequence.incrementAndGet(),
            title = request.title.trim(),
            description = request.description.trim(),
        )
        tasks.add(task)
        task
    }
}