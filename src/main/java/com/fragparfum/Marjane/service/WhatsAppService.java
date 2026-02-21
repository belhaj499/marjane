package com.fragparfum.Marjane.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.from}")
    private String from;   // can be +1415... or whatsapp:+1415...

    @Value("${twilio.admin}")
    private String admin;  // can be 06... or +2126...

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    private String normalizeToWhatsApp(String raw) {
        if (raw == null) return null;
        String s = raw.trim();

        if (s.startsWith("whatsapp:")) return s;

        if (s.startsWith("0")) s = "+212" + s.substring(1);
        if (!s.startsWith("+")) s = "+" + s;

        return "whatsapp:" + s;
    }

    public void notifyAdmin(String text) {
        String to = normalizeToWhatsApp(admin);
        String fromWa = normalizeToWhatsApp(from);

        Message msg = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromWa),
                text
        ).create();

        System.out.println("WhatsApp sent. SID=" + msg.getSid());
    }
}

