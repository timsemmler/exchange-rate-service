package com.software.exchangerateservice.domain;

public class ExchangeRate {
    private Currency fromCurrency;
    private Currency toCurrency;
    private Double exchangeRate;

    public ExchangeRate(String fromCurrency, String toCurrency, Double exchangeRate) {
        this.fromCurrency = new Currency(fromCurrency);
        this.toCurrency = new Currency(toCurrency);
        this.exchangeRate = exchangeRate;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }
}
