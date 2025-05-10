package web.meta.wave.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import web.meta.wave.service.LoginService;
import web.meta.wave.statements.LoginStatenemts;

public class LoginServiceTest {

    @Mock
    private LoginStatenemts loginStatenemts;

    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loginService = new LoginService();
        loginService = mock(LoginService.class);

        when(loginStatenemts.getCHARACTERS()).thenReturn("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        when(loginStatenemts.getCODE_LENGTH()).thenReturn(10);
    }

    @Test
    public void testGenerateCodeLength() {
        String code = loginService.generateCode();
        assertEquals(10, code.length());
    }

    @Test
    public void testGenerateCodeCharacters() {
        String code = loginService.generateCode();
        String characters = loginStatenemts.getCHARACTERS();

        for (char c : code.toCharArray()) {
            assertTrue(characters.contains(String.valueOf(c)));
        }
    }

    @Test
    public void testGenerateCodeUniqueCodes() {
        String code1 = loginService.generateCode();
        String code2 = loginService.generateCode();

        assertNotEquals(code1, code2);
    }
}

