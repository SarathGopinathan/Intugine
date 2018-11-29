package com.example.user.intugine.Model;

import com.google.gson.annotations.SerializedName;

public class ModelMumTempDetails {

    @SerializedName("temp")
    public String temp;

    @SerializedName("temp_min")
    public String tempMin;

    @SerializedName("temp_max")
    public String tempMax;

    @SerializedName("humidity")
    public String humidity;

}
