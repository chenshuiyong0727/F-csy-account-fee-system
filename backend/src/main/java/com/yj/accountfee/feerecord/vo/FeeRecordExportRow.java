package com.yj.accountfee.feerecord.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeRecordExportRow {
    @ExcelProperty("公司名称")
    private String companyName;
    @ExcelProperty("收款日期")
    private String receiveDate;
    @ExcelProperty("付款人/转账人")
    private String payerName;
    @ExcelProperty("付款联系人")
    private String payerContactName;
    @ExcelProperty("收款方式")
    private String payMethod;
    @ExcelProperty("收款人")
    private String receiverName;
    @ExcelProperty("收款金额")
    private BigDecimal amount;
    @ExcelProperty("收费到日期")
    private String chargeEndDate;
    @ExcelProperty("备注")
    private String remark;
    @ExcelProperty("登记人")
    private String createBy;
    @ExcelProperty("登记时间")
    private String createTime;
}
