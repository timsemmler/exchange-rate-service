package com.software.exchangerate.data;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.software.exchangerate.domain.Currency;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class ExchangeRateDataProviderImplTest {
    public static MockWebServer mockBackEnd;
    public ExchangeRateDataProvider exchangeRateDataProvider;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @BeforeEach
    void setUpBeforeEach() throws IOException {
        XmlMapper mapper = new XmlMapper();
        Resource resource = new ClassPathResource("testdata/ecbResponse.xml");
        String xml = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
        mockBackEnd.enqueue(new MockResponse()
                .setBody(xml)
                .addHeader("Content-Type", "text/xml"));
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        exchangeRateDataProvider = new ExchangeRateDataProviderImpl(baseUrl);

    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Should load all currencies delivered by the ecb and add EUR")
    void shouldLoadCurrenciesSuccessfully() {
        Map<String, Currency> currencies = exchangeRateDataProvider.loadCurrencies();
        Assertions.assertThat(currencies.values()).isNotEmpty().hasSize(6);
        Assertions.assertThat(currencies.keySet()).contains("EUR","USD","JPY","BGN","CZK","DKK");
        Assertions.assertThat(currencies.get("EUR")).isEqualTo(new Currency("EUR",1.0d));
        Assertions.assertThat(currencies.get("USD")).isEqualTo(new Currency("USD",1.2201d));
        Assertions.assertThat(currencies.get("JPY")).isEqualTo(new Currency("JPY",133.79d));
        Assertions.assertThat(currencies.get("BGN")).isEqualTo(new Currency("BGN",1.9558d));
        Assertions.assertThat(currencies.get("CZK")).isEqualTo(new Currency("CZK",25.454d));
        Assertions.assertThat(currencies.get("DKK")).isEqualTo(new Currency("DKK",7.4365d));
    }

}
