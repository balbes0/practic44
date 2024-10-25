package com.example.practic44;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Random;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor settingEditor;
    ImageButton changeTheme;
    Button button1, button2, button3, button4, button5, button6, button7, button8, button9, buttonRestart;
    TextView winsXTV, winsOTV, drawsTV;
    Boolean lastIsX = false;
    String[][] pole = new String[3][3];
    Button[] buttons;
    String mark;
    int winsX, winsO, draws;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeTheme = findViewById(R.id.switchThemeButton);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        buttonRestart = findViewById(R.id.restartButton);
        winsXTV = findViewById(R.id.winsX);
        winsOTV = findViewById(R.id.winsO);
        drawsTV = findViewById(R.id.draws);
        sharedPreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);

        if (!sharedPreferences.contains("MODE_NIGHT_ON") || !sharedPreferences.contains("winsX")) {
            settingEditor = sharedPreferences.edit();
            settingEditor.putBoolean("MODE_NIGHT_ON", false);
            settingEditor.putInt("winsX", 0);
            settingEditor.putInt("winsO", 0);
            settingEditor.putInt("draws", 0);
            settingEditor.apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "Первый запуск...", Toast.LENGTH_LONG).show();
        }else{
            setCurrentStats();
            setCurrentTheme();
        }

        if(!sharedPreferences.contains("MODE_NIGHT_ON")){
            changeTheme.setImageResource(R.drawable.icon_night);
        }else{
            if(!sharedPreferences.getBoolean("MODE_NIGHT_ON", false)){
                changeTheme.setImageResource(R.drawable.icon_night);
            }else{
                changeTheme.setImageResource(R.drawable.icon_sun);
            }
        }

        changeTheme.setOnClickListener(v -> {
            settingEditor = sharedPreferences.edit();
            if (!sharedPreferences.getBoolean("MODE_NIGHT_ON", false)){
                settingEditor.putBoolean("MODE_NIGHT_ON", true);
            }else{
                settingEditor.putBoolean("MODE_NIGHT_ON", false);
            }

            settingEditor.apply();
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
        });

        buttons = new Button[]{button1, button2, button3, button4, button5, button6, button7, button8, button9};
        for (Button button : buttons) {
            button.setOnClickListener(v -> onButtonClick((Button) v));
        }

        buttonRestart.setOnClickListener(this::bRestart);
    }

    public void onButtonClick(Button button){
        int index = IntStream.range(0, buttons.length).filter(i -> buttons[i] == button).findFirst().orElse(-1);
        if (index >= 0 && index < 9) {
            // Вычисление строки и столбца
            int row = (index / 3); // Делим на 3 для получения строки
            int col = (index % 3); // Остаток от деления на 3 для получения столбца

            if (lastIsX){
                mark = "0";
                lastIsX = false;
            } else {
                mark = "X";
                lastIsX = true;
            }

            pole[row][col] = mark;
            button.setText(mark);
            button.setEnabled(false);
            proverka();
        }
    }

    public void bRestart(View v){
        restart();
    }

    //Проверка на ничью или победу
    private void proverka() {
        // Проверка строк
        for (int i = 0; i < 3; i++) {
            if (pole[i][0] != null && pole[i][0].equals(pole[i][1]) && pole[i][1].equals(pole[i][2])) {
                announceWinner(pole[i][0]);
                return;
            }
        }

        // Проверка столбцов
        for (int i = 0; i < 3; i++) {
            if (pole[0][i] != null && pole[0][i].equals(pole[1][i]) && pole[1][i].equals(pole[2][i])) {
                announceWinner(pole[0][i]);
                return;
            }
        }

        // Проверка диагоналей
        if (pole[0][0] != null && pole[0][0].equals(pole[1][1]) && pole[1][1].equals(pole[2][2])) {
            announceWinner(pole[0][0]);
            return;
        }
        if (pole[0][2] != null && pole[0][2].equals(pole[1][1]) && pole[1][1].equals(pole[2][0])) {
            announceWinner(pole[0][2]);
            return;
        }

        // Проверка на ничью
        if (isBoardFull()) {
            announceDraw();
        }
    }

    //Функция для проверка на ничью
    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (pole[i][j] == null) {
                    return false; // Есть пустая ячейка
                }
            }
        }
        return true; // Нет пустых ячеек
    }

    //Объявление о победе одной из сторон
    private void announceWinner(String winner) {
        Toast.makeText(this, winner + " - выиграл!", Toast.LENGTH_LONG).show();
        settingEditor = sharedPreferences.edit();
        restart();
        if (winner.equals("X")){
            winsX += 1;
        } else {
            winsO += 1;
        }
        saveScores();
    }

    //Объявление ничьи
    private void announceDraw() {
        Toast.makeText(this, "Ничья!", Toast.LENGTH_LONG).show();
        restart();
        draws += 1;
        saveScores();
    }

    //Рестарт игры
    private void restart(){
        for (Button button : buttons) {
            button.setText("");
            button.setEnabled(true);
            pole = new String[3][3];
        }
    }

    //Выгружаем последнюю примененную тему и применяем
    private void setCurrentTheme() {
        if(!sharedPreferences.getBoolean("MODE_NIGHT_ON", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    //Выгружаем последние статы и применяем
    private void setCurrentStats() {
        SharedPreferences sharedPreferences = getSharedPreferences("GAME_RESULTS", MODE_PRIVATE);
        winsX = sharedPreferences.getInt("SCORE_X", 0);
        winsO = sharedPreferences.getInt("SCORE_O", 0);
        draws = sharedPreferences.getInt("DRAWS", 0);
        winsXTV.setText("Побед X: " + winsX);
        winsOTV.setText("Побед O: " + winsO);
        drawsTV.setText("Ничьих: " + draws);
    }

    //Сохраняем в память статы
    private void saveScores() {
        SharedPreferences sharedPreferences = getSharedPreferences("GAME_RESULTS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("SCORE_X", winsX);
        editor.putInt("SCORE_O", winsO);
        editor.putInt("DRAWS", draws);
        editor.apply();
        winsXTV.setText("Побед X: " + winsX);
        winsOTV.setText("Побед O: " + winsO);
        drawsTV.setText("Ничьих: " + draws);
    }
}