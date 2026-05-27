package com.yj.accountfee.contact.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yj.accountfee.common.BizException;
import com.yj.accountfee.contact.dto.ContactSaveDTO;
import com.yj.accountfee.contact.entity.CustomerContact;
import com.yj.accountfee.contact.mapper.CustomerContactMapper;
import com.yj.accountfee.contact.vo.ContactVO;
import com.yj.accountfee.customer.service.CustomerService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class CustomerContactService {
    private final CustomerContactMapper contactMapper;
    private final CustomerService customerService;

    public CustomerContactService(CustomerContactMapper contactMapper, CustomerService customerService) {
        this.contactMapper = contactMapper;
        this.customerService = customerService;
    }

    public List<ContactVO> list(Long customerId) {
        customerService.getActive(customerId);
        return contactMapper.selectList(new LambdaQueryWrapper<CustomerContact>()
            .eq(CustomerContact::getCustomerId, customerId)
            .eq(CustomerContact::getDeleted, 0)
            .orderByDesc(CustomerContact::getIsPrimary)
            .orderByDesc(CustomerContact::getCreateTime))
            .stream().map(this::toVO).toList();
    }

    public Long create(Long customerId, ContactSaveDTO dto) {
        customerService.getActive(customerId);
        if (dto.getIsPrimary() != null && dto.getIsPrimary() == 1) {
            clearPrimary(customerId);
        }
        CustomerContact contact = new CustomerContact();
        BeanUtils.copyProperties(dto, contact);
        contact.setCustomerId(customerId);
        contact.setIsPrimary(dto.getIsPrimary() == null ? 0 : dto.getIsPrimary());
        contact.setDeleted(0);
        contact.setCreateTime(LocalDateTime.now());
        contactMapper.insert(contact);
        return contact.getId();
    }

    public void update(Long id, ContactSaveDTO dto) {
        CustomerContact contact = getActive(id);
        if (dto.getIsPrimary() != null && dto.getIsPrimary() == 1) {
            clearPrimary(contact.getCustomerId());
        }
        BeanUtils.copyProperties(dto, contact);
        contact.setIsPrimary(dto.getIsPrimary() == null ? 0 : dto.getIsPrimary());
        contact.setUpdateTime(LocalDateTime.now());
        contactMapper.updateById(contact);
    }

    public void delete(Long id) {
        CustomerContact contact = getActive(id);
        contact.setDeleted(1);
        contact.setUpdateTime(LocalDateTime.now());
        contactMapper.updateById(contact);
    }

    public void setPrimary(Long id) {
        CustomerContact contact = getActive(id);
        clearPrimary(contact.getCustomerId());
        contact.setIsPrimary(1);
        contact.setUpdateTime(LocalDateTime.now());
        contactMapper.updateById(contact);
    }

    public CustomerContact getActive(Long id) {
        CustomerContact contact = contactMapper.selectById(id);
        if (contact == null || contact.getDeleted() == null || contact.getDeleted() == 1) {
            throw new BizException("联系人不存在");
        }
        return contact;
    }

    private void clearPrimary(Long customerId) {
        List<CustomerContact> contacts = contactMapper.selectList(new LambdaQueryWrapper<CustomerContact>()
            .eq(CustomerContact::getCustomerId, customerId)
            .eq(CustomerContact::getDeleted, 0)
            .eq(CustomerContact::getIsPrimary, 1));
        for (CustomerContact contact : contacts) {
            contact.setIsPrimary(0);
            contact.setUpdateTime(LocalDateTime.now());
            contactMapper.updateById(contact);
        }
    }

    private ContactVO toVO(CustomerContact contact) {
        ContactVO vo = new ContactVO();
        BeanUtils.copyProperties(contact, vo);
        return vo;
    }
}
