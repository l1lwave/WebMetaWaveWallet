package web.meta.wave.controller;

import org.apache.jasper.tagplugins.jstl.core.If;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.meta.wave.mail.EmailService;
import web.meta.wave.model.UserRole;
import web.meta.wave.service.BalanceService;
import web.meta.wave.service.NetworkService;
import web.meta.wave.service.TokenService;
import web.meta.wave.service.UserService;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Controller
public class LoginController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final NetworkService networkService;
    private final BalanceService balanceService;

    public LoginController(UserService userService, PasswordEncoder passwordEncoder, EmailService emailService, TokenService tokenService, NetworkService networkService, BalanceService balanceService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenService = tokenService;
        this.networkService = networkService;
        this.balanceService = balanceService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping(value = "/newuser")
    public String newuser(@RequestParam(required = true) String email,
                          @RequestParam(required = true) String password,
                          @RequestParam(required = true) String passwordCheck,
                          Model model) {
        if(password == null || passwordCheck == null ||
                password.isEmpty() || passwordCheck.isEmpty()) {
            model.addAttribute("nullError", true);
            return "registerWindow";
        }

        if(password.length() < 8) {
            model.addAttribute("passwordLengthError", true);
            return "registerWindow";
        }

        if(userService.existByEmail(email)) {
            model.addAttribute("emailError", true);
            return "registerWindow";
        }

        if(!password.equals(passwordCheck)) {
            model.addAttribute("passError", true);
            return "registerWindow";
        }

        String passHash = passwordEncoder.encode(password);
        String code = generateCode();

        emailService.sendMessage(email, code);
        model.addAttribute("email", email);
        model.addAttribute("password", passHash);
        model.addAttribute("trueCode", code);
        return "codeWindow";
    }

    @PostMapping(value = "/code")
    public String code(@RequestParam String inputCode,
                       @RequestParam String email,
                       @RequestParam String password,
                       @RequestParam String trueCode,
                       Model model) {


        if (!inputCode.equals(trueCode)) {
            model.addAttribute("errorCode", true);
            model.addAttribute("email", email);
            model.addAttribute("password", password);
            model.addAttribute("trueCode", trueCode);
            return "codeWindow";
        }

        userService.createUser(email, password, UserRole.USER);

        Long countTokens = tokenService.countAll();
        Long countNetworks = networkService.countAll();
        for (long i = 1L; i <= countNetworks; i++) {
            if (networkService.findById(i).isPresent()){
                for (long j = 1L; j <= countTokens; j++) {
                    if (tokenService.findByTokenId(j).isPresent()){
                        balanceService.addBalance(networkService.findById(i).get(), tokenService.findByTokenId(j).get(), userService.findByEmail(email), BigDecimal.valueOf(0));
                    }
                }
            }
        }

        return "redirect:/login";
    }

    @PostMapping("/resendCode")
    public String resendCode(
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        String newCode = LoginController.generateCode();
        emailService.sendMessage(email, newCode);

        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("trueCode", newCode);
        return "codeWindow";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "loginWindow";
    }

    @GetMapping("/register")
    public String register() {
        return "registerWindow";
    }

    public static String generateCode() {
        final String CHARACTERS = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int CODE_LENGTH = 10;

        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }
}
