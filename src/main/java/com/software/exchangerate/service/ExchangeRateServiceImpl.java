package com.software.exchangerate.service;

import com.software.exchangerate.data.ExchangeRateDataProvider;
import com.software.exchangerate.domain.Currency;
import com.software.exchangerate.domain.ExchangeRate;
import com.software.exchangerate.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private ExchangeRateDataProvider dataProvider;

    public ExchangeRateServiceImpl(ExchangeRateDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public Set<Currency> loadAllCurrencies() {
        return new HashSet<>(dataProvider.loadCurrencies().values());
    }

    @Override
    public ExchangeRate loadExchangeRateAndIncreaseCounter(String from, String to) {
        Map<String, Currency> currencies = dataProvider.loadCurrencies();
        if (currencies.containsKey(from) && currencies.containsKey(to)) {
            ExchangeRate exchangeRate = new ExchangeRate(currencies.get(from), currencies.get(to));
            exchangeRate.getFrom().incrementAccessCounter();
            exchangeRate.getTo().incrementAccessCounter();
            return exchangeRate;
        } else {
            throw new ResourceNotFoundException(String.format("The exchange rate from %s to %s was not found", from, to));
        }
    }
}
