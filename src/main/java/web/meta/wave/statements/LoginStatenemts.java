package web.meta.wave.statements;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginStatenemts {
    private final String nullError = "nullError";
    private final boolean isTrue = true;
    private final String passwordLengthError = "passwordLengthError";
    private final String emailError = "emailError";
    private final String emailInput = "emailInput";
    private final String passError = "passError";
    private final String email = "email";
    private final String password = "password";
    private final String trueCode = "trueCode";
    private final String errorCode = "errorCode";
    private final String CHARACTERS = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final int CODE_LENGTH = 10;

    //Windows
    private final String index = "index";
    private final String registerWindow = "registerWindow";
    private final String codeWindow = "codeWindow";
    private final String redirect = "redirect:/login";
    private final String loginWindow = "loginWindow";

}
