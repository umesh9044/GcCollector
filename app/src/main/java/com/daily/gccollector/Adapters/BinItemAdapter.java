package com.daily.gccollector.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.daily.gccollector.Interfaces.ClickListener;
import com.daily.gccollector.Models.BinMaster;
import com.daily.gccollector.R;

import java.util.List;

public class BinItemAdapter extends RecyclerView.Adapter<BinItemAdapter.MyViewHolder> {

    public List<BinMaster> itemsList;
    public ClickListener clickListener;

    public BinItemAdapter(List<BinMaster> mItemList){
        this.itemsList = mItemList;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
    @Override
    public BinItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_bin_master_item_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BinItemAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final BinMaster item = itemsList.get(position);
        holder.binname.setText("Bin: "+item.getBinLocName());
        holder.bincode.setText("Code: "+String.valueOf(item.getBinLocCode()));
        holder.bindesc.setText(String.valueOf(item.getDescription()));
        holder.binname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Position = "+position+"\n Item = "+holder.binname.getText(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView binname,bincode,bindesc;
        private LinearLayout itemLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            binname = itemView.findViewById(R.id.tvbinname);
            bincode = itemView.findViewById(R.id.tvbincode);
            bindesc = itemView.findViewById(R.id.tvDesc);
            itemLayout =  itemView.findViewById(R.id.itemLayout);
        }
    }
}
