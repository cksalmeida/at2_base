package com.fatec.at2_base

import com.fatec.at2_base.model.NewTaskRequest
import com.fatec.at2_base.model.Task
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun getTasksReturnsList() = testApplication {
        application { module() }
        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, response.status)

        val tasks: List<Task> = response.body()
        assertTrue(tasks.isNotEmpty(), "Deve haver tarefas de exemplo")
    }

    @Test
    fun postTaskCreatesTask() = testApplication {
        application { module() }
        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(NewTaskRequest("Nova tarefa", "Descrição"))
        }
        assertEquals(HttpStatusCode.Created, response.status)

        val created: Task = response.body()
        assertEquals("Nova tarefa", created.title)
        assertTrue(created.id > 0)
    }

    @Test
    fun postWithoutTitleReturnsBadRequest() = testApplication {
        application { module() }
        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(NewTaskRequest("", ""))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}