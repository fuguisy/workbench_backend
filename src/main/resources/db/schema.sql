-- ============================================================
-- 晓贵工作台 数据库建表脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS workbench DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE workbench;

-- -----------------------------------------------------------
-- 1. 系统用户表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    role VARCHAR(20) DEFAULT 'user',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- -----------------------------------------------------------
-- 2. 任务待办表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS todo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    priority VARCHAR(10),
    done TINYINT DEFAULT 0,
    note TEXT,
    category VARCHAR(50),
    deadline VARCHAR(20),
    recurring VARCHAR(20),
    est_pom INT,
    pomo_minutes INT DEFAULT 0,
    pomo_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务待办表';

-- -----------------------------------------------------------
-- 3. 项目进度表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    current INT,
    target INT,
    unit VARCHAR(20),
    note TEXT,
    status VARCHAR(20),
    deadline VARCHAR(20),
    category VARCHAR(50),
    owner VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目进度表';

-- -----------------------------------------------------------
-- 4. Bug记录表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS bugs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    mood VARCHAR(20),
    date VARCHAR(20),
    service VARCHAR(50),
    level VARCHAR(20),
    stage VARCHAR(20),
    fix_ver VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Bug记录表';

-- -----------------------------------------------------------
-- 5. 代码仓库表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS repo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    mood VARCHAR(20),
    date VARCHAR(20),
    category VARCHAR(50),
    repo VARCHAR(255),
    branch VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码仓库表';

-- -----------------------------------------------------------
-- 6. 服务部署表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS svc (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    mood VARCHAR(20),
    date VARCHAR(20),
    host VARCHAR(100),
    port VARCHAR(20),
    deploy VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务部署表';

-- -----------------------------------------------------------
-- 7. 阅读进度表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS read_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    current INT,
    target INT,
    unit VARCHAR(20),
    note TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阅读进度表';

-- -----------------------------------------------------------
-- 8. 技术笔记表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS tech (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    mood VARCHAR(20),
    date VARCHAR(20),
    lang VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术笔记表';

-- -----------------------------------------------------------
-- 9. AI实验室表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS ai_lab (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    mood VARCHAR(20),
    date VARCHAR(20),
    category VARCHAR(50),
    model VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI实验室表';

-- -----------------------------------------------------------
-- 10. 灵感创意表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS idea (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    mood VARCHAR(20),
    date VARCHAR(20),
    platform VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='灵感创意表';

-- -----------------------------------------------------------
-- 11. 媒体内容表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS media (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    current INT,
    target INT,
    unit VARCHAR(20),
    note TEXT,
    stage VARCHAR(20),
    platform VARCHAR(50),
    date VARCHAR(20),
    url VARCHAR(500),
    views INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体内容表';

-- -----------------------------------------------------------
-- 12. 媒体数据看板表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS media_board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    mood VARCHAR(20),
    date VARCHAR(20),
    platform VARCHAR(50),
    impression INT DEFAULT 0,
    clicks INT DEFAULT 0,
    follows INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体数据看板表';

-- -----------------------------------------------------------
-- 13. 文档记录表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS docs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    mood VARCHAR(20),
    date VARCHAR(20),
    category VARCHAR(50),
    word_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档记录表';

-- -----------------------------------------------------------
-- 14. 打卡记录表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS checkin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    log TEXT,
    group_name VARCHAR(50),
    cycle VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡记录表';

-- 打卡明细表（每次打卡一条记录，习惯维度）
CREATE TABLE IF NOT EXISTS checkin_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    habit_id BIGINT NOT NULL COMMENT '习惯id，关联checkin.id',
    check_date DATE NOT NULL COMMENT '打卡日期',
    note VARCHAR(500) COMMENT '打卡备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_user_habit_date (user_id, habit_id, check_date),
    INDEX idx_habit_date (habit_id, check_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡明细表';

-- -----------------------------------------------------------
-- 15. 密码秘钥表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS secrets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    mood VARCHAR(20),
    date VARCHAR(20),
    category VARCHAR(50),
    location VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密码秘钥表';

-- -----------------------------------------------------------
-- 16. 备份日志表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS backup_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    mood VARCHAR(20),
    date VARCHAR(20),
    category VARCHAR(50),
    destination VARCHAR(100),
    size_gb DECIMAL(10,2) DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备份日志表';

-- -----------------------------------------------------------
-- 17. 记账流水表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS money (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    type VARCHAR(20),
    amount DECIMAL(10,2),
    category VARCHAR(50),
    date VARCHAR(20),
    source VARCHAR(50),
    budget VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记账流水表';

-- -----------------------------------------------------------
-- 18. Markdown文档管理表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS markdown_doc (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(50),
    content LONGTEXT,
    tags VARCHAR(200),
    status VARCHAR(20) DEFAULT '草稿',
    file_path VARCHAR(500),
    word_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Markdown文档管理表';

-- -----------------------------------------------------------
-- 19. 文档分类树表（支持无限层级）
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS doc_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    parent_id BIGINT DEFAULT 0 COMMENT '父节点id，0表示根节点',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '同级排序，越小越靠前',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_parent (user_id, parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档分类树表';

-- -----------------------------------------------------------
-- 20. 通知中心表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    type VARCHAR(20) COMMENT 'TASK|BUG|DOC|CHECKIN|MONEY|SYSTEM',
    `read` TINYINT DEFAULT 0 COMMENT '0未读 1已读',
    extra VARCHAR(500),
    priority VARCHAR(20),
    date VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_read (user_id, `read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知中心表';

-- -----------------------------------------------------------
-- 21. 日历日程表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS calendar_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    category VARCHAR(20) COMMENT 'WORK|MEDIA|FOCUS|REVIEW|LIFE',
    location VARCHAR(200),
    attendees VARCHAR(500),
    start_time VARCHAR(30),
    end_time VARCHAR(30),
    all_day TINYINT DEFAULT 0,
    priority VARCHAR(20),
    status VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_start (user_id, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日历日程表';

-- -----------------------------------------------------------
-- 22. OKR目标表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS okr_objective (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    category VARCHAR(20) COMMENT 'WORK|MEDIA|TECH|READ|HEALTH',
    period VARCHAR(30) COMMENT 'Q3_2026|Y2026|MONTH_2026_08',
    curr_value DECIMAL(10,2) DEFAULT 0,
    target_value DECIMAL(10,2) DEFAULT 0,
    unit VARCHAR(20),
    start_date VARCHAR(20),
    end_date VARCHAR(20),
    status VARCHAR(20) COMMENT 'ACTIVE|DONE|ARCHIVED',
    progress INT DEFAULT 0 COMMENT '进度百分比 0-100（由KR聚合计算）',
    score DECIMAL(3,1) DEFAULT NULL COMMENT '周期结束0-1打分',
    review TEXT COMMENT '复盘备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_period (user_id, period)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OKR目标表';

-- -----------------------------------------------------------
-- 22b. OKR关键结果(KR)表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS okr_key_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    objective_id BIGINT NOT NULL COMMENT '所属O目标ID',
    title VARCHAR(200) NOT NULL COMMENT 'KR标题',
    curr_value DECIMAL(10,2) DEFAULT 0 COMMENT '当前值',
    target_value DECIMAL(10,2) DEFAULT 0 COMMENT '目标值',
    unit VARCHAR(20),
    progress INT DEFAULT 0 COMMENT '进度百分比 0-100',
    progress_mode VARCHAR(20) DEFAULT 'MANUAL' COMMENT 'MANUAL|TASK_LINKED',
    task_id BIGINT DEFAULT NULL COMMENT '关联任务ID（progress_mode=TASK_LINKED时）',
    weight INT DEFAULT 1 COMMENT '权重（默认1，用于O进度加权计算）',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE|DONE|CANCEL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_objective (objective_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OKR关键结果表';

-- -----------------------------------------------------------
-- 23. 番茄专注记录表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS focus_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    scene VARCHAR(20) COMMENT 'DEEP|CREATE|READ|MEET|SPORT',
    minutes_actual INT DEFAULT 0,
    minutes_standard INT DEFAULT 0,
    pomos_expected INT DEFAULT 0,
    status VARCHAR(20) COMMENT 'DOING|DONE|CANCEL',
    record_date VARCHAR(20),
    task_id BIGINT COMMENT '关联待办任务ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='番茄专注记录表';

-- 23.1 升级脚本：为已有的 focus_record 表添加 task_id 列
-- ALTER TABLE focus_record ADD COLUMN task_id BIGINT COMMENT '关联待办任务ID' AFTER record_date;

-- -----------------------------------------------------------
-- 24. 订阅账单表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS subscription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    category VARCHAR(20) COMMENT 'AI|DEV|CLOUD|OFFICE|ENTERTAINMENT',
    provider VARCHAR(100),
    plan_type VARCHAR(20) COMMENT 'MONTHLY|YEARLY|ONCE',
    cost_yuan DECIMAL(10,2) DEFAULT 0,
    renew_date VARCHAR(20),
    auto_renew TINYINT DEFAULT 0,
    status VARCHAR(20) COMMENT 'ACTIVE|EXPIRING|EXPIRED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_renew (user_id, renew_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订阅账单表';

-- -----------------------------------------------------------
-- 25. OPC 线索管理表（商机漏斗：新线索→沟通中→已报价→已成交→交付中→已复盘/已流失）
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS opc_lead (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200) COMMENT '客户/线索名称',
    source VARCHAR(20) COMMENT '来源：公众号|知乎|B站|朋友介绍|社群|其他',
    stage VARCHAR(20) DEFAULT '新线索' COMMENT '阶段：新线索|沟通中|已报价|已成交|交付中|已复盘|已流失',
    contact VARCHAR(100) COMMENT '联系方式',
    budget DECIMAL(10,2) DEFAULT 0 COMMENT '客户预算(元)',
    quote DECIMAL(10,2) DEFAULT 0 COMMENT '报价(元)',
    next_follow VARCHAR(20) COMMENT '下次跟进日期',
    demand TEXT COMMENT '需求描述',
    note TEXT COMMENT '跟进记录/备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_stage (user_id, stage)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OPC线索管理表';

-- -----------------------------------------------------------
-- 默认管理员由 DataInitializer 在应用启动时自动创建
-- 密码: admin123  (BCrypt 实时哈希)
-- -----------------------------------------------------------
