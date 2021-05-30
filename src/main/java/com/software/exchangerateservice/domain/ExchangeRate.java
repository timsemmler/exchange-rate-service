package com.software.exchangerateservice.domain;

public class ExchangeRate {
    private Currency fromCurrency = new Currency("EUR", "Euro");
    private Currency toCurrency = new Currency("USD", "US-Dollar");;
    private Double exchangeRate = 1.1d;

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
