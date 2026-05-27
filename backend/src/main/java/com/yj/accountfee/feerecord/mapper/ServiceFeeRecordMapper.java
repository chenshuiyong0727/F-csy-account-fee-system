package com.yj.accountfee.feerecord.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yj.accountfee.feerecord.dto.FeeRecordQueryDTO;
import com.yj.accountfee.feerecord.entity.ServiceFeeRecord;
import com.yj.accountfee.feerecord.vo.FeeRecordVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ServiceFeeRecordMapper extends BaseMapper<ServiceFeeRecord> {
    List<FeeRecordVO> selectRecordPage(Page<FeeRecordVO> page, @Param("query") FeeRecordQueryDTO query);

    List<FeeRecordVO> selectRecordList(@Param("query") FeeRecordQueryDTO query);

    FeeRecordVO selectRecordDetail(@Param("id") Long id);
}
