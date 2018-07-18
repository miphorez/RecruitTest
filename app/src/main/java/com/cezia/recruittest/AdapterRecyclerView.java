package com.cezia.recruittest;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cezia.recruittest.structure.CaseRecord;
import com.cezia.recruittest.structure.CaseRecordList;
import com.squareup.picasso.Picasso;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;

import static com.cezia.recruittest.Utils.createRequest;

public class AdapterRecyclerView extends RecyclerView.Adapter<RecHolder>{
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
        if (holder != null) holder.bind(record, mCallback);
        //if last position in list go to update and refresh
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

    void bind(CaseRecord record, final IRecyclerViewListener mCallback){
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
            //if the field is empty - show a blank image
            vThumbn.setImageResource(R.drawable.cat_100);
        }

        final String strId = record.getId();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCallback != null)
                    mCallback.onSpinnerGo();

                //transfer to the server the record id that was selected from the list
                Observable observable = createRequest("https://sandbox.1click2deliver.com:10999/panel/proxy.php/?action=setStatus&"+strId);
                observable = observable.subscribeOn(Schedulers.io());
                observable = observable.observeOn(AndroidSchedulers.mainThread());

                //subscription to data received from the server
                observable.subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onNext(String s) {
                        if(mCallback != null)
                            mCallback.onSpinnerStop();
                        //if there is a response from the server - to report the transfer of id
                        Toast.makeText(itemView.getContext(),"The record id (#"+strId+") was passed to the server",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d("debug", "onError: " + throwable);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
            }
        });
    }
}
