package com.xyzecommerce;

import com.google.gson.annotations.SerializedName;

public class TransactionDetails {
    @SerializedName("AMOUNT")
    private   int money;
    @SerializedName("FROM")
    private String FromId;

    @SerializedName("TO")
    private String ToId;
    public TransactionDetails(int amount,String id){
        money=amount;
        FromId=id;
    }

    public void setfId(String ImageId){
        FromId=ImageId;
    }
    public String getfId(){
        return FromId;
    }
    public void setAmount(int Amount){
        money=Amount;
    }
    public int getMoney(){
        return money;
    }
} 