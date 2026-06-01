package com.fatec.at2_base.network

import com.fatec.at2_base.model.NewTaskRequest
import com.fatec.at2_base.model.Task
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Cliente HTTP que consome os endpoints do backend Ktor.
 *
 * O [baseUrl] é configurável: no emulador Android o host da máquina é acessível
 * via `10.0.2.2`; em um dispositivo físico use o IP da máquina na rede local.
 */
class TaskApi(
    private val baseUrl: String,
    private val client: HttpClient = defaultClient(),
) {

    /** GET /tasks — retorna a lista de tarefas cadastradas. */
    suspend fun getTasks(): List<Task> =
        client.get("$baseUrl/tasks").body()

    /** POST /tasks — cadastra uma nova tarefa e retorna a tarefa criada. */
    suspend fun addTask(request: NewTaskRequest): Task =
        client.post("$baseUrl/tasks") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    companion object {
        fun defaultClient(): HttpClient = HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
        }
    }
}