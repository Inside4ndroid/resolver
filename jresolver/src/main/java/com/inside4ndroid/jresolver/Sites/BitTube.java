package com.inside4ndroid.jresolver.Sites;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.inside4ndroid.jresolver.Jresolver;
import com.inside4ndroid.jresolver.Model.Jmodel;
import com.inside4ndroid.jresolver.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitTube {

    private static String getBitTubeID(String string){
        final String regex = "(embed|watch)/(.+)";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return matcher.group(2).replaceAll("&|/","");
        }
        return null;
    }

    public static void fetch(String url, final Jresolver.OnTaskCompleted onTaskCompleted){
        String id = getBitTubeID(url);
        if (id!=null) {
            AndroidNetworking.get("https://bittube.video/api/v1/videos/" + id)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            ArrayList<Jmodel> jModels = parseVideo(response);
                            if (jModels.isEmpty()) {
                                onTaskCompleted.onError();
                            } else onTaskCompleted.onTaskCompleted(Utils.sortMe(jModels), true);
                        }

                        @Override
                        public void onError(ANError anError) {
                            onTaskCompleted.onError();
                        }
                    });
        }else onTaskCompleted.onError();
    }

    private static ArrayList<Jmodel> parseVideo(String html){

        ArrayList<Jmodel> jModels = new ArrayList<>();
        try {
            JSONArray array = new JSONObject(html).getJSONArray("files");
            for (int i=0;i<array.length();i++){
                String label = array.getJSONObject(i).getJSONObject("resolution").getString("label");
                String src = array.getJSONObject(i).getString("fileDownloadUrl");
                if (label.length()>1) {
                    Jmodel jModel = new Jmodel();
                    jModel.setQuality(label);
                    jModel.setUrl(src);
                    jModels.add(jModel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jModels;
    }
}
