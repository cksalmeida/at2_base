package com.fatec.at2_base

import com.fatec.at2_base.model.NewTaskRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
    }
    install(CORS) {
        anyHost() // libera o acesso a partir do app (uso de desenvolvimento)
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (cause.message ?: "Erro inesperado")),
            )
        }
    }

    routing {
        get("/") {
            call.respondText("API Lista de Tarefas no ar. Use GET/POST em /tasks")
        }

        // GET: retorna a lista de tarefas em JSON.
        get("/tasks") {
            call.respond(TaskRepository.all())
        }

        // POST: cadastra uma nova tarefa.
        post("/tasks") {
            val request = call.receive<NewTaskRequest>()
            if (request.title.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "O título é obrigatório"),
                )
                return@post
            }
            val created = TaskRepository.add(request)
            call.respond(HttpStatusCode.Created, created)
        }
    }
}