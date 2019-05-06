package com.example.metronomia;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Song> songItems;
    public int selectedPosition=-1;

    public MyAdapter(List<Song> songItems, Context context) {
        this.songItems = songItems;
        this.context = context;
    }

    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_list, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,final int i) {
        final Song songItem = songItems.get(i);

        viewHolder.songTextView.setText(songItem.getNume());
        viewHolder.bpmTextView.setText(songItem.getBpm());

//        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(Livee.getInstance()!= null){
//                    Livee.getInstance().setBpmTextView(songItem.getBpm());
//                }
//            }
//        });

        if(selectedPosition==i)
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#D81B60"));
        else
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#008577"));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition=i;
                notifyDataSetChanged();

                if(Livee.getInstance()!= null){
                    Livee.getInstance().setSongView(songItem.getBpm(), selectedPosition, songItem.getAccent());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView songTextView;
        public TextView bpmTextView;
        public ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            songTextView = (TextView)itemView.findViewById(R.id.songTextView);
            bpmTextView = (TextView) itemView.findViewById(R.id.BPMTextView);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayout);
        }
    }


}
