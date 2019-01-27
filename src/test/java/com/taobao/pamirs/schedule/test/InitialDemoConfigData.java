package com.taobao.pamirs.schedule.test;

import com.taobao.pamirs.schedule.strategy.ScheduleStrategy;
import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

@SpringApplicationContext({"schedule.xml"})
public class InitialDemoConfigData extends UnitilsJUnit4 {
    protected static transient Logger log = LoggerFactory
            .getLogger(InitialDemoConfigData.class);
    @SpringBeanByName
    TBScheduleManagerFactory scheduleManagerFactory;

    public void setScheduleManagerFactory(
            TBScheduleManagerFactory tbScheduleManagerFactory) {
        this.scheduleManagerFactory = tbScheduleManagerFactory;
    }

    String baseTaskTypeName = "DemoTask";
    String beanName = "demoTaskBean";

    String taskName = baseTaskTypeName + "$TEST";
    String strategyName = baseTaskTypeName + "-Strategy";

    @Test
    public void deleteMachineStrategy() throws Exception {
        scheduleManagerFactory.start = false;
        while (!this.scheduleManagerFactory.isZookeeperInitialSucess()) {
            Thread.sleep(1000);
        }
        scheduleManagerFactory.stopServer(null);
        Thread.sleep(1000);
        this.scheduleManagerFactory.getScheduleStrategyManager()
                .deleteMachineStrategy(strategyName, true);
    }

    @Test
    public void deleteTaskType() throws Exception {
        while (!this.scheduleManagerFactory.isZookeeperInitialSucess()) {
            Thread.sleep(1000);
        }
        scheduleManagerFactory.stopServer(null);
        Thread.sleep(1000);
        this.scheduleManagerFactory.getScheduleDataManager()
                .deleteTaskType(baseTaskTypeName);

        this.scheduleManagerFactory.getScheduleStrategyManager()
                .deleteMachineStrategy(strategyName, true);
    }

    @Test
    public void initialConfigData() throws Exception {
        while (!this.scheduleManagerFactory.isZookeeperInitialSucess()) {
            Thread.sleep(1000);
        }
        scheduleManagerFactory.stopServer(null);
        Thread.sleep(1000);
        // 创建任务调度DemoTask的基本信息
        ScheduleTaskType baseTaskType = new ScheduleTaskType();
        baseTaskType.setBaseTaskType(baseTaskTypeName);
        baseTaskType.setDealBeanName(beanName);// spring 中的beanName
        baseTaskType.setHeartBeatRate(2000);
        baseTaskType.setJudgeDeadInterval(10000);
        baseTaskType.setTaskParameter("AREA=杭州,YEAR>30");
        baseTaskType.setTaskItems(ScheduleTaskType.splitTaskItem(
                "0:{TYPE=A,KIND=1},1:{TYPE=A,KIND=2},2:{TYPE=A,KIND=3},3:{TYPE=A,KIND=4}"
//                        + ",4:{TYPE=A,KIND=5},5:{TYPE=A,KIND=6},6:{TYPE=A,KIND=7},7:{TYPE=A,KIND=8}"
//                        + ",8:{TYPE=A,KIND=9},9:{TYPE=A,KIND=10}"
        ));
        this.scheduleManagerFactory.getScheduleDataManager()
                .createBaseTaskType(baseTaskType);
        log.info("创建调度任务成功:" + baseTaskType.toString());

        // 创建任务DemoTask的调度策略

        ScheduleStrategy strategy = new ScheduleStrategy();
        strategy.setStrategyName(strategyName);
        strategy.setKind(ScheduleStrategy.Kind.Schedule);
        strategy.setTaskName(taskName);
        strategy.setTaskParameter("中国");

        strategy.setNumOfSingleServer(1);
        strategy.setAssignNum(10);
        strategy.setIPList("127.0.0.1".split(","));
        this.scheduleManagerFactory.getScheduleStrategyManager()
                .createScheduleStrategy(strategy);
        log.info("创建调度策略成功:" + strategy.toString());

    }
}
