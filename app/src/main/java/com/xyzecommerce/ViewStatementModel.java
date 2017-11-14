package com.xyzecommerce;

import com.google.gson.annotations.SerializedName;

import java.util.List;



public class ViewStatementModel {
    @SerializedName("SENTMONEY")
    private List<TransactionDetails> mSentMoneyList;
    @SerializedName("RECEIVEDMONEY")
    private List<TransactionDetails> mReceivedMoneyList;

    public void setmSentMoneyList(List<TransactionDetails> list){
        this.mSentMoneyList=list;
    }
    public List<TransactionDetails> getmReceivedMoneyList() {
        return mReceivedMoneyList;
    }

    public List<TransactionDetails> getmSentMoneyList() {
        return mSentMoneyList;
    }

    public void setmReceivedMoneyList(List<TransactionDetails> mReceivedMoneyList) {
        this.mReceivedMoneyList = mReceivedMoneyList;
    }
}
