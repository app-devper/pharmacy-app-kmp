package app.devper.pharm.domain.command

import app.devper.pharm.domain.extension.newClientRequestId

enum class CommandKind { Sale, Return }

class CommandAttempts(private val newId: () -> String = ::newClientRequestId) {

    private data class Attempt(val request: Any, val clientRequestId: String)

    private val open = mutableMapOf<CommandKind, Attempt>()

    fun idFor(kind: CommandKind, request: Any): String {
        val reused = open[kind]?.takeIf { it.request == request }?.clientRequestId
        val id = reused ?: newId()
        open[kind] = Attempt(request, id)
        return id
    }

    fun settled(kind: CommandKind) {
        open.remove(kind)
    }
}
