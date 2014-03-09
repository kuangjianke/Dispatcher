package com.renren.test.master;

import com.renren.test.basic.TestUtil;

/**
 * mm测试套件.
 * 主要用于测试高负载下的数据并发正确性.
 * 每套测试都分为一个master和若干agent，master通过向agent发送命令来调度测试行为，并且总结最终结果.
 *
 * @author yu.zhang@renren-inc.com
 * 2012-4-27 下午2:31:39
 */
public abstract class TestMaster {

    private final TestAgentManager agent;

    public TestMaster() {
        this.agent = new TestAgentManager(this);
    }

    /**
     * 执行测试前的准备工作.
     */
    protected abstract void before();

    /**
     * 执行测试后的数据收集工作.
     */
    protected abstract void after();

    /**
     * 执行测试.
     */
    public void runTests() {
        before();
        agent.beginTests();
    }

    public void conclude() {
        waitForReady();
        after();
        TestUtil.printToTermin("测试成功");
    }

    /**
     * 等待测试完成，此方法不可终端，无超时概念.
     */
    private void waitForReady() {
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                TestUtil.handleInterrupt(e);//不想应中断.
            }
        }
    }

    /**
     * 测试完成后调用此方法.
     */
    public void ready() {
        TestUtil.printToTermin("所有测试已经完成");
        synchronized (this) {
            this.notifyAll();
        }
    }

    protected void close() {
        agent.close();
    }

    /**
     * 执行测试.
     */
    protected void test() {
        runTests();
        conclude();
    }

    /**
     * 停止测试任务，所有测试节点收到此命令后都会停止.
     */
    protected void stop() {
        agent.stopTests();
    }

    /**
     * 重置所有测试节点,
     */
    protected void reset() {
        agent.resetTests();
    }
}
