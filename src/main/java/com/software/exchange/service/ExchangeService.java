package com.software.exchange.service;

import com.software.exchange.domain.Currency;
import com.software.exchange.domain.Exchange;

import java.util.Set;

public interface ExchangeService {

    Set<Currency> loadAllCurrencies();

    Exchange exchangeCurrency(String from, String to, double amount);

}
