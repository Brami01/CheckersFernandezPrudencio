package checkers.bot.fenandezprudencioversion2;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.exception.BadMoveException;

public class ChildBoard {
    public CheckersBoard board;
    public CheckersMove move;
    public int depth;
    public ChildBoard parent;
    public int utility;
    //Creates root ChildBoard
    public ChildBoard(CheckersBoard board) {
        this.board = board;
        this.depth = 0;
        this.move = null;
        this.parent = null;
        this.utility = 0;
    }
    //Creates possible Future ChildBoards
    public ChildBoard(ChildBoard parent, CheckersMove move) throws BadMoveException {
        this.depth = parent.depth + 1;
        this.move = move;
        this.parent = parent;
        this.board = createBoardWithAssignedMove(parent.board,move);
    }
    private CheckersBoard createBoardWithAssignedMove(CheckersBoard board, CheckersMove move) throws BadMoveException {
        CheckersBoard newBoard = board.clone();
        newBoard.processMove(move);
        return newBoard;
    }
    public CheckersMove getMoveDone() {
        if (parent.parent == null) {
            return move;
        }
        return parent.getMoveDone();
    }
}
