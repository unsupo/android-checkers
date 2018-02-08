package arndt.com.checkers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import arndt.com.checkers.objects.Pair;

import static arndt.com.checkers.Checkers.BLACK;
import static arndt.com.checkers.Checkers.BLACK_KING;
import static arndt.com.checkers.Checkers.WHITE;
import static arndt.com.checkers.Checkers.WHITE_KING;

public class MainActivity extends AppCompatActivity {
    private static Gson gson = new GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .create();

    public static Gson getGson(){
        return gson;
    }
    BitCheckers checkers;

    private TableLayout tableLayout;
    public TableLayout getTableLayout(){
        if(tableLayout == null) {
            tableLayout = findViewById(R.id.BoardLayout);
        }
        return tableLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reset();
    }

    private int userPiece = WHITE;
    int player = BitCheckers.WHITE;
    int wins = 0, loses = 0, ties = 0;
    private void reset(){
        if(textViews == null)
            createGrid(8,8);
        //    public BitCheckers(long whiteBitBoard, long blackBitBoard, long whiteKingBitBoard, long blackKingBitBoard, int player) {
        checkers = new BitCheckers();
        updateUI();
    }

    List<TextView> allowedMoves = new ArrayList<>();
    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.turn)).setText(
                        checkers.getPlayerString()+"'s turn, current score: "+checkers.getBoardScore()+", wins: "+wins+", loses: "+loses+", ties: "+ties);
                if(checkers.getVictoryState() != BitCheckers.INVALID || checkers.getAllPlayerMoves().isEmpty()
                        || checkers.movesWithoutAnything >= 40){
                    String v = "", n = "";
                    if(checkers.getVictoryState() != player) {
                        v = "You Lose, Black Wins";
                        n="loses";
                        loses++;
                    }else if(checkers.getVictoryState() == player) {
                        v = "You Win, White Wins";
                        n="wins";
                        wins++;
                    }else if(checkers.movesWithoutAnything >= 40) {
                        v = "Draw, no one wins";
                        n = "draws";
                        ties++;
                    }
                    writeToInternalStorage(
                            n+"-"+UUID.randomUUID().toString().replace("-",""),
                            getGson().toJson(checkers.allMovesMade)
                    );
                    player = player == BitCheckers.WHITE ? BitCheckers.BLACK : BitCheckers.WHITE;
                    if(player == BitCheckers.WHITE)
                        getTableLayout().setRotation(180);
                    else
                        getTableLayout().setRotation(0);
                    for (int i = 0; i < textViews.length; i++) {
                        for (int j = 0; j < textViews[i].length; j++) {
                            if(player == BitCheckers.WHITE)
                                textViews[i][j].setRotation(180);
                            else
                                textViews[i][j].setRotation(0);
                        }
                    }
                    ((TextView)findViewById(R.id.turn)).setText(v);
                    setDelayed(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reset();
                            setDelayed(false);
                        }
                    }, 2000);
                }
                for(TextView tv : allowedMoves)
                    tv.setBackground(originalDrawable);
                allowedMoves.clear();
                if(checkers.getPlayer() == player) {
                    for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> s : checkers.getAllPlayerMoves()) {
                        TextView v = textViews[s.getFirst().getFirst()][s.getFirst().getSecond()];
                        allowedMoves.add(v);
                        addBorder(v, Color.WHITE);
                    }
                }
            }
        });
        for (int i = 0; i < checkers.getBoard().length; i++) {
            for (int j = 0; j < checkers.getBoard()[i].length; j++) {
                String v = " ";
                switch (checkers.getBoard()[i][j]) {
                    case WHITE:
                        v = "w"; break;
                    case WHITE_KING:
                        v = "W"; break;
                    case BLACK:
                        v = "b"; break;
                    case BLACK_KING:
                        v = "B"; break;
                }
                textViews[i][j].setText(v);
            }
        }
        if(player != checkers.getPlayer()){
            setAiIsThinking(true);
            ((TextView)findViewById(R.id.turn)).setTextColor(Color.RED);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> cmove = null;
                    try {
                         cmove = checkers.getComputerMove();
                    }catch (Exception | Error e){
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        writeToInternalStorage(
                                "exceptions-"+ UUID.randomUUID().toString().replace("-",""),
                                        sw.toString()+"\n"+checkers.toString()
                                );
                        throw e;
                    }
                    if(cmove == null){
                        setAiIsThinking(false);
                        return;
                    }
                    checkers.makeMove(cmove.getFirst(),cmove.getSecond());
                    addBorder(textViews[cmove.getFirst().getFirst()][cmove.getFirst().getSecond()],Color.rgb(255,165,0));
                    addBorder(textViews[cmove.getSecond().getFirst()][cmove.getSecond().getSecond()],Color.YELLOW);
                    for (Pair<Integer, Integer> s : getAiLastMoves())
                        textViews[s.getFirst()][s.getSecond()].setBackground(originalDrawable);
                    getAiLastMoves().clear();
                    getAiLastMoves().add(cmove.getFirst());
                    getAiLastMoves().add(cmove.getSecond());
                    updateUI();
                    setAiIsThinking(false);
                }
            });
        }
    }
    private boolean delayed = false;
    private synchronized boolean getDelayed(){
        return delayed;
    } private synchronized void setDelayed(boolean delayed){
        this.delayed = delayed;
    }
    private boolean aiIsThinking = false;
    private synchronized boolean getAiIsThinking(){
        return aiIsThinking;
    }private synchronized void setAiIsThinking(boolean aiIsThinking){
        this.aiIsThinking = aiIsThinking;
    }private List<Pair<Integer,Integer>> aiLastMoves = new ArrayList<>();
    private synchronized List<Pair<Integer, Integer>> getAiLastMoves(){
        return aiLastMoves;
    }

    private List<Pair<Integer,Integer>> selectedList = new ArrayList<>();
    private Pair<Integer,Integer> selected;
    private TextView[][] textViews;
    private void createGrid(int rows, int columns) {
        textViews = new TextView[rows][columns];
        for (int i = 0; i < rows; i++) {
            TableRow row = new TableRow(this);
            for (int j = 0; j < columns; j++) {
                TextView b = new TextView(this);
                b.setPadding(0,0,0,0);
                b.setMinHeight(getScreenHeight()/(rows+3));
                b.setMinWidth(0);
                b.setTextColor(Color.WHITE);
                b.setGravity(Gravity.CENTER);
                final int n = i, m = j;
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(getAiIsThinking() || getDelayed())
                            return;
                        Pair<Integer, Integer> per = new Pair<>(n, m);
                        if(selected != null && selected.equals(per)) {
                            clearSelected();
                            return;
                        }else if(selectedList.contains(per)){
                            checkers.makeMove(selected,per);
                            clearSelected();
                            updateUI();
                            return;
                        }
                        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves =
                                checkers.getAllPlayerMoves();
                        List<Pair<Integer,Integer>> cMoves = new ArrayList<>();
                        for(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> m : moves)
                            if(m.getFirst().equals(per))
                                cMoves.add(m.getSecond());
                        if(!cMoves.isEmpty()) {
                            clearSelected();
                            addBorder(v, Color.GREEN);
                            selected = new Pair<>(n,m);
                            for(Pair<Integer, Integer> s : cMoves) {
                                addBorder(textViews[s.getFirst()][s.getSecond()], Color.BLUE);
                                selectedList.add(s);
                            }
                        }
                    }
                });
                if(((i+j)+1)%2==0)
                    b.setBackgroundColor(Color.RED);
                else
                    b.setBackgroundColor(Color.BLACK);
                if(player == BitCheckers.WHITE)
                    b.setRotation(180);
                row.addView(b);
                textViews[i][j]=b;
            }
            getTableLayout().addView(row);
        }
        if(player == BitCheckers.WHITE)
            getTableLayout().setRotation(180);
    }

    private void clearSelected() {
        if(selected != null)
            textViews[selected.getFirst()][selected.getSecond()].setBackground(originalDrawable);
        selected = null;
        for(Pair<Integer,Integer> p : selectedList)
            textViews[p.getFirst()][p.getSecond()].setBackground(originalDrawable);
        selectedList.clear();
    }

    Drawable originalDrawable;
    public void addBorder(View view, int stroke){
        //use a GradientDrawable with only one color set, to make it a solid color
        GradientDrawable border = new GradientDrawable();
        if(view.getBackground() instanceof ColorDrawable) {
            originalDrawable = view.getBackground();
            border.setColor(((ColorDrawable) view.getBackground()).getColor()); //white background
        }else
            border.setColor(((GradientDrawable)view.getBackground()).getColor());
        border.setStroke(3, stroke); //black border with full opacity
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(border);
        } else {
            view.setBackground(border);
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void writeToInternalStorage(String fileName, String contents){
        // Create a file in the Internal Storage
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_APPEND);
            outputStream.write(contents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        writeToInternalStorage(
                "program_closed-"+ UUID.randomUUID().toString().replace("-",""),
                checkers.toString()
        );
    }
}
