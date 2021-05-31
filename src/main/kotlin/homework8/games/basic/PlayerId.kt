package homework8.games.basic

enum class PlayerId {
    PLAYER_1, PLAYER_2;

    fun other() = when (this) {
        PLAYER_1 -> PLAYER_2
        PLAYER_2 -> PLAYER_1
    }
}
