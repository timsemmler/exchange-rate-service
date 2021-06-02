package com.software.exchange.data;

import com.software.exchange.domain.Exchange;

import java.util.Map;

public interface ExchangeDataProvider {
    Map<String, Exchange> loadExchanges();
}
