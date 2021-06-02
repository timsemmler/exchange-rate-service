package com.software.exchange.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.software.exchange.domain.Exchange;
import com.software.exchange.exceptions.DataNotPresentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class ExchangeDataProviderImpl implements ExchangeDataProvider {

    private Map<LocalDate, Map<String, Exchange>> dailyCurrencyValues;
    private String ecbExchangesRatesUrl;

    public ExchangeDataProviderImpl(@Value("${com.software.exchangerates.ecb.url}") String ecbExchangesRatesUrl) {
        this.ecbExchangesRatesUrl = ecbExchangesRatesUrl;
        // Make our dailyCurrencyValues thread-safe
        dailyCurrencyValues = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public Map<String, Exchange> loadExchanges() {
        Map<String, Exchange> exchangeRates;
        LocalDate now = LocalDate.now();
        if (!dailyCurrencyValues.containsKey(now)) {
            EcbExchangeData exchangeRateData = convertXmlToExchangeData(loadDataFromEcb().block());
            if (exchangeRateData == null) {
                Optional<LocalDate> newestEntryDate = dailyCurrencyValues.keySet().stream().max(LocalDate::compareTo);
                if (newestEntryDate.isEmpty()) {
                    throw new DataNotPresentException("Currency Data couldn't be loaded");
                }
                return dailyCurrencyValues.get(newestEntryDate.get());
            }
            exchangeRates = createAllCurrenciesFrom(exchangeRateData);
            dailyCurrencyValues.putIfAbsent(exchangeRateData.getDate(), exchangeRates);
        } else {
            exchangeRates = dailyCurrencyValues.get(now);
        }
        return exchangeRates;
    }

    private Map<String, Exchange> createAllCurrenciesFrom(EcbExchangeData ecbExchangeData) {
        Map<String, Exchange> allExchangeRates = new HashMap<>();
        //We need to add EUR because EUR is never present in the ecbCurrencyDataList
        allExchangeRates.put("EUR", Exchange.createEURTo("EUR",1.0d));
        ecbExchangeData.getEcbCurrencyData().forEach(item -> allExchangeRates.put(item.getCurrency(), Exchange.createEURTo(item.getCurrency(), item.getRate())));
        return allExchangeRates;
    }

    private Mono<String> loadDataFromEcb() {
        return WebClient.create()
                .get()
                .uri(ecbExchangesRatesUrl)
                .retrieve()
                .bodyToMono(String.class);
    }

    private EcbExchangeData convertXmlToExchangeData(String xmlString) {
        if (xmlString == null || xmlString.isBlank()) {
            return null;
        }
        XmlMapper mapper = new XmlMapper();
        try {
            return mapper.readValue(xmlString, EcbExchangeData.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
