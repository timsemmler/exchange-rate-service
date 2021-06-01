package com.software.exchangerate.endoints;


import com.software.exchangerate.domain.Currency;
import com.software.exchangerate.domain.ExchangeRate;
import com.software.exchangerate.service.ExchangeRateService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/rest/")
public class RestApiController {

    ExchangeRateService exchangeRateService;

    public RestApiController(ExchangeRateService exchangeRateService){
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping(value = "exchangerate/{fromCurrency}/{toCurrency}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeRate> getExchangeRate(@PathVariable String fromCurrency, @PathVariable String toCurrency){
        ExchangeRate exchangeRate = exchangeRateService.loadExchangeRateAndIncreaseCounter(fromCurrency, toCurrency);
        return ResponseEntity.ok(exchangeRate);
    }

    @GetMapping(value = "currencies",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Currency>> getExchangeRate(){
        return ResponseEntity.ok(exchangeRateService.loadAllCurrencies());
    }
}
