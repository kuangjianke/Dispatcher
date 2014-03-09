package com.renren.test.master;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.ParseException;

import com.renren.test.basic.TestProtocol;
import com.renren.test.basic.TestUtil;
import com.renren.test.exception.ConfigException;

public class TestMasterCmdLine {

    private static Options opt;
    private static HelpFormatter helper = new HelpFormatter();
    private static CommandLineParser parser = new PosixParser();

    public static void main(String[] args) {
        opt = createOptions();
        responseCmd(args);
    }

    public static Options createOptions() {
        Options opt = new Options();
        opt.addOption("b", TestProtocol.BEGIN, false, "开始测试");
        opt.addOption("r", TestProtocol.RESET, false, "重置测试");
        opt.addOption("s", TestProtocol.STOP, false, "停止测试");
        opt.addOption("h", "help", false, "查看帮助");
        return opt;
    }

    /**
     * 加载master实现类.
     * @return
     * @throws ConfigException
     */
    public static TestMaster loadMaster() throws ConfigException {
        Properties p = TestUtil.getTestProperties();
        String masterClazz = p.getProperty("master");
        return TestUtil.loadInstance(masterClazz);
    }

    /**
     * 解析命令行参数.
     * @param args
     */
    public static void responseCmd(String[] args) {
        CommandLine cl = null;
        try {
            cl = parser.parse(opt, args);
        } catch (ParseException e) {
            helper.printHelp("options", opt);
            return;
        }
        TestMaster suit = null;
        try {
            suit = loadMaster();
        } catch (ConfigException e) {
            e.printStackTrace();
            return;
        }
        if (cl.hasOption("b")) {
            TestUtil.printToTermin("开始测试");
            suit.test();
        } else if (cl.hasOption("r")) {
            suit.reset();
        } else if (cl.hasOption("s")) {
            suit.stop();
        } else {
            helper.printHelp("options", opt);
        }
        suit.close();
        System.exit(1);
    }
}
