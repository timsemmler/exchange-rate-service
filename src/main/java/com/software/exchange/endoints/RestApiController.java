package com.software.exchange.endoints;


import com.software.exchange.domain.Currency;
import com.software.exchange.domain.Exchange;
import com.software.exchange.service.ExchangeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ControllerAdvice
@RequestMapping("/rest/")
public class RestApiController {

    private ExchangeService exchangeRateService;

    public RestApiController(ExchangeService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping(value = "currencies/exchange",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Exchange> getExchange(@RequestParam String from, @RequestParam String to, @RequestParam(defaultValue = "1.0") double amount) {
        Exchange exchange = exchangeRateService.exchangeCurrency(from, to, amount);
        return ResponseEntity.ok(exchange);
    }

    @GetMapping(value = "currencies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Currency>> getSupportetCurrencies() {
        return ResponseEntity.ok(exchangeRateService.loadAllCurrencies());
    }

}
