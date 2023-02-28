package com.nitc.fyproject;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommonWords{
    private Context mContext;

    public CommonWords(Context context) {
        this.mContext = context;
    }

    public List<String> readLine() {
        String path = "vocab.txt";
        List<String> mLines = new ArrayList<>();

        AssetManager am = mContext.getAssets();

        try {
            InputStream is = am.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null)
                mLines.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mLines;
    }
    public List<String> commonWords() throws IOException {
        List<String> words = new ArrayList<>();
        File file = new File("assets/vocab.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null)
            words.add(st);
        return words;
    }
}
