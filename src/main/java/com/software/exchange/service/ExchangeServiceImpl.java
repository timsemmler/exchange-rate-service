package com.software.exchange.service;

import com.software.exchange.data.ExchangeDataProvider;
import com.software.exchange.domain.Currency;
import com.software.exchange.domain.Exchange;
import com.software.exchange.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    private ExchangeDataProvider dataProvider;

    public ExchangeServiceImpl(ExchangeDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public Set<Currency> loadAllCurrencies() {
        Map<String, Exchange> exchangeRates = dataProvider.loadExchanges();
        return exchangeRates.values().stream().map(Exchange::getTo).collect(Collectors.toSet());
    }

    @Override
    public Exchange exchangeCurrency(String from, String to, double amount) {
        Currency fromCurrency = loadExchangeRateByName(from);
        Currency toCurrency = loadExchangeRateByName(to);
        Exchange exchange = new Exchange(fromCurrency, toCurrency);
        exchange.normalize();
        exchange.multiplyBy(amount);
        exchange.getFrom().incrementAccessCounter();
        exchange.getTo().incrementAccessCounter();
        return exchange;
    }

    private Currency loadExchangeRateByName(String currencyName) {
        Map<String, Exchange> exchangeRates = dataProvider.loadExchanges();
        if (exchangeRates.containsKey(currencyName)) {
            return exchangeRates.get(currencyName).getTo();
        } else {
            throw new ResourceNotFoundException(String.format("The currency with name %s is currently not supported", currencyName));
        }
    }
}
