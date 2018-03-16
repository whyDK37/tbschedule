package com.taobao.pamirs.schedule.test;


import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

/**
 * 调度测试, 测试在调度中增加 trace id, 便于追踪调用链
 * @author xuannan
 *
 */
@SpringApplicationContext( { "schedule-trace.xml" })
public class TraceStartDemoSchedule extends UnitilsJUnit4{
	@SpringBeanByName
	TBScheduleManagerFactory scheduleManagerFactory;
	
    public void setScheduleManagerFactory(
			TBScheduleManagerFactory tbScheduleManagerFactory) {
		this.scheduleManagerFactory = tbScheduleManagerFactory;
	}
	@Test    
	public void testRunData() throws Exception {
		Thread.sleep(100000000000000L);
	}
}
