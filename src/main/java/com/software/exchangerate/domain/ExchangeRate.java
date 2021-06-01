package com.software.exchangerate.domain;

import com.fasterxml.jackson.annotation.JsonView;

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

    @JsonView(Views.ExchangeRate.class)
    public Currency getFrom() {
        return from;
    }

    @JsonView(Views.ExchangeRate.class)
    public Currency getTo() {
        return to;
    }

    @JsonView(Views.ExchangeRate.class)
    public String getChart() {
        return chart;
    }
    @JsonView(Views.ExchangeRate.class)
    public double getExchangeRate() {
        return to.getRate() / from.getRate();
    }
}
