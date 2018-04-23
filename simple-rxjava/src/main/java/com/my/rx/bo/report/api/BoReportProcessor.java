package com.my.rx.bo.report.api;

/**
 */
public interface BoReportProcessor<T, R> {

    void setTemplate(T report);

    void write(R report);
}