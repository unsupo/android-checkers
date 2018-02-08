package arndt.com.checkers;

/**
 * Created by jarndt on 1/30/18.
 */

import java.util.*;

import arndt.com.checkers.objects.Node;
import arndt.com.checkers.objects.Pair;

public class Checkers {
    public static final int NOTHING = 0, WHITE = 1, BLACK = 2, WHITE_KING = 3, BLACK_KING = 4,
            WIDTH = 8, HEIGHT = 8;
    private static int maxDepth = 7;
    private int player = WHITE;
    private int[][] board;
    public Checkers() {
        setUp(WIDTH,HEIGHT);
    }
    public Checkers(int[][] board, int player){
        this.board = board;
        this.player = player;
    }

    private void setUp(int width, int height) {
        board = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(j<3)//whites
                    if(isOdd(j))//j is odd
                        if(isOdd(i))//i is odd
                            board[i][j]=NOTHING;
                        else
                            board[i][j]=WHITE;
                    else
                    if(isOdd(i))//i is odd
                        board[i][j]=WHITE;
                    else
                        board[i][j]=NOTHING;
                else if(j>=3 && j<5)//nothing
                    board[i][j]=NOTHING;
                else //whites
                    if(isOdd(j))//j is odd
                        if(isOdd(i))//i is odd
                            board[i][j]=NOTHING;
                        else
                            board[i][j]=BLACK;
                    else
                    if(isOdd(i))//i is odd
                        board[i][j]=BLACK;
                    else
                        board[i][j]=NOTHING;
            }
        }
    }

    public double getCurrentScore(){
        double score = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                switch (board[i][j]){
                    case WHITE:
                        score+=1; break;
                    case WHITE_KING:
                        score+=1.5; break;
                    case BLACK:
                        score-=1; break;
                    case BLACK_KING:
                        score-=1.5; break;
                }
            }
        }
        return score;
    }

    public boolean canMove(int x, int y){
        if(board[x][y] == NOTHING)
            return false;
        List<Pair<Integer, Integer>> m = getAllMoveableLocations(x, y);
        if(m.isEmpty())
            return false;
        return true;
    }
    public Map<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> getAllMoveLocations(){
        Map<Pair<Integer,Integer>,List<Pair<Integer,Integer>>> map = new HashMap<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                map.put(new Pair<>(i,j), getAllMoveableLocations(i,j));
            }
        }
        return map;
    }public Map<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> getAllMoveLocationsForPlayer(int player){
        Map<Pair<Integer,Integer>,List<Pair<Integer,Integer>>> map = new HashMap<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if(board[i][j]!= NOTHING && ((isOdd(board[i][j]) && isOdd(player)) || (!isOdd(board[i][j]) && !isOdd(player))))
                    map.put(new Pair<>(i,j), getAllMoveableLocations(i,j));
            }
        }
        return map;
    }

    public List<Pair<Integer,Integer>> getAllMoveableLocations(int x, int y){
        List<Pair<Integer,Integer>> options = new ArrayList<>();
        if(jumped != null && !new Pair<>(x,y).equals(jumped))
            return options;
        return getAllOptions(options,x,y);
    }

    private List<Pair<Integer, Integer>> getAllOptions(List<Pair<Integer, Integer>> options, int x, int y){
        int d = board[x][y]==WHITE?1:board[x][y]==BLACK?-1:0;
        if(d==0) {//kinged
            d=1;
            check(isOdd(board[x][y]),x,y,x+1,y+d,options);
            check(isOdd(board[x][y]),x,y,x-1,y+d,options);
            d=-1;
        }
        check(isOdd(board[x][y]),x,y,x+1,y+d,options);
        check(isOdd(board[x][y]),x,y,x-1,y+d,options);
        return options;
    }

    private void check(boolean odd, int x1, int y1, int x, int y, List<Pair<Integer, Integer>> options) {
        if(x < board.length && x >= 0 && y>=0 && y < board.length){
            if(board[x][y]==NOTHING){ //if there's nothing there then you can move to that spot
                options.add(new Pair<>(x,y));
                return;
            }
            if(odd && isOdd(board[x][y])) //if it's the same color do nothing
                return;
            if(!odd && !isOdd(board[x][y])) //if it's the same color do nothing
                return;

            //it's a different color, so jump it.  Must jump in same direction
            int x2 = x + (x-x1),y2 = y + (y-y1);
            if(x2 < board.length && x2 >= 0 && y2 >= 0 && y2 < board.length) {
                if (board[x2][y2] == NOTHING) { //if there's nothing there then you can move to that spot
                    options.add(new Pair<>(x2, y2));
                    return;
                }
                if (odd && isOdd(board[x2][y2])) //if it's the same color do nothing
                    return;
                if (!odd && !isOdd(board[x2][y2])) //if it's the same color do nothing
                    return;
            }
        }
    }

    public boolean makeMove(Pair<Integer,Integer> pieceLocation, Pair<Integer,Integer> toLocation){
        int p = board[pieceLocation.getFirst()][pieceLocation.getSecond()];
        if(p == NOTHING)
            return false;
        if(!((isOdd(p) && isOdd(player)) || (!isOdd(p) && !isOdd(player))))
            return false;
        List<Pair<Integer, Integer>> m = getAllMoveableLocations(pieceLocation.getFirst(), pieceLocation.getSecond());
        if(m.isEmpty())
            return false;
        if(!m.contains(toLocation))
            return false;
        int x = toLocation.getFirst(),y = toLocation.getSecond();
        int o = board[x][y];
        board[pieceLocation.getFirst()][pieceLocation.getSecond()]=NOTHING;
        board[x][y]=p;
        int xb = toLocation.getFirst()-pieceLocation.getFirst(), yb = toLocation.getSecond() - pieceLocation.getSecond();
        boolean didJump = false;
        if(Math.abs(xb) > 1 || Math.abs(yb) > 1) {
            board[pieceLocation.getFirst() + xb / 2][pieceLocation.getSecond() + yb / 2] = NOTHING;
            didJump = true;
        }
        //King the pieces if needs be
        if(board[x][y] == WHITE && y==board.length - 1)
            board[x][y]=p+2;
        else if(board[x][y] == BLACK && y==0)
            board[x][y]=p+2;
        if(!didJump || (didJump && getAllJumpableLocations(toLocation) == null)) {
            player = player == WHITE ? BLACK : WHITE;
            jumped = null;
        }else
            jumped = toLocation;
        return true;
    }
    private Pair<Integer,Integer> jumped;

    public List<Pair<Integer,Integer>> getAllJumpableLocations(Pair<Integer,Integer> pieceLocation){
        List<Pair<Integer, Integer>> m = getAllMoveableLocations(pieceLocation.getFirst(), pieceLocation.getSecond());
        if(m.isEmpty())
            return null;
        List<Pair<Integer, Integer>> j = new ArrayList<>();
        for(Pair<Integer, Integer> s : m)
            if(Math.abs(s.getFirst() - pieceLocation.getFirst()) > 1 ||
                    Math.abs(s.getSecond() - pieceLocation.getSecond()) > 1)
                j.add(s);
        if(j.isEmpty())
            return null;
        return j;
    }

    public int isVictory(){
        int wCount = 0, bCount = 0;
        for (int i = 0; i < board.length; i++) {
            if(wCount > 0 && bCount > 0)
                return -1;
            for (int j = 0; j < board[i].length; j++) {
                if(wCount > 0 && bCount > 0)
                    return -1;
                if(board[i][j]!=NOTHING)
                    if(isOdd(board[i][j]))
                        wCount++;
                    else
                        bCount++;
            }
        }
        return wCount == 0 ? BLACK : bCount == 0 ? WHITE : -1;
    }

    private boolean isOdd(int i){
        return i==0?false:i%2!=0;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getPlayer() {
        return player;
    }

    public String getPlayerString(){
        if(isOdd(player))
            return "WHITE";
        return "BLACK";
    }

    public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> getComputerMove() {
        Node<Integer,Double> parent = new Node<>(board.hashCode(),getCurrentScore());
        double v = minimax(parent,player,this,0, -Double.MAX_VALUE,Double.MAX_VALUE);
        List<Node<Integer,Double>> moves = new ArrayList<>();
        for(Node<Integer,Double> s : parent.getChildren())
            if(s.getValues().get("score").equals(v))
                moves.add(s);
        if(moves.isEmpty())
            return null;
        return (Pair<Pair<Integer, Integer>,Pair<Integer, Integer>>) moves.get(randBetween(0,moves.size()-1)).getValues().get("pair");
    }
    private static double minimax(Node<Integer, Double> parent, int player, Checkers checkers, int depth, double alpha, double beta) {
        double bs = checkers.getCurrentScore();
        if(checkers.isVictory() != -1 || depth >= maxDepth)
            return bs+(player==WHITE?1:(-1))*depth/100.+(checkers.isVictory()==-1?0:checkers.isVictory()==player?1000:-1000);
        Map<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> moves = checkers.getAllMoveLocationsForPlayer(checkers.getPlayer());
        double bestVal = player == WHITE ? -Double.MAX_VALUE : Double.MAX_VALUE;
        boolean breaker = false;
        for(Pair<Integer, Integer> i : moves.keySet()) {
            for (Pair<Integer, Integer> j : moves.get(i)) {
                Checkers c = new Checkers(clone(checkers.board), checkers.player);
                c.makeMove(i, j);
                Node<Integer, Double> n = new Node<>(c.hashCode(), bs);
                parent.getChildren().add(n);
                n.setParent(parent);
                double score = minimax(n, c.getPlayer(), c, c.getPlayer() == player ? depth : depth + 1, alpha, beta);
                n.getValues().put("score", score);
                n.getValues().put("pair", new Pair<>(i, j));
                if (player == WHITE) {
                    bestVal = Math.max(bestVal, score);
                    alpha = Math.max(alpha, bestVal);
                } else {
                    bestVal = Math.min(bestVal, score);
                    beta = Math.min(beta, bestVal);
                }
                if (beta <= alpha) {
                    breaker = true;
                    break;
                }
            }
            if(breaker)
                break;
        }
        return bestVal;
    }

    private static int[][] clone(int[][] b) {
        int[][] tmp = new int[b.length][b[0].length];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                tmp[i][j]=b[i][j];
            }
        }
        return tmp;
    }
    private static Random r;    // pseudo-random number generator
    private static long seed;        // pseudo-random number generator seed

    // static initializer
    static {
        // this is how the seed was set in Java 1.4
        seed = System.currentTimeMillis();
        r = new Random(seed);
    }
    public static int randBetween(int min, int max){
        return r.nextInt(max - min + 1) + min;
    }
}
