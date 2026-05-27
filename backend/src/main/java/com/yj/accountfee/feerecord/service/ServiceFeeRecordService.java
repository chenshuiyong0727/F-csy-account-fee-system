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
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ServiceFeeRecordService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M月d日");

    private final ServiceFeeRecordMapper recordMapper;
    private final CustomerService customerService;

    public ServiceFeeRecordService(ServiceFeeRecordMapper recordMapper, CustomerService customerService) {
        this.recordMapper = recordMapper;
        this.customerService = customerService;
    }

    public PageResult<FeeRecordVO> page(FeeRecordQueryDTO query) {
        Page<FeeRecordVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        page.setRecords(recordMapper.selectRecordPage(page, query));
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

    public void exportCashDetail(FeeRecordQueryDTO query, HttpServletResponse response) throws IOException {
        List<FeeRecordVO> records = recordMapper.selectRecordList(query);
        String fileName = URLEncoder.encode("现金收支明细表.xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        try (Workbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("现金收支明细表");
            int[] widths = {12, 12, 58, 14, 14, 14, 14, 24};
            for (int i = 0; i < widths.length; i++) {
                sheet.setColumnWidth(i, widths[i] * 256);
            }

            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle metaStyle = createMetaStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle textStyle = createTextStyle(workbook);
            CellStyle amountStyle = createAmountStyle(workbook);

            Row title = sheet.createRow(0);
            title.setHeightInPoints(28);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("现金收支明细表");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            Row meta = sheet.createRow(1);
            createCell(meta, 0, "编制部门：", metaStyle);
            createCell(meta, 1, "", metaStyle);
            createCell(meta, 2, "经办人：" + safe(CurrentUser.username()), metaStyle);
            createCell(meta, 5, "日期：", metaStyle);
            createCell(meta, 6, dateRangeText(query), metaStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 7));

            Row header = sheet.createRow(2);
            String[] headers = {"日期", "编号", "摘要", "上月余额", "收入", "支出", "余额", "备注"};
            for (int i = 0; i < headers.length; i++) {
                createCell(header, i, headers[i], headerStyle);
            }
            sheet.setAutoFilter(new CellRangeAddress(2, 2, 0, 7));

            BigDecimal balance = query.getOpeningBalance() == null ? BigDecimal.ZERO : query.getOpeningBalance();
            Row opening = sheet.createRow(3);
            createCell(opening, 2, "上月余额", textStyle);
            createAmountCell(opening, 6, balance, amountStyle);

            int rowIndex = 4;
            for (FeeRecordVO record : records) {
                Row row = sheet.createRow(rowIndex++);
                row.setHeightInPoints(22);
                createCell(row, 0, record.getReceiveDate() == null ? "" : record.getReceiveDate().format(DATE_FORMATTER), textStyle);
                createCell(row, 1, safe(record.getReceiverName()), textStyle);
                createCell(row, 2, summaryText(record), textStyle);
                createCell(row, 3, "", textStyle);
                createAmountCell(row, 4, record.getAmount(), amountStyle);
                createCell(row, 5, "", amountStyle);
                balance = balance.add(record.getAmount() == null ? BigDecimal.ZERO : record.getAmount());
                createAmountCell(row, 6, balance, amountStyle);
                createCell(row, 7, safe(record.getRemark()), textStyle);
            }

            Row bank = sheet.createRow(rowIndex + 1);
            createCell(bank, 2, "银行账号：", metaStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex + 1, rowIndex + 1, 2, 7));

            workbook.write(response.getOutputStream());
        }
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

    private String dateRangeText(FeeRecordQueryDTO query) {
        if (query.getReceiveDateStart() != null && query.getReceiveDateEnd() != null) {
            return query.getReceiveDateStart() + "-" + query.getReceiveDateEnd();
        }
        if (query.getDateType() != null && query.getDateType().equals("month")) {
            return "本月";
        }
        if (query.getDateType() != null && query.getDateType().equals("year")) {
            return "本年";
        }
        return "";
    }

    private String summaryText(FeeRecordVO record) {
        StringBuilder builder = new StringBuilder("收款");
        builder.append(safe(record.getCompanyName()));
        if (record.getPayerName() != null && !record.getPayerName().isBlank()) {
            builder.append(" ").append(record.getPayerName());
        }
        if (record.getChargeEndDate() != null) {
            builder.append(" 会计服务费收费到").append(record.getChargeEndDate());
        }
        return builder.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void createCell(Row row, int index, String value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createAmountCell(Row row, int index, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(index);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
        cell.setCellStyle(style);
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createMetaStyle(Workbook workbook) {
        CellStyle style = baseBorderStyle(workbook);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = baseBorderStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor((short) 22);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createTextStyle(Workbook workbook) {
        CellStyle style = baseBorderStyle(workbook);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createAmountStyle(Workbook workbook) {
        CellStyle style = baseBorderStyle(workbook);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    private CellStyle baseBorderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
}
