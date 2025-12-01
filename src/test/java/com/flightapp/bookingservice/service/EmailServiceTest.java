package com.flightapp.bookingservice.service;

import com.flightapp.bookingservice.config.RabbitMQConfig;
import com.flightapp.bookingservice.messaging.BookingEvent;
import com.flightapp.bookingservice.messaging.BookingListener;
import com.flightapp.bookingservice.messaging.BookingPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EmailService emailService;

    @InjectMocks
    private BookingPublisher bookingPublisher;

    @InjectMocks
    private BookingListener bookingListener;

    @Test
    void testSendEmail_Success() {
        BookingEvent event = new BookingEvent();
        event.setUserEmail("valid@test.com");
        event.setPnr("PNR123");
        event.setUserName("John");
        event.setNumberOfSeats(1);
        event.setTotalPrice(100.0);
        event.setFlightId("F1");
        
        emailService.sendBookingEmail(event);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_Invalid() {
        BookingEvent event = new BookingEvent();
        event.setUserEmail("invalid-email-string"); 
        
        emailService.sendBookingEmail(event);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_Exception() {
        BookingEvent event = new BookingEvent();
        event.setUserEmail("error@test.com");
        doThrow(new RuntimeException("Mail Error")).when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendBookingEmail(event);
    }

    @Test
    void testPublisher() {
        BookingEvent event = new BookingEvent();
        event.setPnr("PNR1");
        bookingPublisher.publishBookingConfirmation(event);
        
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.BOOKING_EXCHANGE), eq(RabbitMQConfig.BOOKING_ROUTING_KEY), eq(event));
    }
    
    @Test
    void testPublisher_NoRabbit() {
        BookingPublisher pub = new BookingPublisher(); 
        pub.publishBookingConfirmation(new BookingEvent());
    }

    @Test
    void testListener() {
        BookingListener listener = new BookingListener();
        
        EmailService mockEmailService = mock(EmailService.class);
        org.springframework.test.util.ReflectionTestUtils.setField(listener, "emailService", mockEmailService);
        
        BookingEvent event = new BookingEvent();
        listener.handleBookingEvent(event);
        
        verify(mockEmailService).sendBookingEmail(event);
    }
}