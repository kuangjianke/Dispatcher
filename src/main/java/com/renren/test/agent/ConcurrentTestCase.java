package com.renren.test.agent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.renren.test.basic.TestUtil;
import com.renren.test.exception.TestException;

/**
 * 并发测试用例.
 * 每一个测试用例都应当继承此类.
 *
 * 每一个测试用例都需要设置“concurrentLevel（并发级别）”，“presure（压力）”,"taskCount(任务数量)"三个参数.
 * 并发级别参数决定了测试程序使用的线程池数量，压力决定了每个线程池中的线程总数，这两个参数可以用来控制并发压力和并发程度.
 *
 * @author yu.zhang@renren-inc.com
 * 2012-4-27 下午2:31:39
 */
public abstract class ConcurrentTestCase {

    /**
     * 并发级别.
     */
    private int                     concurrentLevel = 40;
    /**
     * 压力.
     */
    private int                     presure         = 1;

    private int                     taskCount       = 0;

    private final ExecutorService[] exes;
    private final CountDownLatch    latch;

    public ConcurrentTestCase(int concurrentLevel, int presure, int taskCount) {
        this.concurrentLevel = concurrentLevel;
        this.presure = presure;
        this.taskCount = taskCount;
        this.exes = setUpExe();
        this.latch = new CountDownLatch(this.taskCount * 2);
    }

    /**
     * 执行测试.
     */
    protected void doTest() {
        TestUtil.printToTermin("开始测试");

        setUp();//准备测试环境

        waitForCases();//等待所有测试用例准备完成

        try {
            runTest();//执行测试用例
            assertion();//执行断言
        } catch (TestException e) {
            testFail();//测试过程出错
            return;
        } finally {
            close();
        }
    }

    /**
     * 执行完一个测试程序.
     */
    protected void countDown() {
        latch.countDown();
    }

    /**
     * 等待其他测试用例准备完毕.
     */
    protected void waitForCases() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            TestUtil.handleInterrupt(e);
        }
    }

    protected abstract void setUp();

    protected abstract void runTest() throws TestException;

    protected abstract void testFail();

    protected abstract void assertion() throws TestException;

    protected abstract void testSuccess();

    private ExecutorService[] setUpExe() {
        ExecutorService[] exes = new ExecutorService[concurrentLevel];
        for (int i = 0; i < concurrentLevel; i++) {
            exes[i] = Executors.newFixedThreadPool(presure);
        }
        return exes;
    }

    /**
     * 关闭线程池,测试结束后调用此方法关闭.
     */
    public void close() {
        TestUtil.printToTermin("正在关闭线程池");
        for (int i = 0; i < concurrentLevel; i++) {
            exes[i].shutdownNow();
        }
        TestUtil.printToTermin("线程池关闭");
    }

    private int indexFor(int currentIndex) {
        return currentIndex % concurrentLevel;
    }

    protected ExecutorService getWorkPool(int id) {
        return exes[indexFor(id)];
    }

}
