package com.yj.accountfee.contact.controller;

import com.yj.accountfee.common.ApiResult;
import com.yj.accountfee.contact.dto.ContactSaveDTO;
import com.yj.accountfee.contact.service.CustomerContactService;
import com.yj.accountfee.contact.vo.ContactVO;
import jakarta.validation.Valid;
import java.util.List;
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
public class CustomerContactController {
    private final CustomerContactService contactService;

    public CustomerContactController(CustomerContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/{customerId}/contact/list")
    public ApiResult<List<ContactVO>> list(@PathVariable Long customerId) {
        return ApiResult.success(contactService.list(customerId));
    }

    @PostMapping("/{customerId}/contact")
    public ApiResult<Long> create(@PathVariable Long customerId, @Valid @RequestBody ContactSaveDTO dto) {
        return ApiResult.success(contactService.create(customerId, dto));
    }

    @PutMapping("/contact/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @Valid @RequestBody ContactSaveDTO dto) {
        contactService.update(id, dto);
        return ApiResult.success();
    }

    @DeleteMapping("/contact/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        contactService.delete(id);
        return ApiResult.success();
    }

    @PutMapping("/contact/{id}/primary")
    public ApiResult<Void> setPrimary(@PathVariable Long id) {
        contactService.setPrimary(id);
        return ApiResult.success();
    }
}
