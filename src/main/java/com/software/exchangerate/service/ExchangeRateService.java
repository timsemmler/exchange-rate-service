package com.software.exchangerate.service;

import com.software.exchangerate.domain.Currency;
import com.software.exchangerate.domain.ExchangeRate;

import java.util.Set;

public interface ExchangeRateService {

    Set<Currency> loadAllCurrencies();

    ExchangeRate loadExchangeRateAndIncreaseCounter(String from, String to);

}
