package com.my.rx.bo.rx.api;

import com.my.rx.bo.report.api.BoReportProcessor;
import com.my.rx.bo.rx.domain.BoTask;
import com.my.rx.bo.rx.domain.BoTaskReport;

/**
 */
public interface BoTaskExecutor extends Runnable {

    void setTaskProcessor(BoTaskProcessor taskProcessor);

    void setTask(BoTask task);

    <T, R> void setReportProcessor(BoReportProcessor<T, BoTaskReport<R>> reportProcessor);
}
