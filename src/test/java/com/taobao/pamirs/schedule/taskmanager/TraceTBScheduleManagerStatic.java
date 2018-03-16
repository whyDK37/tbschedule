package com.taobao.pamirs.schedule.taskmanager;

import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceTBScheduleManagerStatic extends TBScheduleManagerStatic {

    private static transient Logger log = LoggerFactory.getLogger(TraceTBScheduleManagerStatic.class);

    protected String traceId;

    public TraceTBScheduleManagerStatic(TBScheduleManagerFactory aFactory, String baseTaskType, String ownSign, IScheduleDataManager aScheduleCenter, String traceId) throws Exception {
        super(aFactory, baseTaskType, ownSign, aScheduleCenter);
        this.traceId = traceId;
    }


    /**
     * 处在了可执行的时间区间，恢复运行
     *
     * @throws Exception
     */
    public void resume(String message) throws Exception {
        if (this.isPauseSchedule) {
            if (log.isDebugEnabled()) {
                log.debug("恢复调度:" + this.currenScheduleServer.getUuid());
            }
            this.isPauseSchedule = false;
            this.pauseMessage = message;
            if (this.taskDealBean != null) {
                if (this.taskTypeInfo.getProcessorType() != null &&
                        this.taskTypeInfo.getProcessorType().equalsIgnoreCase("NOTSLEEP")) {
                    this.taskTypeInfo.setProcessorType("NOTSLEEP");
                    this.processor = new TraceTBScheduleProcessorNotSleep(this,
                            taskDealBean, this.statisticsInfo, traceId);
                } else {
                    this.processor = new TraceTBScheduleProcessorSleep(this, taskDealBean, this.statisticsInfo, traceId);
                    this.taskTypeInfo.setProcessorType("SLEEP");
                }
            }
            rewriteScheduleInfo();
        }
    }
}
