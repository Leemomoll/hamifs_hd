-- ============================================================
-- Hamifs 登录鉴权 - 建表脚本（阶段 1）
-- 说明：本脚本只放建表 DDL，默认数据由 AdminInitializer 在启动时
--       若 sys_user 不存在 admin 时自动插入 BCrypt(123456) 哈希。
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL                COMMENT '登录名',
    password    VARCHAR(100) NOT NULL                COMMENT 'BCrypt 哈希',
    name        VARCHAR(50)                          COMMENT '显示名（顶栏）',
    avatar      VARCHAR(255) DEFAULT ''               COMMENT '头像 URL',
    status      TINYINT      DEFAULT 1               COMMENT '状态：1启用 0禁用',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';