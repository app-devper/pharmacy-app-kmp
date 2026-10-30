package app.devper.pharm.common.platform

interface LaunchTicket {
    val isPending: Boolean

    fun peek(): String?

    fun consume()

    object None : LaunchTicket {
        override val isPending: Boolean = false
        override fun peek(): String? = null
        override fun consume() = Unit
    }
}
