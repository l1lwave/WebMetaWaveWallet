package web.meta.wave.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    public SecurityConfig(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/").permitAll()
                    .antMatchers("/main").hasAnyRole("USER", "ADMIN")
                    .antMatchers("/register").permitAll()
                    .antMatchers("/profile/allTxns").hasRole("ADMIN")
                    .antMatchers("/profile/allUsers/users").hasRole("ADMIN")
                    .antMatchers("/profile/allUsers/banned").hasRole("ADMIN")
                    .antMatchers("/profile/allUsers/admins").hasRole("ADMIN")
                .and()
                .exceptionHandling()
                    .accessDeniedPage("/unauthorized")
                .and()
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/login_security_check")
                    .failureUrl("/login?error")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .permitAll()
                    .successHandler(authenticationSuccessHandler)
                .and()
                .logout()
                    .permitAll()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout");

        return http.build();
    }

}

