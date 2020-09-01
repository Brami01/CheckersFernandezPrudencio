package checkers.bot.fernandezprudencio;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
        LinkedList<NodeBoard> q = new LinkedList<>();
        q.add(initialBoard);
        do {
            NodeBoard n = q.removeFirst();
            LinkedList<NodeBoard> s = new LinkedList<NodeBoard>(successors(n));

            //BFS
            q.addAll(s);
            if(q.isEmpty())
                return n.getMoveDone();
        } while (q.getFirst().depth < 4);
        /*
        if(q.getFirst().initialPlayer == CheckersBoard.Player.RED) {
            return getBestRandomNodeBoard(q,q.removeFirst()).getMoveDone();
        } else {
            return getBestNodeBoard(q,q.removeFirst()).getMoveDone();
        }
        */
        return getBestRandomNodeBoard(q,q.removeFirst()).getMoveDone();
    }
    private NodeBoard getBestNodeBoard(LinkedList<NodeBoard> q, NodeBoard bestNodeBoard) {
        int highestUtility = bestNodeBoard.accumulatedUtility;
        for (NodeBoard possibleSelected: q) {
            if(possibleSelected.accumulatedUtility>highestUtility){
                bestNodeBoard = possibleSelected;
                highestUtility = possibleSelected.accumulatedUtility;
            }
        }
        return bestNodeBoard;
    }
    private NodeBoard getBestRandomNodeBoard(LinkedList<NodeBoard> q, NodeBoard bestNodeBoard) {
        int highestUtility = bestNodeBoard.accumulatedUtility;
        for (NodeBoard possibleSelected: q) { //Gets highest utility
            if(possibleSelected.accumulatedUtility>highestUtility){
                highestUtility = possibleSelected.accumulatedUtility;
            }
        }
        LinkedList<NodeBoard> bestNodes = new LinkedList<>();
        for (NodeBoard possibleSelected: q) {
            if(highestUtility == possibleSelected.accumulatedUtility){
                bestNodes.add(possibleSelected);
            }
        }
        if(bestNodeBoard.accumulatedUtility ==highestUtility) {
            return bestNodeBoard;
        }
        if(bestNodes.size() == 1){ //Only one
            return bestNodes.getFirst();
        }
        else {// If there are more possibilities with max utility
            return bestNodes.get(ThreadLocalRandom.current().nextInt(bestNodes.size()));
        }
    }

    private LinkedList<NodeBoard> getEfficientNodeBoards(LinkedList<NodeBoard> s) {
        if (s.getFirst().initialPlayer == s.getFirst().board.otherPlayer()) { //enemy
            s = ordenar(s);
            for (int i = 0; i<(Math.floor(s.size()*0.5)); i++) {
                s.removeLast();
            }
        }
        return s;
    }
    public List<NodeBoard> successors (NodeBoard nodeBoard) throws BadMoveException {
        List<CheckersMove> possiblePlays = nodeBoard.board.possibleCaptures();
        if(possiblePlays.isEmpty())
            possiblePlays = nodeBoard.board.possibleMoves();
        return generateSuccessors(nodeBoard, possiblePlays);
    }
    private List<NodeBoard> generateSuccessors(NodeBoard nodeBoard, List<CheckersMove> possiblePlays) throws BadMoveException {
        List<NodeBoard> possibleFutureBoards = new LinkedList<>();
        for (CheckersMove possiblePlay: possiblePlays) {
            NodeBoard possibleStateofBoard = new NodeBoard(nodeBoard, possiblePlay);
            possibleFutureBoards.add(possibleStateofBoard);
        }
        return possibleFutureBoards;
    }
    public LinkedList<NodeBoard> ordenar(LinkedList<NodeBoard> nodeBoards) {
        LinkedList<NodeBoard> n = new LinkedList<>();
        while (!nodeBoards.isEmpty()) {
            int i = Integer.MAX_VALUE;
            int j = -1;
            for (NodeBoard a: nodeBoards) {
                if (a.accumulatedUtility < i) {
                    i = a.accumulatedUtility;
                }
                j++;
            }
            n.add(nodeBoards.remove(j));
        }
        return n;
    }


}

//if (!s.isEmpty()) //We eliminate non efficient uitlite successors
//s = getEfficientNodeBoards(s);