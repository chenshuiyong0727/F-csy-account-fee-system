package com.yj.accountfee.dashboard;

import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final DashboardMapper dashboardMapper;

    public DashboardService(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    public DashboardSummaryVO summary() {
        DashboardSummaryVO summary = dashboardMapper.selectSummary();
        return summary == null ? new DashboardSummaryVO() : summary;
    }
}
