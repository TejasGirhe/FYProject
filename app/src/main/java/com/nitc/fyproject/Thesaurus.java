package com.nitc.fyproject;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Thesaurus {

    private static final String API_KEY = "c9f472a5-16d5-4c9f-807e-99ee5208c535";

    public static class GetSynonymsTask extends AsyncTask<String, Void, List<String>> {

        private ThesaurusCallback callback;

        public GetSynonymsTask(ThesaurusCallback callback) {
            this.callback = callback;
        }

        @Override
        protected List<String> doInBackground(String... params) {
            List<String> synonyms = new ArrayList<>();
            synonyms.add(params[0] + " : ");

            try {
                URL url = new URL("https://www.dictionaryapi.com/api/v3/references/thesaurus/json/" + params[0] + "?key=" + API_KEY);

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
                System.out.println("IO : An error occurred while fetching synonyms: " + e.getMessage());
            }

            return synonyms;
        }

        @Override
        protected void onPostExecute(List<String> synonyms) {
            callback.onSynonymsFetched(synonyms);
        }
    }

    public interface ThesaurusCallback {
        void onSynonymsFetched(List<String> synonyms);
    }

}
