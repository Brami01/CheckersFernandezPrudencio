package checkers.bot.fernandezprudencio;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.exception.BadMoveException;

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
        this.initialPlayer = board.getCurrentPlayer();
        this.depth = 0;
        this.accumulatedUtility = 0;
        this.moveDone = null;
        this.parent = null;
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
        return newBoard;
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
