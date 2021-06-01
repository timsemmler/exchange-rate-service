package com.software.exchangerate.data;

import com.software.exchangerate.domain.Currency;
import com.software.exchangerate.exceptions.DataNotPresentException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ExchangeRateDataProviderImplTest {
    public static MockWebServer mockBackEnd;
    public ExchangeRateDataProvider exchangeRateDataProvider;

    @BeforeEach
    void setUpBeforeEach() throws IOException {
        if (mockBackEnd != null) {
            mockBackEnd.shutdown();
        }
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        exchangeRateDataProvider = new ExchangeRateDataProviderImpl(baseUrl);

    }

    @Test
    @DisplayName("Should load all currencies delivered by the ecb and add EUR")
    void shouldLoadCurrenciesSuccessfully() throws InterruptedException {
        mockResponseAndSetDate("testdata/ecbResponse.xml", LocalDate.now());
        Map<String, Currency> currencies = exchangeRateDataProvider.loadCurrencies();
        Assertions.assertThat(currencies.values()).isNotEmpty().hasSize(6);
        Assertions.assertThat(currencies.keySet()).contains("EUR", "USD", "JPY", "BGN", "CZK", "DKK");
        Assertions.assertThat(currencies.get("EUR")).isEqualTo(new Currency("EUR", 1.0d));
        Assertions.assertThat(currencies.get("USD")).isEqualTo(new Currency("USD", 1.2201d));
        Assertions.assertThat(currencies.get("JPY")).isEqualTo(new Currency("JPY", 133.79d));
        Assertions.assertThat(currencies.get("BGN")).isEqualTo(new Currency("BGN", 1.9558d));
        Assertions.assertThat(currencies.get("CZK")).isEqualTo(new Currency("CZK", 25.454d));
        Assertions.assertThat(currencies.get("DKK")).isEqualTo(new Currency("DKK", 7.4365d));
        Assertions.assertThat(mockBackEnd.getRequestCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should use cache, when called a second time")
    void shouldLoadCurrenciesFromCache() {
        mockResponseAndSetDate("testdata/ecbResponse.xml", LocalDate.now());
        exchangeRateDataProvider.loadCurrencies();
        Map<String, Currency> currencies = exchangeRateDataProvider.loadCurrencies();
        Assertions.assertThat(currencies.values()).isNotEmpty().hasSize(6);
        Assertions.assertThat(currencies.keySet()).contains("EUR", "USD", "JPY", "BGN", "CZK", "DKK");
        Assertions.assertThat(currencies.get("EUR")).isEqualTo(new Currency("EUR", 1.0d));
        Assertions.assertThat(currencies.get("USD")).isEqualTo(new Currency("USD", 1.2201d));
        Assertions.assertThat(currencies.get("JPY")).isEqualTo(new Currency("JPY", 133.79d));
        Assertions.assertThat(currencies.get("BGN")).isEqualTo(new Currency("BGN", 1.9558d));
        Assertions.assertThat(currencies.get("CZK")).isEqualTo(new Currency("CZK", 25.454d));
        Assertions.assertThat(currencies.get("DKK")).isEqualTo(new Currency("DKK", 7.4365d));
        Assertions.assertThat(mockBackEnd.getRequestCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should ignore currency, when currency or rate in xml is Empty")
    void shouldLoadCurrenciesSuccessfullyIgnoreMalformedCurrencies() {
        mockResponseAndSetDate("testdata/ecbResponseMalformed.xml", LocalDate.now());
        Map<String, Currency> currencies = exchangeRateDataProvider.loadCurrencies();
        Assertions.assertThat(currencies.values()).isNotEmpty().hasSize(4);
        Assertions.assertThat(currencies.keySet()).contains("EUR", "USD", "CZK", "DKK");
        Assertions.assertThat(currencies.get("EUR")).isEqualTo(new Currency("EUR", 1.0d));
        Assertions.assertThat(currencies.get("USD")).isEqualTo(new Currency("USD", 1.2201d));
        Assertions.assertThat(currencies.get("CZK")).isEqualTo(new Currency("CZK", 25.454d));
        Assertions.assertThat(currencies.get("DKK")).isEqualTo(new Currency("DKK", 7.4365d));
        Assertions.assertThat(mockBackEnd.getRequestCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should load newest entry from cache, when response body is empty")
    void shouldLoadCurrenciesFromCacheWhenResponseIsEmpty() {
        mockResponseAndSetDate("testdata/ecbResponse.xml", LocalDate.now().minusDays(1));
        mockEmptyResponse();
        exchangeRateDataProvider.loadCurrencies();
        Map<String, Currency> currencies = exchangeRateDataProvider.loadCurrencies();
        Assertions.assertThat(currencies.values()).isNotEmpty().hasSize(6);
        Assertions.assertThat(currencies.keySet()).contains("EUR", "USD", "JPY", "BGN", "CZK", "DKK");
        Assertions.assertThat(currencies.get("EUR")).isEqualTo(new Currency("EUR", 1.0d));
        Assertions.assertThat(currencies.get("USD")).isEqualTo(new Currency("USD", 1.2201d));
        Assertions.assertThat(currencies.get("JPY")).isEqualTo(new Currency("JPY", 133.79d));
        Assertions.assertThat(currencies.get("BGN")).isEqualTo(new Currency("BGN", 1.9558d));
        Assertions.assertThat(currencies.get("CZK")).isEqualTo(new Currency("CZK", 25.454d));
        Assertions.assertThat(currencies.get("DKK")).isEqualTo(new Currency("DKK", 7.4365d));
        Assertions.assertThat(mockBackEnd.getRequestCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should throw DataNotPresentException, when data could not be loaded")
    void shouldLoadCurrenciesUnsuccessfullyAndThrowException() {
        mockEmptyResponse();
        Assertions.assertThatThrownBy(() -> exchangeRateDataProvider.loadCurrencies()).isInstanceOf(DataNotPresentException.class);
    }

    private void mockResponseAndSetDate(String resourceString, LocalDate date) {
        try {
            Resource resource = new ClassPathResource(resourceString);
            String xml = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            xml = xml.replace("'[DATE]'", String.format("'%s'", date.format(DateTimeFormatter.ISO_DATE)));
            mockBackEnd.enqueue(new MockResponse()
                    .setBody(xml)
                    .addHeader("Content-Type", "text/xml"));
        } catch (IOException e) {
            throw new RuntimeException("Error when converting testdata/ecbResponse.xml to String.");
        }
    }

    private void mockEmptyResponse() {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("")
                .addHeader("Content-Type", "text/xml"));
    }
}
