package com.yj.accountfee.customer.controller;

import com.yj.accountfee.common.ApiResult;
import com.yj.accountfee.common.PageResult;
import com.yj.accountfee.customer.dto.CustomerQueryDTO;
import com.yj.accountfee.customer.dto.CustomerSaveDTO;
import com.yj.accountfee.customer.service.CustomerService;
import com.yj.accountfee.customer.vo.CustomerExpireVO;
import com.yj.accountfee.customer.vo.CustomerVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/page")
    public ApiResult<PageResult<CustomerVO>> page(CustomerQueryDTO query) {
        return ApiResult.success(customerService.page(query));
    }

    @GetMapping("/expire-page")
    public ApiResult<PageResult<CustomerExpireVO>> expirePage(CustomerQueryDTO query) {
        return ApiResult.success(customerService.expirePage(query));
    }

    @GetMapping("/{id}")
    public ApiResult<CustomerVO> detail(@PathVariable Long id) {
        return ApiResult.success(customerService.detail(id));
    }

    @PostMapping
    public ApiResult<Long> create(@Valid @RequestBody CustomerSaveDTO dto) {
        return ApiResult.success(customerService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @Valid @RequestBody CustomerSaveDTO dto) {
        customerService.update(id, dto);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ApiResult.success();
    }
}
