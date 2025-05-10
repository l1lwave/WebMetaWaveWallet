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
import web.meta.wave.statements.ConfigStatements;

import java.math.BigDecimal;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WalletConfig extends GlobalMethodSecurityConfiguration {

    private static final ConfigStatements configStatements = new ConfigStatements();

    @Bean
    public CommandLineRunner demo(final UserService userService,
                                  final NetworkService networkService,
                                  final TokenService tokenService,
                                  final BalanceService balanceService,
                                  final NotificationsService notificationsService,
                                  final PasswordEncoder encoder) {
        return strings -> {
            String name1 = configStatements.getName1();
            String name2 = configStatements.getName2();
            String name3 = configStatements.getName3();

            if(!networkService.existByName(name1)) {
                networkService.addNetwork(configStatements.getName1());
            }

            if(!networkService.existByName(name2)) {
                networkService.addNetwork(configStatements.getName2());
            }

            if(!networkService.existByName(name3)) {
                networkService.addNetwork(configStatements.getName3());
            }

            tokenService.addToken(configStatements.getId1());
            tokenService.addToken(configStatements.getId2());
            tokenService.addToken(configStatements.getId3());
            tokenService.addToken(configStatements.getId4());
            tokenService.addToken(configStatements.getId5());
            tokenService.addToken(configStatements.getId6());

            if(!userService.existByEmail(configStatements.getADMIN_EMAIL())) {
                userService.createUser(
                        configStatements.getADMIN_EMAIL(),
                        encoder.encode(configStatements.getADMIN_PASSWORD()),
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
                                        userService.findByEmail(configStatements.getADMIN_EMAIL()),
                                        BigDecimal.valueOf(50));
                            }
                        }
                    }
                }
            }

            if(!userService.existByEmail(configStatements.getUSER_EMAIL())) {
                userService.createUser(
                        configStatements.getUSER_EMAIL(),
                        encoder.encode(configStatements.getUSER_PASSWORD()),
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
                                        userService.findByEmail(configStatements.getUSER_EMAIL()),
                                        BigDecimal.valueOf(20));
                            }
                        }
                    }
                }

            }

            if(!userService.existByEmail(configStatements.getGAS_EMAIL())) {
                userService.createUser(
                        configStatements.getGAS_EMAIL(),
                        encoder.encode(configStatements.getGAS_PASSWORD()),
                        UserRole.ADMIN);

                Long countNetworks = networkService.countAll();
                for (long i = 1L; i <= countNetworks; i++) {
                    if (networkService.findById(i).isPresent()){
                        balanceService.addBalance(
                                networkService.findById(i).get(),
                                tokenService.findByTokenCode(1027L),
                                userService.findByEmail(configStatements.getGAS_EMAIL()),
                                BigDecimal.valueOf(0));
                    }
                }
            }


            notificationsService.saveNotifications(new Notifications(
                    configStatements.getTittle1(),
                    configStatements.getContent1()
            ));

            notificationsService.saveNotifications(new Notifications(
                    configStatements.getTittle2(),
                    configStatements.getContent2()
            ));
        };
    }
}
