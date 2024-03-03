package com.example.application.services;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubscriptionManager {
    public Set<String> subscribedChannels = new HashSet<String>();

    public String subscribe(String instType, String channel, String instId) {
        JSONObject arg = new JSONObject();
        try {
            arg.put("instType", instType);
            arg.put("channel", channel);
            arg.put("instId", instId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSONArray args = new JSONArray();
        args.put(arg);

        JSONObject message = new JSONObject();
        try {
            message.put("op", "subscribe");
            message.put("args", args);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        subscribedChannels.add(channel);
        return message.toString();
    }

    public String unsubscribe(String instType, String channel, String instId) {
        JSONObject arg = new JSONObject();
        try {
            arg.put("instType", instType);
            arg.put("channel", channel);
            arg.put("instId", instId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSONArray args = new JSONArray();
        args.put(arg);

        JSONObject message = new JSONObject();
        try {
            message.put("op", "unsubscribe");
            message.put("args", args);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return message.toString();
    }
}