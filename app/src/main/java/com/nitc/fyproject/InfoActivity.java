package com.nitc.fyproject;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class InfoActivity extends AppCompatActivity {
    private String[] words;
    private int currentWordIndex = 0;
    private TextToSpeech textToSpeech;

    private TextView sentenceTextView;
    private TextView currentWordTextView;
    private LinearLayout synonymsLayout;
    private TextView synonymsTextView;
    private ImageView imageView;
    private Button previousButton;

    private Button play, pause;
    private Button nextButton;
    private boolean speaking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        sentenceTextView = findViewById(R.id.sentence_textview);
        currentWordTextView = findViewById(R.id.current_word_textview);
        synonymsTextView = findViewById(R.id.synonyms_textview);
        imageView = findViewById(R.id.imageview);
        previousButton = findViewById(R.id.previous_button);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        nextButton = findViewById(R.id.next_button);
        synonymsLayout = findViewById(R.id.synonyms_layout);

        String sentence = getIntent().getStringExtra("recognisedText");
        words = sentence.split(" ");
        sentenceTextView.setText(sentence);
        speaking = false;

        textToSpeech = new TextToSpeech(this,  new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        Runnable runnable = new Runnable() {
            public void run() {
                while(speaking){
                    final String word = words[currentWordIndex++];
                    textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                synonymsLayout.setVisibility(View.VISIBLE);
                                currentWordTextView.setText(word);
                                synonymsTextView.setText(getSynonyms(word));
                                imageView.setImageResource(getImageResource(word));
                            }
                        });
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if(currentWordIndex == words.length){
                        currentWordIndex = 0;
                        speaking = false;
                    }
                }
            }
        };
        Thread read = new Thread(runnable);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakWord(-1);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speaking = true;
                read.start();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speaking = false;
                speakWord(10);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakWord(1);
            }
        });
    }

    private void speakWord(int i) {
         for(; currentWordIndex < words.length ; currentWordIndex++) {
             String word = words[currentWordIndex];
             if (!speaking) {
                 break;
             }
             textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
             try {
                 Thread.sleep(1000);
                 synonymsLayout.setVisibility(View.VISIBLE);
                 currentWordTextView.setText(word);
                 synonymsTextView.setText(getSynonyms(word));
                 imageView.setImageResource(getImageResource(word));
             } catch (InterruptedException e) {
                 throw new RuntimeException(e);
             }

         }
//        currentWordTextView.setText(word);
//        synonymsLayout.setVisibility(View.VISIBLE);
//        synonymsTextView.setText(getSynonyms(word));
//        imageView.setImageResource(getImageResource(word));
    }

    private String getSynonyms(String word) {
        // Get synonyms for the word using an API or a local database
        return "synonym1, synonym2, synonym3";
    }

    private int getImageResource(String word) {
        // Get the image resource for the word using an API or a local database
        return R.drawable.car;
    }

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
    }
}