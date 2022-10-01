package com.example.digi_dhobi.api;

import static com.example.digi_dhobi.utils.Constants.BASE_URL;

import com.example.digi_dhobi.model.User;
import com.example.digi_dhobi.utils.Constants;
import com.example.digi_dhobi.utils.HttpCallHelper;
import com.fasterxml.jackson.core.JsonProcessingException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserService {

    private final static String LOGIN_USER_PATH = "/test/users/login";
    private final static String REGISTER_USER_PATH = "/test/users/register";

    public UserService() {
    }

    public void loginUser(final String roll,
                      final Runnable postFinishRunnable) {
        String jsonBody = String.format("{ \"roll\": \"%s\" }",
                roll);

        try {
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), jsonBody);

            Request.Builder httpPost = new Request.Builder()
                    .url(BASE_URL + LOGIN_USER_PATH)
                    .post(body);
            HttpCallHelper.executeHttpCall(httpPost, postFinishRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerUser(final User user, final Runnable postFinishRunnable) {
        String jsonBody = null;
        try {
            jsonBody = Constants.OBJECT_MAPPER.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), jsonBody);

            Request.Builder httpPost = new Request.Builder()
                    .url(BASE_URL + REGISTER_USER_PATH)
                    .post(body);
            HttpCallHelper.executeHttpCall(httpPost, postFinishRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void authenticateUser(final String phone,
//                          final Runnable postFinishRunnable) {
//        String jsonBody = String.format("{ \"phone\": \"%s\" }",
//                phone);
//
//        try {
//            RequestBody body = RequestBody.create(
//                    MediaType.parse("application/json"), jsonBody);
//
//            Request.Builder httpPost = new Request.Builder()
//                    .url(BASE_URL + AUTHENTICATE_USER_PATH)
//                    .post(body);
//            HttpCallHelper.executeHttpCall(httpPost, postFinishRunnable);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void editUser(final String name, String email, String address, String phone,
//                         final Runnable postFinishRunnable) {
//        String jsonBody = String.format("{ \"name\": \"%s\", \"userId\": \"%s\", \"email\": \"%s\", \"address\": \"%s\"}",
//                name,
//                phone,
//                email,
//                address);
//
//        try {
//            RequestBody body = RequestBody.create(
//                    MediaType.parse("application/json"), jsonBody);
//
//            Request.Builder httpPost = new Request.Builder()
//                    .url(BASE_URL + EDIT_USER_PATH)
//                    .post(body);
//            HttpCallHelper.executeHttpCall(httpPost, postFinishRunnable);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
