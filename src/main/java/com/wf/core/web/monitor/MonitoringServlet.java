package com.wf.core.web.monitor;

import com.alibaba.fastjson.JSON;
import com.wf.core.web.monitor.model.*;
import com.wf.core.web.monitor.model.ThreadInfo;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenlongfei
 * 获取jvm信息的MonitoringServlet
 * 用于系统监控
 */
public class MonitoringServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(MonitoringServlet.class);

    public MonitoringServlet() {
        super();
    }

    /**
     * 系统启动初始化
     */
    public void init(ServletConfig config) throws ServletException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println(monitoringJVM());
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println(monitoringJVM());
        out.close();
    }

    /**
     * @Description 获取监控信息
     * @author jiaofei
     */
    public static String monitoringJVM() {
        JvmInfo jvmInfo = new JvmInfo();
        getMemoryInfo(jvmInfo);
        getOsInfo(jvmInfo);
        getThreadInfo(jvmInfo);
        getCompilerInfo(jvmInfo);
        getMemoryPoolsInfo(jvmInfo);
        getGcMap(jvmInfo);
        getRuntimeInfo(jvmInfo);
        return JSON.toJSONString(jvmInfo);
    }

    /**
     * 获取jvm 内存信息
     *
     * @param jvm
     * @return
     * @author jiaofei
     */
    public static void getMemoryInfo(JvmInfo jvm) {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        logger.debug("Java 虚拟机内存系统的管理接口");
        // 获取jvm内存分布情况
        MemoryUsage heapMemoryUsageUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsageUsage = memoryMXBean.getNonHeapMemoryUsage();
        //Java 虚拟机内存系统的管理接口。 MemoryMXBean
        logger.debug(" 返回用于对象分配的堆的当前内存使用量:" + heapMemoryUsageUsage);
        long divisor = 1024 * 1024;
        long initHeapSize = heapMemoryUsageUsage.getInit() / divisor;//得到MB为单位的数据
        long usedHeapSize = heapMemoryUsageUsage.getUsed() / divisor;//得到MB为单位的数据
        long maxHeapSize = heapMemoryUsageUsage.getMax() / divisor;//得到MB为单位的数据
        long committedHeapSize = heapMemoryUsageUsage.getCommitted() / divisor;//得到MB为单位的数据
        JvmHeapMemoryInfo jvmHeap = new JvmHeapMemoryInfo();
        jvmHeap.setInitHeapMemory(initHeapSize);
        jvmHeap.setUsedHeapMemory(usedHeapSize);
        jvmHeap.setMaxHeapMemory(maxHeapSize);
        jvmHeap.setCommittedHeapMemory(committedHeapSize);
        jvmHeap.setUsedMaxPercent((usedHeapSize / (double) maxHeapSize));
        jvm.setJvmHeapMemoryInfo(jvmHeap);
        logger.debug("Java 虚拟机使用的非堆内存的当前内存使用量:" + nonHeapMemoryUsageUsage);
        long initNonHeapSize = nonHeapMemoryUsageUsage.getInit() / divisor;//得到MB为单位的数据
        long usedNonHeapSize = nonHeapMemoryUsageUsage.getUsed() / divisor;
        long maxNonHeapSize = nonHeapMemoryUsageUsage.getMax() / divisor;
        long committedNonHeapSize = nonHeapMemoryUsageUsage.getCommitted() / divisor;
        JvmNonHeapMemoryInfo jvmNonHeap = new JvmNonHeapMemoryInfo();
        jvmNonHeap.setInitNonHeapMemory(initNonHeapSize);
        jvmNonHeap.setUsedNonHeapMemory(usedNonHeapSize);
        jvmNonHeap.setMaxNonHeapMemory(maxNonHeapSize);
        jvmNonHeap.setCommittedHeapMemory(committedNonHeapSize);
        jvm.setJvmNonHeapMemoryInfo(jvmNonHeap);
        // 获取java参数
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        jvm.setInputArguments(inputArguments);
        logger.debug("===================java options=============== ");
        logger.debug(inputArguments);
        logger.debug("=======================通过java来获取相关系统状态============================ ");
        long totalMemory = Runtime.getRuntime().totalMemory() / divisor;// Java
        jvm.setTotalMemory(totalMemory);
        logger.debug("虚拟机总的内存量：" + totalMemory);
        long freeMemory = Runtime.getRuntime().freeMemory() / divisor;// Java
        jvm.setFreeMemory(freeMemory);
        logger.debug("虚拟机空闲内存量 ：" + freeMemory); // 虚拟机中的空闲内存量
        long maxMemory = Runtime.getRuntime().maxMemory() / divisor;
        jvm.setMaxMemory(maxMemory);
        logger.debug("虚拟机试图使用的最大内存量 ：" + maxMemory);
    }

    /**
     * 获取操作系统信息
     *
     * @return
     * @author jiaofei
     */
    public static void getOsInfo(JvmInfo jvm) {
        OsInfo osInfo = new OsInfo();
        logger.debug("=======================OperatingSystemMXBean============================ ");
        //用于操作系统的管理接口，Java 虚拟机在此操作系统上运行 OperatingSystemMXBean
        OperatingSystemMXBean op = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        logger.debug("返回操作系统的架构:" + op.getArch());
        osInfo.setArchitecture(op.getArch());
        logger.debug("返回操作系统名称:" + op.getName());
        osInfo.setName(op.getName());
        logger.debug("返回操作系统的版本:" + op.getVersion());
        osInfo.setVersion(op.getVersion());
        logger.debug("Java 虚拟机可以使用的处理器数目:" + op.getAvailableProcessors());
        osInfo.setOsProcessors(op.getAvailableProcessors());
        jvm.setOsInfo(osInfo);
    }

    /**
     * 获取各个线程的状态
     *
     * @return
     * @author jiaofei
     */
    public static void getThreadInfo(JvmInfo jvm) {
        com.wf.core.web.monitor.model.ThreadInfo threadInfo = new ThreadInfo();
        // 获取各个线程的各种状态，CPU 占用情况，以及整个系统中的线程状况
        logger.debug("=======================获取各个线程的各种状态，CPU 占用情况，以及整个系统中的线程状况============================ ");
        //Java 虚拟机线程系统的管理接口 ThreadMXBean
        ThreadMXBean th = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        logger.debug("活动线程的当前数目:" + th.getThreadCount());
        logger.debug("返回活动守护线程的当前数目:" + th.getDaemonThreadCount());
        logger.debug("Java 虚拟机启动或峰值重置以来峰值活动线程计数:" + th.getPeakThreadCount());
        double cupTime = (double) th.getCurrentThreadCpuTime() / 1000000000;//纳秒转为秒
        double userTime = (double) th.getCurrentThreadUserTime() / 1000000000;
        logger.debug("返回当前线程的总 CPU 时间(秒):" + cupTime);
        logger.debug("当前线程在用户模式中执行的 CPU 时间(秒):" + userTime);
        threadInfo.setCurrentThreadCpuTime(cupTime);
        threadInfo.setCurrentThreadUserTime(userTime);
        threadInfo.setThreadCount(th.getThreadCount());
        threadInfo.setPeakThreadCount(th.getPeakThreadCount());
        threadInfo.setDaemonThreadCount(th.getDaemonThreadCount());
        jvm.setThreadInfo(threadInfo);
    }

    /**
     * 获取编译器信息
     *
     * @return
     * @author jiaofei
     */
    public static void getCompilerInfo(JvmInfo jvm) {
        logger.debug("=======================CompilationMXBean============================ ");
        // Java 虚拟机的编译系统的管理接口 CompilationMXBean
        CompilationMXBean com = (CompilationMXBean) ManagementFactory.getCompilationMXBean();
        logger.debug("即时 (JIT) 编译器的名称:" + com.getName());
        double totalCompilationTime = (double) com.getTotalCompilationTime() / 1000;
        logger.debug("在编译上花费的累积耗费时间的近似值(秒):" + totalCompilationTime);
        jvm.setTotalCompilationTime(totalCompilationTime);
        jvm.setCompilationName(com.getName());
    }

    /**
     * 获取内存池使用情况
     *
     * @return
     * @author jiaofei
     */
    public static void getMemoryPoolsInfo(JvmInfo jvm) {
        List<JvmMemoryPoolInfo> poolInfoList = new ArrayList<JvmMemoryPoolInfo>();
        logger.debug("=======================MemoryPoolMXBean============================ ");
        //内存池的管理接口。内存池表示由 Java 虚拟机管理的内存资源，
        //由一个或多个内存管理器对内存池进行管理 MemoryPoolMXBean
        List<MemoryPoolMXBean> list = ManagementFactory.getMemoryPoolMXBeans();
        for (int i = 1; i <= list.size(); i++) {
            String name = list.get(i - 1).getName();
            String type = list.get(i - 1).getType() + "";
            String peakUsage = list.get(i - 1).getPeakUsage() + "";
            String usage = list.get(i - 1).getUsage() + "";
            JvmMemoryPoolInfo poolInfo = new JvmMemoryPoolInfo();
            poolInfo.setName(name);
            poolInfo.setType(type);
            poolInfo.setPeakUsage(peakUsage);
            poolInfo.setUsage(usage);
            poolInfoList.add(poolInfo);
            logger.debug("返回此内存池的类型:" + type);
            logger.debug("Java 虚拟机启动以来或自峰值重置以来此内存池的峰值内存使用量:" + peakUsage);
            logger.debug("内存使用量超过其阈值的次数:" + usage);
        }
        jvm.setJvmMemoryPoolInfoList(poolInfoList);
    }

    /**
     * 获取gc时的信息
     *
     * @return
     * @author jiaofei
     */
    public static void getGcMap(JvmInfo jvm) {
        List<GcInfo> gcInfoList = new ArrayList<>();
        // 获取GC的次数以及花费时间之类的信息
        logger.debug("=======================gc时的信息============================ ");
        List<GarbageCollectorMXBean> gcmList = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcm : gcmList) {
            GcInfo gcInfo = new GcInfo();
            logger.debug("getName " + gcm.getName());
            gcInfo.setName(gcm.getName());
            gcInfo.setCollectionCount(gcm.getCollectionCount());
            gcInfo.setCollectionTime((double) gcm.getCollectionTime());
            gcInfo.setMemoryPoolName(gcm.getMemoryPoolNames());
            gcInfoList.add(gcInfo);
        }
        jvm.setGcInfoList(gcInfoList);
    }

    /**
     * 获取运行时信息
     *
     * @return
     * @author jiaofei
     */
    public static void getRuntimeInfo(JvmInfo jvm) {
        logger.debug("=======================Runtime信息============================ ");
        //Java 虚拟机的运行时系统的管理接口。 RuntimeMXBean
        RuntimeMXBean run = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();
        logger.debug("正在运行的 Java 虚拟机标准名称:" + run.getName());
        logger.debug("Java 虚拟机名称:" + run.getVmName());
        logger.debug("Java 虚拟机Version:" + run.getVmVersion());
        logger.debug("返回 Java 库路径:" + run.getLibraryPath());
        logger.debug("系统类加载器用于搜索类文件的 Java 类路径:" + run.getClassPath());
        jvm.setJvmName(run.getVmName());
        jvm.setJvmVersion(run.getVmVersion());
    }

    public static void main(String[] args) {
        monitoringJVM();
    }
}


