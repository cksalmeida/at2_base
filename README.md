# at2_base — Lista de Tarefas

Aplicação completa com **backend Ktor** e **app Android nativo (Jetpack Compose)**,
organizada como projeto Kotlin Multiplatform com três módulos:

| Módulo        | Descrição                                                                 |
|---------------|---------------------------------------------------------------------------|
| `server`      | Backend Ktor (Netty) com armazenamento **em memória** (`MutableList`).    |
| `composeApp`  | App Android em Jetpack Compose (tela de lista + formulário).              |
| `shared`      | Código compartilhado: modelo `Task` e cliente HTTP `TaskApi`.            |

## Backend (Ktor)

Servidor em `server/src/main/kotlin/com/fatec/at2_base/Application.kt`, porta **8080**.

Endpoints:

| Método | Rota      | Descrição                                              |
|--------|-----------|--------------------------------------------------------|
| GET    | `/tasks`  | Retorna a lista de tarefas em JSON.                    |
| POST   | `/tasks`  | Cadastra uma nova tarefa (`{ "title", "description" }`). Retorna `201`. Título vazio → `400`. |
| GET    | `/`       | Mensagem de status.                                    |

Dados ficam em memória (`TaskRepository`), reiniciam ao reiniciar o servidor.

### Rodar o servidor

```bash
./gradlew :server:run
```

### Testar manualmente

```bash
curl http://localhost:8080/tasks
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Comprar café","description":"mercado"}'
```

### Testes automatizados

```bash
./gradlew :server:test
```

## App Android (Jetpack Compose)

Código em `composeApp/` (UI compartilhada em `commonMain`):

- **Tela de lista** (`App.kt` → `TaskListScreen`): consome `GET /tasks` e exibe as tarefas
  em uma `LazyColumn`, com botão **Atualizar**.
- **Formulário** (`TaskForm`): consome `POST /tasks` para cadastrar uma nova tarefa.
- **Feedback visual**: indicador de carregamento, estado de envio no botão, e `Snackbar`
  com mensagem de sucesso/erro. A lista é atualizada automaticamente após o cadastro.

A lógica de estado fica no `TaskViewModel`, que usa o `TaskApi` do módulo `shared`.

### Endereço do backend

Definido em `composeApp/src/commonMain/kotlin/com/fatec/at2_base/ui/TaskViewModel.kt`:

```kotlin
const val DEFAULT_BASE_URL = "http://10.0.2.2:8080"
```

- `10.0.2.2` é o alias do **emulador Android** para o `localhost` da máquina.
- Em um **dispositivo físico**, troque pelo IP da máquina na rede (ex.: `http://192.168.0.10:8080`).

### Rodar o app

1. Inicie o backend: `./gradlew :server:run`
2. Abra o projeto no Android Studio e execute a configuração `composeApp` em um emulador,
   ou gere o APK de debug:

```bash
./gradlew :composeApp:assembleDebug
# APK em composeApp/build/outputs/apk/debug/composeApp-debug.apk
```