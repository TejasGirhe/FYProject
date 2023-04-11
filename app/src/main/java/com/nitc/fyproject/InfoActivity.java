package com.nitc.fyproject;

import android.annotation.SuppressLint;
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

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoActivity extends AppCompatActivity {
    private String[] words;
    private int currentWordIndex = 0;
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

    private Button play, pause;
    private Button nextButton;
    private boolean speaking;
    TextView def, fos;
    Thread read;

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

        String sentence = getIntent().getStringExtra("recognisedText");
        words = sentence.split(" ");
        sentenceTextView.setText(sentence);
        speaking = false;

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
                    final String word = words[currentWordIndex];

                    runOnUiThread(() -> {
                        highlightCurrentWord(sentence);
                        synonymsLayout.setVisibility(View.VISIBLE);
                        currentWordTextView.setText(word);
                        getSynonyms(word);
                        getImageResource(word);
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
                speaking = false;
                // Interrupt the thread to make it stop
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
                if (currentWordIndex < words.length) {
                    currentWordIndex++;
                    highlightCurrentWord(sentence);
                    textToSpeech.speak(words[currentWordIndex], TextToSpeech.QUEUE_FLUSH, null, null);
                    updateWordUI(words[currentWordIndex]);
                    read.interrupt();
                    speaking = false;
                }
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
        getImageResource(word);

        String keyword = "car";
        String url = "https://api.sketchfab.com/v3/search?type=models&q=" + keyword;
        String apiKey = "your_api_key_here";
        InputStream inputStream = null;
        try {
            URL apiURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " + apiKey);
            inputStream = connection.getInputStream();
            StringBuilder response = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");
            String modelUrl = results.getJSONObject(0).getString("viewerUrl");
        } catch (Exception e) {
            System.out.println("Error in 3d model code");
        }

    }

    private void getSynonyms(String word) {
        synonymsTask = new Thesaurus.GetSynonymsTask(new Thesaurus.ThesaurusCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSynonymsFetched(ArrayList<ArrayList<String>> List) {
                synonymsTextView.setText("Synonyms : " + List.get(0).toString().substring(1, List.get(0).toString().length() - 1));
                fos.setText("FOS : " + List.get(2).get(0).toString());
                if(!(List.get(2).get(0).contains("conjuction") || List.get(2).get(0).contains("preposition") || List.get(2).get(0).contains("article")))
                    def.setText("DEF : " + List.get(1).get(0).toString());
            }
        });
        synonymsTask.execute(word);
    }

    private void getImageResource(String word) {

        loadWordImage(word);
        // Get the image resource for the word using an API or a local database
//        String imageUrl = "https://source.unsplash.com/800x600/?" + word;
//
//        Glide.with(this)
//                .load(imageUrl)
//                .into(imageView);
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

//    VImlsGmykNR8H9NYbJKdtdn4Q09DhPAIdvb3vsK7y1JcxbkmSeCjY7bt

    public void loadWordImage(String word) {
        String apiKey = "VImlsGmykNR8H9NYbJKdtdn4Q09DhPAIdvb3vsK7y1JcxbkmSeCjY7bt";
        String apiUrl = "https://api.pexels.com/v1/search?query=" + word + "&per_page=1&width=800&height=600"; // Replace with your Pexels API endpoint URL
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", apiKey)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    JSONObject json = new JSONObject(jsonData);
                    JSONArray photos = json.getJSONArray("photos");
                    if (photos.length() > 0) {
                        JSONObject photo = photos.getJSONObject(0);
                        String imageUrl = photo.getJSONObject("src").getString("medium");
                        runOnUiThread(() -> Picasso.get().load(imageUrl).into(imageView));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}