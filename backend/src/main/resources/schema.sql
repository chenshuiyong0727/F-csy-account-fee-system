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
