package checkers.bot.fernandezprudencio;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;
import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FernandezPrudencioBot implements CheckersPlayer{
    @Override
    public CheckersMove play(CheckersBoard board) {
        List<CheckersMove> children = successors(board);
        CheckersMove bestAction = searchBestAction(children,board);
        //CheckersMove.builder().fromPosition(i,j).toPosition(i+2, j+2).build()

        return null;
    }

    private CheckersMove searchBestAction(List<CheckersMove> children, CheckersBoard board) throws BadMoveException {
        CheckersMove min = searchMin(children,board);
        CheckersMove max = searchMax(children,board);
        // Reward and move selected
        HashMap <Integer, CheckersMove> possibleMin = new HashMap<>();
        HashMap <Integer, CheckersMove> possibleMax = new HashMap<>();

        List<CheckersBoard> boards = new ArrayList<>();

        for (CheckersMove move: children) {
           boards.add(board.clone());
           boards.get(boards.size()-1).processMove(move);
        }
        for(int i=0; i<boards.size();i++ ) {
            boards.get(i).
            possibleMax.put();


        }
        return null;
    }

    private CheckersBoard clone (CheckersBoard board) {
        CheckersBoard newboard = new CheckersBoard();
        //private char[][] board;
        //private CheckersBoard.Player currentPlayer;

        return newboard;
    }


    private CheckersMove searchMax(List<CheckersMove> children, CheckersBoard board) {



        return null;
    }

    private CheckersMove searchMin(List<CheckersMove> children, CheckersBoard board) {
        return null;
    }


    private List<CheckersMove> successors (CheckersBoard board) {

        List<CheckersBoard> boards = new ArrayList<>();
        List<CheckersMove> possibleCaptures = board.possibleCaptures();
        List<CheckersMove> possibleMoves = board.possibleMoves();

        if(possibleCaptures.isEmpty())

            return  possibleMoves;
        else
            return possibleCaptures;
    }

}

/*
        List<CheckersMove> possibleCaptures = board.possibleCaptures();
        if (possibleCaptures.isEmpty()) {
            List<CheckersMove> possibleMoves = board.possibleMoves();
            return possibleMoves.get(ThreadLocalRandom.current().nextInt(possibleMoves.size()));
        }
        return possibleCaptures.get(ThreadLocalRandom.current().nextInt(possibleCaptures.size()));
*/









