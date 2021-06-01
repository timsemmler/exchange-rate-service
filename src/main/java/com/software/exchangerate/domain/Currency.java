package com.software.exchangerate.domain;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Currency {
    private String name;
    private double rate;
    private static Map<String, Integer> accessCounter = Collections.synchronizedMap(new HashMap<>());

    public Currency(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }

    public void incrementAccessCounter() {
        accessCounter.putIfAbsent(name, 0);
        accessCounter.merge(name, 1, Integer::sum);
    }

    public int getAccessCounter() {
        return accessCounter.getOrDefault(name, 0);
    }

    public double getRate() {
        return rate;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Double.compare(currency.rate, rate) == 0 && name.equals(currency.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rate);
    }
}
