package com.comsysto.netflix.report.cache.repository;

import com.comsysto.netflix.common.model.Report;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepositoryImpl implements ReportRepository {

    private Report data;

    @Override
    public Report get() {
        return this.data;
    }

    @Override
    public void put(Report report) {
        this.data = report;
    }
}
