package com.netlab.ui;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ZQ on 2017/4/18.
 */

public class GlobalSettings {


    private  final static String TAG = "GlobalSettings";
     private static HashMap<String, HashMap<String, Boolean>> activation_Policy = new HashMap<>();

    private static LinkedBlockingQueue<Activation> waiting_queue = new LinkedBlockingQueue<>();

    private static LinkedBlockingQueue<Boolean> decision_queue = new LinkedBlockingQueue<>();

    private static LinkedBlockingQueue<Boolean> user_decision_queue = new LinkedBlockingQueue<>();
    /**
     * check if the pkg has been set
     * @param pkg
     * @return
     */
    public static boolean IsFirstTime(String pkg)
    {
        return activation_Policy.containsKey(pkg);
    }

    /**
     * set a policy
     * @param source_pkg
     * @param sink_pkg
     * @param decision
     */
    public static void setPolicy(String source_pkg, String sink_pkg, boolean decision)
    {
        HashMap<String, Boolean> policy = new HashMap<String, Boolean>();
        policy.put(sink_pkg, decision);
        activation_Policy.put(source_pkg, policy);
    }

    /**
     * given a source-sink package pair, check the policy map to decide whether to cut the activation
     * @param source_pkg
     * @param sink_pkg
     * @return
     */
    public static boolean IsAllowed(String source_pkg, String sink_pkg)
    {
        HashMap<String, Boolean> policy = activation_Policy.get(source_pkg);
        return policy.get(sink_pkg);
    }

    /**
     * Add a new activation for decision
     * @param act
     */
    public static void addActivation(Activation act)
    {
        Log.d(TAG,"GlobalSettings start to put a new activation");
        try {
            waiting_queue.put(act);
            Log.d(TAG,"GlobalSettings end to put a new activation");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * poll an activation from the decision queue
     * @return
     */
    public static Activation takeActivation()
    {
        Activation atc = null;
        try {
            Log.d(TAG,"GlobalSettings start to take activation");
            atc = waiting_queue.take();
            Log.d(TAG,"GlobalSettings stop to take activation");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return atc;
    }


    /**
     * add a new decision to the queue
     * @param decision
     */
    public static void addDecision(boolean decision)
    {
        try {
            decision_queue.put(decision);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * poll a decision from the queue
     * @return
     */
    public static Boolean takeDecision()
    {
        boolean decision = false;

        try {
            decision = decision_queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return decision;
    }



    /**
     * add a new decision to the queue
     * @param decision
     */
    public static void addUserDecision(boolean decision)
    {
        try {
            user_decision_queue.put(decision);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * take a decision from the queue
     * @return
     */
    public static Boolean takeUserDecision()
    {
        boolean user_decision = false;

        try {
            user_decision = user_decision_queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return user_decision;

    }



}
