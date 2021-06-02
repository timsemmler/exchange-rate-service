package com.software.exchange.domain;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Currency {

    private String name;
    private BigDecimal amount;
    private static Map<String, Integer> accessCounter = Collections.synchronizedMap(new HashMap<>());

    public Currency(String name, double amount) {
        this.name = name;
        this.amount = BigDecimal.valueOf(amount);
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount.doubleValue();
    }

    public Currency multiplyBy(double factor){
        return new Currency(name,amount.multiply(BigDecimal.valueOf(factor)).setScale(8, RoundingMode.HALF_UP).doubleValue());
    }

    public Currency divideBy(double divisor){
        return new Currency(name,amount.divide(BigDecimal.valueOf(divisor), 8, RoundingMode.HALF_UP).doubleValue());
    }

    public int getAccessCounter() {
        return accessCounter.getOrDefault(name, 0);
    }

    public void incrementAccessCounter() {
        accessCounter.putIfAbsent(name, 0);
        accessCounter.merge(name, 1, Integer::sum);
    }


    //We currently only need this for testing purposes.
    public static void clearAccessCounter() {
        accessCounter.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return name.equals(currency.name) && amount.equals(currency.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount);
    }
}



