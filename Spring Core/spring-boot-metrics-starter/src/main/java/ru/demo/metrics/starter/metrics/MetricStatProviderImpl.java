package ru.demo.metrics.starter.metrics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MetricStatProviderImpl implements MetricStatProvider {
    @Value("${metrics.limit:10000}")
    private Long metricsLimit;

    Map<String, MethodMetricStat> map = new ConcurrentHashMap<>();

    @Override
    public List<MethodMetricStat> getTotalStat() {
        if(map.values().isEmpty()) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(map.values());
        }
    }

    @Override
    public List<MethodMetricStat> getTotalStatForPeriod(LocalDateTime from, LocalDateTime to) {
        List<MethodMetricStat> datedMethodMetricStatList = new ArrayList<>();
        for(MethodMetricStat metric : map.values()) {
            datedMethodMetricStatList.add(new MethodMetricStat(metric.getMetricsBetweenDate(from, to)));
        }
        return datedMethodMetricStatList;
    }

    @Override
    public MethodMetricStat getTotalStatByMethod(String method) {
        return map.get(method);
    }

    @Override
    public MethodMetricStat getTotalStatByMethodForPeriod(String method, LocalDateTime from, LocalDateTime to) {
        MethodMetricStat fullMethodMetricStat = map.get(method);
        return new MethodMetricStat(fullMethodMetricStat.getMetricsBetweenDate(from, to));
    }

    public void addMethodMetricStat(String methodName, MethodMetricStat methodMetricStat) {
        map.put(methodName, methodMetricStat);
    }

    public Long getMetricsLimit(){
        return metricsLimit;
    }
}
