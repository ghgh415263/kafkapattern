package com.example.kafkapattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Configuration
public class PersistenceConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new SessionAuditorAware();
    }

    static class SessionAuditorAware implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attr == null) {
                return Optional.of("NoServletRequestAttributes");
            }
            HttpServletRequest request = attr.getRequest();
            HttpSession session = request.getSession(false);
            if (session == null) {
                return Optional.of("NoSession");
            }
            Object userId = session.getAttribute("userId");
            return Optional.of((String) userId);
        }

    }
}
