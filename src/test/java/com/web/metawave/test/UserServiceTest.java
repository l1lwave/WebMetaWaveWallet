package com.web.metawave.test;


import org.junit.jupiter.api.Test;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.UserRole;
import web.meta.wave.repository.UserRepository;
import web.meta.wave.service.UserService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserServiceTest {
    @Test
    public void testAddNewUsers() {
        UserRepository mockRepo = mock(UserRepository.class);
        UserService userService = new UserService(mockRepo);
        CustomUser customUser = new CustomUser(1L, "email.com", "qweqwe1231eqwe", userService.generateWalletNumber(), UserRole.USER);
        when(mockRepo.save(customUser)).thenReturn(customUser);

        assertEquals("email.com", customUser.getEmail());
    }

    @Test
    public void testExistByEmail() {
        UserRepository mockRepo = mock(UserRepository.class);
        UserService userService = new UserService(mockRepo);
        when(mockRepo.existsByEmail("email.com")).thenReturn(true);

        assertEquals(true, userService.existByEmail("email.com"));
    }

    @Test
    public void testFindByEmail() {
        UserRepository mockRepo = mock(UserRepository.class);
        UserService userService = new UserService(mockRepo);
        when(mockRepo.findByEmail("email.com")).thenReturn(new CustomUser(1L, "email.com", "qweqwe1231eqwe", userService.generateWalletNumber(), UserRole.USER));

        assertEquals("email.com", userService.findByEmail("email.com").getEmail());
    }

    @Test
    public void testExistByWalletNumber() {
        UserRepository mockRepo = mock(UserRepository.class);
        UserService userService = new UserService(mockRepo);
        String walletNumber = userService.generateWalletNumber();
        when(mockRepo.existsByWalletNumber(walletNumber)).thenReturn(true);

        assertEquals(true, userService.existByWalletNumber(walletNumber));
    }

    @Test
    public void testFindByWalletNumber() {
        UserRepository mockRepo = mock(UserRepository.class);
        UserService userService = new UserService(mockRepo);
        String walletNumber = userService.generateWalletNumber();
        when(mockRepo.findByWalletNumber(walletNumber)).thenReturn(new CustomUser(1L, "email.com", "qweqwe1231eqwe", walletNumber, UserRole.USER));

        assertEquals("email.com", userService.findByWalletNumber(walletNumber).getEmail());
    }
}
