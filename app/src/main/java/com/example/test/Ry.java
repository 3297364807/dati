package com.example.test;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Ry extends RecyclerView.Adapter {
    private List<Tools> list = new ArrayList<>();
    private Context context;

    public Ry(List<Tools> list) {
        this.list=list;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder2(LayoutInflater.from(context).inflate(R.layout.xiala_ry, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder2 v2= (ViewHolder2) holder;
        v2.title.setText(list.get(position).getTitle());
        v2.info.setText(list.get(position).getInfo());
        v2.info.setTextColor(Color.parseColor("#FF0000"));
        v2.title.setTextColor(Color.parseColor("#000000"));
        v2.info.setVisibility(View.GONE);
        v2.cleck.setOnClickListener(v -> {
            if(v2.info.getVisibility()==View.GONE){
                v2.iv.setBackgroundResource(R.drawable.jiantou2);
                v2.info.setVisibility(View.VISIBLE);
            }else {
                v2.iv.setBackgroundResource(R.drawable.jiantou1);
                v2.info.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        private ImageView iv;
        private TextView info;
        private LinearLayout cleck;
        private TextView title;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            iv=itemView.findViewById(R.id.iv);
            cleck=itemView.findViewById(R.id.cleck);
            info=itemView.findViewById(R.id.info);
            title=itemView.findViewById(R.id.title);
        }
    }
}
