package com.jonyshev.intershop;

import com.jonyshev.intershop.service.PaymentServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class IntershopCoreApplicationTests {
    @MockitoBean
    private PaymentServiceClient paymentServiceClient;


    @Test
    void contextLoads() {
    }

}
