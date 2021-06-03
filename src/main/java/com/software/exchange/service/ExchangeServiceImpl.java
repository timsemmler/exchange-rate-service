package com.software.exchange.service;

import com.software.exchange.data.ExchangeDataProvider;
import com.software.exchange.domain.Currency;
import com.software.exchange.domain.Exchange;
import com.software.exchange.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    private ExchangeDataProvider dataProvider;

    public ExchangeServiceImpl(ExchangeDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public List<Currency> loadAllCurrencies() {
        return dataProvider.loadExchanges().values()
                .stream()
                .map(Exchange::getTo)
                .sorted(Comparator.comparing(Currency::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Exchange exchangeCurrency(String from, String to, double amount) {
        Exchange exchange = new Exchange(loadExchangeRateByName(from), loadExchangeRateByName(to));
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
