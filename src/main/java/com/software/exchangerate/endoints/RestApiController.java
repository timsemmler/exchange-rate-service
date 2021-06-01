package com.software.exchangerate.endoints;


import com.fasterxml.jackson.annotation.JsonView;
import com.software.exchangerate.domain.Currency;
import com.software.exchangerate.domain.ExchangeRate;
import com.software.exchangerate.domain.Views;
import com.software.exchangerate.service.ExchangeRateService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@ControllerAdvice
@RequestMapping("/rest/")
public class RestApiController {

    private ExchangeRateService exchangeRateService;

    public RestApiController(ExchangeRateService exchangeRateService){
        this.exchangeRateService = exchangeRateService;
    }

    @JsonView(Views.ExchangeRate.class)
    @GetMapping(value = "exchangerate/{fromCurrency}/{toCurrency}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeRate> getExchangeRate(@PathVariable String fromCurrency, @PathVariable String toCurrency){
        ExchangeRate exchangeRate = exchangeRateService.loadExchangeRateAndIncreaseCounter(fromCurrency, toCurrency);
        return ResponseEntity.ok(exchangeRate);
    }
    @JsonView(Views.SupportetCurrencies.class)
    @GetMapping(value = "currencies",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Currency>> getSupportetCurrencies(){
        return ResponseEntity.ok(exchangeRateService.loadAllCurrencies());
    }

}
