package com.fatec.at2_base.model

import kotlinx.serialization.Serializable

/**
 * Uma tarefa armazenada no backend.
 */
@Serializable
data class Task(
    val id: Int,
    val title: String,
    val description: String = "",
    val done: Boolean = false,
)

/**
 * Payload enviado pelo app no cadastro (POST /tasks). O `id` é gerado no servidor.
 */
@Serializable
data class NewTaskRequest(
    val title: String,
    val description: String = "",
)