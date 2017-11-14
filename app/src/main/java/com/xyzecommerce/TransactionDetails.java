package com.xyzecommerce;

import com.google.gson.annotations.SerializedName;

public class TransactionDetails {
    @SerializedName("AMOUNT")
    private   int money;
    @SerializedName("FROM")
    private String FromId;

    @SerializedName("TO")
    private String ToId;

    TransactionDetails(int amount,String id){
        money=amount;
        FromId=id;
    }

    public void setfId(String ImageId){
        FromId=ImageId;
    }
    String getfId(){
        return FromId;
    }
    public void setAmount(int Amount){
        money=Amount;
    }
    int getMoney(){
        return money;
    }
} 