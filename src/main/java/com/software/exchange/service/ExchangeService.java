package com.software.exchange.service;

import com.software.exchange.domain.Currency;
import com.software.exchange.domain.Exchange;

import java.util.List;

public interface ExchangeService {

    List<Currency> loadAllCurrencies();

    Exchange exchangeCurrency(String from, String to, double amount);

}
