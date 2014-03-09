package com.renren.test.agent;

import java.util.Properties;

import com.renren.test.basic.TestUtil;
import com.renren.test.exception.ConfigException;

public class TestAgentStartup {

    public static ConcurrentTestCase loadCase() throws ConfigException {
        Properties p = TestUtil.getTestProperties();
        String masterClazz = p.getProperty("agent.testcase");
        return TestUtil.loadInstance(masterClazz);
    }

    public static void main(String[] args) {
        TestAgent agent = new TestAgent();
        try {
            agent.addTestCase(loadCase());
        } catch (ConfigException e) {
            e.printStackTrace();
            return;
        }
        agent.execute();
    }
}
