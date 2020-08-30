package checkers.bot.fernandezprudencio;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;
import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FernandezPrudencioBot implements CheckersPlayer {
    @Override
    public CheckersMove play(CheckersBoard board) {
        try {
            return checkingAllMoves(board);
        } catch (BadMoveException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Something went wrong");
    }
    public CheckersMove checkingAllMoves(CheckersBoard board) throws BadMoveException {
        NodeBoard initialBoard = new NodeBoard(board);
        LinkedList<NodeBoard> q= new LinkedList<>();
        q.add(initialBoard);
        do {
            NodeBoard n = q.removeFirst();

            //n.printBoard();
            LinkedList<NodeBoard> s = new LinkedList<NodeBoard>(successors(n));
            //DFS
            //q.addAll(0,s);
            //BFS
            q.addAll(s);
            if(q.isEmpty()){
                return n.getMoveDone();
            }
        } while (!q.isEmpty() && q.getFirst().depth < 6);
        //if(!q.isEmpty()){
        NodeBoard bestNodeBoard =q.removeFirst();
        int highestUtility = Integer.MIN_VALUE;
        int lowestUtility = Integer.MAX_VALUE;
        for (NodeBoard possibleSelected: q ) {
            if (board.otherPlayer() == CheckersBoard.Player.RED) {
                if(possibleSelected.accumulatedUtility>highestUtility){
                    bestNodeBoard = possibleSelected;
                    highestUtility = possibleSelected.accumulatedUtility;
                }
            } else {
                if(possibleSelected.accumulatedUtility<lowestUtility){
                    bestNodeBoard = possibleSelected;
                    lowestUtility = possibleSelected.accumulatedUtility;
                }
            }
        }
        return bestNodeBoard.getMoveDone();
       //}
        //throw new IllegalArgumentException("No moves left, you already won/lost");
    }

    public List<NodeBoard> successors (NodeBoard nodeBoard) throws BadMoveException {
        List<CheckersMove> possiblePlays = nodeBoard.board.possibleCaptures();
        if(possiblePlays.isEmpty())
            possiblePlays = nodeBoard.board.possibleMoves();
        List<NodeBoard> possibleFutureBoards = new LinkedList<>();
        for (CheckersMove possiblePlay: possiblePlays) {
            NodeBoard possibleStateofBoard = new NodeBoard(nodeBoard,possiblePlay);
            possibleFutureBoards.add(possibleStateofBoard);

        for (NodeBoard n: possibleFutureBoards) {
        }
        return possibleFutureBoards;
    }

}


/*
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
        return null;
    }
*/

/*
    private List<CheckersMove> successors (CheckersBoard board) {

        List<CheckersBoard> boards = new ArrayList<>();
        List<CheckersMove> possibleCaptures = board.possibleCaptures();
        List<CheckersMove> possibleMoves = board.possibleMoves();

        if(possibleCaptures.isEmpty())

            return  possibleMoves;
        else
            return possibleCaptures;
    }
*/

/*
    private CheckersMove searchMax(List<CheckersMove> children, CheckersBoard board) {
        return null;
    }

    private CheckersMove searchMin(List<CheckersMove> children, CheckersBoard board) {
        return null;
    }
*/



/*
        List<CheckersMove> possibleCaptures = board.possibleCaptures();
        if (possibleCaptures.isEmpty()) {
            List<CheckersMove> possibleMoves = board.possibleMoves();
            return possibleMoves.get(ThreadLocalRandom.current().nextInt(possibleMoves.size()));
        }
        return possibleCaptures.get(ThreadLocalRandom.current().nextInt(possibleCaptures.size()));
*/









