package com.renren.test.basic;

/**
 * 测试状态协议
 *
 * @author yu.zhang@renren-inc.com
 * 2012-8-24 下午4:42:34
 */
public class TestProtocol {

    //master发出的命令
    public static final String BEGIN    = "begin";
    public static final String RESET    = "reset";
    public static final String STOP     = "stop";

    //agent执行状态
    public static final String RUNNABLE = "runnable";
    public static final String RUNNING  = "running";
    public static final String FINISHED = "finished";
}
