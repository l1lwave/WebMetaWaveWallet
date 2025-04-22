package web.meta.wave.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.meta.wave.config.WalletConfig;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.UserRole;
import web.meta.wave.repository.UserRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public boolean createUser(String email,
                              String hashPass,
                              UserRole role
                              ) {
        if (email == null || email.isEmpty() || userRepository.existsByEmail(email)) {
            return false;
        }
        String walletNumber = generateWalletNumber();
        while (existByWalletNumber(walletNumber)){
            walletNumber = generateWalletNumber();
        }

        userRepository.save(new CustomUser(email, hashPass, walletNumber, role));
        return true;
    }

    @Transactional
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public CustomUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public boolean existByWalletNumber(String walletNumber) {
        return userRepository.existsByWalletNumber(walletNumber);
    }

    @Transactional(readOnly = true)
    public CustomUser findByWalletNumber(String walletNumber) {
        return userRepository.findByWalletNumber(walletNumber);
    }

    public String generateWalletNumber() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom RANDOM = new SecureRandom();

        StringBuilder walletNumber = new StringBuilder("0x1");
        for (int i = 0; i < 17; i++) {
            walletNumber.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return walletNumber.toString();
    }

    public User getCurrentUser() {
        return (org.springframework.security.core.userdetails.User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
