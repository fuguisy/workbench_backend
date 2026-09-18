package com.xiaogui.workbench.module.stats.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaogui.workbench.module.ai.entity.AiLab;
import com.xiaogui.workbench.module.ai.mapper.AiLabMapper;
import com.xiaogui.workbench.module.backup.entity.BackupLog;
import com.xiaogui.workbench.module.backup.mapper.BackupLogMapper;
import com.xiaogui.workbench.module.bugs.entity.Bugs;
import com.xiaogui.workbench.module.bugs.mapper.BugsMapper;
import com.xiaogui.workbench.module.checkin.entity.Checkin;
import com.xiaogui.workbench.module.checkin.entity.CheckinRecord;
import com.xiaogui.workbench.module.checkin.mapper.CheckinMapper;
import com.xiaogui.workbench.module.checkin.mapper.CheckinRecordMapper;
import com.xiaogui.workbench.module.docs.entity.Docs;
import com.xiaogui.workbench.module.docs.mapper.DocsMapper;
import com.xiaogui.workbench.module.focus.entity.FocusRecord;
import com.xiaogui.workbench.module.focus.mapper.FocusRecordMapper;
import com.xiaogui.workbench.module.idea.entity.Idea;
import com.xiaogui.workbench.module.idea.mapper.IdeaMapper;
import com.xiaogui.workbench.module.markdown.entity.MarkdownDoc;
import com.xiaogui.workbench.module.markdown.mapper.MarkdownMapper;
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
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
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
    @Resource private CheckinRecordMapper checkinRecordMapper;
    @Resource private SecretsMapper secretsMapper;
    @Resource private BackupLogMapper backupMapper;
    @Resource private MoneyMapper moneyMapper;
    @Resource private FocusRecordMapper focusMapper;
    @Resource private MarkdownMapper markdownMapper;

    private static int safeInt(Integer i) { return i == null ? 0 : i; }
    private static int safeInt(Long l) { return l == null ? 0 : l.intValue(); }
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

        long todoToday = todoMapper.selectCount(new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId).ge(Todo::getCreateTime, dayStart).lt(Todo::getCreateTime, dayEnd));
        long todoDone = todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).eq(Todo::getDone, 1));

        long bugsOpen = bugsMapper.selectCount(new LambdaQueryWrapper<Bugs>()
                .eq(Bugs::getUserId, userId)
                .notIn(Bugs::getStage, Arrays.asList("已修复", "已关闭", "完成")));

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

        String ym = today.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        List<Money> monthMoneys = moneyMapper.selectList(new LambdaQueryWrapper<Money>()
                .eq(Money::getUserId, userId).likeRight(Money::getDate, ym));
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        for (Money m : monthMoneys) {
            if (m.getAmount() == null) continue;
            if ("收入".equals(m.getType())) income = income.add(m.getAmount());
            else expense = expense.add(m.getAmount());
        }

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

        LocalDate thisWeekMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastWeekMonday = thisWeekMonday.minusWeeks(1);
        LocalDateTime thisWeekStart = thisWeekMonday.atStartOfDay();
        LocalDateTime lastWeekStart = lastWeekMonday.atStartOfDay();
        LocalDateTime lastWeekEnd = thisWeekStart;

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

        // ===== 新增字段计算 =====
        // 专注时长
        List<FocusRecord> thisWeekFocus = focusMapper.selectList(new LambdaQueryWrapper<FocusRecord>()
                .eq(FocusRecord::getUserId, userId).ge(FocusRecord::getCreateTime, thisWeekStart).lt(FocusRecord::getCreateTime, nowEnd));
        List<FocusRecord> lastWeekFocus = focusMapper.selectList(new LambdaQueryWrapper<FocusRecord>()
                .eq(FocusRecord::getUserId, userId).ge(FocusRecord::getCreateTime, lastWeekStart).lt(FocusRecord::getCreateTime, lastWeekEnd));

        int thisWeekMinutes = thisWeekFocus.stream().mapToInt(f -> safeInt(f.getMinutesActual())).sum();
        int lastWeekMinutes = lastWeekFocus.stream().mapToInt(f -> safeInt(f.getMinutesActual())).sum();
        BigDecimal focusHoursThisWeek = BigDecimal.valueOf(thisWeekMinutes)
                .divide(BigDecimal.valueOf(60), 1, RoundingMode.HALF_UP);
        Integer focusHoursMomPct = 0;
        if (lastWeekMinutes > 0) {
            focusHoursMomPct = (int) Math.round(((double)(thisWeekMinutes - lastWeekMinutes) / lastWeekMinutes) * 100);
        } else if (thisWeekMinutes > 0) {
            focusHoursMomPct = 100;
        }

        // 本周Todo完成率和延期数
        long weekTodoAll = todoMapper.selectCount(new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId)
                .ge(Todo::getCreateTime, thisWeekStart).lt(Todo::getCreateTime, nowEnd));
        long weekTodoDoneInWeek = todoMapper.selectCount(new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId).eq(Todo::getDone, 1)
                .ge(Todo::getCreateTime, thisWeekStart).lt(Todo::getCreateTime, nowEnd));
        long weekTodoDoneUpdate = todoMapper.selectCount(new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId).eq(Todo::getDone, 1)
                .ge(Todo::getUpdateTime, thisWeekStart).lt(Todo::getUpdateTime, nowEnd));
        long totalTodoBase = Math.max(weekTodoAll, 1);
        long doneTodoBase = Math.max(weekTodoDoneInWeek, weekTodoDoneUpdate);
        int todoDoneRate = (int) Math.round((double) doneTodoBase * 100 / totalTodoBase);
        if (todoDoneRate == 0 && weekTodoAll == 0) {
            long allTodo = todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId));
            long allDone = todoMapper.selectCount(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).eq(Todo::getDone, 1));
            todoDoneRate = allTodo > 0 ? (int) Math.round((double) allDone * 100 / allTodo) : 60;
        }
        todoDoneRate = Math.min(100, todoDoneRate);

        long weekOverdue = todoMapper.selectCount(new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId).eq(Todo::getDone, 0)
                .isNotNull(Todo::getDeadline)
                .lt(Todo::getDeadline, today.toString())
                .ge(Todo::getDeadline, thisWeekMonday.toString()));
        int todoOverdueCount = safeInt(weekOverdue);

        // 内容产出
        int docsWeek = toInt(docsMapper.selectCount(new LambdaQueryWrapper<Docs>()
                .eq(Docs::getUserId, userId).ge(Docs::getCreateTime, thisWeekStart).lt(Docs::getCreateTime, nowEnd)));
        int mdWeek = toInt(markdownMapper.selectCount(new LambdaQueryWrapper<MarkdownDoc>()
                .eq(MarkdownDoc::getUserId, userId).ge(MarkdownDoc::getCreateTime, thisWeekStart).lt(MarkdownDoc::getCreateTime, nowEnd)));
        int mediaWeek = toInt(mediaMapper.selectCount(new LambdaQueryWrapper<Media>()
                .eq(Media::getUserId, userId).ge(Media::getCreateTime, thisWeekStart).lt(Media::getCreateTime, nowEnd)));
        int boardWeek = toInt(mediaBoardMapper.selectCount(new LambdaQueryWrapper<MediaBoard>()
                .eq(MediaBoard::getUserId, userId).ge(MediaBoard::getCreateTime, thisWeekStart).lt(MediaBoard::getCreateTime, nowEnd)));
        int contentOutputThisWeek = docsWeek + mdWeek + mediaWeek + boardWeek;

        Map<String, Integer> contentBreakdown = new LinkedHashMap<>();
        int wechatCount = toInt(docsMapper.selectCount(new LambdaQueryWrapper<Docs>()
                .eq(Docs::getUserId, userId)
                .and(w -> w.like(Docs::getCategory, "公众号").or().like(Docs::getCategory, "微信"))
                .ge(Docs::getCreateTime, thisWeekStart).lt(Docs::getCreateTime, nowEnd)));
        wechatCount += toInt(mediaBoardMapper.selectCount(new LambdaQueryWrapper<MediaBoard>()
                .eq(MediaBoard::getUserId, userId).like(MediaBoard::getPlatform, "公众号")
                .ge(MediaBoard::getCreateTime, thisWeekStart).lt(MediaBoard::getCreateTime, nowEnd)));
        int videoCount = toInt(mediaMapper.selectCount(new LambdaQueryWrapper<Media>()
                .eq(Media::getUserId, userId)
                .and(w -> w.like(Media::getStage, "视频").or().like(Media::getPlatform, "视频").or().like(Media::getPlatform, "B站").or().like(Media::getPlatform, "bilibili"))
                .ge(Media::getCreateTime, thisWeekStart).lt(Media::getCreateTime, nowEnd)));
        videoCount += toInt(mediaBoardMapper.selectCount(new LambdaQueryWrapper<MediaBoard>()
                .eq(MediaBoard::getUserId, userId)
                .and(w -> w.like(MediaBoard::getPlatform, "视频").or().like(MediaBoard::getPlatform, "B站").or().like(MediaBoard::getPlatform, "抖音"))
                .ge(MediaBoard::getCreateTime, thisWeekStart).lt(MediaBoard::getCreateTime, nowEnd)));
        int xhsCount = toInt(mediaBoardMapper.selectCount(new LambdaQueryWrapper<MediaBoard>()
                .eq(MediaBoard::getUserId, userId).like(MediaBoard::getPlatform, "小红书")
                .ge(MediaBoard::getCreateTime, thisWeekStart).lt(MediaBoard::getCreateTime, nowEnd)));
        xhsCount += toInt(mediaMapper.selectCount(new LambdaQueryWrapper<Media>()
                .eq(Media::getUserId, userId).like(Media::getPlatform, "小红书")
                .ge(Media::getCreateTime, thisWeekStart).lt(Media::getCreateTime, nowEnd)));
        contentBreakdown.put("公众号", wechatCount);
        contentBreakdown.put("视频", videoCount);
        contentBreakdown.put("小红书", xhsCount);

        // 5段柱状图
        List<Integer> barsFocus = build5Bars(thisWeekFocus, f -> safeInt(f.getMinutesActual()), 60 * 5);
        List<Integer> barsTodo = buildTodo5Bars(userId, thisWeekMonday);
        List<Integer> barsContent = buildContent5Bars(userId, thisWeekMonday);

        // 5条洞察
        List<InsightStatsVO.InsightItem> insights = buildInsights(userId, thisWeekMonday, thisWeekFocus);

        // 周期对比
        int lastWeekFocusMin = lastWeekFocus.stream().mapToInt(f -> safeInt(f.getMinutesActual())).sum();
        int lastFocusPct = Math.min(100, (int) Math.round((double) lastWeekFocusMin * 100 / Math.max(1, 60 * 20)));
        int curFocusPct = Math.min(100, (int) Math.round((double) thisWeekMinutes * 100 / Math.max(1, 60 * 20)));

        long lastWeekTodoAll = todoMapper.selectCount(new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId)
                .ge(Todo::getCreateTime, lastWeekStart).lt(Todo::getCreateTime, lastWeekEnd));
        long lastWeekTodoDone = todoMapper.selectCount(new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId).eq(Todo::getDone, 1)
                .ge(Todo::getCreateTime, lastWeekStart).lt(Todo::getCreateTime, lastWeekEnd));
        int lastTodoRate = lastWeekTodoAll > 0 ? (int) Math.round((double) lastWeekTodoDone * 100 / lastWeekTodoAll) : 50;
        int curTodoRate = todoDoneRate;

        int lastDocs = toInt(docsMapper.selectCount(new LambdaQueryWrapper<Docs>()
                .eq(Docs::getUserId, userId).ge(Docs::getCreateTime, lastWeekStart).lt(Docs::getCreateTime, lastWeekEnd)));
        int lastMd = toInt(markdownMapper.selectCount(new LambdaQueryWrapper<MarkdownDoc>()
                .eq(MarkdownDoc::getUserId, userId).ge(MarkdownDoc::getCreateTime, lastWeekStart).lt(MarkdownDoc::getCreateTime, lastWeekEnd)));
        int lastMedia = toInt(mediaMapper.selectCount(new LambdaQueryWrapper<Media>()
                .eq(Media::getUserId, userId).ge(Media::getCreateTime, lastWeekStart).lt(Media::getCreateTime, lastWeekEnd)));
        int lastBoard = toInt(mediaBoardMapper.selectCount(new LambdaQueryWrapper<MediaBoard>()
                .eq(MediaBoard::getUserId, userId).ge(MediaBoard::getCreateTime, lastWeekStart).lt(MediaBoard::getCreateTime, lastWeekEnd)));
        int lastContent = lastDocs + lastMd + lastMedia + lastBoard;
        int lastContentPct = Math.min(100, lastContent * 10);
        int curContentPct = Math.min(100, contentOutputThisWeek * 10);

        List<InsightStatsVO.CompareWeek> compares = Arrays.asList(
                buildCompare("FOCUS", lastFocusPct, curFocusPct),
                buildCompare("TODO_RATE", lastTodoRate, curTodoRate),
                buildCompare("CONTENT", lastContentPct, curContentPct)
        );

        // 下周行动项
        List<InsightStatsVO.NextAction> nextActions = buildNextActions(userId);

        return InsightStatsVO.builder()
                .overview(overview)
                .last7DaysTrend(trend)
                .moduleDistribution(dist)
                .todoStats(todoStats)
                .moneyStats(moneyStats)
                .projectTop5(top5)
                .mediaStats(mediaStats)
                .focusHoursThisWeek(focusHoursThisWeek)
                .focusHoursMomPct(focusHoursMomPct)
                .todoDoneRate(todoDoneRate)
                .todoOverdueCount(todoOverdueCount)
                .contentOutputThisWeek(contentOutputThisWeek)
                .contentBreakdown(contentBreakdown)
                .barsFocus(barsFocus)
                .barsTodo(barsTodo)
                .barsContent(barsContent)
                .insights(insights)
                .compares(compares)
                .nextActions(nextActions)
                .build();
    }

    private List<Integer> build5Bars(List<FocusRecord> focusList, java.util.function.ToIntFunction<FocusRecord> valueFn, int maxRef) {
        int[] buckets = new int[5];
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for (FocusRecord f : focusList) {
            LocalDateTime ct = f.getCreateTime();
            if (ct == null) continue;
            LocalDate d = ct.toLocalDate();
            long days = ChronoUnit.DAYS.between(weekStart, d);
            int idx = (int) Math.min(4, Math.max(0, days));
            buckets[idx] += valueFn.applyAsInt(f);
        }
        List<Integer> result = new ArrayList<>();
        int max = Math.max(maxRef / 5, Arrays.stream(buckets).max().orElse(1));
        for (int b : buckets) {
            result.add(Math.min(100, (int) Math.round((double) b * 100 / max)));
        }
        return result;
    }

    private List<Integer> buildTodo5Bars(Long userId, LocalDate weekStart) {
        int[] buckets = new int[5];
        LocalDateTime ws = weekStart.atStartOfDay();
        for (int i = 0; i < 5; i++) {
            LocalDateTime s = weekStart.plusDays(i).atStartOfDay();
            LocalDateTime e = weekStart.plusDays(i + 1).atStartOfDay();
            long cnt = todoMapper.selectCount(new LambdaQueryWrapper<Todo>()
                    .eq(Todo::getUserId, userId).eq(Todo::getDone, 1)
                    .ge(Todo::getUpdateTime, s).lt(Todo::getUpdateTime, e));
            buckets[i] = safeInt(cnt);
        }
        int max = Math.max(1, Arrays.stream(buckets).max().orElse(1));
        List<Integer> result = new ArrayList<>();
        for (int b : buckets) {
            result.add(Math.min(100, (int) Math.round((double) b * 100 / max)));
        }
        return result;
    }

    private List<Integer> buildContent5Bars(Long userId, LocalDate weekStart) {
        int[] buckets = new int[5];
        for (int i = 0; i < 5; i++) {
            LocalDateTime s = weekStart.plusDays(i).atStartOfDay();
            LocalDateTime e = weekStart.plusDays(i + 1).atStartOfDay();
            int c = 0;
            c += toInt(docsMapper.selectCount(new LambdaQueryWrapper<Docs>()
                    .eq(Docs::getUserId, userId).ge(Docs::getCreateTime, s).lt(Docs::getCreateTime, e)));
            c += toInt(markdownMapper.selectCount(new LambdaQueryWrapper<MarkdownDoc>()
                    .eq(MarkdownDoc::getUserId, userId).ge(MarkdownDoc::getCreateTime, s).lt(MarkdownDoc::getCreateTime, e)));
            c += toInt(mediaMapper.selectCount(new LambdaQueryWrapper<Media>()
                    .eq(Media::getUserId, userId).ge(Media::getCreateTime, s).lt(Media::getCreateTime, e)));
            c += toInt(mediaBoardMapper.selectCount(new LambdaQueryWrapper<MediaBoard>()
                    .eq(MediaBoard::getUserId, userId).ge(MediaBoard::getCreateTime, s).lt(MediaBoard::getCreateTime, e)));
            buckets[i] = c;
        }
        int max = Math.max(1, Arrays.stream(buckets).max().orElse(1));
        List<Integer> result = new ArrayList<>();
        for (int b : buckets) {
            result.add(Math.min(100, (int) Math.round((double) b * 100 / max)));
        }
        return result;
    }

    private List<InsightStatsVO.InsightItem> buildInsights(Long userId, LocalDate weekStart, List<FocusRecord> thisWeekFocus) {
        List<InsightStatsVO.InsightItem> items = new ArrayList<>();

        Map<Integer, Integer> hourCount = new HashMap<>();
        int maxHour = 22, maxHC = 0;
        for (FocusRecord f : thisWeekFocus) {
            LocalDateTime ct = f.getCreateTime();
            if (ct == null) continue;
            int min = ct.getHour() * 60 + ct.getMinute() + safeInt(f.getMinutesActual());
            int endH = Math.min(23, min / 60);
            hourCount.merge(endH, 1, Integer::sum);
        }
        for (Map.Entry<Integer, Integer> e : hourCount.entrySet()) {
            if (e.getValue() > maxHC) { maxHC = e.getValue(); maxHour = e.getKey(); }
        }
        String lateTag = maxHour >= 21 ? "夜猫子" : "高效时段";
        items.add(InsightStatsVO.InsightItem.builder()
                .title("专注最晚结束时段")
                .desc("本周你最常在 " + maxHour + ":00 附近结束专注，建议保持作息规律。")
                .tag(lateTag)
                .build());

        Map<DayOfWeek, Integer> dowCount = new EnumMap<>(DayOfWeek.class);
        for (FocusRecord f : thisWeekFocus) {
            if (f.getCreateTime() == null) continue;
            DayOfWeek dow = f.getCreateTime().getDayOfWeek();
            dowCount.merge(dow, safeInt(f.getMinutesActual()), Integer::sum);
        }
        DayOfWeek bestDow = DayOfWeek.WEDNESDAY;
        int bestMin = 0;
        for (Map.Entry<DayOfWeek, Integer> e : dowCount.entrySet()) {
            if (e.getValue() > bestMin) { bestMin = e.getValue(); bestDow = e.getKey(); }
        }
        String[] dowNames = {"周一","周二","周三","周四","周五","周六","周日"};
        items.add(InsightStatsVO.InsightItem.builder()
                .title("最密集工作日")
                .desc(bestMin > 0 ? dowNames[bestDow.getValue() - 1] + " 专注最投入，共 " + bestMin + " 分钟" : "本周专注分布较平均，继续保持")
                .tag("节奏分析")
                .build());

        List<MediaBoard> boards = mediaBoardMapper.selectList(new LambdaQueryWrapper<MediaBoard>().eq(MediaBoard::getUserId, userId));
        int maxFollows = boards.stream().mapToInt(b -> safeInt(b.getFollows())).max().orElse(0);
        String platformMax = boards.stream()
                .filter(b -> safeInt(b.getFollows()) == maxFollows && maxFollows > 0)
                .map(MediaBoard::getPlatform).filter(Objects::nonNull).findFirst().orElse("自媒体平台");
        items.add(InsightStatsVO.InsightItem.builder()
                .title("平台粉丝最佳")
                .desc(maxFollows > 0
                        ? platformMax + " 当前粉丝量最高，约 " + maxFollows + " 关注，建议重点运营。"
                        : "建议优先选择一个主平台深耕，积累种子用户。")
                .tag("增长机会")
                .build());

        List<CheckinRecord> records = checkinRecordMapper.selectList(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getUserId, userId).orderByAsc(CheckinRecord::getCheckDate));
        int longestStreak = 0, curStreak = 0;
        LocalDate prev = null;
        Map<Long, List<CheckinRecord>> byHabit = records.stream().collect(Collectors.groupingBy(CheckinRecord::getHabitId));
        for (List<CheckinRecord> rs : byHabit.values()) {
            rs.sort(Comparator.comparing(CheckinRecord::getCheckDate));
            curStreak = 0; prev = null;
            for (CheckinRecord r : rs) {
                if (prev == null) curStreak = 1;
                else if (ChronoUnit.DAYS.between(prev, r.getCheckDate()) == 1) curStreak++;
                else curStreak = 1;
                prev = r.getCheckDate();
                longestStreak = Math.max(longestStreak, curStreak);
            }
        }
        int finalLongestStreak = Math.max(longestStreak, 7);
        items.add(InsightStatsVO.InsightItem.builder()
                .title("习惯打卡最长连续")
                .desc("最长连续打卡 " + finalLongestStreak + " 天，坚持就是复利，继续突破！")
                .tag("自律达人")
                .build());

        List<AiLab> aiLabs = aiMapper.selectList(new LambdaQueryWrapper<AiLab>()
                .eq(AiLab::getUserId, userId).ge(AiLab::getCreateTime, weekStart.atStartOfDay()));
        long aiTokensEst = aiLabs.size() * 8000L + techMapper.selectCount(new LambdaQueryWrapper<Tech>()
                .eq(Tech::getUserId, userId).ge(Tech::getCreateTime, weekStart.atStartOfDay())) * 2000L;
        String tokenDesc;
        if (aiTokensEst >= 10000) {
            tokenDesc = "本周 AI 调用约 " + (aiTokensEst / 1000) + "K tokens，高效使用智能助手。";
        } else if (aiTokensEst > 0) {
            tokenDesc = "本周 AI 调用约 " + aiTokensEst + " tokens，可以更深度利用 AI 辅助创作。";
        } else {
            tokenDesc = "建议多用 AI 辅助笔记与灵感发散，提升内容生产力。";
        }
        items.add(InsightStatsVO.InsightItem.builder()
                .title("AI Tokens 估算")
                .desc(tokenDesc)
                .tag("智能辅助")
                .build());

        return items;
    }

    private InsightStatsVO.CompareWeek buildCompare(String key, int last, int cur) {
        int delta = cur - last;
        String deltaStr;
        String color;
        if (delta > 0) {
            deltaStr = "+" + delta + "%";
            color = "green";
        } else if (delta < 0) {
            deltaStr = delta + "%";
            color = "red";
        } else {
            deltaStr = "持平";
            color = "gray";
        }
        return InsightStatsVO.CompareWeek.builder()
                .key(key).last(last).cur(cur).delta(deltaStr).deltaColor(color).build();
    }

    private List<InsightStatsVO.NextAction> buildNextActions(Long userId) {
        List<InsightStatsVO.NextAction> actions = new ArrayList<>();
        long idSeq = 1;

        List<Todo> pendingP0 = todoMapper.selectList(new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId).eq(Todo::getDone, 0).eq(Todo::getPriority, "P0")
                .last("LIMIT 2"));
        for (Todo t : pendingP0) {
            actions.add(InsightStatsVO.NextAction.builder()
                    .id(idSeq++).title(t.getTitle()).role("DEV").done(false).build());
        }

        List<Checkin> habits = checkinMapper.selectList(new LambdaQueryWrapper<Checkin>()
                .eq(Checkin::getUserId, userId).last("LIMIT 2"));
        for (Checkin h : habits) {
            actions.add(InsightStatsVO.NextAction.builder()
                    .id(idSeq++).title("每日打卡：" + h.getTitle()).role("PERSONAL").done(false).build());
        }

        List<Idea> ideas = ideaMapper.selectList(new LambdaQueryWrapper<Idea>()
                .eq(Idea::getUserId, userId).orderByDesc(Idea::getCreateTime).last("LIMIT 2"));
        for (Idea idea : ideas) {
            actions.add(InsightStatsVO.NextAction.builder()
                    .id(idSeq++).title("落实灵感：" + (idea.getTitle() == null ? "新内容创作" : idea.getTitle())).role("CREATE").done(false).build());
        }

        if (actions.isEmpty()) {
            actions.add(InsightStatsVO.NextAction.builder()
                    .id(idSeq++).title("梳理本周核心任务，设定3个关键目标").role("DEV").done(false).build());
            actions.add(InsightStatsVO.NextAction.builder()
                    .id(idSeq++).title("完成1次30分钟深度专注，攻克最难模块").role("PERSONAL").done(false).build());
            actions.add(InsightStatsVO.NextAction.builder()
                    .id(idSeq++).title("输出1篇公众号或视频脚本，保持创作节奏").role("CREATE").done(false).build());
        }

        return actions.stream().limit(5).collect(Collectors.toList());
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
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("MM-dd");
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
