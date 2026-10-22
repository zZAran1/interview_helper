-- ============================================================
-- interview_helper 数据库初始化脚本（结构还原自本地开发库，共 7 张表）
-- 用法一（命令行）：mysql -u root -p < init.sql
-- 用法二（Docker）：挂载到 MySQL 容器 /docker-entrypoint-initdb.d/ 自动执行
-- ============================================================

CREATE DATABASE IF NOT EXISTS interview_helper
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE interview_helper;

-- ---------------- 用户表 ----------------
-- 已有数据库升级（不重建表时执行）：
--   ALTER TABLE users ADD COLUMN token_version INT NOT NULL DEFAULT 1 COMMENT 'token版本号';
CREATE TABLE IF NOT EXISTS users (
  user_id       INT          NOT NULL AUTO_INCREMENT COMMENT '用户id',
  username      VARCHAR(50)  NOT NULL COMMENT '用户名',
  password_hash VARCHAR(255) NOT NULL COMMENT '加密存储的密码',
  email         VARCHAR(100) NOT NULL COMMENT '用户邮箱',
  created_at    DATETIME     NULL COMMENT '注册时间',
  token_version INT          NOT NULL DEFAULT 1 COMMENT 'token版本号，登出/注销时+1使旧token全部失效',
  PRIMARY KEY (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- ---------------- 简历表（一个用户一份简历） ----------------
CREATE TABLE IF NOT EXISTS resume (
  id               INT          NOT NULL AUTO_INCREMENT COMMENT '简历id',
  user_id          INT          NOT NULL COMMENT '用户id',
  summary          TEXT         NULL COMMENT '个人简介',
  skill            VARCHAR(150) NULL COMMENT '技能',
  phone_number     VARCHAR(11)  NULL COMMENT '手机号码',
  real_name        VARCHAR(50)  NULL COMMENT '真实姓名',
  age              INT          NULL COMMENT '年龄',
  gender           VARCHAR(1)   NULL COMMENT '性别',
  education        VARCHAR(20)  NULL COMMENT '学历',
  certificate      VARCHAR(200) NULL COMMENT '证书',
  desired_position VARCHAR(100) NULL COMMENT '期望职位',
  PRIMARY KEY (id),
  UNIQUE KEY uk_resume_user (user_id),
  CONSTRAINT fk_resume_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- ---------------- 面试记录表 ----------------
CREATE TABLE IF NOT EXISTS interview (
  id         INT      NOT NULL AUTO_INCREMENT,
  user_id    INT      NULL COMMENT '所属用户ID',
  rating     FLOAT    NULL COMMENT '评分',
  suggestion TEXT     NULL COMMENT '整体建议',
  created_at DATETIME NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_interview_user (user_id),
  CONSTRAINT fk_interview_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- ---------------- 面试会话消息表 ----------------
-- 注意：外键无级联删除（与代码一致：删除面试前需先删除消息）
CREATE TABLE IF NOT EXISTS session_message (
  id           INT            NOT NULL AUTO_INCREMENT,
  interview_id INT            NULL,
  role         ENUM ('ai', 'user') NULL,
  messagetext  VARCHAR(1024)  NULL,
  send_date    DATETIME       NULL,
  PRIMARY KEY (id),
  KEY idx_message_interview (interview_id),
  CONSTRAINT fk_message_interview FOREIGN KEY (interview_id) REFERENCES interview (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- ---------------- 简历-教育经历表 ----------------
CREATE TABLE IF NOT EXISTS resume_education (
  id             INT          NOT NULL AUTO_INCREMENT COMMENT 'id',
  resume_id      INT          NULL COMMENT '所属简历id',
  school_name    VARCHAR(100) NULL COMMENT '学校名称',
  degree         VARCHAR(50)  NULL COMMENT '学历层次',
  field_of_study VARCHAR(100) NULL COMMENT '专业',
  start_date     DATE         NULL COMMENT '开始日期',
  end_date       DATE         NULL COMMENT '结束日期',
  core_courses   VARCHAR(100) NULL COMMENT '主修课程',
  appendix       TEXT         NULL COMMENT '在校经历',
  PRIMARY KEY (id),
  KEY idx_edu_resume (resume_id),
  CONSTRAINT fk_edu_resume FOREIGN KEY (resume_id) REFERENCES resume (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- ---------------- 简历-项目经历表 ----------------
CREATE TABLE IF NOT EXISTS resume_project (
  id            INT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '项目经历id',
  project_name  VARCHAR(100)  NULL COMMENT '项目名称',
  role          VARCHAR(50)   NULL COMMENT '在项目中承担的角色',
  start_date    DATE          NULL COMMENT '项目开始日期',
  end_date      DATE          NULL COMMENT '项目结束日期',
  description   TEXT          NULL COMMENT '补充描述',
  resume_id     INT           NULL COMMENT '所属简历id',
  PRIMARY KEY (id),
  KEY idx_project_resume (resume_id),
  CONSTRAINT fk_project_resume FOREIGN KEY (resume_id) REFERENCES resume (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- ---------------- 简历-工作经历表 ----------------
CREATE TABLE IF NOT EXISTS resume_work (
  id           INT          NOT NULL AUTO_INCREMENT COMMENT '工作经历id',
  resume_id    INT          NULL COMMENT '所属简历id',
  company_name VARCHAR(100) NULL COMMENT '公司名字',
  position     VARCHAR(50)  NULL COMMENT '工作岗位',
  start_date   DATE         NULL COMMENT '开始日期',
  end_date     DATE         NULL COMMENT '结束日期',
  description  TEXT         NULL COMMENT '补充描述',
  PRIMARY KEY (id),
  KEY idx_work_resume (resume_id),
  CONSTRAINT fk_work_resume FOREIGN KEY (resume_id) REFERENCES resume (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
