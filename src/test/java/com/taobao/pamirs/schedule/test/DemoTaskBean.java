package com.taobao.pamirs.schedule.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.taobao.pamirs.schedule.TraceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;

/**
 * 单个任务处理实现
 * 
 * @author xuannan
 * 
 */
public class DemoTaskBean implements IScheduleTaskDealSingle<Long> {
	protected static transient Logger log = LoggerFactory.getLogger(DemoTaskBean.class);

	public Comparator<Long> getComparator() {
		return new Comparator<Long>() {
			public int compare(Long o1, Long o2) {
				return o1.compareTo(o2);
			}

			public boolean equals(Object obj) {
				return this == obj;
			}
		};
	}

	boolean b = false;
	public List<Long> selectTasks(String taskParameter,String ownSign, int taskItemNum,
			List<TaskItemDefine> queryCondition, int fetchNum) throws Exception {
//		if(b) return null;

		String traceId = TraceId.get();
		List<Long> result = new ArrayList<Long>();
		int num = fetchNum / queryCondition.size();
		Random random = new Random(System.currentTimeMillis());
		StringBuilder message = new StringBuilder("[" + traceId + "]获取数据...[ownSign=" + ownSign + ",taskParameter=\"" + taskParameter + "\"]:");
		boolean isFirst = true;
		for (TaskItemDefine s : queryCondition) {
			long taskItem = Integer.parseInt(s.getTaskItemId()) * 10000000L;
			for (int i = 0; i < num; i++) {
				result.add(taskItem + random.nextLong()% 100000L);
			}
			if (!isFirst) {
				message.append(",");
			}else{
				isFirst = false;
			}
			message.append(s.getTaskItemId()).append("{").append(s.getParameter()).append("}");
		}
		log.info(message.toString());

		b = true;
		return result;
	}

	public boolean execute(Long task, String ownSign) throws Exception {
		Thread.sleep(50);
		String traceId = TraceId.get();
		log.info("[" + traceId + "] 处理任务[" + ownSign + "]:" + task);
		return true;
	}
}