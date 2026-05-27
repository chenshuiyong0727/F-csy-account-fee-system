package com.yj.accountfee.customer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.alibaba.excel.EasyExcel;
import com.yj.accountfee.common.BizException;
import com.yj.accountfee.common.CurrentUser;
import com.yj.accountfee.common.PageResult;
import com.yj.accountfee.customer.dto.CustomerQueryDTO;
import com.yj.accountfee.customer.dto.CustomerSaveDTO;
import com.yj.accountfee.customer.entity.Customer;
import com.yj.accountfee.customer.mapper.CustomerMapper;
import com.yj.accountfee.customer.vo.CustomerExpireExportRow;
import com.yj.accountfee.customer.vo.CustomerExpireVO;
import com.yj.accountfee.customer.vo.CustomerVO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CustomerService {
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    public PageResult<CustomerVO> page(CustomerQueryDTO query) {
        Page<Customer> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>()
            .eq(Customer::getDeleted, 0)
            .like(StringUtils.hasText(query.getCompanyName()), Customer::getCompanyName, query.getCompanyName())
            .eq(query.getStatus() != null, Customer::getStatus, query.getStatus())
            .orderByDesc(Customer::getCreateTime);
        Page<Customer> result = customerMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords().stream().map(this::toVO).toList());
    }

    public PageResult<CustomerExpireVO> expirePage(CustomerQueryDTO query) {
        Page<CustomerExpireVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        customerMapper.selectExpirePage(page, query);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }

    public void exportExpire(CustomerQueryDTO query, HttpServletResponse response) throws IOException {
        var rows = customerMapper.selectExpireList(query).stream()
            .map(this::toExpireExportRow)
            .toList();
        String fileName = URLEncoder.encode("客户到期列表.xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        EasyExcel.write(response.getOutputStream(), CustomerExpireExportRow.class)
            .sheet("客户到期列表")
            .doWrite(rows);
    }

    public CustomerVO detail(Long id) {
        Customer customer = getActive(id);
        return toVO(customer);
    }

    public Long create(CustomerSaveDTO dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        customer.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        customer.setDeleted(0);
        customer.setCreateBy(CurrentUser.username());
        customer.setCreateTime(LocalDateTime.now());
        customerMapper.insert(customer);
        return customer.getId();
    }

    public void update(Long id, CustomerSaveDTO dto) {
        Customer customer = getActive(id);
        BeanUtils.copyProperties(dto, customer);
        customer.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        customer.setUpdateBy(CurrentUser.username());
        customer.setUpdateTime(LocalDateTime.now());
        customerMapper.updateById(customer);
    }

    public void delete(Long id) {
        Customer customer = getActive(id);
        customer.setDeleted(1);
        customer.setUpdateBy(CurrentUser.username());
        customer.setUpdateTime(LocalDateTime.now());
        customerMapper.updateById(customer);
    }

    public Customer getActive(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null || customer.getDeleted() == null || customer.getDeleted() == 1) {
            throw new BizException("客户不存在");
        }
        return customer;
    }

    private CustomerVO toVO(Customer customer) {
        CustomerVO vo = new CustomerVO();
        BeanUtils.copyProperties(customer, vo);
        return vo;
    }

    private CustomerExpireExportRow toExpireExportRow(CustomerExpireVO vo) {
        return new CustomerExpireExportRow(
            vo.getCompanyName(),
            vo.getLatestChargeEndDate() == null ? "" : vo.getLatestChargeEndDate().toString(),
            vo.getRemainDays(),
            expireStatusName(vo.getExpireStatus()),
            vo.getPrimaryContactName(),
            vo.getPrimaryContactPhone(),
            vo.getPrimaryContactWechat(),
            vo.getLatestReceiveDate() == null ? "" : vo.getLatestReceiveDate().toString(),
            vo.getLatestReceiveAmount() == null ? "" : vo.getLatestReceiveAmount().toPlainString(),
            vo.getRemark()
        );
    }

    private String expireStatusName(String status) {
        if ("NORMAL".equals(status)) {
            return "正常";
        }
        if ("WARNING_60".equals(status)) {
            return "60天内到期";
        }
        if ("WARNING_30".equals(status)) {
            return "30天内到期";
        }
        if ("WARNING_5".equals(status)) {
            return "5天内到期";
        }
        if ("EXPIRED".equals(status)) {
            return "已过期/欠费";
        }
        if ("NO_RECORD".equals(status)) {
            return "无收费记录";
        }
        return status;
    }
}
