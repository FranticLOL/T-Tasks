package ru.demo.metrics.starter.metrics;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MethodMetricStat {
    /**
     * Наименование/идентификатор метода
     */
    private String methodName;
    /**
     * Кол-во вызовов метода
     */
    private Integer invocationsCount;
    /**
     * Минимальное время работы метода
     */
    private Long minTime;
    /**
     * Среднее время работы метода
     */
    private Long averageTime;
    /**
     * максимальное время работы метода
     */
    private Long maxTime;

    private ConcurrentLinkedQueue<MethodInvocationMetric> queue;

    MethodMetricStat(MethodInvocationMetric methodInvocationMetric) {
        methodName = methodInvocationMetric.getMethod();
        invocationsCount = 1;
        minTime = methodInvocationMetric.getTotalTime();
        averageTime = methodInvocationMetric.getTotalTime();
        maxTime = methodInvocationMetric.getTotalTime();
        queue = new ConcurrentLinkedQueue<>(Collections.singleton(methodInvocationMetric));
    }

    MethodMetricStat(List<MethodInvocationMetric> metrics) {
        methodName = metrics.get(0).getMethod();
        invocationsCount = metrics.size();
        minTime = metrics.stream().mapToLong(MethodInvocationMetric::getTotalTime).min().getAsLong();
        maxTime = metrics.stream().mapToLong(MethodInvocationMetric::getTotalTime).max().getAsLong();
        averageTime = metrics.stream().mapToLong(MethodInvocationMetric::getTotalTime).sum() / invocationsCount;
        queue = new ConcurrentLinkedQueue<>(metrics);
    }

    public void addMethodMetric(MethodInvocationMetric methodInvocationMetric, Long queueMaxSize) {
        if (queue.size() < queueMaxSize) {
            queue.add(methodInvocationMetric);
            invocationsCount++;
            minTime = getNewMinTime(methodInvocationMetric);
            maxTime = getNewMaxTime(methodInvocationMetric);
            averageTime = getNewAverageTime();
        } else {
            queue.poll();
            queue.add(methodInvocationMetric);
            minTime = getNewMinTime(methodInvocationMetric);
            maxTime = getNewMaxTime(methodInvocationMetric);
            averageTime = getNewAverageTime();
        }
    }

    public List<MethodInvocationMetric> getMetricsBetweenDate(LocalDateTime from, LocalDateTime to) {
        List<MethodInvocationMetric> list = new ArrayList<>();
        for(MethodInvocationMetric metric : queue) {
            if(metric.getInvocationTime().isBefore(to) && metric.getInvocationTime().isAfter(from)) {
                list.add(metric);
            }
        }
        return list;
    }

    private Long getNewMinTime(MethodInvocationMetric methodInvocationMetric) {
        if(minTime > methodInvocationMetric.getTotalTime()) {
            return methodInvocationMetric.getTotalTime();
        } else {
            return minTime;
        }
    }

    private Long getNewMaxTime(MethodInvocationMetric methodInvocationMetric) {
        if(maxTime < methodInvocationMetric.getTotalTime()) {
            return methodInvocationMetric.getTotalTime();
        } else {
            return maxTime;
        }
    }

    private Long getNewAverageTime() {
        Long time = 0L;
        for(MethodInvocationMetric metric : queue) {
            time += metric.getTotalTime();
        }
        return time / invocationsCount;
    }

    public String getMethodName() {
        return methodName;
    }

    public Integer getInvocationsCount() {
        return invocationsCount;
    }

    public Long getMinTime() {
        return minTime;
    }

    public Long getAverageTime() {
        return averageTime;
    }

    public Long getMaxTime() {
        return maxTime;
    }
}
