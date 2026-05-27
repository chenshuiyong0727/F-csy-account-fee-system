INSERT INTO sys_user (username, password, real_name, role_code, status)
SELECT 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '管理员', 'ADMIN', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');
