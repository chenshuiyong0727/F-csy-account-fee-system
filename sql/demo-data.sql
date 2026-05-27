USE account_fee;

INSERT INTO acct_customer (company_name, tax_no, address, remark, create_by)
VALUES
('杭州晨星贸易有限公司', '91330100MADEMO001', '杭州市西湖区', '演示客户：正常', 'admin'),
('宁波蓝海科技有限公司', '91330200MADEMO002', '宁波市鄞州区', '演示客户：即将到期', 'admin'),
('温州启明商贸有限公司', '91330300MADEMO003', '温州市鹿城区', '演示客户：已过期', 'admin'),
('绍兴云账企业管理有限公司', '91330600MADEMO004', '绍兴市越城区', '演示客户：无收费记录', 'admin');

INSERT INTO acct_customer_contact (customer_id, contact_name, contact_phone, contact_wechat, position_name, is_primary)
SELECT id, '张经理', '13800000001', 'demo001', '老板', 1 FROM acct_customer WHERE company_name = '杭州晨星贸易有限公司'
UNION ALL
SELECT id, '李会计', '13800000002', 'demo002', '财务', 1 FROM acct_customer WHERE company_name = '宁波蓝海科技有限公司'
UNION ALL
SELECT id, '王老板', '13800000003', 'demo003', '老板', 1 FROM acct_customer WHERE company_name = '温州启明商贸有限公司';

INSERT INTO acct_service_fee_record (customer_id, company_name, receive_date, payer_name, pay_method, receiver_name, amount, charge_end_date, remark, create_by)
SELECT id, company_name, CURDATE(), '张经理', 'WECHAT', '管理员', 2400.00, DATE_ADD(CURDATE(), INTERVAL 90 DAY), '演示收款', 'admin'
FROM acct_customer WHERE company_name = '杭州晨星贸易有限公司'
UNION ALL
SELECT id, company_name, CURDATE(), '李会计', 'BANK', '管理员', 1800.00, DATE_ADD(CURDATE(), INTERVAL 20 DAY), '演示收款', 'admin'
FROM acct_customer WHERE company_name = '宁波蓝海科技有限公司'
UNION ALL
SELECT id, company_name, DATE_SUB(CURDATE(), INTERVAL 40 DAY), '王老板', 'ALIPAY', '管理员', 1200.00, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '演示收款', 'admin'
FROM acct_customer WHERE company_name = '温州启明商贸有限公司';
