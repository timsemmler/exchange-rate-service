package com.software.exchangerate.endpoints;

import com.software.exchangerate.data.ExchangeRateDataProvider;
import com.software.exchangerate.data.ExchangeRateDataProviderImpl;
import com.software.exchangerate.endoints.RestApiController;
import com.software.exchangerate.service.ExchangeRateServiceImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.format.DateTimeFormatter;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestApiControllerTest {

        @Autowired
    private MockMvc mockMvc;

    public static MockWebServer mockBackEnd;

    @BeforeEach
    void setUpBeforeEach() throws IOException {
        if (mockBackEnd != null) {
            mockBackEnd.shutdown();
        }
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        mockResponse();
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
    }

    @Test
    @DisplayName("Should list all supported currencies when making GET request to endpoint - /rest/currencies")
    void shouldListAllCurrencies() throws Exception {
        mockMvc.perform(get("/rest/currencies/")).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Should list all supported currencies when making GET request to endpoint - /rest/currencies")
    void shouldReturnExchangeRate() throws Exception {
        mockMvc.perform(get("/rest/exchangerate/DKK/USD")).andDo(MockMvcResultHandlers.print());
    }

    private void mockResponse() {
        try {
            Resource resource = new ClassPathResource("/testdate/ecbResponse.xml");
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
