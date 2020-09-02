package checkers.bot.fenandezprudenciofinalversion;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class FernandezPrudencioBot2 implements CheckersPlayer {
    private static final int EXPLORATIONDEPTH = 4;
    private int roundsDoingNothingElseThanRunning = 0;
    @Override
    public CheckersMove play(CheckersBoard board) {
        CheckersBoard.Player myPlayer = board.getCurrentPlayer();
        if(board.possibleCaptures().isEmpty()) {roundsDoingNothingElseThanRunning ++;}
        else {roundsDoingNothingElseThanRunning=0;}
        try {
            if(roundsDoingNothingElseThanRunning > 3) { //Possible Draw Loop :(
                if(iCanCoronate(board)) { //Force Coronations to get Better Chances
                    return thenCoronate(board);
                }
                if(iHaveMorePiecesThanEnemy(board)) {
                    return returnPossibleAdvantageMove(board, myPlayer);
                } else {
                    return returnNormalBestMove(board, myPlayer);
                }
            } else {
                return returnNormalBestMove(board, myPlayer);
            }
        } catch (BadMoveException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Something went wrong");
    }

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

    private boolean iCanCoronate(CheckersBoard board) {
        if(board.getCurrentPlayer() == CheckersBoard.Player.RED) {
            for (int j = 0 ; j< board.getBoard().length;j++){
                if(board.getBoard()[6][j] == CheckersBoard.RED_PLAIN && isDownLeftMovePossible(board,6,j)) {
                    return true;
                }
                if(board.getBoard()[6][j] == CheckersBoard.RED_PLAIN && isDownRightMovePossible(board,6,j)) {
                    return true;
                }
            }
        } else {
            for (int j = 0 ; j< board.getBoard().length;j++){
                if(board.getBoard()[1][j] == CheckersBoard.BLACK_PLAIN && isUpLeftMovePossible(board,1,j)) {
                    return true;
                }
                if(board.getBoard()[1][j] == CheckersBoard.BLACK_PLAIN && isUpRightMovePossible(board,1,j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private CheckersMove returnPossibleAdvantageMove(CheckersBoard board, CheckersBoard.Player myPlayer) throws BadMoveException {
        HashMap<ChildBoard, Integer> possibleScenariosValue = calculateValuesofChildBoards(casesIsacrificePiecesToKillAnotherOne(board,possibleBoards(board)), myPlayer);
        return calculateBestAction(possibleScenariosValue);
    }

    private CheckersMove returnNormalBestMove(CheckersBoard board, CheckersBoard.Player myPlayer) throws BadMoveException {
        HashMap<ChildBoard, Integer> possibleScenariosValue = calculateValuesofChildBoards(possibleBoards(board), myPlayer);
        return calculateBestAction(possibleScenariosValue);
    }

    private LinkedList<ChildBoard> casesIsacrificePiecesToKillAnotherOne(CheckersBoard board, LinkedList<ChildBoard> possibleBoards) {
        LinkedList<ChildBoard> bestCases = new LinkedList<>();
        for (ChildBoard possibleCase : possibleBoards) {
            if(iHaveAtLeastSamePiecesThanEnemy(board,possibleCase.board)) {
                bestCases.add(possibleCase);
            }
        }
        return bestCases;
    }

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

    private LinkedList<ChildBoard> possibleBoards(CheckersBoard board) throws BadMoveException {
        ChildBoard initialBoard = new ChildBoard(board);
        LinkedList<ChildBoard> q = new LinkedList<>();
        q.add(initialBoard);
        do {
            ChildBoard n = q.removeFirst();
            LinkedList<ChildBoard> s = new LinkedList<ChildBoard>(successors(n));
            //BFS
            q.addAll(s);
            if(q.isEmpty()) {
                q.add(n);
                break;
            }
        } while (q.getFirst().depth < EXPLORATIONDEPTH);
        return q;
    }

    private List<ChildBoard> successors(ChildBoard board) throws BadMoveException {
        List<CheckersMove> possiblePlays = getPossiblePlays(board.board);
        List<ChildBoard> possibleFutureBoards = new LinkedList<>();
        for (CheckersMove possiblePlay: possiblePlays) {
            ChildBoard possibleStateofBoard = new ChildBoard(board, possiblePlay);
            possibleFutureBoards.add(possibleStateofBoard);
        }
        return possibleFutureBoards;
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

    private boolean iHaveMorePiecesThanEnemy(CheckersBoard board) { //Directly to main board
        return board.countPiecesOfPlayer(board.getCurrentPlayer())>board.countPiecesOfPlayer(board.otherPlayer());
    }

    private boolean iHaveAtLeastSamePiecesThanEnemy(CheckersBoard board, CheckersBoard childBoard) { //To Child boards
        return childBoard.countPiecesOfPlayer(board.getCurrentPlayer())>=childBoard.countPiecesOfPlayer(board.otherPlayer());
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
}
