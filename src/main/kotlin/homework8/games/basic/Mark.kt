package homework8.games.basic

enum class Mark {
    CROSS, NOUGHT;

    fun other() = when (this) {
        CROSS -> NOUGHT
        NOUGHT -> CROSS
    }
}
