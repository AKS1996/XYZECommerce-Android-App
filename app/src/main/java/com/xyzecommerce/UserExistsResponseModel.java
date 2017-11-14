package com.xyzecommerce;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tawrun on 14/11/17.
 */

public class UserExistsResponseModel {

    @SerializedName("UNAME")
    private String mUsername;
    @SerializedName("CELL")
    private String mcell;
    @SerializedName("SLID")
    private String slid;

    public UserExistsResponseModel(String username,String cell){
        this.mUsername=username;
        this.mcell=cell;
    }
    public UserExistsResponseModel(String slid){
        this.slid=slid;
    }
    public void setmUsername(String username){
        this.mUsername=username;
    }
    public void setMcell(String  cell){
        this.mcell= cell;
    }

    public String getMcell() {
        return mcell;
    }

    public String getmUsername() {
        return mUsername;
    }
    public void setSlid(String slid){
        this.slid=slid;
    }

    public String getSlid() {
        return slid;
    }
}
