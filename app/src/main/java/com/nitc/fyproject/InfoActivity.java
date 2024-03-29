package com.nitc.fyproject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.Locale;

public class InfoActivity extends AppCompatActivity {
    private String[] words;
    private int currentWordIndex = 0;
    private ArFragment arFragment;
    private int highlightedWordStartIndex = 0;
    private int highlightedWordEndIndex = 0;
    private TextToSpeech textToSpeech;
    Thesaurus.GetSynonymsTask synonymsTask;
    private TextView sentenceTextView;
    private TextView currentWordTextView;
    private LinearLayout synonymsLayout;
    private TextView synonymsTextView;
    private ImageView imageView;
    private Button previousButton;
    String sentence = "";
    private Button play, pause;
    private Button nextButton;
    private boolean speaking;
    TextView def, fos;
    Thread read;
    ProgressDialog progressDialog;
    OnLoaded onLoaded;
    RecyclerView recyclerView;
    LinearLayout LoadLayout;
    Button LoadImage;
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
        def = findViewById(R.id.def);
        fos = findViewById(R.id.fos);
        LoadImage = findViewById(R.id.load_image);
        LoadLayout = findViewById(R.id.load_button);
        LoadLayout.setVisibility(View.INVISIBLE);

        sentence = getIntent().getStringExtra("recognisedText");
        char[] symbols = {'!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~', '\n'};
        for (char symbol : symbols) {
            sentence = sentence.replace(String.valueOf(symbol), " ");
        }

        words = sentence.split(" ");
        sentenceTextView.setText(sentence);
        speaking = false;

        progressDialog = new ProgressDialog(InfoActivity.this);
        progressDialog.setMessage("Loading Image...");

        recyclerView = findViewById(R.id.recycler);

        onLoaded = new OnLoaded() {
            @Override
            public void loaded(ArrayList<String> arrayList) {
                Toast.makeText(InfoActivity.this, arrayList.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                ImageAdapter adapter = new ImageAdapter(InfoActivity.this, arrayList);
                recyclerView.setAdapter(adapter);
            }
        };

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });


        Runnable runnable = new Runnable() {
            public void run() {
                while (speaking && currentWordIndex < words.length) {
                    final String word = words[currentWordIndex].trim();

                    runOnUiThread(() -> {
                        highlightCurrentWord(sentence);
                        synonymsLayout.setVisibility(View.VISIBLE);
                        currentWordTextView.setText(word);
                        getSynonyms(word);
                        updateWordUI(words[currentWordIndex]);
                    });


                    textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
                    while (textToSpeech.isSpeaking()) {
                        // Wait for the first word to finish speaking before moving on to the next one
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }

                    currentWordIndex++;
                    if (currentWordIndex >= words.length || currentWordIndex < 0) {
                        currentWordIndex = 0;
                        speaking = false;
                    }
                }
            }
        };
        read = new Thread(runnable);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speaking = true;
                if (currentWordIndex >= words.length) {
                    currentWordIndex = 0;
                }
                if (!read.isAlive()) {
                    read = new Thread(runnable);
                    read.start();
                } else {
                    synchronized (read) {
                        read.notify();
                    }
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentWordIndex--;
                speaking = false;
                read.interrupt();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentWordIndex > 0) {
                    currentWordIndex--;
                    highlightCurrentWord(sentence);
                    textToSpeech.speak(words[currentWordIndex], TextToSpeech.QUEUE_FLUSH, null, null);
                    updateWordUI(words[currentWordIndex]);
                    read.interrupt();
                    speaking = false;
                }

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentWordIndex >= words.length - 1) {
                    currentWordIndex = 0;
                } else {
                    currentWordIndex++;
                }
                highlightCurrentWord(sentence);
                textToSpeech.speak(words[currentWordIndex], TextToSpeech.QUEUE_FLUSH, null, null);
                updateWordUI(words[currentWordIndex]);
                read.interrupt();
                speaking = false;

            }
        });

    }

    private void highlightCurrentWord(String sentence) {
        String[] words = sentence.split(" ");
        String currentWord = words[currentWordIndex];

        int wordStartIndex = 0;

        int spaces = 0;
        for (int i = 0; i < sentence.length(); i++) {
            if (sentence.charAt(i) == ' ') {
                spaces++;
            }

            if (spaces == currentWordIndex) {
                if (spaces == 0) {
                    wordStartIndex = 0;
                    break;
                } else {
                    wordStartIndex = i + 1;
                    break;
                }
            }

        }
        int wordEndIndex = wordStartIndex + currentWord.length();

        SpannableStringBuilder builder = new SpannableStringBuilder(sentence);
        builder.setSpan(new BackgroundColorSpan(Color.BLUE), wordStartIndex, wordEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sentenceTextView.setText(builder);

        highlightedWordStartIndex = wordStartIndex;
        highlightedWordEndIndex = wordEndIndex;
    }

    private void updateWordUI(String word) {
        currentWordTextView.setText(word);
        getSynonyms(word);
    }

    private void getSynonyms(String word){
        try{
            synonymsTask = new Thesaurus.GetSynonymsTask(new Thesaurus.ThesaurusCallback() {
                @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
                @Override
                public void onSynonymsFetched(ArrayList<ArrayList<String>> List) {
                    synonymsTextView.setText("Synonyms : " + List.get(0).toString().substring(1, List.get(0).toString().length() - 1));
                    fos.setText("FOS : " + List.get(2).get(0).toString());
                    if(!(List.get(2).get(0).contains("conjuction") || List.get(2).get(0).contains("preposition") || List.get(2).get(0).contains("article")))
                    {
                        try{
                            def.setText("DEF : " + List.get(1).get(0).toString());
                        }catch (Exception e){
                            System.out.println(e.toString());
                        }
                    }

                    if(List.get(2).get(0).contains("noun") || List.get(2).get(0).contains("pronoun") || List.get(2).get(0).contains("verb"))
                    {
                        if(List.get(2).get(0).contains("pronoun"))
                        {
                            if(word.toLowerCase(Locale.ROOT).contains("he") || word.toLowerCase(Locale.ROOT).contains("him") || word.toLowerCase(Locale.ROOT).contains("her") || word.toLowerCase(Locale.ROOT).contains("she"))
                            {
                                getImageResource(word);
                            }
                            else
                            {
                                imageView.setImageDrawable(null);
                                LoadLayout.setVisibility(View.INVISIBLE);
                            }
                        }
                        else{
                            getImageResource(word);
                        }
                    }
                    else{
                        imageView.setImageDrawable(null);
                        LoadLayout.setVisibility(View.INVISIBLE);
                    }
                }
            });
            synonymsTask.execute(word);
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private void getImageResource(String word) {
        word = word.trim();
        Toast.makeText(this, word, Toast.LENGTH_SHORT).show();
        LoadLayout.setVisibility(View.VISIBLE);
        progressDialog.show();
        pause.performClick();
        new ImageGenerator(InfoActivity.this).generate(word, 600, 600, 2, onLoaded);
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