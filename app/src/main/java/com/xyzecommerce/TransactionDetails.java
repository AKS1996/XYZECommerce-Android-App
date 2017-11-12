package com.xyzecommerce;

public class TransactionDetails {
    private   int money;
    private String Id;
    public TransactionDetails(int amount,String id){
        money=amount;
        Id=id;
    }

    public void setfId(String ImageId){
        Id=ImageId;
    }
    public String getfId(){
        return Id;
    }
    public void setAmount(int Amount){
        money=Amount;
    }
    public int getMoney(){
        return money;
    }
} 