package com.renren.test.master;

import java.util.HashMap;
import java.util.List;

import com.mop.dbstorm.zookeeper.DataListener;
import com.mop.dbstorm.zookeeper.ZKClient;
import com.renren.test.basic.TestProtocol;
import com.renren.test.basic.TestZKHelper;

public class MasterZkProxy {

    public final String            PATH   = "/concurrenttest";

    private final ZKClient         client = new ZKClient("host1:2181,host1:2181", 1000);

    private final TestAgentManager manager;

    public MasterZkProxy(TestAgentManager manager) {
        this.manager = manager;
    }

    public void register() {
        List<String> children = client.getChildren(PATH);
        for (String child : children) {
            client.registerDataListener(new AgentListener(PATH + "/" + child));
        }
    }

    /**
     * 发出开始测试命令.
     */
    public void beginTests() {
        client.writeData(PATH, TestProtocol.BEGIN, TestZKHelper.zkSer);
    }

    /**
     * 发出重置测试命令.
     */
    public void resetTests() {
        client.writeData(PATH, TestProtocol.RESET, TestZKHelper.zkSer);
    }

    /**
     * 发出停止测试命令.
     */
    public void stopTests() {
        client.writeData(PATH, TestProtocol.STOP, TestZKHelper.zkSer);
    }

    private class AgentListener implements DataListener {

        private final String cp;

        public AgentListener(String cp) {
            this.cp = cp;
        }

        @Override
        public void onDataChange() {
            HashMap<String, String> stateSet = new HashMap<String, String>();
            List<String> children = client.getChildren(PATH);
            for (String child : children) {
                String state = client.readData(PATH + "/" + child, TestZKHelper.zkDeSer);
                stateSet.put(child, state);
            }

            manager.handleAgentStateChange(stateSet);
        }

        @Override
        public String getPath() {
            return cp;
        }
    }

    public void close() {
        this.client.close();
    }

}
