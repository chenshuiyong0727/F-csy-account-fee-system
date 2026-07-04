CREATE DATABASE IF NOT EXISTS account_fee DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE account_fee;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) DEFAULT NULL,
    role_code VARCHAR(30) NOT NULL DEFAULT 'USER',
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS acct_customer (
    id BIGINT NOT NULL AUTO_INCREMENT,
    company_name VARCHAR(200) NOT NULL,
    tax_no VARCHAR(100) DEFAULT NULL,
    address VARCHAR(300) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    deleted TINYINT NOT NULL DEFAULT 0,
    create_by VARCHAR(50) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50) DEFAULT NULL,
    update_time DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_company_name (company_name),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS acct_customer_contact (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    contact_name VARCHAR(50) NOT NULL,
    contact_phone VARCHAR(30) DEFAULT NULL,
    contact_wechat VARCHAR(100) DEFAULT NULL,
    position_name VARCHAR(100) DEFAULT NULL,
    is_primary TINYINT NOT NULL DEFAULT 0,
    remark VARCHAR(500) DEFAULT NULL,
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id),
    KEY idx_contact_phone (contact_phone),
    KEY idx_is_primary (is_primary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS acct_service_fee_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    receive_date DATE NOT NULL,
    payer_name VARCHAR(100) DEFAULT NULL,
    payer_contact_id BIGINT DEFAULT NULL,
    pay_method VARCHAR(20) NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    charge_end_date DATE NOT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_by VARCHAR(50) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50) DEFAULT NULL,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id),
    KEY idx_company_name (company_name),
    KEY idx_receive_date (receive_date),
    KEY idx_charge_end_date (charge_end_date),
    KEY idx_receiver_name (receiver_name),
    KEY idx_pay_method (pay_method),
    KEY idx_payer_contact_id (payer_contact_id),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO sys_user (username, password, real_name, role_code, status)
SELECT 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '管理员', 'ADMIN', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

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
