package com.adda.datingapp.model;
/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 */

public class LiveNowModel {
    String LName, LProfile;

    public LiveNowModel(String LName, String LProfile) {
        this.LName = LName;
        this.LProfile = LProfile;
    }

    public LiveNowModel() {
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getLProfile() {
        return LProfile;
    }

    public void setLProfile(String LProfile) {
        this.LProfile = LProfile;
    }
}
