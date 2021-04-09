package ru.demo.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.demo.metrics.starter.metrics.MetricStatProvider;
import ru.demo.metrics.starter.metrics.Timed;

@Service
@Timed
public class SomeServiceImpl implements SomeService {

    public void method() throws InterruptedException {
        System.out.println("Phase 1");
        Thread.sleep(1000);
        System.out.println("Phase 2");
    }

    public void method2() {
        System.out.println("Method 2");
    }
}
