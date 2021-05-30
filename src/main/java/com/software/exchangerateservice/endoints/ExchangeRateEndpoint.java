package com.software.exchangerateservice.endoints;


import com.software.exchangerateservice.data.ExchangeRateProvider;
import com.software.exchangerateservice.domain.ExchangeRate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class ExchangeRateEndpoint {

    ExchangeRateProvider exchangeRateProvider;

    public ExchangeRateEndpoint(ExchangeRateProvider exchangeRateProvider){
        this.exchangeRateProvider = exchangeRateProvider;
    }

    @GetMapping(value = "exchangerate/{fromCurrency}/{toCurrency}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeRate> getExchangeRate(String fromCurrency, String toCurrency){
        exchangeRateProvider.getExchangeRate(fromCurrency, toCurrency);
        return ResponseEntity.ok(new ExchangeRate());
    }

}
