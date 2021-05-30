package com.software.exchangerateservice.data;

public interface ExchangeRateProvider {
    EcbExchangeRateData getExchangeRate(String fromCurrency, String toCurrency);
}
