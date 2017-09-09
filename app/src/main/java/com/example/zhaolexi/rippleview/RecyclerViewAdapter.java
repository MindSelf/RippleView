package com.example.zhaolexi.rippleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ZHAOLEXI on 2017/9/4.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    List<String> datas;
    Context context;

    public RecyclerViewAdapter(List<String> datas) {
        this.datas=datas;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private RippleView rv;
        private TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            rv = (RippleView) itemView.findViewById(R.id.ripple);
            tv = (TextView) itemView.findViewById(R.id.name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.content_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.rv.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                int position=holder.getAdapterPosition();
                Toast.makeText(context, "Click "+datas.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv.setText(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
}
