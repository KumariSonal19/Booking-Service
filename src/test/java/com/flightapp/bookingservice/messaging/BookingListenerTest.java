package com.flightapp.bookingservice.messaging;

import com.flightapp.bookingservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BookingListenerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BookingListener bookingListener;

    @Test
    void testHandleEvent() {
        BookingEvent event = new BookingEvent();
        bookingListener.handleBookingEvent(event);
        verify(emailService).sendBookingEmail(event);
    }
}