package com.xyzecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<TransactionDetails> {

    public CustomAdapter(Context context, int layoutId, List<TransactionDetails> list){
        super(context,layoutId,list);
    }
    public View getView(int Position, View convertview, ViewGroup parent){
        if(convertview==null){
            convertview= LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view,null);
        }
        TransactionDetails transactionDetails=getItem(Position);
        if(transactionDetails!=null){
            TextView textViewId=(TextView)convertview.findViewById(R.id.person_id);
            textViewId.setText("ID  "+transactionDetails.getfId());
            TextView textViewamount=(TextView)convertview.findViewById(R.id.Amount_tran);
            textViewamount.setText("Amount  "+transactionDetails.getMoney());
        }
        return convertview;
    }

}