package com.software.exchange.service;


import com.software.exchange.data.ExchangeDataProvider;
import com.software.exchange.domain.Currency;
import com.software.exchange.domain.Exchange;
import com.software.exchange.exceptions.ResourceNotFoundException;
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
public class ExchangeServiceTest {
    @Mock
    ExchangeDataProvider dataProvider;
    @InjectMocks
    ExchangeServiceImpl exchangeRateService;

    @Test
    @DisplayName("Should load all supported currencies sucessfully")
    void shouldLoadAllSupportedCurrenciesSuccessfully() {
        Map<String, Exchange> currencies = new HashMap<>();
        currencies.put("EUR", Exchange.createEURTo("EUR", 1d));
        currencies.put("USD", Exchange.createEURTo("USD", 1.2d));
        currencies.put("DKK", Exchange.createEURTo("DKK", 7.5d));
        Mockito.when(dataProvider.loadExchanges()).thenReturn(currencies);
        Assertions.assertThat(exchangeRateService.loadAllCurrencies()).contains(
                new Currency("EUR", 1d),
                new Currency("USD", 1.2d),
                new Currency("DKK", 7.5d)
        );
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when one of the currencies not found")
    void shouldThrowResourceNotFoundException() {
        Map<String, Exchange> exchangeRates = new HashMap<>();
        exchangeRates.put("EUR", Exchange.createEURTo("EUR", 1d));
        exchangeRates.put("USD", Exchange.createEURTo("USD", 1.2d));
        Mockito.when(dataProvider.loadExchanges()).thenReturn(exchangeRates);

        Assertions
                .assertThatThrownBy(() -> exchangeRateService.exchangeCurrency("EUR", "DKK", 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should increment the access counter when the currencies are loaded")
    void shouldIncrementSuccessfully() {
        Map<String, Exchange> exchangeRates = new HashMap<>();
        exchangeRates.put("EUR", Exchange.createEURTo("EUR", 1d));
        exchangeRates.put("USD", Exchange.createEURTo("USD", 1.2d));
        exchangeRates.put("DKK", Exchange.createEURTo("DKK", 7.5d));
        Mockito.when(dataProvider.loadExchanges()).thenReturn(exchangeRates);
        Currency.clearAccessCounter();
        exchangeRateService.exchangeCurrency("EUR", "DKK", 1.0d);
        exchangeRateService.exchangeCurrency("DKK", "USD", 1.0d);
        Assertions.assertThat(exchangeRates.get("EUR").getTo().getAccessCounter()).isEqualTo(1);
        Assertions.assertThat(exchangeRates.get("USD").getTo().getAccessCounter()).isEqualTo(1);
        Assertions.assertThat(exchangeRates.get("DKK").getTo().getAccessCounter()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should load exchange with ratio of 1:2 and 1:0.5 sucessfully")
    void shouldExchangeCurrencyNormalizedSuccessfully() {
        Map<String, Exchange> exchangeRates = new HashMap<>();
        exchangeRates.put("EUR", Exchange.createEURTo("EUR", 1d));
        exchangeRates.put("USD", Exchange.createEURTo("USD", 4d));
        exchangeRates.put("DKK", Exchange.createEURTo("DKK", 8d));

        Mockito.when(dataProvider.loadExchanges()).thenReturn(exchangeRates);

        Exchange exchange = exchangeRateService.exchangeCurrency("USD", "DKK", 1.0d);
        Assertions.assertThat(exchange).isNotNull();
        Assertions.assertThat(exchange.getFrom().getName()).isEqualTo("USD");
        Assertions.assertThat(exchange.getTo().getName()).isEqualTo("DKK");
        Assertions.assertThat(exchange.getTo().getAmount()).isEqualTo(2.0d);
        Assertions.assertThat(exchange.getChart()).isEqualTo("https://www.xe.com/currencycharts/?from=USD&to=DKK");

        Exchange exchangeRateReversed = exchangeRateService.exchangeCurrency("DKK", "USD", 1.0d);
        Assertions.assertThat(exchangeRateReversed).isNotNull();
        Assertions.assertThat(exchangeRateReversed.getFrom().getName()).isEqualTo("DKK");
        Assertions.assertThat(exchangeRateReversed.getTo().getName()).isEqualTo("USD");
        Assertions.assertThat(exchangeRateReversed.getTo().getAmount()).isEqualTo(0.5d);
        Assertions.assertThat(exchangeRateReversed.getChart()).isEqualTo("https://www.xe.com/currencycharts/?from=DKK&to=USD");
    }

    @Test
    @DisplayName("Should exchange currency with ratio of 10:20 and 1:0.5")
    void shouldExchangeCurrencySuccessfully() {
        Map<String, Exchange> exchangeRates = new HashMap<>();
        exchangeRates.put("USD", Exchange.createEURTo("USD", 4d));
        exchangeRates.put("DKK", Exchange.createEURTo("DKK", 8d));

        Mockito.when(dataProvider.loadExchanges()).thenReturn(exchangeRates);

        Exchange exchange = exchangeRateService.exchangeCurrency("USD", "DKK", 2.5d);
        Assertions.assertThat(exchange).isNotNull();
        Assertions.assertThat(exchange.getFrom().getName()).isEqualTo("USD");
        Assertions.assertThat(exchange.getTo().getName()).isEqualTo("DKK");
        Assertions.assertThat(exchange.getTo().getAmount()).isEqualTo(5d);
        Assertions.assertThat(exchange.getChart()).isEqualTo("https://www.xe.com/currencycharts/?from=USD&to=DKK");
    }


}
