package com.nitc.fyproject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class WordsApi {
    public void getInfo(String word){

        ArrayList<ArrayList<String>> list = new ArrayList<>();
        ArrayList<String> synonyms = new ArrayList<>();
        ArrayList<String> defList = new ArrayList<String>();
        String partOfSpeech = "";
        try {
            URL url = new URL("https://wordsapiv1.p.mashape.com/words/" + word);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONArray arr = jsonObject.getJSONObject("meta").getJSONArray("syns");
            JSONArray def = jsonObject.getJSONArray("shortdef");
            for(int i = 0; i < def.length(); i++){
                defList.add(def.getString(i));
            }
            for(int i = 0; i < arr.length(); i++){
                JSONArray array =  arr.getJSONArray(i);
                for(int j = 0; j < array.length(); j++){
                    synonyms.add(array.get(j).toString());
                }
            }


            String[] stringArray = synonyms.toArray(new String[synonyms.size()]);
            Arrays.sort(stringArray, Comparator.comparing(String::length));
            synonyms = new ArrayList<>();
            for(int i = 0; i < stringArray.length; i++){
                if(i < 5){
                    String str = stringArray[i];
                    String res = str.substring(0, 1).toUpperCase() + str.substring(1);
                    synonyms.add(res);
                }else {
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
