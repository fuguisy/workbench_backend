package com.xiaogui.workbench.module.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsightStatsVO {

    // 总览卡片
    private Overview overview;

    // 近7日每日新增趋势 (日期 -> 数量)
    private List<TrendPoint> last7DaysTrend;

    // 按模块分组的记录数 (模块名 -> 数量)
    private Map<String, Long> moduleDistribution;

    // 任务完成率统计
    private TodoStats todoStats;

    // 收支分类统计
    private MoneyStats moneyStats;

    // 项目进度排行 Top5
    private List<ProjectRank> projectTop5;

    // 自媒体产出统计
    private MediaStats mediaStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Overview {
        private Long totalRecords;
        private Long thisWeekNew;
        private Long thisMonthNew;
        private Long doneTodoCount;
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPoint {
        private String date;       // MM-dd
        private Long todoCount;    // 任务
        private Long ideaCount;    // 灵感
        private Long noteCount;    // 笔记(技术+稿件)
        private Long moneyCount;   // 记账笔数
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodoStats {
        private Long total;
        private Long done;
        private Long doing;
        private Long pending;
        private Long overdue;
        private Map<String, Long> priorityCount; // P0/P1/P2/P3 数量
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoneyStats {
        private BigDecimal income;
        private BigDecimal expense;
        private Map<String, BigDecimal> incomeByCategory;
        private Map<String, BigDecimal> expenseByCategory;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectRank {
        private String title;
        private Integer current;
        private Integer target;
        private Integer percent;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaStats {
        private Long ideaCount;     // 灵感素材
        private Long mediaCount;    // 素材库
        private Long docsCount;     // 稿件
        private Long boardCount;    // 运营数据
    }
}
