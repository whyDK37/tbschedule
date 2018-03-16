package com.taobao.pamirs.schedule.strategy;

import com.taobao.pamirs.schedule.ScheduleUtil;
import com.taobao.pamirs.schedule.taskmanager.TraceTBScheduleManagerStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在执行调度是, 设置trace id, 便于追踪调用上下文.
 */
public class TraceTBScheduleManagerFactory extends TBScheduleManagerFactory {

    /**
     * 创建调度服务器
     *
     * @param strategy
     * @return
     * @throws Exception
     */
    public IStrategyTask createStrategyTask(ScheduleStrategy strategy)
            throws Exception {
        return createStrategyTask(strategy, UUID.randomUUID().toString());
    }

    public IStrategyTask createStrategyTask(ScheduleStrategy strategy, String traceId)
            throws Exception {
        IStrategyTask result = null;
        try {
            if (ScheduleStrategy.Kind.Schedule == strategy.getKind()) {
                String baseTaskType = ScheduleUtil.splitBaseTaskTypeFromTaskType(strategy.getTaskName());
                String ownSign = ScheduleUtil.splitOwnsignFromTaskType(strategy.getTaskName());
                result = new TraceTBScheduleManagerStatic(this, baseTaskType, ownSign, getScheduleDataManager(), traceId);
            } else if (ScheduleStrategy.Kind.Java == strategy.getKind()) {
                result = (IStrategyTask) Class.forName(strategy.getTaskName()).newInstance();
                result.initialTaskParameter(strategy.getStrategyName(), strategy.getTaskParameter());
            } else if (ScheduleStrategy.Kind.Bean == strategy.getKind()) {
                result = (IStrategyTask) this.getBean(strategy.getTaskName());
                result.initialTaskParameter(strategy.getStrategyName(), strategy.getTaskParameter());
            }
        } catch (Exception e) {
            logger.error("strategy 获取对应的java or bean 出错,schedule并没有加载该任务,请确认" + strategy.getStrategyName(), e);
        }
        return result;
    }


    private Map<String, List<IStrategyTask>> managerMap = new ConcurrentHashMap<String, List<IStrategyTask>>();


    @Override
    public void reRunScheduleServer() throws Exception {
        for (ScheduleStrategyRunntime run : this.getScheduleStrategyManager().loadAllScheduleStrategyRunntimeByUUID(getUuid())) {
            List<IStrategyTask> list = this.managerMap.get(run.getStrategyName());
            if (list == null) {
                list = new ArrayList<IStrategyTask>();
                this.managerMap.put(run.getStrategyName(), list);
            }
            while (list.size() > run.getRequestNum() && !list.isEmpty()) {
                IStrategyTask task = list.remove(list.size() - 1);
                try {
                    task.stop(run.getStrategyName());
                } catch (Throwable e) {
                    logger.error("注销任务错误：strategyName=" + run.getStrategyName(), e);
                }
            }
            //不足，增加调度器
            ScheduleStrategy strategy = this.getScheduleStrategyManager().loadStrategy(run.getStrategyName());

            String traceId = UUID.randomUUID().toString();
            System.err.println("traceId:" + traceId);
            while (list.size() < run.getRequestNum()) {
                IStrategyTask result = this.createStrategyTask(strategy, traceId);
                if (result == null) {
                    logger.error("strategy 对应的配置有问题。 strategy name=" + strategy.getStrategyName());
                }
                list.add(result);
            }
        }
    }
}
