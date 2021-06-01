package com.software.exchangerate.service;


import com.software.exchangerate.data.ExchangeRateDataProvider;
import com.software.exchangerate.domain.Currency;
import com.software.exchangerate.domain.ExchangeRate;
import com.software.exchangerate.exceptions.DataNotPresentException;
import com.software.exchangerate.exceptions.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {
    @Mock
    ExchangeRateDataProvider dataProvider;
    @InjectMocks
    ExchangeRateServiceImpl exchangeRateService;

    @Test
    @DisplayName("Should load all supported currencies sucessfully")
    void shouldLoadAllSupportedCurrenciesSuccessfully() {
        Map<String, Currency> currencies = new HashMap<>();
        currencies.put("EUR", new Currency("EUR", 1d));
        currencies.put("USD", new Currency("USD", 1.2d));
        currencies.put("DKK", new Currency("USD", 1.2d));
        Mockito.when(dataProvider.loadCurrencies()).thenReturn(currencies);
        Assertions.assertThat(exchangeRateService.loadAllCurrencies()).contains(
                new Currency("EUR", 1d),
                new Currency("USD", 1.2d),
                new Currency("USD", 1.2d)
        );
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when one of the currencies not found")
    void shouldThrowResourceNotFoundException() {
        Map<String, Currency> currencies = new HashMap<>();
        currencies.put("EUR", new Currency("EUR", 1d));
        currencies.put("USD", new Currency("USD", 1.2d));
        Mockito.when(dataProvider.loadCurrencies()).thenReturn(currencies);

        Assertions
                .assertThatThrownBy(() -> exchangeRateService.loadExchangeRateAndIncreaseCounter("EUR", "DKK"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should increment the access counter when the currencies are loaded")
    void shouldIncrementSuccessfully() {
        Map<String, Currency> currencies = new HashMap<>();
        currencies.put("EUR", new Currency("EUR", 1d));
        currencies.put("USD", new Currency("USD", 1.2d));
        currencies.put("DKK", new Currency("DKK", 7.5d));

        Mockito.when(dataProvider.loadCurrencies()).thenReturn(currencies);
        exchangeRateService.loadExchangeRateAndIncreaseCounter("EUR", "DKK");
        exchangeRateService.loadExchangeRateAndIncreaseCounter("DKK", "USD");
        Assertions.assertThat(currencies.get("EUR").getAccessCounter()).isEqualTo(1);
        Assertions.assertThat(currencies.get("USD").getAccessCounter()).isEqualTo(1);
        Assertions.assertThat(currencies.get("DKK").getAccessCounter()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should load exchange rate sucessfully")
    void shouldLoadExchangeRateSuccessfully() {
        Map<String, Currency> currencies = new HashMap<>();
        currencies.put("EUR", new Currency("EUR", 1d));
        currencies.put("USD", new Currency("USD", 4d));
        currencies.put("DKK", new Currency("DKK", 8d));

        Mockito.when(dataProvider.loadCurrencies()).thenReturn(currencies);

        ExchangeRate exchangeRate = exchangeRateService.loadExchangeRateAndIncreaseCounter("USD", "DKK");
        Assertions.assertThat(exchangeRate).isNotNull();
        Assertions.assertThat(exchangeRate.getFrom().getName()).isEqualTo("USD");
        Assertions.assertThat(exchangeRate.getTo().getName()).isEqualTo("DKK");
        Assertions.assertThat(exchangeRate.getExchangeRate()).isEqualTo(2d);
        Assertions.assertThat(exchangeRate.getChart()).isEqualTo("https://www.xe.com/currencycharts/?from=USD&to=DKK");

        ExchangeRate exchangeRateReversed = exchangeRateService.loadExchangeRateAndIncreaseCounter("DKK", "USD");
        Assertions.assertThat(exchangeRateReversed).isNotNull();
        Assertions.assertThat(exchangeRateReversed.getFrom().getName()).isEqualTo("DKK");
        Assertions.assertThat(exchangeRateReversed.getTo().getName()).isEqualTo("USD");
        Assertions.assertThat(exchangeRateReversed.getExchangeRate()).isEqualTo(0.5d);
        Assertions.assertThat(exchangeRateReversed.getChart()).isEqualTo("https://www.xe.com/currencycharts/?from=DKK&to=USD");
    }


}
