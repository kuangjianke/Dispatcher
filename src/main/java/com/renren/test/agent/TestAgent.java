package com.renren.test.agent;

import java.util.HashSet;
import java.util.UUID;

import com.renren.test.basic.TestProtocol;
import com.renren.test.basic.TestUtil;

/**
 * 测试agent.
 * 每个agent表示驻守在一台服务器的测试程序实例.
 * agent监听来自master节点的命令，并执行相应动作.
 *
 * @author yu.zhang@renren-inc.com
 * 2012-4-27 下午2:49:54
 */
public class TestAgent {

    private AgentZkProxy                proxy;
    //测试agentuid
    private final String                agentId    = UUID.randomUUID().toString();

    //tester当前状态
    public volatile String              state      = TestProtocol.RUNNABLE;
    public volatile String              currentCmd = "";

    private HashSet<ConcurrentTestCase> testCases  = new HashSet<ConcurrentTestCase>();

    public TestAgent() {
        this.proxy = new AgentZkProxy(this); //zookeeper代理
    }

    /**
     * 加入一个新的测试用例.
     * @param caze
     * @return
     */
    public boolean addTestCase(ConcurrentTestCase caze) {
        return testCases.add(caze);
    }

    /**
     * 执行所有的测试用例.
     */
    private void runTestCases() {
        state = TestProtocol.RUNNING;
        reportTestState(state);

        for (ConcurrentTestCase caze : testCases) {
            caze.doTest();
        }

        state = TestProtocol.FINISHED;
        reportTestState(state);
        currentCmd = "";
    }

    private void closeAllCases() {
        for (ConcurrentTestCase caze : testCases) {
            caze.close();
        }
    }

    /**
     * 响应来自master的命令.
     * @param cmd
     */
    public void responseCmd(String cmd) {
        this.currentCmd = cmd;
        if (!state.equals(TestProtocol.RUNNING)) {
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    /**
     * 等待master发出命令.
     */
    private void waitForMasterCmd() {
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {}
        }
    }

    public void execute() {
        proxy.register();//注册

        /*一个特别简单的状态机*/
        while (true) {
            if (state.equals(TestProtocol.RUNNABLE)) {
                TestUtil.printToTermin("等待master命令");
                waitForMasterCmd();//阻塞主线程
                TestUtil.printToTermin("收到命令:" + currentCmd);
                if (currentCmd.equals(TestProtocol.BEGIN)) {
                    responseBegin();
                } else if (currentCmd.equals(TestProtocol.RESET)) {
                    TestUtil.printToTermin("重置agent");
                    responseReset();
                } else if (currentCmd.equals(TestProtocol.STOP)) {
                    TestUtil.printToTermin("准备停止agent");
                    responseStop();
                    break;
                }
            } else if (state.equals(TestProtocol.FINISHED)) {
                TestUtil.printToTermin("等待master命令");
                waitForMasterCmd();
                if (currentCmd.equals(TestProtocol.RESET)) {
                    responseReset();
                } else if (currentCmd.equals(TestProtocol.STOP)) {
                    responseStop();
                    break;
                }
            }
        }
        System.exit(1);//状态机退出，系统停止运行
    }

    /**
     * 将执行状态写入zookeeper，方便调度节点汇总数据，执行最终断言.
     *
     * @param state
     */
    private void reportTestState(String state) {
        proxy.reportState(state);
    }

    private void responseBegin() {
        runTestCases();
    }

    private void responseStop() {
        closeAllCases();
    }

    private void responseReset() {
        state = TestProtocol.RUNNABLE;
        reportTestState(state);
        TestUtil.printToTermin("state change to:" + state);
    }

    public String getAgentId() {
        return agentId;
    }

}
