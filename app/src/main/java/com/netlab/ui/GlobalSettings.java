package com.netlab.ui;

import java.util.HashMap;

/**
 * Created by ZQ on 2017/4/18.
 */

public class GlobalSettings {

    private static HashMap<String, HashMap<String, Boolean>> activation_Policy = new HashMap<>();


    /**
     * check if the pkg has been set
     * @param pkg
     * @return
     */
    public boolean IsFirstTime(String pkg)
    {
        return activation_Policy.containsKey(pkg);
    }

    /**
     * set a policy
     * @param source_pkg
     * @param sink_pkg
     * @param decision
     */
    public void setPolicy(String source_pkg, String sink_pkg, boolean decision)
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
    public boolean IsAllowed(String source_pkg, String sink_pkg)
    {
        HashMap<String, Boolean> policy = activation_Policy.get(source_pkg);
        return policy.get(sink_pkg);
    }


}
