package com.daily.gccollector.Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.daily.gccollector.BinMasterActivityEdit;
import com.daily.gccollector.DeviceActivity;
import com.daily.gccollector.Interfaces.ClickListener;
import com.daily.gccollector.LoginActivity;
import com.daily.gccollector.Models.BinMaster;
import com.daily.gccollector.R;

import java.util.ArrayList;
import java.util.List;

public class BinItemAdapter extends RecyclerView.Adapter<BinItemAdapter.MyViewHolder> implements Filterable {

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

        String mBase64string= item.getLocImage();
        if(mBase64string.length()>0) {
            byte[] decodedString = Base64.decode(mBase64string, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.BinImage.setImageBitmap(decodedByte);
        }
        holder.binname.setText("Bin: "+item.getBinLocName());
        holder.bincode.setText("Code: "+String.valueOf(item.getBinLocCode()));
        holder.bindesc.setText(item.getDescription());
        holder.btnEditBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(),"Position = "+position+"\n Item = "+holder.binname.getText(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), BinMasterActivityEdit.class);
                intent.putExtra("getBinLocID",item.getBinLocID());
                intent.putExtra("getBinLocName",item.getBinLocName());
                intent.putExtra("getBinLocCode",item.getBinLocCode());
                intent.putExtra("getDescription",item.getDescription());
                intent.putExtra("getLocImage",item.getLocImage());
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
    @Override
    public Filter getFilter() {
        return FilterBin;
    }
    private Filter FilterBin = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BinMaster> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemsList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (BinMaster item : itemsList) {
                    if (item.getBinLocName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemsList.clear();
            itemsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView binname,bincode,bindesc;
        public ImageView BinImage;
        public Button btnEditBin;
        private LinearLayout itemLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            BinImage = itemView.findViewById(R.id.BinImage);
            binname = itemView.findViewById(R.id.tvbinname);
            bincode = itemView.findViewById(R.id.tvbincode);
            bindesc = itemView.findViewById(R.id.tvDesc);
            btnEditBin = itemView.findViewById(R.id.btnEditBin);
            itemLayout =  itemView.findViewById(R.id.itemLayout);
        }
    }
}
