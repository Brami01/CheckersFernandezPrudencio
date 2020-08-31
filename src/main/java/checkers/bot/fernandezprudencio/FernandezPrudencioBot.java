package checkers.bot.fernandezprudencio;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import java.util.LinkedList;
import java.util.List;

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
            s = ordenar(s);
            if (!s.isEmpty()) {
                if (s.getFirst().initialPlayer == s.getFirst().board.otherPlayer()) { //enemy
                    for (int i = 0; i<(Math.floor(s.size()*0.5)); i++) {
                        s.removeLast();
                    }
                }
            }
            //BFS
            q.addAll(s);
            if(q.isEmpty()){
                return n.getMoveDone();
            }

        } while (!q.isEmpty() && q.getFirst().depth < 6);
        NodeBoard bestNodeBoard =q.removeFirst();
        int highestUtility = bestNodeBoard.accumulatedUtility;
        for (NodeBoard possibleSelected: q ) {
            if(possibleSelected.accumulatedUtility>highestUtility){
                bestNodeBoard = possibleSelected;
                highestUtility = possibleSelected.accumulatedUtility;
            }
        }
        return bestNodeBoard.getMoveDone();
    }

    public List<NodeBoard> successors (NodeBoard nodeBoard) throws BadMoveException {
        List<CheckersMove> possiblePlays = nodeBoard.board.possibleCaptures();
        if(possiblePlays.isEmpty())
            possiblePlays = nodeBoard.board.possibleMoves();
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
<<<<<<< HEAD
    }
}
=======


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
*/
