package com.software.exchangerate.data;

import com.software.exchangerate.domain.Currency;

import java.util.Map;

public interface ExchangeRateDataProvider {
    Map<String, Currency> loadCurrencies();
}
