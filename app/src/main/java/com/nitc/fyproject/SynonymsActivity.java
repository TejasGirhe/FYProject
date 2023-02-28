package com.nitc.fyproject;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SynonymsActivity extends AppCompatActivity {
    Thesaurus.GetSynonymsTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synonyms);

        String recognisedText = getIntent().getStringExtra("recognisedText");
        System.out.println("Input : " + recognisedText);

        ListView listView1 = findViewById(R.id.list_view_1);
        ListView listView2 = findViewById(R.id.list_view_2);

        ArrayList<String> hardWords = null;
        try {
            hardWords = HardWords(recognisedText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String[] list1 = new String[hardWords.size()];
        String[] list2 = new String[hardWords.size()];
        for(int i = 0; i < hardWords.size(); i++){
            list1[i] = hardWords.get(i);
        }
        System.out.println("Hardwords " + hardWords.toString());
//        System.out.println("Objects " + objects.toString());

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list1);
        listView1.setAdapter(adapter1);


        for(int i = 0; i < hardWords.size(); i++){
            int finalI = i;
            task = new Thesaurus.GetSynonymsTask(new Thesaurus.ThesaurusCallback() {
                @Override
                public void onSynonymsFetched(List<String> synonyms) {
                    if(synonyms.size()>0 && synonyms != null) {
                        list2[finalI] = synonyms.toString().substring(1,synonyms.toString().length()-1);
                    }
                    System.out.println(finalI + " " + synonyms.toString() + " " + list2[finalI]);
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, list2);
                    listView2.setAdapter(adapter2);
                }
            });
            task.execute(hardWords.get(i));
            System.out.println("List 2 : " + Arrays.toString(Arrays.stream(list2).toArray()));

        }
        System.out.println("List 2 : " + Arrays.toString(Arrays.stream(list2).toArray()));
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list2);
//        listView2.setAdapter(adapter2);
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

}