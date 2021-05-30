package com.software.exchangerateservice.endoints;


import com.software.exchangerateservice.domain.ExchangeRate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class ExchangeRateEndpoint {

    @GetMapping("exchangerate/{currency1}/{currency2}")
    public ResponseEntity<ExchangeRate> getExchangeRate(){
        return ResponseEntity.ok(new ExchangeRate());
    }

}
