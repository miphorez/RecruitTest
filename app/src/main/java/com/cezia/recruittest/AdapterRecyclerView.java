package com.cezia.recruittest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cezia.recruittest.structure.CaseRecord;
import com.cezia.recruittest.structure.CaseRecordList;
import com.squareup.picasso.Picasso;

import io.realm.RealmList;

public class AdapterRecyclerView extends RecyclerView.Adapter<RecHolder> {
    private RealmList<CaseRecord> records;

    //organizing a callback for managing the refresh
    private IRecyclerViewListener mCallback;
    public void setCallbackForRefresh(IRecyclerViewListener callback) {
        mCallback = callback;
    }

    public AdapterRecyclerView(CaseRecordList records) {
        this.records = records.getItems();
    }

    @Override
    public RecHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_card, parent, false);
        return new RecHolder(view);
    }

    @Override
    public void onBindViewHolder(RecHolder holder, int position) {
        CaseRecord record = records.get(position);
        if (holder != null) holder.bind(record);
        if (position == records.size()-1){
            if(mCallback != null)
                mCallback.onPullToRefresh();
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}

class RecHolder extends RecyclerView.ViewHolder{

    public RecHolder(View itemView) {
        super(itemView);
    }

    void bind(CaseRecord record){
        //show on the UI a card with data from a database record
        TextView vTitle = (TextView) itemView.findViewById(R.id.item_title);
        TextView vDescr = (TextView) itemView.findViewById(R.id.item_descript);
        ImageView vThumbn = (ImageView) itemView.findViewById(R.id.item_thumb);
        vTitle.setText(record.getShort_name());
        vDescr.setText(record.getDesc());
        if (record.getIcon_name()!=null) {
            //It is assumed that this field contains a link to the image
            Picasso.with(vThumbn.getContext()).load(record.getIcon_name()).into(vThumbn);
        }else {
            //if the field is empty - show a stub-image
            vThumbn.setImageResource(R.drawable.cat_100);
        }
    }
}
