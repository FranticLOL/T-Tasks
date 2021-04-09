package ru.demo.metrics.starter.metrics;

import java.time.LocalDateTime;

public class MethodInvocationMetric {

    /**
     * Наименование метода, он же уникальный идентификатор
     * <p>
     * Прим: org.demo.repository.OrderRepository.getById
     */
    private String method;
    /**
     * Время вызова метода
     */
    private LocalDateTime invocationTime;
    /**
     * Время работы метода
     */
    private Long totalTime;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LocalDateTime getInvocationTime() {
        return invocationTime;
    }

    public void setInvocationTime(LocalDateTime invocationTime) {
        this.invocationTime = invocationTime;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }
}
