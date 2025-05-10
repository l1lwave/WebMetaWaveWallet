package web.meta.wave.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.Network;
import web.meta.wave.model.MetaToken;
import web.meta.wave.model.UserRole;
import web.meta.wave.repository.UserRepository;
import web.meta.wave.service.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private TokenService tokenService;
    private NetworkService networkService;
    private BalanceService balanceService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        tokenService = mock(TokenService.class);
        networkService = mock(NetworkService.class);
        balanceService = mock(BalanceService.class);
        userService = new UserService(userRepository, tokenService, networkService, balanceService);
    }

    @Test
    void testCreateUserNewUser() {
        String email = "test@example.com";
        String password = "hashedpassword";
        UserRole role = UserRole.USER;

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByWalletNumber(anyString())).thenReturn(false);

        userService.createUser(email, password, role);

        verify(userRepository, times(1)).save(any(CustomUser.class));
    }

    @Test
    void testCreateUserEmailExists() {
        String email = "test@example.com";
        String password = "hashedpassword";
        UserRole role = UserRole.USER;

        when(userRepository.existsByEmail(email)).thenReturn(true);

        userService.createUser(email, password, role);

        verify(userRepository, never()).save(any());
    }

    @Test
    void testExistByEmail() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userService.existByEmail(email);

        assertTrue(result);
    }

    @Test
    void testFindByEmail() {
        String email = "test@example.com";
        CustomUser user = new CustomUser();
        when(userRepository.findByEmail(email)).thenReturn(user);

        CustomUser result = userService.findByEmail(email);

        assertEquals(user, result);
    }

    @Test
    void testExistByWalletNumber() {
        String walletNumber = "WALLET123";
        when(userRepository.existsByWalletNumber(walletNumber)).thenReturn(true);

        boolean result = userService.existByWalletNumber(walletNumber);

        assertTrue(result);
    }

    @Test
    void testFindByWalletNumber() {
        String walletNumber = "WALLET123";
        CustomUser user = new CustomUser();
        when(userRepository.findByWalletNumber(walletNumber)).thenReturn(user);

        CustomUser result = userService.findByWalletNumber(walletNumber);

        assertEquals(user, result);
    }

    @Test
    void testGenerateWalletNumber() {
        String walletNumber = userService.generateWalletNumber();

        assertNotNull(walletNumber);
        assertTrue(walletNumber.startsWith("0x"));
        assertEquals(20, walletNumber.length());
    }

    @Test
    void testIsAdmin() {
        CustomUser admin = new CustomUser();
        admin.setRole(UserRole.ADMIN);

        assertTrue(userService.isAdmin(admin));

        CustomUser user = new CustomUser();
        user.setRole(UserRole.USER);

        assertFalse(userService.isAdmin(user));
    }

    @Test
    void testCreateAccount() {
        CustomUser customUser = new CustomUser();
        customUser.setId(1L);

        when(tokenService.countAll()).thenReturn(2L);
        when(networkService.countAll()).thenReturn(2L);

        when(networkService.findById(1L)).thenReturn(Optional.of(new Network()));
        when(networkService.findById(2L)).thenReturn(Optional.of(new Network()));
        when(tokenService.findByTokenId(1L)).thenReturn(Optional.of(new MetaToken()));
        when(tokenService.findByTokenId(2L)).thenReturn(Optional.of(new MetaToken()));

        userService.createAccount(customUser);

        verify(balanceService, times(4)).addBalance(any(Network.class), any(MetaToken.class), eq(customUser), eq(BigDecimal.ZERO));
    }
}

