package web.meta.wave.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.meta.wave.mail.EmailService;
import web.meta.wave.model.UserRole;
import web.meta.wave.service.*;
import web.meta.wave.statements.LoginStatenemts;

@Controller
public class LoginController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final LoginStatenemts loginStatenemts = new LoginStatenemts();

    public LoginController(UserService userService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String index() {
        return loginStatenemts.getIndex();
    }

    @PostMapping(value = "/newuser")
    public String newuser(@RequestParam() String email,
                          @RequestParam() String password,
                          @RequestParam() String passwordCheck,
                          Model model) {
        if(password == null || passwordCheck == null ||
                password.isEmpty() || passwordCheck.isEmpty()) {
            model.addAttribute(loginStatenemts.getEmailInput(), email);
            model.addAttribute(loginStatenemts.getNullError(), loginStatenemts.isTrue());
            return loginStatenemts.getRegisterWindow();
        }

        if(password.length() < 8) {
            model.addAttribute(loginStatenemts.getEmailInput(), email);
            model.addAttribute(loginStatenemts.getPasswordLengthError(), loginStatenemts.isTrue());
            return loginStatenemts.getRegisterWindow();
        }

        if(userService.existByEmail(email)) {
            model.addAttribute(loginStatenemts.getEmailInput(), email);
            model.addAttribute(loginStatenemts.getEmailError(), loginStatenemts.isTrue());
            return loginStatenemts.getRegisterWindow();
        }

        if(!password.equals(passwordCheck)) {
            model.addAttribute(loginStatenemts.getEmailInput(), email);
            model.addAttribute(loginStatenemts.getPassError(), loginStatenemts.isTrue());
            return loginStatenemts.getRegisterWindow();
        }

        String passHash = passwordEncoder.encode(password);
        String code = LoginService.generateCode();

        emailService.sendMessage(email, code);
        model.addAttribute(loginStatenemts.getEmail(), email);
        model.addAttribute(loginStatenemts.getPassword(), passHash);
        model.addAttribute(loginStatenemts.getTrueCode(), code);
        return loginStatenemts.getCodeWindow();
    }

    @PostMapping(value = "/code")
    public String code(@RequestParam String inputCode,
                       @RequestParam String email,
                       @RequestParam String password,
                       @RequestParam String trueCode,
                       Model model) {

        if (!inputCode.equals(trueCode)) {
            model.addAttribute(loginStatenemts.getErrorCode(), loginStatenemts.isTrue());
            model.addAttribute(loginStatenemts.getEmail(), email);
            model.addAttribute(loginStatenemts.getPassword(), password);
            model.addAttribute(loginStatenemts.getTrueCode(), trueCode);
            return loginStatenemts.getCodeWindow();
        }

        userService.createUser(email, password, UserRole.USER);
        userService.createAccount(userService.findByEmail(email));

        return loginStatenemts.getRedirect();
    }

    @PostMapping("/resendCode")
    public String resendCode(
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        String newCode = LoginService.generateCode();
        emailService.sendMessage(email, newCode);

        model.addAttribute(loginStatenemts.getEmail(), email);
        model.addAttribute(loginStatenemts.getPassword(), password);
        model.addAttribute(loginStatenemts.getTrueCode(), newCode);
        return loginStatenemts.getCodeWindow();
    }

    @GetMapping("/login")
    public String loginPage() {
        return loginStatenemts.getLoginWindow();
    }

    @GetMapping("/register")
    public String register() {
        return loginStatenemts.getRegisterWindow();
    }
}
