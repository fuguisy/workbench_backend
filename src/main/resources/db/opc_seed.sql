-- ============================================================
-- OPC 90天计划 · 工作台种子数据
-- 用法：USE workbench; 然后整体执行本文件（或 SOURCE opc_seed.sql）
-- 说明：
--   1. 自动取 admin 用户 id 作为归属人
--   2. 可重复执行：开头会清理旧的 OPC 种子（按标题前缀【OPC / period=OPC_90D），不影响手动录入的数据
--   3. 日期锚点：W1=2026-09-18 起，每 7 天一周，W13 截止 2026-12-18
-- ============================================================

SET @uid = (SELECT id FROM sys_user WHERE username = 'admin' LIMIT 1);

-- ---------- 0. 清理旧种子 ----------
DELETE FROM todo WHERE user_id = @uid AND title LIKE '【OPC%';
DELETE FROM project WHERE user_id = @uid AND title LIKE '【OPC%';
DELETE FROM checkin WHERE user_id = @uid AND title LIKE '【OPC%';
DELETE FROM okr_key_result WHERE user_id = @uid AND objective_id IN (
    SELECT id FROM (SELECT id FROM okr_objective WHERE user_id = @uid AND period = 'OPC_90D') t
);
DELETE FROM okr_objective WHERE user_id = @uid AND period = 'OPC_90D';

-- ---------- 1. OKR：90天三大目标 ----------
INSERT INTO okr_objective (user_id, title, category, period, curr_value, target_value, unit, start_date, end_date, status)
VALUES (@uid, '跑通企业AI智能体交付，签下首个付费单', 'WORK', 'OPC_90D', 0, 1, '单', '2026-09-18', '2026-12-18', 'ACTIVE');
SET @o1 = LAST_INSERT_ID();
INSERT INTO okr_key_result (user_id, objective_id, title, curr_value, target_value, unit) VALUES
(@uid, @o1, '完成 3 个可演示的智能体 Demo（演示环境）', 0, 3, '个'),
(@uid, @o1, '线索入池 30 条（线索管理模块登记）', 0, 30, '条'),
(@uid, @o1, '成交首单，金额 ≥ 5000 元', 0, 1, '单');

INSERT INTO okr_objective (user_id, title, category, period, curr_value, target_value, unit, start_date, end_date, status)
VALUES (@uid, '技术自媒体矩阵起量，跑通内容生产线', 'MEDIA', 'OPC_90D', 0, 36, '篇', '2026-09-18', '2026-12-18', 'ACTIVE');
SET @o2 = LAST_INSERT_ID();
INSERT INTO okr_key_result (user_id, objective_id, title, curr_value, target_value, unit) VALUES
(@uid, @o2, '公众号/知乎/B站 三平台各发布 ≥ 12 篇（条）', 0, 36, '篇'),
(@uid, @o2, '全网粉丝 ≥ 1000', 0, 1000, '粉'),
(@uid, @o2, '单篇最高阅读 ≥ 5000', 0, 5000, '阅读');

INSERT INTO okr_objective (user_id, title, category, period, curr_value, target_value, unit, start_date, end_date, status)
VALUES (@uid, '一人公司工具链落地，内容生产 ≤ 2 小时/篇', 'TECH', 'OPC_90D', 0, 6, '个', '2026-09-18', '2026-12-18', 'ACTIVE');
SET @o3 = LAST_INSERT_ID();
INSERT INTO okr_key_result (user_id, objective_id, title, curr_value, target_value, unit) VALUES
(@uid, @o3, '6 个自建工具全部上线（见项目进度）', 0, 6, '个'),
(@uid, @o3, '单篇内容生产耗时 ≤ 2 小时', 0, 2, '小时');

-- ---------- 2. 任务看板：13 周作战清单 ----------
INSERT INTO todo (user_id, title, priority, category, deadline, est_pom, note) VALUES
-- W1 打地基（截止 09-25）
(@uid, '【OPC·W1】注册公众号/知乎/B站账号并完善资料', 'P0', 'work', '2026-09-25', 3, '头像/昵称/简介统一：Java + AI 实战派'),
(@uid, '【OPC·W1】确定内容定位并写进各平台简介', 'P0', 'work', '2026-09-25', 2, '一句话定位：帮中小企业用上 AI 智能体的 Java 程序员'),
(@uid, '【OPC·W1】搭建演示环境：本地跑通第一个智能体 Demo', 'P1', 'work', '2026-09-25', 6, 'Dify 或 n8n + 本地模型/API，能录屏演示'),
(@uid, '【OPC·W1】发布第一篇文章', 'P1', 'work', '2026-09-25', 4, '主题建议：Java 程序员为什么 all in AI 智能体'),
(@uid, '【OPC·W1】建选题库：素材库存入 20 条选题', 'P1', 'work', '2026-09-25', 3, '来源：热点/踩坑/客户问题/工具搭建过程'),
(@uid, '【OPC·W1】线索管理模块上线（建表 + 重启后端）', 'P0', 'work', '2026-09-25', 2, '执行 opc_lead 建表 SQL，验证 线索/商机 页面'),
(@uid, '【OPC·W1】W1 周复盘', 'P2', 'work', '2026-09-25', 2, '洞察复盘模块记录：数据/问题/下周调整'),
-- W2（截止 10-02）
(@uid, '【OPC·W2】导入 OPC 种子数据并熟悉工作台各模块', 'P1', 'work', '2026-10-02', 1, '执行 opc_seed.sql（本文件）'),
(@uid, '【OPC·W2】热点监控推送上线，每天早上看通知选题', 'P1', 'work', '2026-10-02', 3, '重启后端后自动运行，POST /hot/scan 可手动触发'),
(@uid, '【OPC·W2】Demo1 录屏并整理成图文素材', 'P1', 'work', '2026-10-02', 4, '一鱼三吃：视频素材 + 图文 + 获客演示'),
(@uid, '【OPC·W2】发布 2 篇文章（公众号+知乎同步）', 'P1', 'work', '2026-10-02', 6, ''),
(@uid, '【OPC·W2】W2 周复盘', 'P2', 'work', '2026-10-02', 2, ''),
-- W3（截止 10-09）
(@uid, '【OPC·W3】上线排版流水线：Markdown 一键转公众号', 'P1', 'work', '2026-10-09', 3, 'md2wechat / mdnice，固化为模板'),
(@uid, '【OPC·W3】发布 2 篇 + 知乎回答 5 个', 'P1', 'work', '2026-10-09', 8, '回答挂公众号引流'),
(@uid, '【OPC·W3】W3 周复盘', 'P2', 'work', '2026-10-09', 2, ''),
-- W4（截止 10-16）
(@uid, '【OPC·W4】视频批处理脚本：ffmpeg + whisper 自动字幕', 'P2', 'work', '2026-10-16', 4, ''),
(@uid, '【OPC·W4】发布第一条 B 站视频（Demo1 讲解）', 'P1', 'work', '2026-10-16', 5, ''),
(@uid, '【OPC·W4】发布 1 篇图文 + 前 4 周数据录入自媒体看板', 'P1', 'work', '2026-10-16', 4, ''),
(@uid, '【OPC·W4】W4 周复盘', 'P2', 'work', '2026-10-16', 2, ''),
-- W5 接单验证（截止 10-23）
(@uid, '【OPC·W5】做报价单模板 + 服务清单（3 档定价）', 'P0', 'work', '2026-10-23', 4, '参考：体验版/标准版/定制版'),
(@uid, '【OPC·W5】主动触达 10 个潜在客户', 'P0', 'work', '2026-10-23', 6, '朋友/社群/前同事，全部登记进线索管理'),
(@uid, '【OPC·W5】发布 2 篇（含 1 篇软性接单文）', 'P1', 'work', '2026-10-23', 6, '主题：我帮企业做智能体能解决什么'),
(@uid, '【OPC·W5】W5 周复盘', 'P2', 'work', '2026-10-23', 2, ''),
-- W6（截止 10-30）
(@uid, '【OPC·W6】数据看板 v1：Vue3 聚合各平台数据', 'P2', 'work', '2026-10-30', 6, ''),
(@uid, '【OPC·W6】月度数据复盘：砍掉低效平台/选题', 'P1', 'work', '2026-10-30', 3, ''),
(@uid, '【OPC·W6】发布 2 篇 + 触达 5 人', 'P1', 'work', '2026-10-30', 8, ''),
-- W7（截止 11-06）
(@uid, '【OPC·W7】Demo2：企业知识库问答智能体', 'P1', 'work', '2026-11-06', 8, ''),
(@uid, '【OPC·W7】触达 10 人，目标 3 个进入「沟通中」', 'P0', 'work', '2026-11-06', 6, ''),
(@uid, '【OPC·W7】发布 2 篇', 'P1', 'work', '2026-11-06', 6, ''),
-- W8（截止 11-13）
(@uid, '【OPC·W8】首单谈判：报价/合同/分期，目标成交 1 单', 'P0', 'work', '2026-11-13', 8, ''),
(@uid, '【OPC·W8】把谈判过程写成案例文（脱敏）', 'P1', 'work', '2026-11-13', 4, ''),
(@uid, '【OPC·W8】发布 2 篇', 'P1', 'work', '2026-11-13', 6, ''),
-- W9 交付产品化（截止 11-20）
(@uid, '【OPC·W9】沉淀交付 starter：环境清单 + 部署脚本 + 验收表', 'P0', 'work', '2026-11-20', 8, ''),
(@uid, '【OPC·W9】首单开工交付', 'P0', 'work', '2026-11-20', 10, ''),
(@uid, '【OPC·W9】发布 1 篇交付日记', 'P1', 'work', '2026-11-20', 3, ''),
-- W10（截止 11-27）
(@uid, '【OPC·W10】首单交付完成 + 客户验收', 'P0', 'work', '2026-11-27', 8, ''),
(@uid, '【OPC·W10】收款并录入收支记账', 'P1', 'work', '2026-11-27', 1, ''),
(@uid, '【OPC·W10】发布交付复盘文', 'P1', 'work', '2026-11-27', 4, ''),
-- W11（截止 12-04）
(@uid, '【OPC·W11】交付 SOP 文档化（报价→开发→验收→收款）', 'P1', 'work', '2026-12-04', 6, ''),
(@uid, '【OPC·W11】向首单客户要转介绍 + 案例授权', 'P0', 'work', '2026-12-04', 2, ''),
(@uid, '【OPC·W11】发布 2 篇', 'P2', 'work', '2026-12-04', 6, ''),
-- W12（截止 12-11）
(@uid, '【OPC·W12】用 SOP 打第二单', 'P0', 'work', '2026-12-11', 8, ''),
(@uid, '【OPC·W12】工具链补漏：优化最耗时的环节', 'P2', 'work', '2026-12-11', 4, ''),
(@uid, '【OPC·W12】发布 2 篇', 'P2', 'work', '2026-12-11', 6, ''),
-- W13（截止 12-18）
(@uid, '【OPC·W13】90 天总复盘：OKR 打分 + 数据汇总', 'P0', 'work', '2026-12-18', 4, ''),
(@uid, '【OPC·W13】输出《一人公司 90 天实战》长文', 'P1', 'work', '2026-12-18', 6, '引流 + 背书'),
(@uid, '【OPC·W13】制定下一季度 OKR', 'P1', 'work', '2026-12-18', 3, ''),
-- 循环习惯型任务
(@uid, '【OPC·循环】内容输出 30 分钟', 'P1', 'work', NULL, 1, '写稿/录屏/剪辑均可，保持手感'),
(@uid, '【OPC·循环】热点浏览 + 选题记录 10 分钟', 'P2', 'work', NULL, 1, '看通知中心热点推送，选题入素材库');

UPDATE todo SET recurring = 'daily' WHERE user_id = @uid AND title = '【OPC·循环】内容输出 30 分钟';
UPDATE todo SET recurring = 'weekly' WHERE user_id = @uid AND title = '【OPC·循环】热点浏览 + 选题记录 10 分钟';

-- ---------- 3. 项目进度：6 个自建工具 ----------
INSERT INTO project (user_id, title, current, target, unit, status, deadline, category, note) VALUES
(@uid, '【OPC】演示环境（Dify/n8n 智能体 Demo）', 0, 100, '%', '进行中', '2026-09-25', 'AI', 'W1-W2 打地基'),
(@uid, '【OPC】热点监控推送（工作台通知）', 0, 100, '%', '进行中', '2026-10-02', 'AI', 'W2，已内置于后端 /hot'),
(@uid, '【OPC】图文排版流水线（md2wechat）', 0, 100, '%', '进行中', '2026-10-09', '自媒体', 'W3 内容提效'),
(@uid, '【OPC】视频批处理脚本（ffmpeg + whisper）', 0, 100, '%', '进行中', '2026-10-16', '自媒体', 'W4 内容提效'),
(@uid, '【OPC】数据看板 v1（Vue3）', 0, 100, '%', '进行中', '2026-10-30', '前端', 'W6 接单验证'),
(@uid, '【OPC】交付 starter 套件', 0, 100, '%', '进行中', '2026-11-20', 'Java后端', 'W9 交付产品化');

-- ---------- 4. 打卡习惯 ----------
INSERT INTO checkin (user_id, title, log, group_name, cycle) VALUES
(@uid, '【OPC】内容输出 30 分钟', 'OPC 90 天计划每日习惯', '学习成长', '每日'),
(@uid, '【OPC】热点浏览 + 选题记录', '配合通知中心热点推送', '学习成长', '每日'),
(@uid, '【OPC】周复盘（数据 + 问题 + 调整）', '每周日晚完成', '学习成长', '每周');

-- ---------- 5. 完成提示 ----------
SELECT CONCAT('OPC 种子数据导入完成：OKR 3 个 / KR 8 条 / 任务 ',
       (SELECT COUNT(*) FROM todo WHERE user_id = @uid AND title LIKE '【OPC%'),
       ' 条 / 工具项目 6 个 / 打卡习惯 3 个') AS result;
