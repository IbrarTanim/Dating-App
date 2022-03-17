/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 *  if your need any help knock this number +8801776254584 whatsapp
 */

package com.adda.datingapp.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ApiService {
    @POST("send")
    Call<String>sendRemoteMessage(
            @HeaderMap HashMap<String ,String> header,
            @Body String remoteBody
    );
}
