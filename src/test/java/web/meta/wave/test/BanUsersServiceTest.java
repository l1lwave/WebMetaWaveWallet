package web.meta.wave.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.UserRole;
import web.meta.wave.repository.UserRepository;
import web.meta.wave.service.BanUsersService;

import java.util.List;
import java.util.Optional;

public class BanUsersServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BanUsersService banUsersService;

    private CustomUser user1;
    private CustomUser user2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new CustomUser();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setRole(UserRole.USER);

        user2 = new CustomUser();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setRole(UserRole.USER);
    }

    @Test
    public void testIsBannedWhenUserIsBanned() {
        when(userRepository.existsByEmailAndRole("user1@example.com", UserRole.BANNED)).thenReturn(true);

        boolean isBanned = banUsersService.isBanned("user1@example.com");

        assertTrue(isBanned);
    }

    @Test
    public void testIsBannedWhenUserIsNotBanned() {
        when(userRepository.existsByEmailAndRole("user2@example.com", UserRole.BANNED)).thenReturn(false);

        boolean isBanned = banUsersService.isBanned("user2@example.com");

        assertFalse(isBanned);
    }

    @Test
    public void testBanUsers() {
        long[] idList = {1L, 2L};

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        banUsersService.banUsers(idList);

        assertEquals(UserRole.BANNED, user1.getRole());
        assertEquals(UserRole.BANNED, user2.getRole());
        verify(userRepository, times(2)).save(any(CustomUser.class));
    }

    @Test
    public void testUnbanUsers() {
        user1.setRole(UserRole.BANNED);
        user2.setRole(UserRole.BANNED);

        long[] idList = {1L, 2L};

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        banUsersService.unbanUsers(idList);

        assertEquals(UserRole.USER, user1.getRole());
        assertEquals(UserRole.USER, user2.getRole());
        verify(userRepository, times(2)).save(any(CustomUser.class));
    }

    @Test
    public void testGetAdminUsers() {
        user1.setRole(UserRole.ADMIN);
        user2.setRole(UserRole.ADMIN);

        when(userRepository.findAllByRole(UserRole.ADMIN)).thenReturn(List.of(user1, user2));

        List<CustomUser> adminUsers = banUsersService.getAdminUsers();

        assertEquals(2, adminUsers.size());
        assertTrue(adminUsers.contains(user1));
        assertTrue(adminUsers.contains(user2));
    }

    @Test
    public void testGetBannedUsers() {
        user1.setRole(UserRole.BANNED);
        user2.setRole(UserRole.BANNED);

        when(userRepository.findAllByRole(UserRole.BANNED)).thenReturn(List.of(user1, user2));

        List<CustomUser> bannedUsers = banUsersService.getBannedUsers();

        assertEquals(2, bannedUsers.size());
        assertTrue(bannedUsers.contains(user1));
        assertTrue(bannedUsers.contains(user2));
    }

    @Test
    public void testGetNormalUsers() {
        user1.setRole(UserRole.USER);
        user2.setRole(UserRole.USER);

        when(userRepository.findAllByRole(UserRole.USER)).thenReturn(List.of(user1, user2));

        List<CustomUser> normalUsers = banUsersService.getNormalUsers();

        assertEquals(2, normalUsers.size());
        assertTrue(normalUsers.contains(user1));
        assertTrue(normalUsers.contains(user2));
    }
}
