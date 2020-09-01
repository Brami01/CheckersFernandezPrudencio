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
            addCaptureUtility(board, move);
            addMultipleCaptureUtility(board, move);
        }
        addPossibleCoronationUtility(board,move);
        return newBoard;
    }

    private void addMultipleCaptureUtility(CheckersBoard board, CheckersMove move) {
        if (isCapturePossibleAtPosition(board.getCurrentPlayer(), move.getEndRow(), move.getEndCol(), board)) {
            addCaptureUtility(board, move);
        }
    }

    private void addPossibleCoronationUtility (CheckersBoard board, CheckersMove move) {
        if(redCoronationInCurrentMove(board, move)) {
            if (!iamblack())
                accumulatedUtility += 2;
            else
                accumulatedUtility -= 2;
        }
        if(blackCoronationInCurrentMove(board, move)) {
            if (iamblack())
                accumulatedUtility += 2;
            else
                accumulatedUtility -= 2;
        }
    }
    private boolean blackCoronationInCurrentMove(CheckersBoard board, CheckersMove move) {
        return move.getEndRow()==0 && board.getBoard()[move.getStartRow()][move.getStartCol()] == CheckersBoard.BLACK_PLAIN;
    }
    private boolean redCoronationInCurrentMove(CheckersBoard board, CheckersMove move) {
        return move.getEndRow()==7 && board.getBoard()[move.getStartRow()][move.getStartCol()] == CheckersBoard.RED_PLAIN;
    }
    private void addCaptureUtility (CheckersBoard board, CheckersMove move) {
        if (iamblack()) {
            switch (getMiddlePiece(board, move)) {
                case CheckersBoard.RED_CROWNED:
                    accumulatedUtility += 5;
                    break;
                case CheckersBoard.RED_PLAIN:
                    accumulatedUtility += 4;
                    break;
                case CheckersBoard.BLACK_CROWNED:
                    accumulatedUtility -= 4;
                    break;
                case CheckersBoard.BLACK_PLAIN:
                    accumulatedUtility -= 3;
                    break;
            }
        } else {
            switch (getMiddlePiece(board, move)) {
                case CheckersBoard.RED_CROWNED:
                    accumulatedUtility -= 4;
                    break;
                case CheckersBoard.RED_PLAIN:
                    accumulatedUtility -= 3;
                    break;
                case CheckersBoard.BLACK_CROWNED:
                    accumulatedUtility += 5;
                    break;
                case CheckersBoard.BLACK_PLAIN:
                    accumulatedUtility += 4;
                    break;
            }
        }
    }
    private boolean iamblack() {
        return initialPlayer == CheckersBoard.Player.BLACK;
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

    private boolean isCapturePossibleAtPosition(CheckersBoard.Player player, int i, int j,  CheckersBoard nodeBoard) {
        return isDownRightCapturePossible(player, i, j, nodeBoard)//
                || isUpLeftCapturePossible(player, i, j, nodeBoard)//
                || isDownLeftCapturePossible(player, i, j, nodeBoard)//
                || isUpRightCapturePossible(player, i, j, nodeBoard);
    }

    private boolean isUpRightCapturePossible(CheckersBoard.Player player, int i, int j, CheckersBoard nodeBoard) {
        return i > 1 && j < 6 && isEnemyPiece(player, i - 1, j + 1, nodeBoard) && nodeBoard.getBoard()[i - 2][j + 2] == CheckersBoard.EMPTY && nodeBoard.getBoard()[i][j] != CheckersBoard.RED_PLAIN;
    }

        private boolean isDownLeftCapturePossible(CheckersBoard.Player player, int i, int j, CheckersBoard nodeBoard) {
        return i < 6 && j > 1 && isEnemyPiece(player, i + 1, j - 1, nodeBoard) && nodeBoard.getBoard()[i + 2][j - 2] == CheckersBoard.EMPTY && nodeBoard.getBoard()[i][j] != CheckersBoard.BLACK_PLAIN;
    }

    private boolean isUpLeftCapturePossible(CheckersBoard.Player player, int i, int j, CheckersBoard nodeBoard) {
        return i > 1 && j > 1 && isEnemyPiece(player, i - 1, j - 1, nodeBoard) && nodeBoard.getBoard()[i - 2][j - 2] == CheckersBoard.EMPTY && nodeBoard.getBoard()[i][j] != CheckersBoard.RED_PLAIN;
    }

    private boolean isDownRightCapturePossible(CheckersBoard.Player player, int i, int j, CheckersBoard nodeBoard) {
        return i < 6 && j < 6 && isEnemyPiece(player, i + 1, j + 1, nodeBoard) && nodeBoard.getBoard()[i + 2][j + 2] == CheckersBoard.EMPTY && nodeBoard.getBoard()[i][j] != CheckersBoard.BLACK_PLAIN;
    }

    private boolean isEnemyPiece(CheckersBoard.Player player, int i, int j, CheckersBoard nodeBoard) {
        return (player == CheckersBoard.Player.BLACK && Character.toLowerCase(nodeBoard.getBoard()[i][j]) == 'r')//
                || (player == CheckersBoard.Player.RED && Character.toLowerCase(nodeBoard.getBoard()[i][j]) == 'b');
    }
}
