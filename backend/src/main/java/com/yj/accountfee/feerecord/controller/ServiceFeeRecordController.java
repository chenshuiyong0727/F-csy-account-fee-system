package com.yj.accountfee.feerecord.controller;

import com.yj.accountfee.common.ApiResult;
import com.yj.accountfee.common.PageResult;
import com.yj.accountfee.feerecord.dto.FeeRecordQueryDTO;
import com.yj.accountfee.feerecord.dto.FeeRecordSaveDTO;
import com.yj.accountfee.feerecord.service.ServiceFeeRecordService;
import com.yj.accountfee.feerecord.vo.FeeRecordVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fee-record")
public class ServiceFeeRecordController {
    private final ServiceFeeRecordService recordService;

    public ServiceFeeRecordController(ServiceFeeRecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/page")
    public ApiResult<PageResult<FeeRecordVO>> page(FeeRecordQueryDTO query) {
        return ApiResult.success(recordService.page(query));
    }

    @GetMapping("/{id}")
    public ApiResult<FeeRecordVO> detail(@PathVariable Long id) {
        return ApiResult.success(recordService.detail(id));
    }

    @PostMapping
    public ApiResult<Long> create(@Valid @RequestBody FeeRecordSaveDTO dto) {
        return ApiResult.success(recordService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @Valid @RequestBody FeeRecordSaveDTO dto) {
        recordService.update(id, dto);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        recordService.delete(id);
        return ApiResult.success();
    }

    @GetMapping("/export")
    public void export(FeeRecordQueryDTO query, HttpServletResponse response) throws IOException {
        recordService.export(query, response);
    }
}
