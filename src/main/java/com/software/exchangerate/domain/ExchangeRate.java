package com.software.exchangerate.domain;

import java.net.URL;

public class ExchangeRate {
    private Currency from;
    private Currency to;
    private String chart;

    public ExchangeRate(Currency from, Currency to) {
        this.from = from;
        this.to = to;
        this.chart = String.format("https://www.xe.com/currencycharts/?from=%s&to=%s", from.getName(),to.getName());

    }

    public Currency getFrom() {
        return from;
    }

    public Currency getTo() {
        return to;
    }

    public String getChart() {
        return chart;
    }
    public double getExchangeRate() {
        return to.getRate() / from.getRate();
    }
}
