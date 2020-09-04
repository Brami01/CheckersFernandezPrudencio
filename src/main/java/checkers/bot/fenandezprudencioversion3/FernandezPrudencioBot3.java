package checkers.bot.fenandezprudencioversion3;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FernandezPrudencioBot3 implements CheckersPlayer {
    private static final int EXPLORATIONDEPTH = 6;
    private int roundsDoingNothingElseThanRunning = 0;

    @Override
    public CheckersMove play(CheckersBoard board) {
        CheckersBoard.Player myPlayer = board.getCurrentPlayer();
        if(board.possibleCaptures().isEmpty()) {
            roundsDoingNothingElseThanRunning++;
        } else {
            roundsDoingNothingElseThanRunning = 0;
        }
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
                previousDepthChildren.addAll(allBoardsToCheck.stream().filter(n -> n.depth== finalPreviousDepth).collect(Collectors.toList()));
                addUtility(myPlayer, previousDepthChildren);
                previousDepth--;
            }
            return getBestMove(rootBoard);
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

    private CheckersMove getBestMove(ChildBoard rootBoard) {
        ChildBoard bestBoard = rootBoard.childrenBoards.get(0);
        for (ChildBoard childBoard: rootBoard.childrenBoards){
            if (childBoard.utility > bestBoard.utility) {
                bestBoard = childBoard;
            }
        }
        return bestBoard.getMoveDone();
    }

    private List<ChildBoard> addAllBoards(ChildBoard rootBoard) {
        List <ChildBoard> allBoardsToCheck = new LinkedList<>();
        if(iHaveChildrens(rootBoard)) {
            for(ChildBoard children: rootBoard.childrenBoards) {
                allBoardsToCheck.addAll(addAllBoards(children));
            }
            allBoardsToCheck.add(rootBoard);
        } else {
            allBoardsToCheck.add(rootBoard);
        }
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

/*
    private ChildBoard findParent(ChildBoard rootBoard, ChildBoard childWhoIsParentOfSuccessors) {
        for(ChildBoard board : rootBoard.childrenBoards) {
            if(board.childrenBoards == null && board.board == childWhoIsParentOfSuccessors.board) {
                return childWhoIsParentOfSuccessors;
            } else if (board.childrenBoards!= null) {
                return findParent(board, childWhoIsParentOfSuccessors);
            }
        }
        throw new IllegalStateException("No parent existent");
    }
    z

    private CheckersMove thenCoronate(CheckersBoard board) {
        CheckersMove move = null;
        if(board.getCurrentPlayer() == CheckersBoard.Player.RED) {
            for (int j = 0 ; j< board.getBoard().length;j++){
                if(board.getBoard()[6][j] == CheckersBoard.RED_PLAIN && isDownLeftMovePossible(board,6,j)) {
                    move = CheckersMove.builder().fromPosition(6,j).toPosition(7, j-1).build();
                } else if(board.getBoard()[6][j] == CheckersBoard.RED_PLAIN && isDownRightMovePossible(board,6,j)) {
                    move = CheckersMove.builder().fromPosition(6,j).toPosition(7, j+1).build();
                }
            }
        } else {
            for (int j = 0 ; j< board.getBoard().length;j++){
                if(board.getBoard()[1][j] == CheckersBoard.BLACK_PLAIN && isUpLeftMovePossible(board,1,j)) {
                    move = CheckersMove.builder().fromPosition(1,j).toPosition(0, j-1).build();
                } else if(board.getBoard()[1][j] == CheckersBoard.BLACK_PLAIN && isUpRightMovePossible(board,1,j)) {
                    move = CheckersMove.builder().fromPosition(1,j).toPosition(0, j+1).build();
                }
            }
        }
        return move;
    }



    private CheckersMove returnNormalBestMove(CheckersBoard board, CheckersBoard.Player myPlayer) throws BadMoveException {
        HashMap<ChildBoard, Integer> possibleScenariosValue = calculateValuesofChildBoards(possibleBoards(board), myPlayer);
        return calculateBestAction(possibleScenariosValue);
    }*/
    /*
    private CheckersMove calculateBestAction(HashMap<ChildBoard, Integer> possibleScenariosValue) throws BadMoveException {
        int highestUtility = getHighestUtility(possibleScenariosValue.values());
        LinkedList<ChildBoard> possibleBestMoves = new LinkedList<>();
        for(Map.Entry<ChildBoard, Integer> possibleScenario : possibleScenariosValue.entrySet()){
            if(possibleScenario.getValue()==highestUtility)
                possibleBestMoves.add(possibleScenario.getKey());
        }
        int indexOfSelectedScenario = ThreadLocalRandom.current().nextInt(possibleBestMoves.size());
        return possibleBestMoves.get(indexOfSelectedScenario).getMoveDone();
    }

    private int getHighestUtility(Collection<Integer> values) {
        int maxU = Integer.MIN_VALUE;
        for (Integer s : values){
            if(s >= maxU) {
                maxU =s;
            }
        }
        return maxU;
    }

    private HashMap<ChildBoard, Integer> calculateValuesofChildBoards(LinkedList<ChildBoard> possibleScenarios, CheckersBoard.Player myPlayer) {
        int utilityOfScenario;
        HashMap<ChildBoard, Integer> scenarioUtility = new HashMap<>();
        for(ChildBoard possibleScenario : possibleScenarios) {
            utilityOfScenario = calculateUtilityoOfScenario(possibleScenario,myPlayer);
            scenarioUtility.put(possibleScenario,utilityOfScenario);
        }
        return scenarioUtility;
    }

    private int calculateUtilityoOfScenario(ChildBoard possibleScenario, CheckersBoard.Player myPlayer) {
        int utility = possibleScenario.board.countPiecesOfPlayer(myPlayer)-possibleScenario.board.countPiecesOfPlayer(myOpponent(myPlayer));
        if(myPlayer == CheckersBoard.Player.BLACK) {
        if (possibleScenario.parent != null) // Sum up the process (should I?)
            utility += calculateUtilityoOfScenario(possibleScenario.parent,myPlayer);
        }
        return utility;
    }


    private List<CheckersMove> getPossiblePlays(CheckersBoard board) {
        List<CheckersMove> possiblePlays = board.possibleCaptures();
        if (possiblePlays.isEmpty())
            possiblePlays = board.possibleMoves();
        return possiblePlays;
    }

    private CheckersBoard.Player myOpponent(CheckersBoard.Player myColorOfPlayer) {
        return myColorOfPlayer == CheckersBoard.Player.RED ? CheckersBoard.Player.BLACK : CheckersBoard.Player.RED;
    }

    private boolean isDownRightMovePossible(CheckersBoard board,int i, int j) {
        return i < 7 && j < 7 && board.getBoard()[i + 1][j + 1] == CheckersBoard.EMPTY
                // we exclude the non-crowned piece that cannot move in this direction
                && board.getBoard()[i][j] != CheckersBoard.BLACK_PLAIN;
    }
    private boolean isDownLeftMovePossible(CheckersBoard board,int i, int j) {
        return i < 7 && j > 0 && board.getBoard()[i + 1][j - 1] == CheckersBoard.EMPTY
                // we exclude the non-crowned piece that cannot move in this direction
                && board.getBoard()[i][j] != CheckersBoard.BLACK_PLAIN;
    }
    private boolean isUpRightMovePossible(CheckersBoard board,int i, int j) {
        return i > 0 && j < 7 && board.getBoard()[i - 1][j + 1] == CheckersBoard.EMPTY
                // we exclude the non-crowned piece that cannot move in this direction
                && board.getBoard()[i][j] != CheckersBoard.RED_PLAIN;
    }
    private boolean isUpLeftMovePossible(CheckersBoard board,int i, int j) {
        return i > 0 && j > 0 && board.getBoard()[i - 1][j - 1] == CheckersBoard.EMPTY
                // we exclude the non-crowned piece that cannot move in this direction
                && board.getBoard()[i][j] != CheckersBoard.RED_PLAIN;
    }


    if(roundsDoingNothingElseThanRunning > 3) { //Possible Draw Loop :(
                if(iCanCoronate(board)) { //Force Coronations to get Better Chances
                    return thenCoronate(board);
                } else {
                    return returnNormalBestMove(board, myPlayer);
                }
            } else {
                return returnNormalBestMove(board, myPlayer);
            }*/
}
