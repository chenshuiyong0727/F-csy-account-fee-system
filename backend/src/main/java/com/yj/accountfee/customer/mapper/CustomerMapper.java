package com.yj.accountfee.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yj.accountfee.customer.dto.CustomerQueryDTO;
import com.yj.accountfee.customer.entity.Customer;
import com.yj.accountfee.customer.vo.CustomerExpireVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CustomerMapper extends BaseMapper<Customer> {
    List<CustomerExpireVO> selectExpirePage(Page<CustomerExpireVO> page, @Param("query") CustomerQueryDTO query);

    CustomerExpireVO selectExpireDetail(@Param("customerId") Long customerId);
}
