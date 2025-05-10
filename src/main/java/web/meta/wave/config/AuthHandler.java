package web.meta.wave.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import web.meta.wave.model.CustomUser;
import web.meta.wave.service.BanUsersService;
import web.meta.wave.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthHandler implements AuthenticationSuccessHandler {
    private final BanUsersService banUsersService;
    private final UserService userService;

    public AuthHandler(BanUsersService banUsersService, UserService userService) {
        this.banUsersService = banUsersService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomUser user = userService.findByEmail(userService.getCurrentUser().getUsername());
        if (banUsersService.isBanned(user.getEmail())) {
            response.sendRedirect("/login?banned");
        } else {
            response.sendRedirect("/network/1");
        }
    }
}
