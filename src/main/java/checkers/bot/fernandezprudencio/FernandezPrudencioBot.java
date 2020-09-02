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
            //s =getEfficientNodeBoards (s);
            //BFS
            //q.add(getMinMaxNode(s));
            if (!s.isEmpty())
                q.addAll(heuristicasimplonaList(s, heuristicasimplona(s)));
              //  q.add(heuristicasimplona(s));
            //s.forEach(ss -> ss.board.printBoard());
            if(q.isEmpty())
                return n.getMoveDone();
        } while (q.getFirst().depth < 6);
        //return getBestRandomNodeBoard(q,q.removeFirst()).getMoveDone();
        return heuristicasimplona(q).getMoveDone();
    }

    private NodeBoard heuristicasimplona(LinkedList<NodeBoard> s) {
        NodeBoard bestNode = s.getFirst();
        for (NodeBoard n: s) {
            if (bestNode.initialPlayer == bestNode.board.getCurrentPlayer()) {
                if(n.accumulatedUtility > bestNode.accumulatedUtility) {
                    bestNode = n;
                }
            } else {
                if (n.accumulatedUtility < bestNode.accumulatedUtility) {
                    bestNode = n;
                }
            }

        }
        return bestNode;
    }

    private LinkedList<NodeBoard> heuristicasimplonaList(LinkedList<NodeBoard> s, NodeBoard bestNode) {
        LinkedList<NodeBoard> bestNodes = new LinkedList<>();
        for (NodeBoard n: s) {
            if (bestNode.accumulatedUtility == n.accumulatedUtility) {
                bestNodes.add(n);
            }
        }
        return bestNodes;
    }

    private NodeBoard getMinMaxNode(LinkedList<NodeBoard> s) {
        NodeBoard bestNode = s.removeFirst();
        //bestNode.accumulatedUtility = heuristicCountPiecesV2(bestNode);
            for (NodeBoard n: s) {
                //if (bestNode.initialPlayer == bestNode.board.getCurrentPlayer()) {
                    if(bestNode.accumulatedUtility > n.accumulatedUtility) {
                        bestNode = n;
                    }
                //} else {
                  //  if(bestNode.accumulatedUtility < n.accumulatedUtility) {
                    //    bestNode = n;
                   // }
                //}
            }
        return bestNode;
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
        s = ordenar(s);
        while (s.size() > 1) {
            if (isEnemyTurnInFutureBoard(s)) {
                s.removeLast(); //Will drop best options for enemy
            } else {
                s.removeFirst(); //Will drop worst options for me
            }
        }
        return s;
    }
    private boolean isEnemyTurnInFutureBoard(LinkedList<NodeBoard> s) {
        return s.getFirst().initialPlayer == s.getFirst().board.otherPlayer();
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
    private boolean heuristicCountPieces(NodeBoard board) {
        CheckersBoard.Player otherplayer = amIblack(board)
                ? CheckersBoard.Player.RED
                : CheckersBoard.Player.BLACK;
        if(board.board.countPiecesOfPlayer(board.initialPlayer)>board.board.countPiecesOfPlayer(otherplayer))
            return true;
        return false;
    }
    private int heuristicCountPiecesV2(NodeBoard board) {
        CheckersBoard.Player otherplayer = amIblack(board)
                ? CheckersBoard.Player.RED
                : CheckersBoard.Player.BLACK;
        return  board.board.countPiecesOfPlayer(board.initialPlayer)-board.board.countPiecesOfPlayer(otherplayer);
    }
    private boolean amIblack(NodeBoard board) {
        return board.initialPlayer == CheckersBoard.Player.BLACK;
    }
}

//if (!s.isEmpty()) //We eliminate non efficient uitlite successors
//s = getEfficientNodeBoards(s);