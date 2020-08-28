package checkers.bot.fernandezprudencio;

import checkers.CheckersBoard;
import checkers.CheckersMove;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FernandezPrudencioBot {

    //@Override
    public CheckersMove play(CheckersBoard board) {
        List<CheckersMove> possibleCaptures = board.possibleCaptures();
        if (possibleCaptures.isEmpty()) {
            List<CheckersMove> possibleMoves = board.possibleMoves();
            return possibleMoves.get(ThreadLocalRandom.current().nextInt(possibleMoves.size()));
        }
        return possibleCaptures.get(ThreadLocalRandom.current().nextInt(possibleCaptures.size()));
    }
}
