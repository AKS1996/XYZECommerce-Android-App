package com.xyzecommerce;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by tawrun on 14/11/17.
 */

public interface RegisterApi {
//    @FormUrlEncoded
    @POST("login.php")
    Call<UserExistsResponseModel> insertUser(
            @Body UserExistsResponseModel login);
//    @POST("singup.php")
//    Call<>


}
