package com.yj.accountfee.customer.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerExpireExportRow {
    @ExcelProperty("公司名称")
    private String companyName;
    @ExcelProperty("最新收费到日期")
    private String latestChargeEndDate;
    @ExcelProperty("剩余天数")
    private Integer remainDays;
    @ExcelProperty("到期状态")
    private String expireStatus;
    @ExcelProperty("主要联系人")
    private String primaryContactName;
    @ExcelProperty("联系电话")
    private String primaryContactPhone;
    @ExcelProperty("微信号")
    private String primaryContactWechat;
    @ExcelProperty("最近收款日期")
    private String latestReceiveDate;
    @ExcelProperty("最近收款金额")
    private String latestReceiveAmount;
    @ExcelProperty("备注")
    private String remark;
}
