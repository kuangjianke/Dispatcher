package com.renren.test.agent;

import com.mop.dbstorm.zookeeper.DataListener;
import com.mop.dbstorm.zookeeper.ZKClient;
import com.renren.test.basic.TestZKHelper;

public class AgentZkProxy implements DataListener {

    private final ZKClient client = new ZKClient("host1:2181,host2:2181", 1000);

    public final String    PATH   = "/concurrenttest";

    public final TestAgent agent;

    public AgentZkProxy(TestAgent agent) {
        this.agent = agent;
    }

    @Override
    public String getPath() {
        return PATH;
    }

    public void register() {
        client.createEphemeral(getAgentPath(), "init", TestZKHelper.zkSer);
        client.registerDataListener(this);
    }

    private String getAgentPath() {
        return getPath() + "/" + agent.getAgentId();
    }

    @Override
    public void onDataChange() {
        String cmd = client.readData(getPath(), TestZKHelper.zkDeSer);
        agent.responseCmd(cmd);
    }

    public void reportState(String state) {
        client.writeData(getAgentPath(), state, TestZKHelper.zkSer);
    }
}
