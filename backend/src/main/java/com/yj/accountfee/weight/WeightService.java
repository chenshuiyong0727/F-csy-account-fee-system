package com.yj.accountfee.weight;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yj.accountfee.common.BizException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WeightService {
    private static final BigDecimal TWO = new BigDecimal("2");
    private final WeightProfileMapper profileMapper;
    private final WeightRecordMapper recordMapper;

    public WeightService(WeightProfileMapper profileMapper, WeightRecordMapper recordMapper) {
        this.profileMapper = profileMapper;
        this.recordMapper = recordMapper;
    }

    public List<WeightProfile> profiles() {
        return profileMapper.selectList(new LambdaQueryWrapper<WeightProfile>()
            .eq(WeightProfile::getStatus, 1)
            .orderByAsc(WeightProfile::getId));
    }

    public WeightProfile defaultProfile() {
        List<WeightProfile> profiles = profiles();
        if (profiles.isEmpty()) {
            throw new BizException("体重档案尚未初始化");
        }
        return profiles.get(0);
    }

    public WeightProfile profile(Long id) {
        WeightProfile profile = profileMapper.selectById(id);
        if (profile == null || !Integer.valueOf(1).equals(profile.getStatus())) {
            throw new BizException("人员档案不存在");
        }
        return profile;
    }

    public Long createProfile(WeightProfileSaveDTO dto) {
        validateProfile(dto);
        WeightProfile profile = new WeightProfile();
        BeanUtils.copyProperties(dto, profile);
        profile.setShareToken(UUID.randomUUID().toString().replace("-", ""));
        profile.setStatus(1);
        profile.setCreateTime(LocalDateTime.now());
        profileMapper.insert(profile);
        return profile.getId();
    }

    public void updateProfile(Long id, WeightProfileSaveDTO dto) {
        WeightProfile profile = profile(id);
        validateProfile(dto);
        BeanUtils.copyProperties(dto, profile);
        profile.setUpdateTime(LocalDateTime.now());
        profileMapper.updateById(profile);
    }

    public void updateAvatar(Long id, String avatarUrl) {
        WeightProfile profile = profile(id);
        if (!avatarUrl.startsWith("/uploads/weight/") && !avatarUrl.startsWith("/brand/")) {
            throw new BizException("头像地址无效");
        }
        profile.setAvatarUrl(avatarUrl);
        profile.setUpdateTime(LocalDateTime.now());
        profileMapper.updateById(profile);
    }

    public void deleteProfile(Long id) {
        WeightProfile profile = profile(id);
        profile.setStatus(0);
        profile.setUpdateTime(LocalDateTime.now());
        profileMapper.updateById(profile);
    }

    public List<WeightRecord> records(Long profileId) {
        profile(profileId);
        return recordMapper.selectList(new LambdaQueryWrapper<WeightRecord>()
            .eq(WeightRecord::getProfileId, profileId)
            .orderByDesc(WeightRecord::getRecordDate)
            .orderByDesc(WeightRecord::getId));
    }

    @Transactional
    public Long createRecord(Long profileId, WeightRecordSaveDTO dto) {
        WeightProfile profile = profile(profileId);
        validateRecord(profile, dto);
        WeightRecord record = new WeightRecord();
        BeanUtils.copyProperties(dto, record);
        record.setProfileId(profileId);
        record.setCreateTime(LocalDateTime.now());
        try {
            recordMapper.insert(record);
        } catch (DuplicateKeyException ex) {
            throw new BizException("该日期已经登记过体重");
        }
        return record.getId();
    }

    @Transactional
    public void updateRecord(Long id, WeightRecordSaveDTO dto) {
        WeightRecord record = recordMapper.selectById(id);
        if (record == null) {
            throw new BizException("体重记录不存在");
        }
        WeightProfile profile = profile(record.getProfileId());
        validateRecord(profile, dto);
        BeanUtils.copyProperties(dto, record);
        record.setUpdateTime(LocalDateTime.now());
        try {
            recordMapper.updateById(record);
        } catch (DuplicateKeyException ex) {
            throw new BizException("该日期已经登记过体重");
        }
    }

    public void deleteRecord(Long id) {
        WeightRecord record = recordMapper.selectById(id);
        if (record == null) {
            throw new BizException("体重记录不存在");
        }
        profile(record.getProfileId());
        recordMapper.deleteById(id);
    }

    @Transactional
    public WeightBatchResultVO batchCreateRecords(Long profileId, WeightBatchSaveDTO dto) {
        WeightProfile profile = profile(profileId);
        if (dto.getEndDate().isBefore(profile.getStartDate())) {
            throw new BizException("截止日期不能早于开始日期");
        }
        List<LocalDate> dates = generateDates(profile.getStartDate(), dto.getEndDate(), dto.getIntervalDays());
        if (dates.size() > 200) {
            throw new BizException("一次最多批量生成200条记录");
        }
        if (dates.size() != dto.getWeights().size()) {
            throw new BizException("体重数量与生成日期数量不一致");
        }

        List<WeightRecord> existing = recordMapper.selectList(new LambdaQueryWrapper<WeightRecord>()
            .eq(WeightRecord::getProfileId, profileId)
            .in(WeightRecord::getRecordDate, dates));
        int inserted = 0;
        int updated = 0;
        int skipped = 0;
        boolean overwrite = Boolean.TRUE.equals(dto.getOverwriteExisting());
        for (int i = 0; i < dates.size(); i++) {
            LocalDate date = dates.get(i);
            BigDecimal weight = dto.getWeights().get(i);
            validateWeight(date, weight);
            WeightRecord old = existing.stream()
                .filter(item -> item.getRecordDate().equals(date))
                .findFirst().orElse(null);
            if (old != null && !overwrite) {
                skipped++;
                continue;
            }
            if (old != null) {
                old.setWeightJin(weight);
                old.setBodyFatPercent(null);
                old.setUpdateTime(LocalDateTime.now());
                recordMapper.updateById(old);
                updated++;
            } else {
                WeightRecord record = new WeightRecord();
                record.setProfileId(profileId);
                record.setRecordDate(date);
                record.setWeightJin(weight);
                record.setCreateTime(LocalDateTime.now());
                recordMapper.insert(record);
                inserted++;
            }
        }
        return new WeightBatchResultVO(inserted, updated, skipped);
    }

    public List<LocalDate> generateDates(LocalDate startDate, LocalDate endDate, int intervalDays) {
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(intervalDays)) {
            dates.add(date);
        }
        return dates;
    }

    public PublicWeightVO publicData(Long profileId) {
        WeightProfile profile = profileId == null ? defaultProfile() : profile(profileId);
        List<WeightRecord> records = recordMapper.selectList(new LambdaQueryWrapper<WeightRecord>()
            .eq(WeightRecord::getProfileId, profile.getId())
            .orderByAsc(WeightRecord::getRecordDate)
            .orderByAsc(WeightRecord::getId));
        if (records.isEmpty()) {
            throw new BizException("还没有体重记录");
        }
        return buildPublicData(profile, records);
    }

    private PublicWeightVO buildPublicData(WeightProfile profile, List<WeightRecord> records) {
        WeightRecord current = records.get(records.size() - 1);
        WeightRecord previous = records.size() > 1 ? records.get(records.size() - 2) : null;
        BigDecimal bmi = calculateBmi(current.getWeightJin(), profile.getHeightCm());
        boolean fatEstimated = current.getBodyFatPercent() == null;
        BigDecimal currentFat = fatEstimated
            ? estimateBodyFat(bmi, profile, current.getRecordDate())
            : current.getBodyFatPercent().setScale(1, RoundingMode.HALF_UP);
        BigDecimal totalLoss = profile.getStartWeightJin().subtract(current.getWeightJin());
        BigDecimal totalNeedLoss = profile.getStartWeightJin().subtract(profile.getTargetWeightJin());
        BigDecimal progress = totalNeedLoss.signum() <= 0
            ? BigDecimal.ZERO
            : totalLoss.multiply(new BigDecimal("100")).divide(totalNeedLoss, 1, RoundingMode.HALF_UP);
        progress = progress.max(BigDecimal.ZERO).min(new BigDecimal("100.0"));

        PublicWeightVO vo = new PublicWeightVO();
        vo.setNickname(profile.getNickname());
        vo.setAvatar(profile.getAvatarUrl());
        vo.setHeightCm(profile.getHeightCm());
        vo.setStartDate(profile.getStartDate());
        vo.setEndDate(current.getRecordDate());
        vo.setStartWeight(scale1(profile.getStartWeightJin()));
        vo.setTargetWeight(scale1(profile.getTargetWeightJin()));
        vo.setTodayWeight(scale1(current.getWeightJin()));
        vo.setChangeFromLast(previous == null
            ? BigDecimal.ZERO
            : scale1(previous.getWeightJin().subtract(current.getWeightJin())));
        vo.setTotalLoss(scale1(totalLoss));
        vo.setDistanceTarget(scale1(current.getWeightJin().subtract(profile.getTargetWeightJin())));
        vo.setBmi(bmi);
        vo.setFat(currentFat);
        vo.setFatEstimated(fatEstimated);
        vo.setDays(Math.max(0, ChronoUnit.DAYS.between(profile.getStartDate(), current.getRecordDate()) + 1));
        vo.setProgress(progress);
        vo.setRecords(records.stream().map(record -> {
            BigDecimal recordBmi = calculateBmi(record.getWeightJin(), profile.getHeightCm());
            boolean estimated = record.getBodyFatPercent() == null;
            BigDecimal fat = estimated
                ? estimateBodyFat(recordBmi, profile, record.getRecordDate())
                : record.getBodyFatPercent().setScale(1, RoundingMode.HALF_UP);
            return new PublicWeightRecordVO(record.getRecordDate(), scale1(record.getWeightJin()), fat, estimated);
        }).toList());
        return vo;
    }

    private void validateProfile(WeightProfileSaveDTO dto) {
        if (dto.getTargetWeightJin().compareTo(dto.getStartWeightJin()) >= 0) {
            throw new BizException("目标体重必须小于起始体重");
        }
    }

    private void validateRecord(WeightProfile profile, WeightRecordSaveDTO dto) {
        if (dto.getRecordDate().isBefore(profile.getStartDate())) {
            throw new BizException("登记日期不能早于开始日期");
        }
    }

    private void validateWeight(LocalDate date, BigDecimal weight) {
        if (weight.compareTo(BigDecimal.ONE) < 0 || weight.compareTo(new BigDecimal("999.99")) > 0) {
            throw new BizException(date + " 的体重必须在1到999.99斤之间");
        }
    }

    private BigDecimal calculateBmi(BigDecimal weightJin, BigDecimal heightCm) {
        BigDecimal weightKg = weightJin.divide(TWO, 4, RoundingMode.HALF_UP);
        BigDecimal heightM = heightCm.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        return weightKg.divide(heightM.multiply(heightM), 1, RoundingMode.HALF_UP);
    }

    private BigDecimal estimateBodyFat(BigDecimal bmi, WeightProfile profile, LocalDate recordDate) {
        int age = Math.max(0, Period.between(profile.getBirthDate(), recordDate).getYears());
        BigDecimal genderFactor = "M".equals(profile.getGender()) ? BigDecimal.ONE : BigDecimal.ZERO;
        BigDecimal estimate = bmi.multiply(new BigDecimal("1.2"))
            .add(new BigDecimal("0.23").multiply(BigDecimal.valueOf(age)))
            .subtract(new BigDecimal("10.8").multiply(genderFactor))
            .subtract(new BigDecimal("5.4"));
        return estimate.max(new BigDecimal("2")).min(new BigDecimal("65"))
            .setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal scale1(BigDecimal value) {
        return value.setScale(1, RoundingMode.HALF_UP);
    }
}
