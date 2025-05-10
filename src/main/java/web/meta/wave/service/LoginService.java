package web.meta.wave.service;

import org.springframework.stereotype.Service;
import web.meta.wave.statements.LoginStatenemts;

import java.security.SecureRandom;

@Service
public class LoginService {
    private static final LoginStatenemts loginStatenemts = new LoginStatenemts();

    public static String generateCode() {
        final String CHARACTERS = loginStatenemts.getCHARACTERS();
        final int CODE_LENGTH = loginStatenemts.getCODE_LENGTH();

        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }
}
