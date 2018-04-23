package com.my.rx.bo.report;

import com.my.rx.bo.report.api.BoReportProcessor;
import com.my.rx.bo.rx.domain.BoJobResult;
import com.my.rx.bo.rx.domain.BoTaskAttribute;
import com.my.rx.bo.rx.domain.BoTaskReport;
import com.my.rx.bo.rx.enums.RxJobStatus;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class ConsoleReportProcessor<T, R> implements BoReportProcessor<String, BoTaskReport<Collection<BoJobResult<T>>>> {

    private String template;

    public ConsoleReportProcessor() {
        this.template = new StringBuffer()
                .append("\n* * * * * * * * * * * * * * * Report * * * * * * * * * * * * * * * * \n")
                .append("\n")
                .append("Task\n")
                .append("\tname: %s\n")
                .append("\tid: %s\n")
                .append("\toperator id: %s\n")
                .append("\tinitiator: %s\n")
                .append("\tstatus: %s\n")
                .append("\n")
                .append("Users\n")
                .append("\tsuccess count: %d\n")
                .append("\tfailure (count: %d):\n")
                .append("\t\t%s\n")
                .append("\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n")
                .toString();
    }

    @Override
    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public void write(BoTaskReport<Collection<BoJobResult<T>>> jobResult) {
        BoTaskAttribute taskAttribute = jobResult.attribute;
        Collection<BoJobResult<T>> resultData = jobResult.result;

        String report = format(template,
                taskAttribute.name,
                taskAttribute.token,
                taskAttribute.operatorId,
                taskAttribute.initiatorName,
                jobResult.status,
                getSuccessfullyCount(resultData),
                getErrorsCount(resultData),
                getUsersErrorList(resultData)
        );

        System.out.println(report);
    }

    private long getSuccessfullyCount(Collection<BoJobResult<T>> resultData) {
        return resultData.stream()
                .filter(u -> RxJobStatus.SUCCESS.equals(u.status))
                .count();
    }

    private long getErrorsCount(Collection<BoJobResult<T>> resultData) {
        return resultData.stream()
                .filter(u -> RxJobStatus.ERROR.equals(u.status) || RxJobStatus.FAILURE.equals(u.status))
                .count();
    }

    private List<String> getUsersErrorList(Collection<BoJobResult<T>> resultData) {
        return resultData.stream()
                .filter(u -> RxJobStatus.ERROR.equals(u.status))
                .map(u -> u.data + "\n\t\t")
                .collect(Collectors.toList());
    }
}
