package checkers.bot.fenandezprudencioversion2;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.exception.BadMoveException;

import java.util.LinkedList;
import java.util.List;

public class ChildBoard {
    public CheckersBoard board;
    public CheckersMove move;
    public int depth;
    public List<ChildBoard> childrenBoards;
    public int utility;
    public ChildBoard parent;
    //Creates root ChildBoard
    public ChildBoard(CheckersBoard board) {
        this.board = board;
        this.depth = 0;
        this.move = null;
        this.parent = null;
        this.childrenBoards = new LinkedList<>();
        this.utility = 0;
    }
    //Creates possible Future ChildBoards
    public ChildBoard(ChildBoard parent, CheckersMove move) throws BadMoveException {
        this.depth = parent.depth + 1;
        this.move = move;
        this.parent = parent;
        this.board = createBoardWithAssignedMove(parent.board, move);
    }

    public void successors(int maxdepth) throws BadMoveException {
        if(this.depth < maxdepth) {
            this.childrenBoards = generatesuccessors(this,maxdepth);
            if (this.childrenBoards.size() > 0) {
                for(ChildBoard children: this.childrenBoards){
                    children.successors(maxdepth);
                }
            }
        }
    }

    private List<ChildBoard> generatesuccessors(ChildBoard board, int finalDepth) throws BadMoveException {
        List<CheckersMove> possiblePlays = getPossiblePlays(board.board);
        List<ChildBoard> possibleFutureBoards = new LinkedList<>();
        for (CheckersMove possiblePlay: possiblePlays) {
            ChildBoard possibleStateofBoard = new ChildBoard(board, possiblePlay);
            if(possibleStateofBoard.depth == finalDepth) {
                possibleStateofBoard.utility = calculateUtility(possibleStateofBoard.board);
            }
            possibleFutureBoards.add(possibleStateofBoard);
        }
        return possibleFutureBoards;
    }

    private List<CheckersMove> getPossiblePlays(CheckersBoard board) {
        List<CheckersMove> possiblePlays = board.possibleCaptures();
        if (possiblePlays.isEmpty())
            return board.possibleMoves();
        return possiblePlays;
    }

    private CheckersBoard createBoardWithAssignedMove(CheckersBoard board, CheckersMove move) throws BadMoveException {
        CheckersBoard newBoard = board.clone();
        newBoard.processMove(move);
        return newBoard;
    }

    private int calculateUtility(CheckersBoard newBoard) {
        CheckersBoard.Player myPlayer = getInitialPlayer();
        /*if (newBoard.countPiecesOfPlayer(myPlayer) == 0) {
            return -100;
        }
        if (newBoard.countPiecesOfPlayer(myOpponent(myPlayer)) == 0) {
            return 100;
        }*//*
        if (newBoard.getCurrentPlayer() == myPlayer) {
            return newBoard.countPiecesOfPlayer(myPlayer) - newBoard.countPiecesOfPlayer(myOpponent(myPlayer)) + newBoard.possibleCaptures().size();
        } else {
            return newBoard.countPiecesOfPlayer(myPlayer) - newBoard.countPiecesOfPlayer(myOpponent(myPlayer)) - newBoard.possibleCaptures().size();
        }
        if (newBoard.getCurrentPlayer() == myPlayer) {
            return countPiecesOfPlayer(myPlayer, newBoard) - countPiecesOfPlayer(myOpponent(myPlayer), newBoard) + newBoard.possibleCaptures().size() * 2;
        } else {
            return countPiecesOfPlayer(myPlayer, newBoard) - countPiecesOfPlayer(myOpponent(myPlayer), newBoard) - newBoard.possibleCaptures().size() * 2;
        }*/
        return newBoard.countPiecesOfPlayer(myPlayer) - newBoard.countPiecesOfPlayer(myOpponent(myPlayer));
        //return countPiecesOfPlayer(myPlayer, newBoard) - countPiecesOfPlayer(myOpponent(myPlayer), newBoard);
    }

    private CheckersBoard.Player getInitialPlayer( ) {
        return parent == null ? board.getCurrentPlayer() : parent.getInitialPlayer();
    }

    private CheckersBoard.Player myOpponent(CheckersBoard.Player myPlayer) {
        return myPlayer == CheckersBoard.Player.RED ? CheckersBoard.Player.BLACK : CheckersBoard.Player.RED;
    }

    public CheckersMove getMoveDone() {
        if (parent.parent == null) {
            return move;
        }
        return parent.getMoveDone();
    }

    public int countPiecesOfPlayer(CheckersBoard.Player player, CheckersBoard board) {
        int numPieces = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((CheckersBoard.Player.RED.equals(player))) {
                    if ((CheckersBoard.Player.RED.equals(player) && board.getBoard()[i][j] == Character.toLowerCase('r'))) {
                        numPieces += 3;
                        if (i == 0 || i == 7 || j == 0 || j == 7) {
                            numPieces++;
                        }
                    }
                } else {
                    if ((CheckersBoard.Player.BLACK.equals(player) && board.getBoard()[i][j] == Character.toLowerCase('b'))) {
                        numPieces += 3;
                        if (i == 0 || i == 7 || j == 0 || j == 7) {
                            numPieces++;
                        }
                    }
                }
            }
        }
        return numPieces;
    }
}
