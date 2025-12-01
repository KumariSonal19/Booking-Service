package com.flightapp.bookingservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookingServiceApplicationTest {

    @Test
    void contextLoads() {
    }
    
    @Test
    void testMain() {
        
         try {
             BookingServiceApplication.main(new String[]{});
         } catch (Exception e) {
             
         }
    }
}