package com.nitc.fyproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.myViewHolder >{
    Context context;
    ArrayList<String> list;
    ArrayList<String> wishlist = new ArrayList<>();

    public Adapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
//        Toast.makeText(context, position + " " + list.get(position), Toast.LENGTH_SHORT).show();
        holder.PName.setText(list.get(position));


//        for(int i = 0; i < list.size(); i++) {
//            TextToSpeech t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                @Override
//                public void onInit(int status) {
//                    if (status != TextToSpeech.ERROR) {
//                        t1.setLanguage(Locale.ENGLISH);
//                    }
//                }
//            });
////
//            RequiresPermission.Read.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    t1.speak(list[i], TextToSpeech.QUEUE_FLUSH, null);
//                }
//            });
//            recyclerView.smoothScrollToPosition(adapter.getItemCount());
//        }

//        TextToSpeech t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != TextToSpeech.ERROR) {
//                    t1.setLanguage(Locale.ENGLISH);
//                }
//            }
//        });
////
//        RequiresPermission.Read.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                t1.speak(recognisedText, TextToSpeech.QUEUE_FLUSH, null);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView PName;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
//            imageView = itemView.findViewById(R.id.image);
            PName = itemView.findViewById(R.id.word);
        }
    }
}
