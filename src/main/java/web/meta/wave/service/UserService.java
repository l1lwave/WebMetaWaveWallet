package web.meta.wave.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.UserRole;
import web.meta.wave.repository.UserRepository;
import web.meta.wave.statements.UserStatements;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final NetworkService networkService;
    private final BalanceService balanceService;

    private static final UserStatements userStatements = new UserStatements();

    public UserService(UserRepository userRepository, TokenService tokenService, NetworkService networkService, BalanceService balanceService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.networkService = networkService;
        this.balanceService = balanceService;
    }

    @Transactional
    public void createUser(String email,
                           String hashPass,
                           UserRole role
                              ) {
        if (email == null || email.isEmpty() || userRepository.existsByEmail(email)) {
            return;
        }
        String walletNumber = generateWalletNumber();
        while (existByWalletNumber(walletNumber)){
            walletNumber = generateWalletNumber();
        }

        userRepository.save(new CustomUser(email, hashPass, walletNumber, role));
    }

    public void createAccount(CustomUser user) {
        Long countTokens = tokenService.countAll();
        Long countNetworks = networkService.countAll();
        for (long i = 1L; i <= countNetworks; i++) {
            networkService.findById(i).ifPresent(network -> {
                for (long j = 1L; j <= countTokens; j++) {
                    tokenService.findByTokenId(j).ifPresent(token -> balanceService.addBalance(network, token, user, BigDecimal.ZERO));
                }
            });
        }
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
        String CHARACTERS = userStatements.getCHARACTERS();
        SecureRandom RANDOM = new SecureRandom();

        StringBuilder walletNumber = new StringBuilder(userStatements.getStart());
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

    public boolean isAdmin(CustomUser user) {
        return user.getRole().equals(UserRole.ADMIN);
    }
}
