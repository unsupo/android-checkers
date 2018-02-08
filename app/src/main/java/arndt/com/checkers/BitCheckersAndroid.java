package arndt.com.checkers;

/**
 * Created by jarndt on 2/5/18.
 */

import java.util.Objects;

import arndt.com.checkers.objects.Node;
import arndt.com.checkers.objects.Pair;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static arndt.com.checkers.Checkers.randBetween;


public class BitCheckersAndroid {/*
    public static final long
            init_whiteBitBoard       = 0b1010101001010101101010100000000000000000000000000000000000000000L,
            init_blackBitBoard       = 0b0000000000000000000000000000000000000000010101011010101001010101L,
            init_whiteKingBitBoard = 0, init_blackKingBitBoard = 0,
            allMovesBitBoard         = 0b1010101001010101101010100101010110101010010101011010101001010101L,
            whiteKingMask            = 0b1010101000000000000000000000000000000000000000000000000000000000L,
            blackKingMask            = 0b0000000000000000000000000000000000000000000000000000000001010101L,
            init_wallsMask           = 0b1010101000000001100000000000000110000000000000011000000001010101L,
            init_cornerMask          = 0b1000000000000000000000000000000000000000000000000000000000000001L;

    public static final long[][] allMovesLookup = {
            {-9223372036854775808L,4611686018427387904L,2305843009213693952L,1152921504606846976L,576460752303423488L,288230376151711744L,144115188075855872L,72057594037927936L},
            {36028797018963968L,18014398509481984L,9007199254740992L,4503599627370496L,2251799813685248L,1125899906842624L,562949953421312L,281474976710656L},
            {140737488355328L,70368744177664L,35184372088832L,17592186044416L,8796093022208L,4398046511104L,2199023255552L,1099511627776L},
            {549755813888L,274877906944L,137438953472L,68719476736L,34359738368L,17179869184L,8589934592L,4294967296L},
            {2147483648L,1073741824L,536870912L,268435456L,134217728L,67108864L,33554432L,16777216L},
            {8388608L,4194304L,2097152L,1048576L,524288L,262144L,131072L,65536L},
            {32768L,16384L,8192L,4096L,2048L,1024L,512L,256L},
            {128L,64L,32L,16L,8L,4L,2L,1L}
    };

    public static final int WHITE = 1, BLACK = -1, INVALID  = 0;

    private long    whiteBitBoard       = init_whiteBitBoard,
            blackBitBoard       = init_blackBitBoard,
            whiteKingBitBoard = init_whiteKingBitBoard, blackKingBitBoard = init_blackKingBitBoard;

    private int player = WHITE; //player = 1 is whites' turn, player = -1 is blacks' turn//white goes down black goes up

    public BitCheckersAndroid() {
//        init();
    }

    public BitCheckersAndroid(long whiteBitBoard, long blackBitBoard, long whiteKingBitBoard, long blackKingBitBoard, int player) {
        this.whiteBitBoard = whiteBitBoard;
        this.blackBitBoard = blackBitBoard;
        this.whiteKingBitBoard = whiteKingBitBoard;
        this.blackKingBitBoard = blackKingBitBoard;
        this.player = player;
//        allMovesBitBoard = getAllMovesBitBoard();
    }
    public int getVictoryState(){
        return (whiteBitBoard|whiteKingBitBoard)==0?0:(blackBitBoard|blackKingBitBoard)==0?1:-1;
    }

    public double getBoardScore(){
        return Long.bitCount(whiteBitBoard)+Long.bitCount(whiteKingBitBoard)*1.5
                - (Long.bitCount(blackBitBoard) + Long.bitCount(blackKingBitBoard)*1.5);
    }

    public long getAllMoves(){
        if(player == BLACK)
            return getAllMovesBlacks();
        return getAllMovesWhites();
    }
    public long getAllMovesWhites(){
        return getMoves(whiteBitBoard,false);
    }public long getAllMovesBlacks(){
        return getMoves(blackBitBoard, true);
    }
    private long getMoves(long bitBoard){
        return getMoves(bitBoard,player==BLACK?true:false);
    }private long getMoves(long bitBoard, boolean up){
        if(up)
            return (((bitBoard << 9 | bitBoard << 7) & allMovesBitBoard) | bitBoard) & ~bitBoard;
        return ((bitBoard >> 9 | bitBoard >> 7) & allMovesBitBoard) & ~bitBoard;
    }
    private long getAllPlayerJumpMoves(long moves){
        return player == WHITE ? getAllJumpMovesWhites(moves) : getAllJumpMovesBlacks(moves);
    }
    private long getAllJumpMoves(long bitBoard, long allMoves){
        return bitBoard&allMoves;
    }
    public long getAllJumpMovesBlacks(long allBlackMoves) {
        //if at least one of the allBlackMoves corresponds to a whiteBitboard then it's a jumpMove
        return getAllJumpMoves(whiteBitBoard,allBlackMoves);
    }private long getAllJumpMovesWhites(long allWhiteMoves) {
        return getAllJumpMoves(blackBitBoard,allWhiteMoves);
    }
    public long getKingMoves(long kingBitBoard){
        return (kingBitBoard >>> 7 | kingBitBoard >>> 9 | kingBitBoard << 7 | kingBitBoard << 9) & allMovesBitBoard;
    }
    public long getAllKingMoves(){
        return player == WHITE ? getKingMoves(whiteKingBitBoard) : getKingMoves(blackKingBitBoard);
    }
    public long getPlayerBitboard(){
        return player == WHITE ? getWhiteBitBoard() : getBlackBitBoard();
    }
    public long getPlayerKingBoard() {
        return player == WHITE ? getWhiteKingBitBoard() : getBlackKingBitBoard();
    }

    public long getBlackBitBoard() {
        return blackBitBoard;
    }

    public void setBlackBitBoard(long blackBitBoard) {
        this.blackBitBoard = blackBitBoard;
    }

    public long getWhiteKingBitBoard() {
        return whiteKingBitBoard;
    }

    public void setWhiteKingBitBoard(long whiteKingBitBoard) {
        this.whiteKingBitBoard = whiteKingBitBoard;
    }

    public long getBlackKingBitBoard() {
        return blackKingBitBoard;
    }

    public void setBlackKingBitBoard(long blackKingBitBoard) {
        this.blackKingBitBoard = blackKingBitBoard;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public String getPlayerString(){
        if(player == WHITE)
            return "WHITE";
        return "BLACK";
    }

    private static long parseLong(String s, int base) {
        return new BigInteger(s, base).longValue();
    }
    private static long parseLong(String s) {
        return parseLong(s, 2);
    }
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getAllPlayerMoves(){
        // TODO: 2/2/18 what about jump moves
        long moves = getAllMoves(), startingPoint,
                jump = getAllPlayerJumpMoves(moves), jumpStartingPoint = getMoves(jump,player == WHITE) & getPlayerBoard();
        moves&=~jump;
        startingPoint = getMoves(moves, player == WHITE) & getPlayerBoard();
        List<Pair<Pair<Integer,Integer>,Pair<Integer,Integer>>> allMoves = new ArrayList<>();
        addMoves(jumpStartingPoint,false, true, allMoves);
        addMoves(startingPoint,false,false, allMoves);
        long kingMoves = getAllKingMoves();
        if(kingMoves == 0)
            return allMoves;
        // TODO: 2/2/18 get king jump moves
        long kingStartingPoint,
                kingJump = kingMoves&(WHITE==player?getBlackBitBoard():getWhiteBitBoard()),
                kingJumpStartingPoint = getKingMoves(kingJump) & getPlayerKingBoard();
        kingMoves&=~kingJump;
        kingStartingPoint = getKingMoves(kingMoves)&getPlayerKingBoard();
        addMoves(kingJumpStartingPoint,true, true, allMoves);
        addMoves(kingStartingPoint,true,false, allMoves);
        return allMoves;
    }
    private void addMoves(long startingPoint, boolean isKing, boolean isJump,
                          List<Pair<Pair<Integer,Integer>,Pair<Integer,Integer>>> allMoves){
        while(startingPoint!=0) {
            int lz = Long.numberOfLeadingZeros(startingPoint);
            Pair<Integer,Integer> first = new Pair<>(lz/8,lz%8);
            long ms = allMovesLookup[lz / 8][lz % 8], m = !isKing ? getMoves(ms, player != WHITE) : getKingMoves(ms);
            m&=~getPlayerBoard()&~getPlayerKingBoard();
            if(isJump) {
                long b = getWhiteBitBoard(), k = getWhiteKingBitBoard(), ob = getBlackBitBoard(), okb = getBlackKingBitBoard();
                if(player == BLACK){ //blacks <<
                    b = getBlackBitBoard();
                    k = getBlackKingBitBoard();
                    ob = getWhiteBitBoard();
                    okb = getWhiteKingBitBoard();
                    if(!isKing)
                        m = (ms<<7&ob)<<7|(ms<<9&ob)<<9|(ms<<7&okb)<<7|(ms<<9&okb)<<9;
                } else { //whites >>>
                    if(!isKing)
                        m = (ms>>>7&ob)>>>7|(ms>>>9&ob)>>>9|(ms>>>7&okb)>>>7|(ms>>>9&okb)>>>9;
                }
                if(isKing)
                    m = (ms<<7&ob)<<7|(ms<<9&ob)<<9|(ms<<7&okb)<<7|(ms<<9&okb)<<9|(ms>>>7&ob)>>>7|(ms>>>9&ob)>>>9|(ms>>>7&okb)>>>7|(ms>>>9&okb)>>>9;
            }
            m&=allMovesBitBoard&~whiteBitBoard&~whiteKingBitBoard&~blackKingBitBoard&~blackBitBoard;
            while(m!=0){
                int lz2 = Long.numberOfLeadingZeros(m);
                Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> second = new Pair<>(first, new Pair<>(lz2 / 8, lz2 % 8));
                if(isJump)
                    allMoves.add(0,second);
                else
                    allMoves.add(second);
                m &= ~allMovesLookup[lz2/8][lz2%8];
            }
            startingPoint &= ~ms;
        }
    }
    private int maxDepth = 6;
    private Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>,Double> node;
    public Pair<Pair<Integer,Integer>,Pair<Integer,Integer>> getComputerMove(){
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves = getAllPlayerMoves();
        node = new Node(null,null);
        node.getValues().put("moves",moves);
        List<Node> children = new ArrayList<>();
        for(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> m : moves)
            children.add(new Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Double>(m,null));
//        node.setChildren(moves.stream().map(a->new Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>,Double>(a,null)).collect(Collectors.toList()));
        node.setChildren(children);
//        double v = pvs(this,maxDepth,-Double.MAX_VALUE,Double.MAX_VALUE);
        double v = minimax(this,maxDepth, maxDepth,-Double.MAX_VALUE,Double.MAX_VALUE);
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> bestMoves = new ArrayList<>();
        for(Node child : node.getChildren())
            if(child.getValue().equals(v))
                bestMoves.add((Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>) child.getData());
//                node.getChildren().stream().filter(a -> a.getValue().equals(v)).map(a -> a.getData()).collect(Collectors.toList());
        return bestMoves.get(randBetween(0,bestMoves.size()-1));
    }
    private double minimax(BitCheckersAndroid checkers, int depth, int absDepth, double alpha, double beta) {
        if(depth == 0 || checkers.getVictoryState() != -1)
            return checkers.getBoardScore();
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves;
        if(absDepth == maxDepth)
            moves = (List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>) node.getValues().get("moves");
        else
            moves = checkers.getAllPlayerMoves();
        double bestVal = checkers.getPlayer() == WHITE ? -Double.MAX_VALUE : Double.MAX_VALUE;
        for(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> i : moves) {
            BitCheckers c = checkers.clone();
            c.movePiece(i.getFirst().getFirst(),i.getFirst().getSecond(),i.getSecond().getFirst(),i.getSecond().getSecond());
            double score = minimax(c, c.getPlayer() == checkers.getPlayer() ? depth : depth - 1, absDepth -1, alpha, beta);
            if(absDepth == maxDepth) {
                double finalScore = score;
                for(Node n : node.getChildren())
                    if(n.getData().equals(i)){
                        n.setValue(finalScore);
                        break;
                    }
            }
            if (checkers.getPlayer() == WHITE) { //white is maximizing player
                bestVal = Math.max(bestVal, score);
                alpha = Math.max(alpha, bestVal);
            } else {
                bestVal = Math.min(bestVal, score);
                beta = Math.min(beta, bestVal);
            }
//            if (beta <= alpha)
//                break;
        }
        return bestVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BitCheckers that = (BitCheckers) o;
        return whiteBitBoard == that.whiteBitBoard &&
                blackBitBoard == that.blackBitBoard &&
                whiteKingBitBoard == that.whiteKingBitBoard &&
                blackKingBitBoard == that.blackKingBitBoard &&
                player == that.player;
    }

    @Override
    public int hashCode() {
        return Objects.hash(whiteBitBoard, blackBitBoard, whiteKingBitBoard, blackKingBitBoard, player);
    }

    @Override
    public BitCheckers clone(){
        return new BitCheckers(whiteBitBoard, blackBitBoard, whiteKingBitBoard, blackKingBitBoard, player);
    }*/
}
