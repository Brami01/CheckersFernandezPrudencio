/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package checkers;

import checkers.bot.fernandezprudencio.FernandezPrudencioBot;
import checkers.bot.gray.GrayRandomBot;

import java.util.Optional;

public class App {

    public static void main(String[] args) {
        CheckersBoard game = CheckersBoard.initBoard();
        CheckersPlayer player1 = new KeyboardPlayer();
        CheckersPlayer player2 = new GrayRandomBot();
        CheckersPlayer player3 = new FernandezPrudencioBot();
        CheckersPlayer player4 = new FernandezPrudencioBot();
        Optional<CheckersPlayer> loser = game.play(player2, player3);
        loser.ifPresent(//
                checkersPlayer -> System.out.println("LOSER! " + checkersPlayer.getClass().getName()));
    }
}
