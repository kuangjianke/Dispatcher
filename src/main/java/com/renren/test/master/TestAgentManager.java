package com.renren.test.master;

import java.util.HashMap;

import com.renren.test.basic.TestProtocol;

/**
 * 测试节点管理器,用来向测试节点发送调度命令，以及检测测试节点状态变化.
 * 命令的实质就是向zookeeper上写入一些参数，所有测试节点在监听到改变后就会执行相应的动作.
 *
 * @author yu.zhang@renren-inc.com
 * 2012-4-27 下午2:31:39
 */
public class TestAgentManager {

    private final TestMaster suit;

    private MasterZkProxy    proxy;

    public TestAgentManager(TestMaster suit) {
        this.suit = suit;
        this.proxy = new MasterZkProxy(this);
        this.proxy.register();
    }

    /**
     * 发出开始测试命令.
     */
    public void beginTests() {
        this.proxy.beginTests();
    }

    /**
     * 发出重置测试命令.
     */
    public void resetTests() {
        this.proxy.resetTests();
    }

    /**
     * 发出停止测试命令.
     */
    public void stopTests() {
        this.proxy.stopTests();
    }

    /**
     * 处理测试agent状态变化信息.
     * @param stateSet
     */
    public void handleAgentStateChange(HashMap<String, String> stateSet) {
        boolean allTestFinished = true;
        for (String state : stateSet.values()) {
            allTestFinished &= state.equals(TestProtocol.FINISHED);
        }

        if (allTestFinished) suit.ready();
    }

    /**
     * 关闭与zookeeper连接.
     */
    public void close() {
        this.proxy.close();
    }
}
