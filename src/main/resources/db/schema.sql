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
-- 默认管理员由 DataInitializer 在应用启动时自动创建
-- 密码: admin123  (BCrypt 实时哈希)
-- -----------------------------------------------------------
