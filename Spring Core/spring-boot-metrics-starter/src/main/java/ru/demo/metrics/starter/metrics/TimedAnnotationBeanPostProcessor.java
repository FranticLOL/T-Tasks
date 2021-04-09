package ru.demo.metrics.starter.metrics;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimedAnnotationBeanPostProcessor implements BeanPostProcessor {
    MetricStatProvider metricStatProvider;

    Map<String, Class> map = new ConcurrentHashMap<>();

    @Autowired
    public void setMetricStatProvider(MetricStatProvider metricStatProvider) {
        this.metricStatProvider = metricStatProvider;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(Timed.class) ||
                Arrays.stream(beanClass.getMethods()).anyMatch(method -> method.isAnnotationPresent(Timed.class))) {
            map.put(beanName, beanClass);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = map.get(beanName);
        if (beanClass != null) {
            return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), (proxy, method, objects) -> {
                Method beanMethod = getBeanMethod(beanClass, method);
                if (beanMethod != null) {
                    String methodFullName = beanClass.getCanonicalName() + "." + beanMethod.getName();
                    MethodInvocationMetric methodInvocationMetric = new MethodInvocationMetric();
                    methodInvocationMetric.setMethod(methodFullName);
                    methodInvocationMetric.setInvocationTime(LocalDateTime.now());
                    System.out.println("Profiling");
                    long before = System.nanoTime();
                    Object retVal = method.invoke(bean, objects);
                    long after = System.nanoTime();
                    methodInvocationMetric.setTotalTime(after - before);

                    MethodMetricStat methodMetricStat = metricStatProvider.getTotalStatByMethod(methodFullName);
                    Long metricsLimit = metricStatProvider.getMetricsLimit();
                    if (methodMetricStat == null) {
                        methodMetricStat = new MethodMetricStat(methodInvocationMetric);
                        metricStatProvider.addMethodMetricStat(methodFullName, methodMetricStat);
                    } else {
                        methodMetricStat.addMethodMetric(methodInvocationMetric, metricsLimit);
                    }

                    return retVal;
                } else {
                    return method.invoke(bean, objects);
                }
            });
        }
        return bean;
    }

    private Method getBeanMethod(Class beanClass, Method method) {
        if (beanClass.isAnnotationPresent(Timed.class)) {
            return Arrays.stream(beanClass.getMethods())
                    .filter(beanMethod -> beanMethod.getName().equals(method.getName()))
                    .findFirst()
                    .orElse(null);
        } else {
            return Arrays.stream(beanClass.getMethods())
                    .filter(beanMethod -> beanMethod.isAnnotationPresent(Timed.class)
                            && beanMethod.getName().equals(method.getName()))
                    .findFirst()
                    .orElse(null);
        }
    }
}
