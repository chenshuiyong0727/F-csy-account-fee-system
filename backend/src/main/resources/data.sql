INSERT INTO sys_user (username, password, real_name, role_code, status)
SELECT 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '管理员', 'ADMIN', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

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
