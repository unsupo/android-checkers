package arndt.com.checkers;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import arndt.com.checkers.objects.Node;
import arndt.com.checkers.objects.Pair;

import static arndt.com.checkers.Checkers.randBetween;

public class BitCheckers {
    public static void print(BitCheckers checkers){
        String whites = fill(Long.toBinaryString(checkers.whiteBitBoard)),
                whiteKings = fill(Long.toBinaryString(checkers.whiteKingBitBoard)),
                blackKings = fill(Long.toBinaryString(checkers.blackKingBitBoard)),
                blacks = fill(Long.toBinaryString(checkers.blackBitBoard));
        for (int i = -1; i < 8; i++) {
            if(i>-1)
                System.out.print(i);
            else
                System.out.print(" ");
            for (int j = 0; j < 8; j++) {
                System.out.print("|");
                if(i==-1){
                    System.out.print(j);
                    continue;
                }
                int v = i*8+j;
                if(whites.charAt(v) == '1')
                    System.out.print("w");
                else if(blacks.charAt(v) == '1')
                    System.out.print("b");
                else if(whiteKings.charAt(v) == '1')
                    System.out.print("W");
                else if(blackKings.charAt(v) == '1')
                    System.out.print("B");
                else
                    System.out.print(" ");
            }
            System.out.println("|");
        }
        System.out.println();
    }
    private static String fill(String v){
        if(v.length() == 64)
            return v;
        StringBuilder vBuilder = new StringBuilder(v);
        while (vBuilder.length()<64)
            vBuilder.insert(0, 0);
        v = vBuilder.toString();
        return v;
    }
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

    public BitCheckers() {
//        init();
    }

    public BitCheckers(long whiteBitBoard, long blackBitBoard, long whiteKingBitBoard, long blackKingBitBoard, int player) {
        this.whiteBitBoard = whiteBitBoard;
        this.blackBitBoard = blackBitBoard;
        this.whiteKingBitBoard = whiteKingBitBoard;
        this.blackKingBitBoard = blackKingBitBoard;
        this.player = player;
//        allMovesBitBoard = getAllMovesBitBoard();
    }
    public int getVictoryState(){
        return (whiteBitBoard|whiteKingBitBoard)==0?BLACK:(blackBitBoard|blackKingBitBoard)==0?WHITE:INVALID;
    }

    public int[][] getBoard() {
        String whites = fill(Long.toBinaryString(whiteBitBoard)),
                whiteKings = fill(Long.toBinaryString(whiteKingBitBoard)),
                blackKings = fill(Long.toBinaryString(blackKingBitBoard)),
                blacks = fill(Long.toBinaryString(blackBitBoard));
        int[][] board = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int v = i*8+j;
                if(whites.charAt(v) == '1')
                    board[i][j]=Checkers.WHITE;
                else if(blacks.charAt(v) == '1')
                    board[i][j]=Checkers.BLACK;
                else if(whiteKings.charAt(v) == '1')
                    board[i][j]=Checkers.WHITE_KING;
                else if(blackKings.charAt(v) == '1')
                    board[i][j]=Checkers.BLACK_KING;
                else
                    board[i][j]=Checkers.NOTHING;//// TODO: 2/5/18 program draws, if no moves left
                // TODO: 2/5/18 Save history of move both on crash and after game ends
            }
        }
        return board;
    }

    public void makeMove(Pair<Integer, Integer> first, Pair<Integer, Integer> second) {
        movePiece(first.getFirst(),first.getSecond(),second.getFirst(),second.getSecond());
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

    List<Pair<Pair<Integer,Integer>,Pair<Integer,Integer>>> allMovesMade = new ArrayList<>();
    int moves = 0, movesWithoutAnything = 0;
    /**
     * moves a piece from (x1,y1) to (x2,y2) if it's a valid move
     *  (meaning it's the correct player's piece, the move doesn't go onto of a player's piece, ... )
     * and sets the player to the next player to go
     * if a jump occurs next player will be the same player to go, otherwise it will be the other player.
     * @param x1 x of the piece to move
     * @param y1 y of the piece to move
     * @param x2 x of the location to move the piece
     * @param y2 y of the location to move the piece
     * @throws IllegalArgumentException if any of the 4 params are outside of this bounds: [0,7]
     * @return -1 if invalid move or the next players turn
     */
    public int movePiece(int x1, int y1, int x2, int y2){
        if(isOutOfBounds(new int[]{x1,y1,x2,y2}))
            throw new IllegalArgumentException("value(s) (is/are) out of bounds [0,7]: "+x1+","+y1+","+x2+","+y2);
        long move1Mask = allMovesLookup[x1][y1], move2Mask = allMovesLookup[x2][y2];

        long bitboard = whiteBitBoard, oBitboard = blackBitBoard, oKingBitboard = blackKingBitBoard,
                moves, jumpMoves = -1, kingMask = blackKingMask, kingBitBoard = whiteKingBitBoard;
        if(player == BLACK) {
            bitboard = blackBitBoard;
            oBitboard = whiteBitBoard;
            kingMask = whiteKingMask;
            kingBitBoard = blackKingBitBoard;
            oKingBitboard = whiteKingBitBoard;
            moves = getAllMovesBlacks();
            jumpMoves = getAllJumpMovesBlacks(moves);
        }else {
            moves = getAllMovesWhites();
            jumpMoves = getAllJumpMovesWhites(moves);
        }
        long mm = getMoves(move1Mask), kingJumpingMoves,
                jumpingMoves = (player == WHITE ?
                        ((((move1Mask >>> 7) &oBitboard) >>> 7) | (((move1Mask >>> 9) & oBitboard) >>> 9)) :
                        ((((move1Mask << 7) & oBitboard) << 7) | (((move1Mask << 9) & oBitboard) << 9)))
                        &~bitboard&~oBitboard&~whiteKingBitBoard&~blackKingBitBoard;
        boolean isKing = (kingBitBoard | move1Mask) == kingBitBoard;
        if(isKing){
            moves = getKingMoves(kingBitBoard);
            mm = getKingMoves (move1Mask);
            jumpMoves = moves&oBitboard;//test and do it above ~getPlayerBoard() for oBitboard
            jumpingMoves = (((((move1Mask >>> 7) &oBitboard) >>> 7) | (((move1Mask >>> 9) &oBitboard) >>> 9))|
                    ((((move1Mask << 7) &oBitboard) << 7) | (((move1Mask << 9) &oBitboard) << 9)))
                    &~bitboard&~oBitboard&~whiteKingBitBoard&~blackKingBitBoard;
            jumpingMoves&=allMovesBitBoard;
        }
        long kingJumpMoves = moves&oKingBitboard;
        if(isKing)
            kingJumpingMoves = getKingMoves(kingJumpMoves)&~bitboard&~oBitboard&~whiteKingBitBoard&~blackKingBitBoard;
        else
            kingJumpingMoves = getMoves(kingJumpMoves)&~bitboard&~oBitboard&~whiteKingBitBoard&~blackKingBitBoard;
        boolean jumped = false, kingJump = false;
        if((moves|move2Mask)==moves || jumpMoves != 0 || kingJumpMoves != 0){/*Move is normal and valid or There are jump moves*///(jumpMoves|move2Mask)!=jumpMoves
            long b = oBitboard;
            if((kingJumpingMoves | move2Mask) == kingJumpingMoves) {
                b = oKingBitboard;
                jumpingMoves = kingJumpingMoves;
                kingJump = true;
            }
            if((jumpMoves != 0 || kingJumpMoves!=0) && (jumpingMoves | move2Mask) == jumpingMoves){
                if((((move1Mask>>>14)&allMovesBitBoard)&move2Mask)==move2Mask)
                    b&=~(move1Mask>>>7);
                if((((move1Mask>>>18)&allMovesBitBoard)&move2Mask)==move2Mask)
                    b&=~(move1Mask>>>9);
                if((((move1Mask<<14)&allMovesBitBoard)&move2Mask)==move2Mask)
                    b&=~(move1Mask<<7);
                if((((move1Mask<<18)&allMovesBitBoard)&move2Mask)==move2Mask)
                    b&=~(move1Mask<<9);
                jumped = true;
            }
            else if((moves|move2Mask)!=moves)
                return INVALID;
            if(jumped) {
                if (kingJump)
                    oKingBitboard = b;
                else
                    oBitboard = b;
            }
        }else
            return INVALID;

        boolean isGotKinged = false;
        if(!jumped && (mm|move2Mask)!=mm)
            return INVALID;
        if(!isKing)
            bitboard &= ~move1Mask;
        else
            kingBitBoard &= ~move1Mask;
        if((kingMask|move2Mask)==kingMask) {//if it gets kinged
            kingBitBoard |= move2Mask;
            isGotKinged = true;
        }else {
            if(!isKing)
                bitboard |= move2Mask;
            else
                kingBitBoard |= move2Mask;
        }

        if(player == BLACK) {
            blackBitBoard = bitboard;
            whiteBitBoard = oBitboard;
            blackKingBitBoard = kingBitBoard;
            whiteKingBitBoard = oKingBitboard;
        }else {
            whiteBitBoard = bitboard;
            blackBitBoard = oBitboard;
            whiteKingBitBoard = kingBitBoard;
            blackKingBitBoard = oKingBitboard;
        }
        long newBitboardJumpMoves;
        long ms = move2Mask,ob=oBitboard,okb=oKingBitboard;
        if(!isKing) {
            if(player==WHITE)
                newBitboardJumpMoves = (ms>>>7&ob)>>>7|(ms>>>9&ob)>>>9|(ms>>>7&okb)>>>7|(ms>>>9&okb)>>>9;
            else
                newBitboardJumpMoves = (ms<<7&ob)<<7|(ms<<9&ob)<<9|(ms<<7&okb)<<7|(ms<<9&okb)<<9;
        }else
            newBitboardJumpMoves = (ms<<7&ob)<<7|(ms<<9&ob)<<9|(ms<<7&okb)<<7|(ms<<9&okb)<<9|(ms>>>7&ob)>>>7|(ms>>>9&ob)>>>9|(ms>>>7&okb)>>>7|(ms>>>9&okb)>>>9;
        newBitboardJumpMoves&=allMovesBitBoard&~whiteBitBoard&~whiteKingBitBoard&~blackKingBitBoard&~blackBitBoard;
        moves++;
        allMovesMade.add(new Pair<>(new Pair<>(x1, y1), new Pair<>(x2, y2)));
        if(!isGotKinged && !jumped)
            movesWithoutAnything++;
        else
            movesWithoutAnything = 0;
        if(!jumped ||
                newBitboardJumpMoves == 0)
            this.player = player == WHITE ? BLACK : WHITE;
        return player;
    }

    private boolean isOutOfBounds(int value) {
        if(value > 7 || value < 0)
            return true;
        return false;
    }private boolean isOutOfBounds(int[] values){
        for(int v : values)
            if(isOutOfBounds(v))
                return true;
        return false;
    }
    public long getPlayerBoard() {
        return player == WHITE ? getWhiteBitBoard() : getBlackBitBoard();
    }

    public long getAllMovesBitBoard() {
        return allMovesBitBoard;
    }

    public long getWhiteBitBoard() {
        return whiteBitBoard;
    }

    public void setWhiteBitBoard(long whiteBitBoard) {
        this.whiteBitBoard = whiteBitBoard;
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

    private static long parseLong(String s, int base) {
        return new BigInteger(s, base).longValue();
    }
    private static long parseLong(String s) {
        return parseLong(s, 2);
    }

    /**
     * get moves for normal pieces first, then get moves for king pieces
     * @return all possible moves each piece can make for whoever's turn it is
     */
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getAllPlayerMoves(){
        long moves = getAllMoves(), startingPoint, kJump = getAllJumpMoves(player==WHITE?getBlackKingBitBoard():getWhiteKingBitBoard(),moves),
                jump = getAllPlayerJumpMoves(moves), jumpStartingPoint = getMoves(jump,player == WHITE) & getPlayerBoard(),
                kJumpStartingPoint = getMoves(kJump,player == WHITE) & getPlayerBitboard();
        moves&=~jump;
        startingPoint = getMoves(moves, player == WHITE) & getPlayerBoard();
        List<Pair<Pair<Integer,Integer>,Pair<Integer,Integer>>> allMoves = new ArrayList<>(), jumpMoves = new ArrayList<>();
        //add pawn jump pawn moves
        addMoves(jumpStartingPoint,false, true, jumpMoves);
        //add pawn jump king moves
        addMoves(kJumpStartingPoint,false, true, jumpMoves);
        //add normal, no jump, moves
        addMoves(startingPoint,false,false, allMoves);
        long kingMoves = getAllKingMoves();
        long kingStartingPoint, pKingJump = kingMoves&(player==WHITE?getBlackBitBoard():getWhiteBitBoard()),
                kingJump = kingMoves&(player==WHITE?getBlackKingBitBoard():getWhiteKingBitBoard()),
                kingJumpStartingPoint = getKingMoves(kingJump) & getPlayerKingBoard(),
                pKingJumpStartingPoint = getKingMoves(pKingJump) & getPlayerKingBoard();
        kingMoves&=~kingJump;
        kingStartingPoint = getKingMoves(kingMoves)&getPlayerKingBoard();
        //king jump pawn moves
        addMoves(pKingJumpStartingPoint,true, true, jumpMoves);
        //king jump king moves
        addMoves(kingJumpStartingPoint,true, true, jumpMoves);
        //normal king moves
        addMoves(kingStartingPoint,true,false, allMoves);
        //this forces you and computer to make jump moves, official ruling
        if(jumpMoves.isEmpty())
            return allMoves;
        return jumpMoves;
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
    private int maxDepth = 12;
    private Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>,Double> node;
    public Pair<Pair<Integer,Integer>,Pair<Integer,Integer>> getComputerMove(){
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves = getAllPlayerMoves();
        node = new Node(null,null);
        node.getValues().put("moves",moves);
        List<Node> children = new ArrayList<>();
        for(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> m : moves)
            children.add(new Node<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Double>(m,null));
        node.setChildren(children);
//        double v = minimax(this,maxDepth, maxDepth,-Double.MAX_VALUE,Double.MAX_VALUE);
        double v = alphaBeta(this,maxDepth, maxDepth,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> bestMoves = new ArrayList<>();
        for(Node child : node.getChildren())
            if(child.getValue() != null && child.getValue().equals(v))
                bestMoves.add((Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>) child.getData());
        if(bestMoves.isEmpty())
            return null;
        return bestMoves.get(randBetween(0,bestMoves.size()-1));
    }

    private double alphaBeta(BitCheckers checkers, int depth, int absDepth, double alpha, double beta){
        if(checkers.movesWithoutAnything >= 40)
            return 0;//tie game//needs testing
        if(depth == 0 || checkers.getVictoryState() != INVALID)
            return checkers.getVictoryState()==INVALID?checkers.getBoardScore()-checkers.getPlayer()*moves/100.:
                    checkers.getPlayer() == checkers.getVictoryState() ? Double.MAX_VALUE*checkers.getPlayer():
                            -Double.MAX_VALUE*checkers.getPlayer();
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves;
        if(absDepth == maxDepth)
            moves = (List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>) node.getValues().get("moves");
        else
            moves = checkers.getAllPlayerMoves();
        if(moves.isEmpty()) //no moves left? then this player loses
            return checkers.getPlayer() * -Double.MAX_VALUE;
        double v = checkers.getPlayer() == WHITE ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        for(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> i : moves){
            BitCheckers c = checkers.clone();
            c.movePiece(i.getFirst().getFirst(),i.getFirst().getSecond(),i.getSecond().getFirst(),i.getSecond().getSecond());
            int newDepth = c.getPlayer() == checkers.getPlayer() ? depth : depth - 1;
            if(checkers.getPlayer() == WHITE) {
                v = Math.max(v, alphaBeta(c, newDepth, absDepth - 1, alpha, beta));
                alpha = Math.max(alpha,v);
            }else {
                v = Math.min(v, alphaBeta(c, newDepth, absDepth - 1, alpha, beta));
                beta = Math.min(beta,v);
            }
            if(absDepth == maxDepth) {
                double finalScore = v;
                for(Node n : node.getChildren())
                    if(n.getData().equals(i)){
                        n.setValue(finalScore);
                        break;
                    }
            }
            if(beta<= alpha)
                break;
        }
        return v;
    }

    private double minimax(BitCheckers checkers, int depth, int absDepth, double alpha, double beta) {
        if(checkers.movesWithoutAnything >= 40)
            return 0;//tie game//needs testing
        if(depth == 0 || checkers.getVictoryState() != INVALID)
            return checkers.getVictoryState()==INVALID?checkers.getBoardScore()-checkers.getPlayer()*moves/100.:
                    checkers.getPlayer() == checkers.getVictoryState() ? Double.MAX_VALUE*checkers.getPlayer():
                    -Double.MAX_VALUE*checkers.getPlayer();
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves;
        if(absDepth == maxDepth)
            moves = (List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>) node.getValues().get("moves");
        else
            moves = checkers.getAllPlayerMoves();
        if(moves.isEmpty()) //no moves left? then this player loses
            return checkers.getPlayer() * -Double.MAX_VALUE;
        double bestVal = checkers.getPlayer()*-Double.MAX_VALUE;
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
        BitCheckers bc =  new BitCheckers(whiteBitBoard, blackBitBoard, whiteKingBitBoard, blackKingBitBoard, player);
        bc.movesWithoutAnything = movesWithoutAnything;
        bc.moves = moves;
        return bc;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{ 'whiteBitBoard':"+whiteBitBoard+",'blackBitBoard':"+blackBitBoard+
                ",'whiteKingBitBoard':"+whiteKingBitBoard+",'blackKingBitBoard':"+blackKingBitBoard+",'player':"+player);
        builder.append("}");
        return builder.toString();
    }

    public String getPlayerString() {
        return player == WHITE ? "WHITE" : "BLACK";
    }

}
