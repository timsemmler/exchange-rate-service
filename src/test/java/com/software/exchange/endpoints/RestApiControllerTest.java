package com.software.exchange.endpoints;


import com.software.exchange.domain.Currency;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(properties = {"com.software.exchangerates.ecb.url=http://localhost:8081"})
public class RestApiControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    public static MockWebServer mockBackEnd;

    @BeforeEach
    void setUpBeforeEach() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        if (mockBackEnd != null) {
            mockBackEnd.shutdown();
        }
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(8081);
        mockResponse();
    }

    @Test
    @DisplayName("Should list all supported currencies when GET - /rest/currencies")
    void shouldListAllCurrencies() throws Exception {
        Currency.clearAccessCounter();
        mockMvc.perform(get("/rest/currencies"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$", Matchers.hasSize(6)))
                .andExpect(jsonPath("$[0].name", Matchers.is("BGN")))
                .andExpect(jsonPath("$[0].amount", Matchers.is(1.9558)))
                .andExpect(jsonPath("$[0].accessCounter", Matchers.is(0)))
                .andExpect(jsonPath("$[1].name", Matchers.is("CZK")))
                .andExpect(jsonPath("$[1].amount", Matchers.is(25.454)))
                .andExpect(jsonPath("$[1].accessCounter", Matchers.is(0)))
                .andExpect(jsonPath("$[2].name", Matchers.is("DKK")))
                .andExpect(jsonPath("$[2].amount", Matchers.is(7.4365)))
                .andExpect(jsonPath("$[2].accessCounter", Matchers.is(0)))
                .andExpect(jsonPath("$[3].name", Matchers.is("EUR")))
                .andExpect(jsonPath("$[3].amount", Matchers.is(1.0)))
                .andExpect(jsonPath("$[3].accessCounter", Matchers.is(0)))
                .andExpect(jsonPath("$[4].name", Matchers.is("JPY")))
                .andExpect(jsonPath("$[4].amount", Matchers.is(133.79)))
                .andExpect(jsonPath("$[4].accessCounter", Matchers.is(0)))
                .andExpect(jsonPath("$[5].name", Matchers.is("USD")))
                .andExpect(jsonPath("$[5].amount", Matchers.is(1.2201)))
                .andExpect(jsonPath("$[5].accessCounter", Matchers.is(0)));



    }

    @Test
    @DisplayName("Should return Exchange from DKK to USD (1:0.1641) when GET - /rest/currencies/exchange?from=DKK&to=USD")
    void shouldGetExchangeNormalized() throws Exception {
        mockMvc.perform(get("/rest/currencies/exchange?from=DKK&to=USD"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$.exchangeRate", Matchers.is(0.16406912)))
                .andExpect(jsonPath("$.from.name", Matchers.is("DKK")))
                .andExpect(jsonPath("$.from.amount", Matchers.is(1.0)))
                .andExpect(jsonPath("$.to.name", Matchers.is("USD")))
                .andExpect(jsonPath("$.to.amount", Matchers.is(0.16406912)))
                .andExpect(jsonPath("$.chart", Matchers.is("https://www.xe.com/currencycharts/?from=DKK&to=USD")));
    }

    @Test
    @DisplayName("Should return Exchange from DKK to USD (5.38:0.1641) when GET - /rest/currencies/exchange?from=DKK&to=USD&amount=5.38")
    void shouldGetExchange() throws Exception {
        mockMvc.perform(get("/rest/currencies/exchange?from=DKK&to=USD&amount=5.38"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$.exchangeRate", Matchers.is(0.16406912)))
                .andExpect(jsonPath("$.from.name", Matchers.is("DKK")))
                .andExpect(jsonPath("$.from.amount", Matchers.is(5.38)))
                .andExpect(jsonPath("$.to.name", Matchers.is("USD")))
                .andExpect(jsonPath("$.to.amount", Matchers.is(0.88269187)))
                .andExpect(jsonPath("$.chart", Matchers.is("https://www.xe.com/currencycharts/?from=DKK&to=USD")));
    }


    private void mockResponse() {
        try {
            Resource resource = new ClassPathResource("/testdata/ecbResponse.xml");
            String xml = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            xml = xml.replace("'[DATE]'", String.format("'%s'", LocalDate.now().format(DateTimeFormatter.ISO_DATE)));
            mockBackEnd.enqueue(new MockResponse()
                    .setBody(xml)
                    .addHeader("Content-Type", "text/xml"));
        } catch (IOException e) {
            throw new RuntimeException("Error when converting testdata/ecbResponse.xml to String.");
        }
    }
}
