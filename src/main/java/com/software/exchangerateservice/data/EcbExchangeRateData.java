package com.software.exchangerateservice.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(value = {"subject", "Sender"})
public class EcbExchangeRateData {
    private LocalDate date;
    private List<EcbCurrencyData> exchangeRates;

    public EcbExchangeRateData() {
        this.exchangeRates = new ArrayList<>();
    }

    @JsonProperty("Cube")
    private void unpack(Map<String, Object> cube) {
        Map internalCube = (Map) cube.get("Cube");
        String timeString = (String) internalCube.get("time");
        this.date = LocalDate.parse(timeString);


        List<Map<String, String>> listOfExchangeRates = (List<Map<String, String>>) internalCube.get("Cube");
        for (Map<String,String> rate: listOfExchangeRates) {
            this.exchangeRates.add(new EcbCurrencyData(rate.get("currency"), Double.parseDouble(rate.get("rate"))));
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public List<EcbCurrencyData> getExchangeRates() {
        return exchangeRates;
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
