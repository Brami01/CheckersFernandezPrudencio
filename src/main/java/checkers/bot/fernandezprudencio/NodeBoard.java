package checkers.bot.fernandezprudencio;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.exception.BadMoveException;

/**
 * For each estimated
 * **/

public class NodeBoard {
    public CheckersBoard board;
    public CheckersMove moveDone;
    public int depth;
    public CheckersBoard.Player initialPlayer;
    public int accumulatedUtility;
    public NodeBoard parent=null;
    //Creates root NodeBoard
    public NodeBoard(CheckersBoard board) {
        this.board = board;
        this.initialPlayer = getInitialPlayer(board);
        this.depth = 0;
        this.accumulatedUtility = 0;
        this.moveDone = null;
        this.parent = null;
    }

    public static CheckersBoard.Player getInitialPlayer(CheckersBoard board) {
        return board.otherPlayer() == CheckersBoard.Player.BLACK
                ? CheckersBoard.Player.RED
                : CheckersBoard.Player.BLACK;
    }

    //Creates possible Future Moves
    public NodeBoard(NodeBoard parent, CheckersMove move) throws BadMoveException {
        this.depth = parent.depth + 1;
        this.accumulatedUtility = parent.accumulatedUtility;
        this.initialPlayer = parent.initialPlayer;
        this.moveDone = move;
        this.parent = parent;
        this.board = createBoardWithAssignedMove(parent.board,move);
    }

    private CheckersBoard createBoardWithAssignedMove(CheckersBoard board, CheckersMove move) throws BadMoveException {
        CheckersBoard newBoard = board.clone();
        newBoard.processMove(move);
        if(isCaptureMove(move)){
            addCaptureUtility(board,move);
        }
        addCoronationUtility(board,move);
        //addWinningUtilyty(board,move);
        return newBoard;
    }

    private void addWinningUtilyty(CheckersBoard board, CheckersMove move) {

    }
    private void addCoronationUtility (CheckersBoard board, CheckersMove move) {
        if(move.getEndRow()==7 && board.getBoard()[move.getStartRow()][move.getStartCol()] == CheckersBoard.RED_PLAIN) {
            if (initialPlayer == CheckersBoard.Player.RED)
                accumulatedUtility += 2;
            else
                accumulatedUtility -= 2;
        }
        if(move.getEndRow()==0 && board.getBoard()[move.getStartRow()][move.getStartCol()] == CheckersBoard.BLACK_CROWNED) {
            if (initialPlayer == CheckersBoard.Player.BLACK)
                accumulatedUtility += 2;
            else
                accumulatedUtility -= 2;

        }
    }

    private void addCaptureUtility (CheckersBoard board, CheckersMove move){
        if (initialPlayer == CheckersBoard.Player.BLACK) {
            switch (getMiddlePiece(board,move)){
                case CheckersBoard.RED_CROWNED:
                    accumulatedUtility += 4;
                    break;
                case CheckersBoard.RED_PLAIN:
                    accumulatedUtility += 3;
                    break;
                case CheckersBoard.BLACK_CROWNED:
                    accumulatedUtility -= 4;
                    break;
                case CheckersBoard.BLACK_PLAIN:
                    accumulatedUtility -= 3;
                    break;
            }
            /*
            if(getMiddlePiece(board, move) == CheckersBoard.RED_CROWNED)
                accumulatedUtility += 4;
            else
                accumulatedUtility += 3;
            */
        } else {
            switch (getMiddlePiece(board,move)){
                case CheckersBoard.RED_CROWNED:
                    accumulatedUtility -= 4;
                    break;
                case CheckersBoard.RED_PLAIN:
                    accumulatedUtility -= 3;
                    break;
                case CheckersBoard.BLACK_CROWNED:
                    accumulatedUtility += 4;
                    break;
                case CheckersBoard.BLACK_PLAIN:
                    accumulatedUtility += 3;
                    break;
            }
            /*
            if(getMiddlePiece(board, move) == CheckersBoard.BLACK_CROWNED)
                accumulatedUtility -= 4;
            else
                accumulatedUtility -= 3;
            */
        }
    }

    private char getMiddlePiece(CheckersBoard board, CheckersMove move){
        return board.getBoard()[move.getMiddlePosition().getRow()][move.getMiddlePosition().getCol()];
    }

    private boolean isCaptureMove(CheckersMove move) {
        return Math.abs(move.getEndRow() - move.getStartRow()) == 2 && Math.abs(move.getEndCol() - move.getStartCol()) == 2;
    }

    public CheckersMove getMoveDone() {
        if (parent.parent == null) {
            return moveDone;
        }
        return parent.getMoveDone();
    }
}

        /*
        if(board.otherPlayer()== CheckersBoard.Player.BLACK){
            board.countPiecesOfPlayer(CheckersBoard.Player.BLACK);
            newBoard.countPiecesOfPlayer(CheckersBoard.Player.RED);
        } else {
            board.countPiecesOfPlayer(CheckersBoard.Player.RED);
            newBoard.countPiecesOfPlayer(CheckersBoard.Player.BLACK);
        }
        */