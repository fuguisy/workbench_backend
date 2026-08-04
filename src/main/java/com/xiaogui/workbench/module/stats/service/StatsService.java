package com.xiaogui.workbench.module.stats.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaogui.workbench.module.ai.entity.AiLab;
import com.xiaogui.workbench.module.ai.mapper.AiLabMapper;
import com.xiaogui.workbench.module.backup.entity.BackupLog;
import com.xiaogui.workbench.module.backup.mapper.BackupLogMapper;
import com.xiaogui.workbench.module.bugs.entity.Bugs;
import com.xiaogui.workbench.module.bugs.mapper.BugsMapper;
import com.xiaogui.workbench.module.checkin.entity.Checkin;
import com.xiaogui.workbench.module.checkin.mapper.CheckinMapper;
import com.xiaogui.workbench.module.docs.entity.Docs;
import com.xiaogui.workbench.module.docs.mapper.DocsMapper;
import com.xiaogui.workbench.module.idea.entity.Idea;
import com.xiaogui.workbench.module.idea.mapper.IdeaMapper;
import com.xiaogui.workbench.module.media.entity.Media;
import com.xiaogui.workbench.module.media.mapper.MediaMapper;
import com.xiaogui.workbench.module.mediaboard.entity.MediaBoard;
import com.xiaogui.workbench.module.mediaboard.mapper.MediaBoardMapper;
import com.xiaogui.workbench.module.money.entity.Money;
import com.xiaogui.workbench.module.money.mapper.MoneyMapper;
import com.xiaogui.workbench.module.project.entity.Project;
import com.xiaogui.workbench.module.project.mapper.ProjectMapper;
import com.xiaogui.workbench.module.read.entity.ReadProgress;
import com.xiaogui.workbench.module.read.mapper.ReadProgressMapper;
import com.xiaogui.workbench.module.repo.entity.Repo;
import com.xiaogui.workbench.module.repo.mapper.RepoMapper;
import com.xiaogui.workbench.module.secrets.entity.Secrets;
import com.xiaogui.workbench.module.secrets.mapper.SecretsMapper;
import com.xiaogui.workbench.module.stats.dto.DashboardStatsVO;
import com.xiaogui.workbench.module.stats.dto.InsightStatsVO;
import com.xiaogui.workbench.module.svc.entity.Svc;
import com.xiaogui.workbench.module.svc.mapper.SvcMapper;
import com.xiaogui.workbench.module.tech.entity.Tech;
import com.xiaogui.workbench.module.tech.mapper.TechMapper;
import com.xiaogui.workbench.module.todo.entity.Todo;
import com.xiaogui.workbench.module.todo.mapper.TodoMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    @Resource private TodoMapper todoMapper;
    @Resource private ProjectMapper projectMapper;
    @Resource private BugsMapper bugsMapper;
    @Resource private RepoMapper repoMapper;
    @Resource private SvcMapper svcMapper;
    @Resource private ReadProgressMapper readMapper;
    @Resource private TechMapper techMapper;
    @Resource private AiLabMapper aiMapper;
    @Resource private IdeaMapper ideaMapper;
    @Resource private MediaMapper mediaMapper;
    @Resource private MediaBoardMapper mediaBoardMapper;
    @Resource private DocsMapper docsMapper;
    @Resource private CheckinMapper checkinMapper;
    @Resource private SecretsMapper secretsMapper;
    @Resource private BackupLogMapper backupMapper;
    @Resource private MoneyMapper moneyMapper;

    private static int safeInt(Integer i) { return i == null ? 0 : i; }
    /**
     * 安全地将任意 Number（Long / long / Integer / int）转为 int，
     * 超出 int 范围时抛 ArithmeticException；为 null 时返回 0。
     */
    private static int toInt(Number n) {
        return n == null ? 0 : Math.toIntExact(n.longValue());
    }

    // ===================== 首页 Dashboard =====================
    public DashboardStatsVO dashboard(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();

        // 任务
        long todoToday = todoMapper.selectCount(new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId).ge(Todo::getCreateTime, dayStart).lt(Todo::getCreateTime, dayEnd));
        long todoDone = todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).eq(Todo::getDone, 1));

        // Bug待修
        long bugsOpen = bugsMapper.selectCount(new LambdaQueryWrapper<Bugs>()
                .eq(Bugs::getUserId, userId)
                .notIn(Bugs::getStage, Arrays.asList("已修复", "已关闭", "完成")));

        // 项目平均进度
        List<Project> projects = projectMapper.selectList(new LambdaQueryWrapper<Project>().eq(Project::getUserId, userId));
        int avgProgress = 0;
        if (!projects.isEmpty()) {
            List<Project> valid = projects.stream().filter(p -> p.getTarget() != null && p.getTarget() > 0).collect(Collectors.toList());
            if (!valid.isEmpty()) {
                int sum = valid.stream().mapToInt(p -> Math.min(100, (safeInt(p.getCurrent()) * 100) / p.getTarget())).sum();
                avgProgress = sum / valid.size();
            }
        }

        int techCnt = toInt(techMapper.selectCount(new LambdaQueryWrapper<Tech>().eq(Tech::getUserId, userId)));
        int repoCnt = toInt(repoMapper.selectCount(new LambdaQueryWrapper<Repo>().eq(Repo::getUserId, userId)));
        int aiCnt = toInt(aiMapper.selectCount(new LambdaQueryWrapper<AiLab>().eq(AiLab::getUserId, userId)));
        int ideaCnt = toInt(ideaMapper.selectCount(new LambdaQueryWrapper<Idea>().eq(Idea::getUserId, userId)));
        int readCnt = toInt(readMapper.selectCount(new LambdaQueryWrapper<ReadProgress>().eq(ReadProgress::getUserId, userId)));
        int mediaCnt = toInt(mediaMapper.selectCount(new LambdaQueryWrapper<Media>().eq(Media::getUserId, userId)));
        int docsCnt = toInt(docsMapper.selectCount(new LambdaQueryWrapper<Docs>().eq(Docs::getUserId, userId)));
        int boardCnt = toInt(mediaBoardMapper.selectCount(new LambdaQueryWrapper<MediaBoard>().eq(MediaBoard::getUserId, userId)));
        int checkinCnt = toInt(checkinMapper.selectCount(new LambdaQueryWrapper<Checkin>().eq(Checkin::getUserId, userId)));

        // 本月收支
        String ym = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<Money> monthMoneys = moneyMapper.selectList(new LambdaQueryWrapper<Money>()
                .eq(Money::getUserId, userId).likeRight(Money::getDate, ym));
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        for (Money m : monthMoneys) {
            if (m.getAmount() == null) continue;
            if ("收入".equals(m.getType())) income = income.add(m.getAmount());
            else expense = expense.add(m.getAmount());
        }

        // 各模块记录数 + 总数
        Map<String, Long> moduleCounts = new LinkedHashMap<>();
        moduleCounts.put("todo", todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId)));
        moduleCounts.put("project", (long) projects.size());
        moduleCounts.put("bugs", bugsMapper.selectCount(new LambdaQueryWrapper<Bugs>().eq(Bugs::getUserId, userId)));
        moduleCounts.put("repo", (long) repoCnt);
        moduleCounts.put("svc", svcMapper.selectCount(new LambdaQueryWrapper<Svc>().eq(Svc::getUserId, userId)));
        moduleCounts.put("read", (long) readCnt);
        moduleCounts.put("tech", (long) techCnt);
        moduleCounts.put("ai", (long) aiCnt);
        moduleCounts.put("idea", (long) ideaCnt);
        moduleCounts.put("media", (long) mediaCnt);
        moduleCounts.put("mediaBoard", (long) boardCnt);
        moduleCounts.put("docs", (long) docsCnt);
        moduleCounts.put("checkin", (long) checkinCnt);
        moduleCounts.put("secrets", secretsMapper.selectCount(new LambdaQueryWrapper<Secrets>().eq(Secrets::getUserId, userId)));
        moduleCounts.put("backup", backupMapper.selectCount(new LambdaQueryWrapper<BackupLog>().eq(BackupLog::getUserId, userId)));
        moduleCounts.put("money", moneyMapper.selectCount(new LambdaQueryWrapper<Money>().eq(Money::getUserId, userId)));
        long total = moduleCounts.values().stream().mapToLong(Long::longValue).sum();

        return DashboardStatsVO.builder()
                .todoTodayCount(toInt(todoToday))
                .todoDoneCount(toInt(todoDone))
                .bugsOpenCount(toInt(bugsOpen))
                .projectProgress(avgProgress)
                .techNoteCount(techCnt)
                .repoCount(repoCnt)
                .aiLabCount(aiCnt)
                .ideaCount(ideaCnt)
                .readCount(readCnt)
                .mediaCount(mediaCnt)
                .docsCount(docsCnt)
                .mediaBoardCount(boardCnt)
                .moneyMonthTotal(income.subtract(expense))
                .moneyMonthIncome(income)
                .moneyMonthExpense(expense)
                .checkinActiveCount(checkinCnt)
                .totalRecords(total)
                .moduleCounts(moduleCounts)
                .build();
    }

    // ===================== 洞察复盘 =====================
    public InsightStatsVO insight(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime weekStart = today.minusDays(6).atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime nowEnd = today.plusDays(1).atStartOfDay();

        long total = sumAll(userId);
        long weekNew = countAllCreatedBetween(userId, weekStart, nowEnd);
        long monthNew = countAllCreatedBetween(userId, monthStart, nowEnd);
        long doneTodo = todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).eq(Todo::getDone, 1));

        List<Money> allMoney = moneyMapper.selectList(new LambdaQueryWrapper<Money>().eq(Money::getUserId, userId));
        BigDecimal inc = BigDecimal.ZERO, exp = BigDecimal.ZERO;
        Map<String, BigDecimal> incMap = new HashMap<>();
        Map<String, BigDecimal> expMap = new HashMap<>();
        for (Money m : allMoney) {
            if (m.getAmount() == null) continue;
            String cat = (m.getCategory() == null || m.getCategory().isBlank()) ? "其他" : m.getCategory();
            if ("收入".equals(m.getType())) {
                inc = inc.add(m.getAmount());
                incMap.merge(cat, m.getAmount(), BigDecimal::add);
            } else {
                exp = exp.add(m.getAmount());
                expMap.merge(cat, m.getAmount(), BigDecimal::add);
            }
        }

        List<InsightStatsVO.TrendPoint> trend = build7DaysTrend(userId, today);

        Map<String, Long> dist = new LinkedHashMap<>();
        dist.put("任务待办", todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId)));
        dist.put("项目进度", projectMapper.selectCount(new LambdaQueryWrapper<Project>().eq(Project::getUserId, userId)));
        dist.put("Bug台账", bugsMapper.selectCount(new LambdaQueryWrapper<Bugs>().eq(Bugs::getUserId, userId)));
        dist.put("代码仓库", repoMapper.selectCount(new LambdaQueryWrapper<Repo>().eq(Repo::getUserId, userId)));
        dist.put("服务器", svcMapper.selectCount(new LambdaQueryWrapper<Svc>().eq(Svc::getUserId, userId)));
        dist.put("学习阅读", readMapper.selectCount(new LambdaQueryWrapper<ReadProgress>().eq(ReadProgress::getUserId, userId)));
        dist.put("技术笔记", techMapper.selectCount(new LambdaQueryWrapper<Tech>().eq(Tech::getUserId, userId)));
        dist.put("AI实验", aiMapper.selectCount(new LambdaQueryWrapper<AiLab>().eq(AiLab::getUserId, userId)));
        dist.put("内容灵感", ideaMapper.selectCount(new LambdaQueryWrapper<Idea>().eq(Idea::getUserId, userId)));
        dist.put("素材库", mediaMapper.selectCount(new LambdaQueryWrapper<Media>().eq(Media::getUserId, userId)));
        dist.put("自媒体看板", mediaBoardMapper.selectCount(new LambdaQueryWrapper<MediaBoard>().eq(MediaBoard::getUserId, userId)));
        dist.put("创作稿件", docsMapper.selectCount(new LambdaQueryWrapper<Docs>().eq(Docs::getUserId, userId)));
        dist.put("习惯打卡", checkinMapper.selectCount(new LambdaQueryWrapper<Checkin>().eq(Checkin::getUserId, userId)));
        dist.put("收支记账", moneyMapper.selectCount(new LambdaQueryWrapper<Money>().eq(Money::getUserId, userId)));

        long todoAll = todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId));
        long todoDoing = todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).eq(Todo::getDone, 0));
        Map<String, Long> priCount = new LinkedHashMap<>();
        priCount.put("P0", todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).eq(Todo::getPriority, "P0")));
        priCount.put("P1", todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).eq(Todo::getPriority, "P1")));
        priCount.put("P2", todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).eq(Todo::getPriority, "P2")));
        priCount.put("P3", todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).eq(Todo::getPriority, "P3")));
        long overdue = todoMapper.selectCount(new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId).eq(Todo::getDone, 0)
                .lt(Todo::getDeadline, today.toString())
                .isNotNull(Todo::getDeadline));

        List<Project> ps = projectMapper.selectList(new LambdaQueryWrapper<Project>().eq(Project::getUserId, userId));
        List<InsightStatsVO.ProjectRank> top5 = ps.stream()
                .map(p -> {
                    int tgt = p.getTarget() == null ? 0 : p.getTarget();
                    int cur = safeInt(p.getCurrent());
                    int percent = tgt > 0 ? Math.min(100, (cur * 100) / tgt) : 0;
                    return InsightStatsVO.ProjectRank.builder()
                            .title(p.getTitle()).current(cur).target(tgt)
                            .percent(percent).status(p.getStatus() == null ? "进行中" : p.getStatus()).build();
                })
                .sorted(Comparator.comparingInt(InsightStatsVO.ProjectRank::getPercent).reversed())
                .limit(5).collect(Collectors.toList());

        InsightStatsVO.TodoStats todoStats = InsightStatsVO.TodoStats.builder()
                .total(todoAll).done(doneTodo).doing(todoDoing)
                .pending(Math.max(0, todoDoing - overdue))
                .overdue(overdue).priorityCount(priCount).build();

        InsightStatsVO.MoneyStats moneyStats = InsightStatsVO.MoneyStats.builder()
                .income(inc).expense(exp)
                .incomeByCategory(incMap).expenseByCategory(expMap).build();

        InsightStatsVO.MediaStats mediaStats = InsightStatsVO.MediaStats.builder()
                .ideaCount(ideaMapper.selectCount(new LambdaQueryWrapper<Idea>().eq(Idea::getUserId, userId)))
                .mediaCount(mediaMapper.selectCount(new LambdaQueryWrapper<Media>().eq(Media::getUserId, userId)))
                .docsCount(docsMapper.selectCount(new LambdaQueryWrapper<Docs>().eq(Docs::getUserId, userId)))
                .boardCount(mediaBoardMapper.selectCount(new LambdaQueryWrapper<MediaBoard>().eq(MediaBoard::getUserId, userId)))
                .build();

        InsightStatsVO.Overview overview = InsightStatsVO.Overview.builder()
                .totalRecords(total).thisWeekNew(weekNew).thisMonthNew(monthNew)
                .doneTodoCount(doneTodo).totalIncome(inc).totalExpense(exp).build();

        return InsightStatsVO.builder()
                .overview(overview)
                .last7DaysTrend(trend)
                .moduleDistribution(dist)
                .todoStats(todoStats)
                .moneyStats(moneyStats)
                .projectTop5(top5)
                .mediaStats(mediaStats)
                .build();
    }

    // ===================== 内部辅助 =====================
    private long sumAll(Long uid) {
        return todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, uid))
                + projectMapper.selectCount(new LambdaQueryWrapper<Project>().eq(Project::getUserId, uid))
                + bugsMapper.selectCount(new LambdaQueryWrapper<Bugs>().eq(Bugs::getUserId, uid))
                + repoMapper.selectCount(new LambdaQueryWrapper<Repo>().eq(Repo::getUserId, uid))
                + svcMapper.selectCount(new LambdaQueryWrapper<Svc>().eq(Svc::getUserId, uid))
                + readMapper.selectCount(new LambdaQueryWrapper<ReadProgress>().eq(ReadProgress::getUserId, uid))
                + techMapper.selectCount(new LambdaQueryWrapper<Tech>().eq(Tech::getUserId, uid))
                + aiMapper.selectCount(new LambdaQueryWrapper<AiLab>().eq(AiLab::getUserId, uid))
                + ideaMapper.selectCount(new LambdaQueryWrapper<Idea>().eq(Idea::getUserId, uid))
                + mediaMapper.selectCount(new LambdaQueryWrapper<Media>().eq(Media::getUserId, uid))
                + mediaBoardMapper.selectCount(new LambdaQueryWrapper<MediaBoard>().eq(MediaBoard::getUserId, uid))
                + docsMapper.selectCount(new LambdaQueryWrapper<Docs>().eq(Docs::getUserId, uid))
                + checkinMapper.selectCount(new LambdaQueryWrapper<Checkin>().eq(Checkin::getUserId, uid))
                + secretsMapper.selectCount(new LambdaQueryWrapper<Secrets>().eq(Secrets::getUserId, uid))
                + backupMapper.selectCount(new LambdaQueryWrapper<BackupLog>().eq(BackupLog::getUserId, uid))
                + moneyMapper.selectCount(new LambdaQueryWrapper<Money>().eq(Money::getUserId, uid));
    }

    private long countAllCreatedBetween(Long uid, LocalDateTime s, LocalDateTime e) {
        return todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, uid).ge(Todo::getCreateTime, s).lt(Todo::getCreateTime, e))
                + projectMapper.selectCount(new LambdaQueryWrapper<Project>().eq(Project::getUserId, uid).ge(Project::getCreateTime, s).lt(Project::getCreateTime, e))
                + bugsMapper.selectCount(new LambdaQueryWrapper<Bugs>().eq(Bugs::getUserId, uid).ge(Bugs::getCreateTime, s).lt(Bugs::getCreateTime, e))
                + repoMapper.selectCount(new LambdaQueryWrapper<Repo>().eq(Repo::getUserId, uid).ge(Repo::getCreateTime, s).lt(Repo::getCreateTime, e))
                + svcMapper.selectCount(new LambdaQueryWrapper<Svc>().eq(Svc::getUserId, uid).ge(Svc::getCreateTime, s).lt(Svc::getCreateTime, e))
                + readMapper.selectCount(new LambdaQueryWrapper<ReadProgress>().eq(ReadProgress::getUserId, uid).ge(ReadProgress::getCreateTime, s).lt(ReadProgress::getCreateTime, e))
                + techMapper.selectCount(new LambdaQueryWrapper<Tech>().eq(Tech::getUserId, uid).ge(Tech::getCreateTime, s).lt(Tech::getCreateTime, e))
                + aiMapper.selectCount(new LambdaQueryWrapper<AiLab>().eq(AiLab::getUserId, uid).ge(AiLab::getCreateTime, s).lt(AiLab::getCreateTime, e))
                + ideaMapper.selectCount(new LambdaQueryWrapper<Idea>().eq(Idea::getUserId, uid).ge(Idea::getCreateTime, s).lt(Idea::getCreateTime, e))
                + mediaMapper.selectCount(new LambdaQueryWrapper<Media>().eq(Media::getUserId, uid).ge(Media::getCreateTime, s).lt(Media::getCreateTime, e))
                + mediaBoardMapper.selectCount(new LambdaQueryWrapper<MediaBoard>().eq(MediaBoard::getUserId, uid).ge(MediaBoard::getCreateTime, s).lt(MediaBoard::getCreateTime, e))
                + docsMapper.selectCount(new LambdaQueryWrapper<Docs>().eq(Docs::getUserId, uid).ge(Docs::getCreateTime, s).lt(Docs::getCreateTime, e))
                + checkinMapper.selectCount(new LambdaQueryWrapper<Checkin>().eq(Checkin::getUserId, uid).ge(Checkin::getCreateTime, s).lt(Checkin::getCreateTime, e))
                + secretsMapper.selectCount(new LambdaQueryWrapper<Secrets>().eq(Secrets::getUserId, uid).ge(Secrets::getCreateTime, s).lt(Secrets::getCreateTime, e))
                + backupMapper.selectCount(new LambdaQueryWrapper<BackupLog>().eq(BackupLog::getUserId, uid).ge(BackupLog::getCreateTime, s).lt(BackupLog::getCreateTime, e))
                + moneyMapper.selectCount(new LambdaQueryWrapper<Money>().eq(Money::getUserId, uid).ge(Money::getCreateTime, s).lt(Money::getCreateTime, e));
    }

    private List<InsightStatsVO.TrendPoint> build7DaysTrend(Long uid, LocalDate today) {
        List<InsightStatsVO.TrendPoint> list = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            LocalDateTime s = d.atStartOfDay();
            LocalDateTime e = d.plusDays(1).atStartOfDay();
            long t = todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, uid).ge(Todo::getCreateTime, s).lt(Todo::getCreateTime, e));
            long idea = ideaMapper.selectCount(new LambdaQueryWrapper<Idea>().eq(Idea::getUserId, uid).ge(Idea::getCreateTime, s).lt(Idea::getCreateTime, e));
            long note = techMapper.selectCount(new LambdaQueryWrapper<Tech>().eq(Tech::getUserId, uid).ge(Tech::getCreateTime, s).lt(Tech::getCreateTime, e))
                    + docsMapper.selectCount(new LambdaQueryWrapper<Docs>().eq(Docs::getUserId, uid).ge(Docs::getCreateTime, s).lt(Docs::getCreateTime, e));
            long money = moneyMapper.selectCount(new LambdaQueryWrapper<Money>().eq(Money::getUserId, uid).ge(Money::getCreateTime, s).lt(Money::getCreateTime, e));
            list.add(InsightStatsVO.TrendPoint.builder().date(d.format(fmt))
                    .todoCount(t).ideaCount(idea).noteCount(note).moneyCount(money).build());
        }
        return list;
    }
}
