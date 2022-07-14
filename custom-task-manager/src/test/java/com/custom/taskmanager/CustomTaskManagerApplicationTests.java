package com.custom.taskmanager;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.comm.CustomUtil;
import com.custom.comm.date.DateTimeUtils;
import com.custom.taskmanager.entity.TaskRecord;
import com.custom.taskmanager.enums.TaskProgressEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomTaskManagerApplicationTests {

    @Resource
    JdbcDao jdbcDao;

    @Test
    public void test01  () throws Exception {

        for (int i = 0; i < 1; i++) {
            TaskRecord taskRecord = new TaskRecord();
            taskRecord.setTaskCode(CustomUtil.getUUID());
            String content = CustomUtil.getUUID().substring(5, 20);
            taskRecord.setTaskTitle("任务标题" + content);
            taskRecord.setTaskContent("任务内容" + content);
            Random random = new Random();
            int progress = random.nextInt(5);
            taskRecord.setCurrentProgress(progress);
            taskRecord.setDifficulty(random.nextInt(3));
            taskRecord.setPriority(random.nextInt(3));
            if (taskRecord.getCurrentProgress().equals(TaskProgressEnum.UN_FINISHED.getCode())) {
                taskRecord.setReason("计划赶不上变化");
            }
            else if (taskRecord.getCurrentProgress().equals(TaskProgressEnum.FINISH_TO_CHECK.getCode())) {
                taskRecord.setTestResult("完成测验，暂无bug才怪");
            }
            int thisTime = DateTimeUtils.getThisTime();
            taskRecord.setStartTime(thisTime);
            taskRecord.setEndTime(thisTime + 96400 * 7);
//            taskRecord.setCreateTime(thisTime);
//            taskRecord.setOperatorTime(thisTime);
            jdbcDao.insert(taskRecord);
        }




    }

}
