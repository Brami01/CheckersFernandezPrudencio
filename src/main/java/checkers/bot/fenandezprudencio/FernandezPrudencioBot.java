package checkers.bot.fenandezprudencio;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class FernandezPrudencioBot implements CheckersPlayer {
    private int EXPLORATIONDEPTH = 6;

    @Override
    public CheckersMove play(CheckersBoard board) {
        CheckersBoard.Player myPlayer = board.getCurrentPlayer();
        try {
            return findBestMove(board, myPlayer, EXPLORATIONDEPTH);
        } catch (BadMoveException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Something went wrong");
    }

    public CheckersMove findBestMove (CheckersBoard board, CheckersBoard.Player myPlayer, int exploration) throws BadMoveException {
        ChildBoard rootBoard = possibleBoards(board, exploration);
        return getMiniMaxUtilityOf(rootBoard, myPlayer);
    }

    public CheckersMove getMiniMaxUtilityOf(ChildBoard rootBoard, CheckersBoard.Player myPlayer) {
        List <ChildBoard> allBoardsToCheck = addAllBoards(rootBoard);
        int previousDepth = greaterDepth(allBoardsToCheck) - 1;
        if(!rootBoard.childrenBoards.isEmpty()) {
            while (previousDepth >= 0) {
                List <ChildBoard> previousDepthChildren = new LinkedList<>();
                int finalPreviousDepth = previousDepth;
                previousDepthChildren.addAll(allBoardsToCheck.stream().filter(n -> n.depth == finalPreviousDepth).collect(Collectors.toList()));
                addUtility(myPlayer, previousDepthChildren);
                previousDepth--;
            }
            return getBestRandomMove(rootBoard);
        }
        return null;
    }

    private void addUtility(CheckersBoard.Player myPlayer, List<ChildBoard> previousDepthChildren) {
        for(ChildBoard parent: previousDepthChildren) {
            for(ChildBoard children: parent.childrenBoards) {
                if (parent.utility == 0) {
                    parent.utility = children.utility;
                } else {
                    if (parent.board.getCurrentPlayer() == myPlayer) {
                        if (children.utility > parent.utility) {
                            parent.utility = children.utility;
                        }
                    } else {
                        if (children.utility < parent.utility) {
                            parent.utility = children.utility;
                        }
                    }
                }
            }
        }
    }

    private CheckersMove getBestRandomMove(ChildBoard rootBoard) {
        List<ChildBoard> bestBoards = new LinkedList<>();
        for (ChildBoard childBoard: rootBoard.childrenBoards){
            if (childBoard.utility == rootBoard.utility) {
                bestBoards.add(childBoard);
            }
        }
        return  bestBoards.get(ThreadLocalRandom.current().nextInt(bestBoards.size())).getMoveDone();
    }

    private List<ChildBoard> addAllBoards(ChildBoard rootBoard) {
        List <ChildBoard> allBoardsToCheck = new LinkedList<>();
        if(iHaveChildrens(rootBoard)) {
            for(ChildBoard children: rootBoard.childrenBoards) {
                allBoardsToCheck.addAll(addAllBoards(children));
            }
        }
        allBoardsToCheck.add(rootBoard);
        return allBoardsToCheck;
    }

    private int greaterDepth(List<ChildBoard> allBoards) {
        int maxDepth = 0;
        for (ChildBoard childBoard: allBoards){
            if (childBoard.depth > maxDepth) {
                maxDepth = childBoard.depth;
            }
        }
        return maxDepth;
    }

    private boolean iHaveChildrens(ChildBoard rootBoard) {
        if (rootBoard.childrenBoards == null) {
            return false;
        }
        return !rootBoard.childrenBoards.isEmpty();
    }

    public ChildBoard possibleBoards(CheckersBoard board, int exploration) throws BadMoveException {
        ChildBoard rootBoard = new ChildBoard(board);
        rootBoard.successors(exploration);
        return rootBoard;
    }
}
