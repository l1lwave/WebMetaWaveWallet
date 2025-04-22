package web.meta.wave.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import web.meta.wave.model.Notifications;
import web.meta.wave.model.UserRole;
import web.meta.wave.service.*;

import java.math.BigDecimal;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WalletConfig extends GlobalMethodSecurityConfiguration {

    public static final String ADMIN_EMAIL = "admin.com";
    public static final String USER_EMAIL = "user.com";
    public static final String GAS_EMAIL = "gas.com";


    @Bean
    public CommandLineRunner demo(final UserService userService,
                                  final NetworkService networkService,
                                  final TokenService tokenService,
                                  final BalanceService balanceService,
                                  final NotificationsService notificationsService,
                                  final PasswordEncoder encoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                String name1 = "Ethereum Mainnet";
                String name2 = "BNB Smart Chain Mainnet";
                String name3 = "Arbitrum One";

                if(!networkService.existByName(name1)) {
                    networkService.addNetwork("Ethereum Mainnet");
                }

                if(!networkService.existByName(name2)) {
                    networkService.addNetwork("BNB Smart Chain Mainnet");
                }

                if(!networkService.existByName(name3)) {
                    networkService.addNetwork("Arbitrum One");
                }

                tokenService.addToken(1L);
                tokenService.addToken(1027L);
                tokenService.addToken(825L);
                tokenService.addToken(52L);
                tokenService.addToken(1839L);
                tokenService.addToken(74L);

                if(!userService.existByEmail(ADMIN_EMAIL)) {
                    userService.createUser(
                            ADMIN_EMAIL,
                            encoder.encode("admin"),
                            UserRole.ADMIN);

                    Long countTokens = tokenService.countAll();
                    Long countNetworks = networkService.countAll();
                    for (long i = 1L; i <= countNetworks; i++) {
                        if (networkService.findById(i).isPresent()){
                            for (long j = 1L; j <= countTokens; j++) {
                                if (tokenService.findByTokenId(j).isPresent()){
                                    balanceService.addBalance(
                                            networkService.findById(i).get(),
                                            tokenService.findByTokenId(j).get(),
                                            userService.findByEmail(USER_EMAIL),
                                            BigDecimal.valueOf(50));
                                }
                            }
                        }
                    }
                }

                if(!userService.existByEmail(USER_EMAIL)) {
                    userService.createUser(
                            USER_EMAIL,
                            encoder.encode("user"),
                            UserRole.USER);

                    Long countTokens = tokenService.countAll();
                    Long countNetworks = networkService.countAll();
                    for (long i = 1L; i <= countNetworks; i++) {
                        if (networkService.findById(i).isPresent()){
                            for (long j = 1L; j <= countTokens; j++) {
                                if (tokenService.findByTokenId(j).isPresent()){
                                    balanceService.addBalance(
                                            networkService.findById(i).get(),
                                            tokenService.findByTokenId(j).get(),
                                            userService.findByEmail(USER_EMAIL),
                                            BigDecimal.valueOf(3.00002344));
                                }
                            }
                        }
                    }

                }

                if(!userService.existByEmail(GAS_EMAIL)) {
                    userService.createUser(
                            GAS_EMAIL,
                            encoder.encode("gas"),
                            UserRole.ADMIN);

                    Long countNetworks = networkService.countAll();
                    for (long i = 1L; i <= countNetworks; i++) {
                        if (networkService.findById(i).isPresent()){
                            balanceService.addBalance(
                                    networkService.findById(i).get(),
                                    tokenService.findByTokenCode(1027L),
                                    userService.findByEmail(GAS_EMAIL),
                                    BigDecimal.valueOf(0));
                        }
                    }
                }


                notificationsService.saveNotifications(new Notifications(
                        "Three Networks Now Supported",
                        "Our wallet now fully supports Ethereum, BNB Smart Chain, and Polygon! You can manage tokens, perform transactions, and use bridge and swap features across all three — all in one app."
                ));

                notificationsService.saveNotifications(new Notifications(
                        "Token Swap Feature Is Live",
                        "You asked — we delivered! Our new swap feature allows you to exchange tokens across supported networks in just a few taps. Say goodbye to complicated exchanges and enjoy built-in convenience."
                ));
            }
        };
    }
}
