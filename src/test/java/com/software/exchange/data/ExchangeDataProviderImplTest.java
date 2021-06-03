package com.software.exchange.data;

import com.software.exchange.domain.Currency;
import com.software.exchange.domain.Exchange;
import com.software.exchange.exceptions.DataNotPresentException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ExchangeDataProviderImplTest {
    public static MockWebServer mockBackEnd;
    public ExchangeDataProvider exchangeDataProvider;

    @BeforeEach
    void setUpBeforeEach() throws IOException {
        if (mockBackEnd != null) {
            mockBackEnd.shutdown();
        }
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        exchangeDataProvider = new ExchangeDataProviderImpl(baseUrl);

    }

    @Test
    @DisplayName("Should load all exchangeRates delivered by the ecb and add EUR")
    void shouldLoadExchangeRatesSuccessfully() throws InterruptedException {
        mockResponseAndSetDate("testdata/ecbResponse.xml", LocalDate.now());
        Map<String, Exchange> exchangeRates = exchangeDataProvider.loadExchanges();
        Assertions.assertThat(exchangeRates.values()).isNotEmpty().hasSize(6);
        Assertions.assertThat(exchangeRates.keySet()).contains("EUR", "USD", "JPY", "BGN", "CZK", "DKK");
        Assertions.assertThat(exchangeRates.get("EUR").getTo()).isEqualTo(new Currency("EUR", 1.0d));
        Assertions.assertThat(exchangeRates.get("USD").getTo()).isEqualTo(new Currency("USD", 1.2201d));
        Assertions.assertThat(exchangeRates.get("JPY").getTo()).isEqualTo(new Currency("JPY", 133.79d));
        Assertions.assertThat(exchangeRates.get("BGN").getTo()).isEqualTo(new Currency("BGN", 1.9558d));
        Assertions.assertThat(exchangeRates.get("CZK").getTo()).isEqualTo(new Currency("CZK", 25.454d));
        Assertions.assertThat(exchangeRates.get("DKK").getTo()).isEqualTo(new Currency("DKK", 7.4365d));
        Assertions.assertThat(mockBackEnd.getRequestCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should use cache, when called a second time")
    void shouldLoadExchangeRatesFromCache() {
        mockResponseAndSetDate("testdata/ecbResponse.xml", LocalDate.now());
        exchangeDataProvider.loadExchanges();
        Map<String, Exchange> exchangeRates = exchangeDataProvider.loadExchanges();
        Assertions.assertThat(exchangeRates.values()).isNotEmpty().hasSize(6);
        Assertions.assertThat(exchangeRates.keySet()).contains("EUR", "USD", "JPY", "BGN", "CZK", "DKK");
        Assertions.assertThat(exchangeRates.get("EUR").getTo()).isEqualTo(new Currency("EUR", 1.0d));
        Assertions.assertThat(exchangeRates.get("USD").getTo()).isEqualTo(new Currency("USD", 1.2201d));
        Assertions.assertThat(exchangeRates.get("JPY").getTo()).isEqualTo(new Currency("JPY", 133.79d));
        Assertions.assertThat(exchangeRates.get("BGN").getTo()).isEqualTo(new Currency("BGN", 1.9558d));
        Assertions.assertThat(exchangeRates.get("CZK").getTo()).isEqualTo(new Currency("CZK", 25.454d));
        Assertions.assertThat(exchangeRates.get("DKK").getTo()).isEqualTo(new Currency("DKK", 7.4365d));
        Assertions.assertThat(mockBackEnd.getRequestCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should ignore currency, when currency or rate in xml is Empty")
    void shouldLoadExchangeRatesSuccessfullyIgnoreMalformedexchangeRates() {
        mockResponseAndSetDate("testdata/ecbResponseMalformed.xml", LocalDate.now());
        Map<String, Exchange> exchangeRates = exchangeDataProvider.loadExchanges();
        Assertions.assertThat(exchangeRates.values()).isNotEmpty().hasSize(4);
        Assertions.assertThat(exchangeRates.keySet()).contains("EUR", "USD", "CZK", "DKK");
        Assertions.assertThat(exchangeRates.get("EUR").getTo()).isEqualTo(new Currency("EUR", 1.0d));
        Assertions.assertThat(exchangeRates.get("USD").getTo()).isEqualTo(new Currency("USD", 1.2201d));
        Assertions.assertThat(exchangeRates.get("CZK").getTo()).isEqualTo(new Currency("CZK", 25.454d));
        Assertions.assertThat(exchangeRates.get("DKK").getTo()).isEqualTo(new Currency("DKK", 7.4365d));
        Assertions.assertThat(mockBackEnd.getRequestCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should load newest entry from cache, when response body is empty")
    void shouldLoadExchangeRatesFromCacheWhenResponseIsEmpty() {
        mockResponseAndSetDate("testdata/ecbResponse.xml", LocalDate.now().minusDays(1));
        mockEmptyResponse();
        exchangeDataProvider.loadExchanges();
        Map<String, Exchange> exchangeRates = exchangeDataProvider.loadExchanges();
        Assertions.assertThat(exchangeRates.values()).isNotEmpty().hasSize(6);
        Assertions.assertThat(exchangeRates.keySet()).contains("EUR", "USD", "JPY", "BGN", "CZK", "DKK");
        Assertions.assertThat(exchangeRates.get("EUR").getTo()).isEqualTo(new Currency("EUR", 1.0d));
        Assertions.assertThat(exchangeRates.get("USD").getTo()).isEqualTo(new Currency("USD", 1.2201d));
        Assertions.assertThat(exchangeRates.get("JPY").getTo()).isEqualTo(new Currency("JPY", 133.79d));
        Assertions.assertThat(exchangeRates.get("BGN").getTo()).isEqualTo(new Currency("BGN", 1.9558d));
        Assertions.assertThat(exchangeRates.get("CZK").getTo()).isEqualTo(new Currency("CZK", 25.454d));
        Assertions.assertThat(exchangeRates.get("DKK").getTo()).isEqualTo(new Currency("DKK", 7.4365d));
        Assertions.assertThat(mockBackEnd.getRequestCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should throw DataNotPresentException, when data could not be loaded")
    void shouldLoadExchangeRatesUnsuccessfullyAndThrowException() {
        mockEmptyResponse();
        Assertions.assertThatThrownBy(() -> exchangeDataProvider.loadExchanges()).isInstanceOf(DataNotPresentException.class);
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
