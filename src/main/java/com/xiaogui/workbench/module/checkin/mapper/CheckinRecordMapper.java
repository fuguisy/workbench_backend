package com.xiaogui.workbench.module.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaogui.workbench.module.checkin.entity.CheckinRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {

    /**
     * 物理删除打卡记录（绕过 MyBatis-Plus 逻辑删除）。
     * 取消打卡时使用，避免唯一约束冲突。
     */
    @Delete("DELETE FROM checkin_record WHERE id = #{id}")
    int physicalDeleteById(Long id);

    /**
     * 按 (userId, habitId, checkDate) 物理删除，清理逻辑删除残留的脏数据。
     */
    @Delete("DELETE FROM checkin_record WHERE user_id = #{userId} AND habit_id = #{habitId} AND check_date = #{checkDate}")
    int physicalDeleteByKey(@Param("userId") Long userId, @Param("habitId") Long habitId, @Param("checkDate") LocalDate checkDate);
}
