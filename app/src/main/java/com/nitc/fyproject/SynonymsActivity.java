package com.nitc.fyproject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SynonymsActivity extends AppCompatActivity {
    Thesaurus.GetSynonymsTask task;
    TextToSpeech t1;
    Button Read;

    private TextView sentenceTextView;
    private String recognisedText = "This is a sample sentence for reading tracking.";

    private int currentWordIndex = 0;
    private int highlightedWordStartIndex = 0;
    private int highlightedWordEndIndex = 0;

    private Handler scrollHandler = new Handler();
    private Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            sentenceTextView.scrollTo((int) (sentenceTextView.getScrollX() + sentenceTextView.getTextSize()), 0);
            scrollHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synonyms);

        sentenceTextView = findViewById(R.id.text_view);
        recognisedText = getIntent().getStringExtra("recognisedText");

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });



        StringBuilder str = new StringBuilder();
        for (int i = 0; i < recognisedText.length(); i++) {
            if(recognisedText.charAt(i) != '\n'){
                str.append(recognisedText.charAt(i));
            }
        }
        recognisedText = str.toString();
        Toast.makeText(this, recognisedText, Toast.LENGTH_SHORT).show();
        ArrayList<String> hardWords = null;
        try {
            hardWords = HardWords(recognisedText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] list1 = new String[hardWords.size()];
        for(int i = 0; i < hardWords.size(); i++){
            list1[i] = hardWords.get(i);
        }
        RecyclerView recyclerView = findViewById(R.id.list_view);
        Adapter adapter = new Adapter(this, new ArrayList<String>(Arrays.asList(recognisedText.split(" "))));
        recyclerView.setLayoutManager(new LinearLayoutManager(SynonymsActivity.this, LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(adapter);

        Read = findViewById(R.id.read);
        Read.setOnClickListener(view -> {
            try {
                sentenceTextView.setText(recognisedText);
                highlightCurrentWord(recognisedText);
                scrollHandler.postDelayed(scrollRunnable, 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private ArrayList<String> HardWords(String recognisedText) throws IOException {
        List<String> commonWords = new CommonWords(getApplicationContext()).readLine();
        ArrayList<String> words = new ArrayList<>();
        String[] arr = recognisedText.split(" ");
        for (String s : arr) {
            if (!commonWords.contains(s.toLowerCase(Locale.ROOT))) {
                words.add(s);
            }
        }
        return words;
    }

    private void highlightCurrentWord(String sentence) throws InterruptedException {
        String[] words = sentence.split(" ");
        String currentWord = words[currentWordIndex];
        t1.speak(currentWord, TextToSpeech.QUEUE_ADD, null);


        int wordStartIndex = sentence.indexOf(currentWord, highlightedWordStartIndex);
        int wordEndIndex = wordStartIndex + currentWord.length();

        SpannableStringBuilder builder = new SpannableStringBuilder(sentence);
        builder.setSpan(new BackgroundColorSpan(Color.YELLOW), wordStartIndex, wordEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sentenceTextView.setText(builder);

        highlightedWordStartIndex = wordStartIndex;
        highlightedWordEndIndex = wordEndIndex;
        Thread.sleep(1000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            currentWordIndex++;
            if (currentWordIndex < recognisedText.split(" ").length) {
                try {
                    highlightCurrentWord(recognisedText);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                scrollHandler.removeCallbacks(scrollRunnable);
            }
        }
        return super.onTouchEvent(event);
    }

}