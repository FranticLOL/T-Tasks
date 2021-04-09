package ru.demo.metrics.starter.autoconfigure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import ru.demo.metrics.starter.metrics.MetricStatProvider;
import ru.demo.metrics.starter.metrics.MetricStatProviderImpl;
import ru.demo.metrics.starter.metrics.TimedAnnotationBeanPostProcessor;

@ComponentScan("ru.demo.metrics.starter")
@ConditionalOnProperty(value = "metrics.enabled", havingValue = "true", matchIfMissing = true)
public class MetricsAutoConfiguration {
    @Bean
    public TimedAnnotationBeanPostProcessor timedAnnotationBeanPostProcessor() {
        return new TimedAnnotationBeanPostProcessor();
    }

    @Qualifier("metricStatProviderImpl")
    @Bean
    public MetricStatProvider metricStatProvider() {
        return new MetricStatProviderImpl();
    }
}
