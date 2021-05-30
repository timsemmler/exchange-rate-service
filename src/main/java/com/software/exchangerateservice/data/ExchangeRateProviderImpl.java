package com.software.exchangerateservice.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.software.exchangerateservice.domain.ExchangeRate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * TODO
 * Create List of Currencies
 * Every Currency have a asEURRate as Reference.
 * We only save HashMap<String,Currency> --> And Create a ExchangeRate on the fly.
 * The ExchangeRateObject may then calculate between the two currencies.
 *
 */


@Service
public class ExchangeRateProviderImpl implements ExchangeRateProvider {

    Map<LocalDate, Map<String, ExchangeRate>> dailyExchangeRates;

    @Override
    public EcbExchangeRateData getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            LocalDate now = LocalDate.now();
            if (dailyExchangeRates.containsKey(now)) {
                dailyExchangeRates.get(now);
            } else {
                EcbExchangeRateData exchangeRateData = convertXmlToExchangeRateData(loadDataFromEcb().block());
                dailyExchangeRates.computeIfAbsent(exchangeRateData.getDate(), createAllExchangeRatesFrom(exchangeRateData.getExchangeRates()));

            }
            return
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, List<ExchangeRate>> createAllExchangeRatesFrom(List<EcbCurrencyData> ecbCurrencyDataList) {
        Map<String, List<ExchangeRate>> allExchangeRates = new HashMap<>();
        //We need to add EUR because it isn't present in the ecbCurrencyDataList
        ecbCurrencyDataList.add(new EcbCurrencyData("EUR", 1.0d));


        for (EcbCurrencyData ecbCurrencyData : ecbCurrencyDataList) {
            List<ExchangeRate> exchangeRates = calculateExchangeRates(ecbCurrencyData.getCurrency(), ecbCurrencyDataList);
            allExchangeRates.put(ecbCurrencyData.getCurrency(), exchangeRates);
        }


        return allExchangeRates;
        //ecbCurrencyDataList.forEach(ecbCurrencyData -> exchangeRates.put("EUR", new ExchangeRate("EUR", ecbCurrencyData.getCurrency(), ecbCurrencyData.getRate())));
        //ecbCurrencyData.forEach();
    }

    private List<ExchangeRate> calculateExchangeRates(String currency, List<EcbCurrencyData> ecbCurrencyDataList) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        ecbCurrencyDataList.forEach(ecbCurrencyData -> {
                }
        );
        return exchangeRates;
    }

    private Mono<String> loadDataFromEcb() {
        return WebClient.create()
                .get()
                .uri("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml")
                .retrieve()
                .bodyToMono(String.class);
    }

    private EcbExchangeRateData convertXmlToExchangeRateData(String xmlString) throws JsonProcessingException {
        XmlMapper mapper = new XmlMapper();
        EcbExchangeRateData data = mapper.readValue(xmlString, EcbExchangeRateData.class);
        return data;
    }
}
