package com.yj.accountfee.feerecord.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yj.accountfee.common.BizException;
import com.yj.accountfee.common.CurrentUser;
import com.yj.accountfee.common.PageResult;
import com.yj.accountfee.customer.entity.Customer;
import com.yj.accountfee.customer.service.CustomerService;
import com.yj.accountfee.feerecord.dto.FeeRecordQueryDTO;
import com.yj.accountfee.feerecord.dto.FeeRecordSaveDTO;
import com.yj.accountfee.feerecord.entity.ServiceFeeRecord;
import com.yj.accountfee.feerecord.mapper.ServiceFeeRecordMapper;
import com.yj.accountfee.feerecord.vo.FeeRecordExportRow;
import com.yj.accountfee.feerecord.vo.FeeRecordVO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ServiceFeeRecordService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ServiceFeeRecordMapper recordMapper;
    private final CustomerService customerService;

    public ServiceFeeRecordService(ServiceFeeRecordMapper recordMapper, CustomerService customerService) {
        this.recordMapper = recordMapper;
        this.customerService = customerService;
    }

    public PageResult<FeeRecordVO> page(FeeRecordQueryDTO query) {
        Page<FeeRecordVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        recordMapper.selectRecordPage(page, query);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }

    public FeeRecordVO detail(Long id) {
        FeeRecordVO vo = recordMapper.selectRecordDetail(id);
        if (vo == null) {
            throw new BizException("收款记录不存在");
        }
        return vo;
    }

    public Long create(FeeRecordSaveDTO dto) {
        Customer customer = customerService.getActive(dto.getCustomerId());
        ServiceFeeRecord record = new ServiceFeeRecord();
        BeanUtils.copyProperties(dto, record);
        record.setCompanyName(customer.getCompanyName());
        record.setCreateBy(CurrentUser.username());
        record.setCreateTime(LocalDateTime.now());
        record.setDeleted(0);
        recordMapper.insert(record);
        return record.getId();
    }

    public void update(Long id, FeeRecordSaveDTO dto) {
        ServiceFeeRecord record = getActive(id);
        Customer customer = customerService.getActive(dto.getCustomerId());
        BeanUtils.copyProperties(dto, record);
        record.setCompanyName(customer.getCompanyName());
        record.setUpdateBy(CurrentUser.username());
        record.setUpdateTime(LocalDateTime.now());
        recordMapper.updateById(record);
    }

    public void delete(Long id) {
        ServiceFeeRecord record = getActive(id);
        record.setDeleted(1);
        record.setUpdateBy(CurrentUser.username());
        record.setUpdateTime(LocalDateTime.now());
        recordMapper.updateById(record);
    }

    public void export(FeeRecordQueryDTO query, HttpServletResponse response) throws IOException {
        List<FeeRecordExportRow> rows = recordMapper.selectRecordList(query).stream()
            .map(this::toExportRow)
            .toList();
        String fileName = URLEncoder.encode("收款记录.xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        EasyExcel.write(response.getOutputStream(), FeeRecordExportRow.class)
            .sheet("收款记录")
            .doWrite(rows);
    }

    private ServiceFeeRecord getActive(Long id) {
        ServiceFeeRecord record = recordMapper.selectById(id);
        if (record == null || record.getDeleted() == null || record.getDeleted() == 1) {
            throw new BizException("收款记录不存在");
        }
        return record;
    }

    private FeeRecordExportRow toExportRow(FeeRecordVO vo) {
        return new FeeRecordExportRow(
            vo.getCompanyName(),
            vo.getReceiveDate() == null ? "" : vo.getReceiveDate().toString(),
            vo.getPayerName(),
            vo.getPayerContactName(),
            payMethodName(vo.getPayMethod()),
            vo.getReceiverName(),
            vo.getAmount(),
            vo.getChargeEndDate() == null ? "" : vo.getChargeEndDate().toString(),
            vo.getRemark(),
            vo.getCreateBy(),
            vo.getCreateTime() == null ? "" : vo.getCreateTime().format(DATE_TIME_FORMATTER)
        );
    }

    private String payMethodName(String payMethod) {
        if ("WECHAT".equals(payMethod)) {
            return "微信";
        }
        if ("ALIPAY".equals(payMethod)) {
            return "支付宝";
        }
        if ("BANK".equals(payMethod)) {
            return "对公";
        }
        if ("CASH".equals(payMethod)) {
            return "现金";
        }
        if ("OTHER".equals(payMethod)) {
            return "其他";
        }
        return payMethod;
    }
}
