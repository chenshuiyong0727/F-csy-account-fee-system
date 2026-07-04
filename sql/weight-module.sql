-- 体重管理模块升级脚本（MySQL 5.7+）
-- 可重复执行，不会覆盖已存在的个人资料和同日体重记录。

USE account_fee;

CREATE TABLE IF NOT EXISTS weight_profile (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nickname VARCHAR(50) NOT NULL,
    avatar_url VARCHAR(500) DEFAULT NULL,
    birth_date DATE NOT NULL,
    gender CHAR(1) NOT NULL COMMENT 'M-男 F-女',
    height_cm DECIMAL(5,2) NOT NULL,
    start_date DATE NOT NULL,
    start_weight_jin DECIMAL(6,2) NOT NULL,
    target_weight_jin DECIMAL(6,2) NOT NULL,
    share_token VARCHAR(64) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_weight_profile_share_token (share_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS weight_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    profile_id BIGINT NOT NULL,
    record_date DATE NOT NULL,
    weight_jin DECIMAL(6,2) NOT NULL,
    body_fat_percent DECIMAL(5,2) DEFAULT NULL COMMENT '实测体脂率，为空时按公式估算',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_weight_record_profile_date (profile_id, record_date),
    KEY idx_weight_record_date (record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO weight_profile (
    id, nickname, avatar_url, birth_date, gender, height_cm,
    start_date, start_weight_jin, target_weight_jin, share_token, status
)
SELECT 1, '最硬', '/brand/person.png', '1992-05-18', 'M', 182.00,
       '2026-05-22', 210.50, 165.00, 'zy-4f8c9d2e7a6b51c3', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM weight_profile WHERE id = 1);

INSERT INTO weight_record (profile_id, record_date, weight_jin, body_fat_percent)
SELECT 1, seed.record_date, seed.weight_jin, seed.body_fat_percent
FROM (
    SELECT DATE('2026-05-22') record_date, 210.50 weight_jin, 31.20 body_fat_percent
    UNION ALL SELECT DATE('2026-05-28'), 205.80, 30.10
    UNION ALL SELECT DATE('2026-06-03'), 199.60, 28.80
    UNION ALL SELECT DATE('2026-06-09'), 193.40, 27.20
    UNION ALL SELECT DATE('2026-06-15'), 188.70, 25.90
    UNION ALL SELECT DATE('2026-06-22'), 183.50, 24.60
    UNION ALL SELECT DATE('2026-06-30'), 183.20, 24.30
) seed
WHERE NOT EXISTS (
    SELECT 1 FROM weight_record r
    WHERE r.profile_id = 1 AND r.record_date = seed.record_date
);
