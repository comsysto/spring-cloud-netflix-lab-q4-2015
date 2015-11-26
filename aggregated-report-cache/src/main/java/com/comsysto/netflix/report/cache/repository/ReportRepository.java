package com.comsysto.netflix.report.cache.repository;

import com.comsysto.netflix.common.model.Report;

/**
 * Holds a single report.
 */
public interface ReportRepository {

    /**
     * Get the current report.
     */
    Report get();

    /**
     * Store a fresh reoprt. Overwrite any present data.
     */
    void put(Report report);
}
