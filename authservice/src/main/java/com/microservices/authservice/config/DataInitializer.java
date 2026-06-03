package com.microservices.authservice.config;

import com.microservices.authservice.entity.User;
import com.microservices.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner
{
    private final UserRepository userRepository;

    @Override
    public void run(String... args)
    {
        if(userRepository.count() == 0)
        {
            User user1 = new User();
            user1.setEmail("manager@test.com");
            user1.setPassword("password123");

            User user2 = new User();
            user2.setEmail("employee@test.com");
            user2.setPassword("password123");

            userRepository.save(user1);
            userRepository.save(user2);

            log.info("Default users inserted successfully.");
        }
    }
}
