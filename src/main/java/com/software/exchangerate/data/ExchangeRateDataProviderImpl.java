package com.software.exchangerate.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.software.exchangerate.domain.Currency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;


@Service
public class ExchangeRateDataProviderImpl implements ExchangeRateDataProvider {

    private Map<LocalDate, Map<String, Currency>> dailyCurrencyValues;
    private String ecbExchangesRatesUrl;

    public ExchangeRateDataProviderImpl(@Value("ecb.exchangerates.url") String ecbExchangesRatesUrl){
        this.ecbExchangesRatesUrl = ecbExchangesRatesUrl;
        // Make our dailyCurrencyValues thread-safe
        dailyCurrencyValues = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public Map<String, Currency> loadCurrencies(){
        Map<String, Currency> currencies;
        LocalDate now = LocalDate.now();
        if (!dailyCurrencyValues.containsKey(now)) {
            try {
                EcbExchangeRateData exchangeRateData = convertXmlToExchangeRateData(loadDataFromEcb().block());
                currencies = createAllCurrenciesFrom(exchangeRateData);
                dailyCurrencyValues.putIfAbsent(exchangeRateData.getDate(), currencies);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            currencies = dailyCurrencyValues.get(now);
        }
        return currencies;
    }

    private Map<String, Currency> createAllCurrenciesFrom(EcbExchangeRateData ecbExchangeRateData) {
        Map<String, Currency> allCurrencyValues = new HashMap<>();
        //We need to add EUR because EUR is never present in the ecbCurrencyDataList
        allCurrencyValues.put("EUR", new Currency("EUR", 1.0d));
        ecbExchangeRateData.getEcbCurrencyData().forEach(item -> allCurrencyValues.put(item.getCurrency(), new Currency(item.getCurrency(), item.getRate())));
        return allCurrencyValues;
    }

    private Mono<String> loadDataFromEcb() {
        return WebClient.create()
                .get()
                .uri(ecbExchangesRatesUrl)
                .retrieve()
                .bodyToMono(String.class);
    }

    private EcbExchangeRateData convertXmlToExchangeRateData(String xmlString) throws JsonProcessingException {
        XmlMapper mapper = new XmlMapper();
        EcbExchangeRateData data = mapper.readValue(xmlString, EcbExchangeRateData.class);
        return data;
    }
}
