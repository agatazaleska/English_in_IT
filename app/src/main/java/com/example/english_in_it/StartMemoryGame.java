package com.example.english_in_it;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.*;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class StartMemoryGame extends AppCompatActivity {
    private HashMap<String, String> glossary;
    private TableLayout table;
    private TableRow row;
    private int total_cards = 0;
    private int columns = 4;
    private int buttonHeight = 308;
    private int paddingLeftAndRight = 15;
    private int paddingTopAndBottom = 0;
    private TextView[] buttons;
    private final Random randomGenerator = new Random();
    private boolean purgatory = false;
    private int level = 0; // liczba par do dopasowania
    private float correctAnswers = 0;
    private float totalAnswers = 0;
    private int pairsLeftToMatch = 0;
    private String[] board;
    private Boolean shown = true;
    private int firstCard = -1;
    private int secondCard = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        setTheme(Utils.getTheme(pref.getString("theme", null)));
        setContentView(R.layout.activity_memory_game);

        glossary = (HashMap<String, String>) getIntent().getSerializableExtra("glossary");

        pairsLeftToMatch = glossary.size();
        total_cards = 2 * glossary.size();
        buttons = new TextView[total_cards];
        board = new String[total_cards];

        ArrayList<Pair<String, Boolean>> cards = new ArrayList<>(); // (karta, czy_użyta)
        for (String key: glossary.keySet()) {
            cards.add(new Pair<>(key, false));
            cards.add(new Pair<>(glossary.get(key), false));
        }

        table = findViewById(R.id.tableLayout);

        //buttonWidth = 250;//Math.round(getResources().getDimension(R.dimen.button_width));
        //buttonHeight = 300;//Math.round(getResources().getDimension(R.dimen.button_height));
        //System.out.println(buttonHeight + " "+ buttonWidth);
        //rowWidth = Math.round(getResources().getDimension(R.dimen.row_width));

        // todo: XD
        //row = new TableRow(this);
        //row.setWeightSum(4);
//        table.addView(row, new ViewGroup.LayoutParams(
//               rowWidth, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
//        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                1.0f
//        );
//        LinearLayout.LayoutParams paramBtn = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                1.0f
//        );
       // row.setLayoutParams(param);
        row = (TableRow) LayoutInflater.from(this).
                inflate(R.layout.table_row_template, null);

//        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
//                TableRow.LayoutParams.WRAP_CONTENT, 1f));
        row.setPadding(paddingLeftAndRight, paddingTopAndBottom,
                paddingLeftAndRight, paddingTopAndBottom);
        table.addView(row);
        int buttonsInCurrentRow = 0;
        for (int i = 0; i < total_cards; i++) {
            int random_card = randomGenerator.nextInt(total_cards);
            while (cards.get(random_card).second) { // while czy_użyta == true
                random_card = randomGenerator.nextInt(total_cards); // losowanie nowej karty
            }

            if (buttonsInCurrentRow == columns) {
                row = (TableRow) LayoutInflater.from(this).
                        inflate(R.layout.table_row_template, null);
//                row = new TableRow(this);
//                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
//                        TableRow.LayoutParams.WRAP_CONTENT, 1f));
                //table.addView(row, new ViewGroup.LayoutParams(
                 //       rowWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                //row.setLayoutParams(param);
               // row.setWeightSum(4);
                row.setPadding(paddingLeftAndRight, paddingTopAndBottom,
                        paddingLeftAndRight, paddingTopAndBottom);
                table.addView(row);
                buttonsInCurrentRow = 0;
            }

            board[i] = cards.get(random_card).first;

            //buttons[i] = (TextView)getLayoutInflater().inflate(R.layout.button_template, null);
            buttons[i] = (TextView) LayoutInflater.from(this).
                    inflate(R.layout.button_template, null);
            //buttons[i] = new TextView(this);
            buttons[i].setText(cards.get(random_card).first);
            //buttons[i].setTextSize(buttonTextSize);
            buttons[i].setId(i);
            //buttons[i].setSingleLine(false);
            //buttons[i].setMaxLines(20);
            //buttons[i].setHeight(300);
            //buttons[i].setLayoutParams(paramBtn);
            //buttons[i].setEms(4);
            //buttons[i].setMinHeight(200);
//            buttons[i].setLayoutParams(new ViewGroup.LayoutParams(
//                    250, 300));
                    //ViewGroup.LayoutParams.WRAP_CONTENT));
            //row.addView(buttons[i]);//, buttonWidth, buttonHeight);
            //buttons[i].setWidth(250);
            //buttons[i].setHeight(310);
            //row.addView(buttons[i]);
            row.addView(buttons[i], new TableRow.LayoutParams(0,
                    buttonHeight, 1f));
                    //TableRow.LayoutParams.MATCH_PARENT, 1f));

            cards.set(random_card, new Pair<>(cards.get(random_card).first, true));
            buttonsInCurrentRow++;
        }

        for (int i = 0; i < total_cards; i++) {
            int finalI = i;
            buttons[i].setOnClickListener(view -> {
                if (purgatory) {
                    switchSpot(firstCard);
                    switchSpot(secondCard);
                    firstCard = -1;
                    secondCard = -1;
                    purgatory = false;
                }

                if (shown) {
                    hideFields();
                } else {
                    switchSpot(finalI);
                    if (firstCard == -1) {
                        firstCard = finalI;
                    } else {
                        totalAnswers++;
                        if ((!Objects.equals(glossary.get(board[firstCard]), board[finalI]) &&
                                !Objects.equals(glossary.get(board[finalI]), board[firstCard])) ||
                                firstCard == finalI) { // niepoprawne
                            secondCard = finalI;
                            purgatory = true;
                        } else { // poprawne
                            // todo: też można by jakoś ładniej ewentualnie kiedyś zrobić
                            board[finalI] = "done";
                            board[firstCard] = "done";
                            correctAnswers++;
                            pairsLeftToMatch -= pairsLeftToMatch; // todo debug
                            checkWin();
                            firstCard = -1;
                        }
                    }
                }
            });
        }
    }

    public void hideFields() {
        for (int i = 0; i < total_cards; i++){
            buttons[i].setText("");
        }
        shown = false;
    }

    public void switchSpot(int i) {
        if (!Objects.equals(board[i], "done")) {
            if (Objects.equals(buttons[i].getText(), "")){
                buttons[i].setText(board[i]);
            } else {
                buttons[i].setText("");
            }
        }
    }

    public void checkWin() {
        if (pairsLeftToMatch > 0) {
            return;
        }
        winner();
    }

    public void winner() {
        Intent intent = new Intent(StartMemoryGame.this, MemoryWin.class);
        int score = Math.max(Math.round(correctAnswers * 100 / totalAnswers), 0);
        intent.putExtra("score", Integer.toString(score));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu:
                Intent settings_intent = new Intent(StartMemoryGame.this, Settings.class);
                startActivity(settings_intent);
                return true;
            case R.id.home_menu:
                Intent home_intent = new Intent(StartMemoryGame.this, StartListActivity.class);
                startActivity(home_intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}