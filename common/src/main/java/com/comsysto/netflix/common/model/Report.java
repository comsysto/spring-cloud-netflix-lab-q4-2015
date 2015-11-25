package com.comsysto.netflix.common.model;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

public class Report {
    private final Date reportGenerationTime;
    private final Map<Location, Map<DataType, BigInteger>> reportData;

    public Report(Date reportGenerationTime, Map<Location, Map<DataType, BigInteger>> reportData) {
        this.reportGenerationTime = reportGenerationTime;
        this.reportData = reportData;
    }

    public Date getReportGenerationTime() {
        return reportGenerationTime;
    }

    public Map<Location, Map<DataType, BigInteger>> getReportData() {
        return reportData;
    }
}
