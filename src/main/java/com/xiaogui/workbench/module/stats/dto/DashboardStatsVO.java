package com.xiaogui.workbench.module.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsVO {

    // 程序员 (dev)
    private Integer todoTodayCount;      // 今日任务数
    private Integer todoDoneCount;       // 已完成任务数
    private Integer bugsOpenCount;       // 待修 Bug 数
    private Integer projectProgress;     // 项目平均进度 (%)
    private Integer techNoteCount;       // 技术笔记数
    private Integer repoCount;           // 代码仓库数

    // AI 探索 (ai)
    private Integer aiLabCount;          // AI 实验数
    private Integer ideaCount;           // 内容灵感数
    private Integer readCount;           // 学习阅读数

    // 自媒体 (media)
    private Integer mediaCount;          // 素材库数
    private Integer docsCount;           // 创作稿件数
    private Integer mediaBoardCount;     // 运营看板数
    private java.math.BigDecimal moneyMonthTotal; // 本月收支 (元，正数=收入-支出结果)
    private java.math.BigDecimal moneyMonthIncome; // 本月收入
    private java.math.BigDecimal moneyMonthExpense; // 本月支出

    // 通用
    private Integer checkinActiveCount;  // 活跃习惯数
    private Long totalRecords;           // 数据总记录数
    private Map<String, Long> moduleCounts; // 各模块记录数明细
}
