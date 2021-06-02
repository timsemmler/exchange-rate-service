package com.software.exchange.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(value = {"subject", "Sender"})
public class EcbExchangeData {
    private LocalDate date;
    private List<EcbCurrencyData> currencyDataList;

    public EcbExchangeData() {
        this.currencyDataList = new ArrayList<>();
    }

    @JsonProperty("Cube")
    private void unpack(Map<String, Object> cube) {
        Map internalCube = (Map) cube.get("Cube");
        String timeString = (String) internalCube.get("time");
        this.date = LocalDate.parse(timeString);


        List<Map<String, String>> listOfExchangeRates = (List<Map<String, String>>) internalCube.get("Cube");
        for (Map<String, String> rate : listOfExchangeRates) {
            if (!rate.get("currency").isBlank() && !rate.get("rate").isBlank()) {
                this.currencyDataList.add(new EcbCurrencyData(rate.get("currency"), Double.parseDouble(rate.get("rate"))));
            }
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public List<EcbCurrencyData> getEcbCurrencyData() {
        return currencyDataList;
    }
}

class EcbCurrencyData {
    private String currency;
    private double rate;

    public EcbCurrencyData(String currency, double rate) {
        this.currency = currency;
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public double getRate() {
        return rate;
    }
}
