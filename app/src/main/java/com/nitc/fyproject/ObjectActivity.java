package com.nitc.fyproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ObjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);
        ListView listView = findViewById(R.id.objectsList);

        String objs = getIntent().getStringExtra("objects");
        String[] list = objs.split(",");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                Toast.makeText(ObjectActivity.this, selectedItem, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ObjectActivity.this, ArActivity.class);
                intent.putExtra("objectName", selectedItem);
                startActivity(intent);
            }
        });
    }
}