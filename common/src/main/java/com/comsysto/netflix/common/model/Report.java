package com.comsysto.netflix.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

public class Report {
    private final Date reportGenerationTime;
    private final Map<Country, Map<DataType, BigInteger>> reportData;

    @JsonCreator
    public Report(@JsonProperty("reportGenerationTime")Date reportGenerationTime, @JsonProperty("reportData")Map<Country, Map<DataType, BigInteger>> reportData) {
        this.reportGenerationTime = reportGenerationTime;
        this.reportData = reportData;
    }

    public Date getReportGenerationTime() {
        return reportGenerationTime;
    }

    public Map<Country, Map<DataType, BigInteger>> getReportData() {
        return reportData;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportGenerationTime=" + reportGenerationTime +
                ", reportData=" + reportData +
                '}';
    }
}
