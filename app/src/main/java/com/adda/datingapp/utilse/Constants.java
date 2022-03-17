package com.adda.datingapp.utilse;

import java.util.HashMap;

public class Constants {
    public static String KEY_USERS="users";
    public static String KEY_PARTNER="partner";
    public static String KEY_NAME="name";
    public static String KEY_EMAIL="email";
    public static String KEY_PASSWORD="password";
    public static String KEY_CONFROM="confrom_password";
    public static String KEY_PHONE="phone";
    public static String KEY_PROFILE="profile";

    public static String KEY_USER_ID="user_id";
    public static String KEY_FCM_TOKEN="fcm_token";


    public static String KEY_PREFERENCE_NAME="videoMeetingPreference";
    public static String KEY_SIGNED_IN="isSignedIn";

    public static String  REMOTE_MSG_AUTHORIZATION="Authorization";
    public static String  REMOTE_MSG_CONTENT_TYPE="Content-Type";

    public static String  REMOTE_MSG_TYPE="type";
    public static String  REMOTE_MSG_INVITATION="invitation";
    public static String  REMOTE_MSG_MEETING_TYPE="meetingType";
    public static String  REMOTE_MSG_INVITER_TOKEN="inviterToken";
    public static String  REMOTE_MSG_DATA="data";
    public static String  REMOTE_MSG_REGISTRATION_IDS="registration_ids";

    public static String  REMOTE_MSG_INVITATION_RESPONSE="invitationResponse";

    public static String  REMOTE_MSG_INVITATION_ACCEPTED="accepted";
    public static String  REMOTE_MSG_INVITATION_REJECTED="rejected";
    public static String  REMOTE_MSG_INVITATION_CANCELLED="cancelled";


    public static String  REMOTE_MSG_MEETING_ROOM="meetingRoom";






    public static HashMap<String ,String> getRemoteMessageHeaders(){
        HashMap<String, String> headers=new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "Key=AAAAijAKnRE:APA91bGj7bNNCWiq5iNKUGg9pALKYg8L8Lm-pT8UzDk7cyL3x_F39mMV0clVehazsRlBna9nffzQ28Yjm4EMFjngPhuartjkyV8lwiSu7yZQ0z_8hynqBHAjpGdP6PpdCb6syPt2H2C_"
        );

        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }


}
