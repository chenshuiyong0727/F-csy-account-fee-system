CREATE DATABASE IF NOT EXISTS account_fee DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE account_fee;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '账号',
    password VARCHAR(100) NOT NULL COMMENT '密码，加密存储',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '姓名',
    role_code VARCHAR(30) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN管理员，USER普通用户',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0停用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS acct_customer (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    company_name VARCHAR(200) NOT NULL COMMENT '公司名称',
    tax_no VARCHAR(100) DEFAULT NULL COMMENT '税号',
    address VARCHAR(300) DEFAULT NULL COMMENT '地址',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0停用',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0正常，1删除',
    create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) DEFAULT NULL COMMENT '修改人',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_company_name (company_name),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户公司表';

CREATE TABLE IF NOT EXISTS acct_customer_contact (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户公司ID',
    contact_name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    contact_phone VARCHAR(30) DEFAULT NULL COMMENT '联系电话',
    contact_wechat VARCHAR(100) DEFAULT NULL COMMENT '微信号',
    position_name VARCHAR(100) DEFAULT NULL COMMENT '职位/身份，如老板、财务、经办人',
    is_primary TINYINT NOT NULL DEFAULT 0 COMMENT '是否主要联系人：1是，0否',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0正常，1删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id),
    KEY idx_contact_phone (contact_phone),
    KEY idx_is_primary (is_primary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户联系人表';

CREATE TABLE IF NOT EXISTS acct_service_fee_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户公司ID',
    company_name VARCHAR(200) NOT NULL COMMENT '公司名称冗余',
    receive_date DATE NOT NULL COMMENT '收款日期',
    payer_name VARCHAR(100) DEFAULT NULL COMMENT '付款人/转账人',
    payer_contact_id BIGINT DEFAULT NULL COMMENT '付款联系人ID，可为空',
    pay_method VARCHAR(20) NOT NULL COMMENT '收款方式：WECHAT微信，ALIPAY支付宝，BANK对公，CASH现金，OTHER其他',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收款人',
    amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '收款金额',
    charge_end_date DATE NOT NULL COMMENT '收费到日期',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(50) DEFAULT NULL COMMENT '登记人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
    update_by VARCHAR(50) DEFAULT NULL COMMENT '修改人',
    update_time DATETIME DEFAULT NULL COMMENT '修改时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0正常，1删除',
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id),
    KEY idx_company_name (company_name),
    KEY idx_receive_date (receive_date),
    KEY idx_charge_end_date (charge_end_date),
    KEY idx_receiver_name (receiver_name),
    KEY idx_pay_method (pay_method),
    KEY idx_payer_contact_id (payer_contact_id),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会计服务费收款记录表';

INSERT INTO sys_user (username, password, real_name, role_code, status)
SELECT 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '管理员', 'ADMIN', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');
