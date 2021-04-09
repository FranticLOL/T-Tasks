package ru.demo.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.demo.metrics.starter.metrics.MethodMetricStat;
import ru.demo.metrics.starter.metrics.MetricStatProvider;

import java.util.List;

@Component
public class TestApplication implements CommandLineRunner {
    private SomeService someService;
    MetricStatProvider metricStatProvider;

    @Autowired
    public void setMetricStatProvider(MetricStatProvider metricStatProvider) {
        this.metricStatProvider = metricStatProvider;
    }

    @Autowired
    public TestApplication(SomeService someService) {
        this.someService = someService;
    }

    @Override
    public void run(String... args) throws Exception {
        someService.method2();
        someService.method();
        List<MethodMetricStat> objects = metricStatProvider.getTotalStat();
        System.out.println(metricStatProvider.getTotalStat().size());
    }

}
