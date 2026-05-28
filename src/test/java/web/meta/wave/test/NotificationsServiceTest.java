package web.meta.wave.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import web.meta.wave.model.Notifications;
import web.meta.wave.repository.NotificationsRepository;
import web.meta.wave.service.NotificationsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationsServiceTest {

    @Mock
    private NotificationsRepository notificationsRepository;

    @InjectMocks
    private NotificationsService notificationsService;

    private Notifications notification;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        notification = new Notifications();
    }

    @Test
    public void testSaveNotifications() {
        notificationsService.saveNotifications(notification);
        verify(notificationsRepository, times(1)).save(notification);
    }

    @Test
    public void testDeleteNotifications() {
        notificationsService.deleteNotifications(notification);
        verify(notificationsRepository, times(1)).delete(notification);
    }

    @Test
    public void testFindNotificationsById() {
        when(notificationsRepository.findById(1L)).thenReturn(Optional.of(notification));

        Optional<Notifications> result = notificationsService.findNotificationsById(1L);

        assertTrue(result.isPresent());
        assertEquals(notification, result.get());
    }

    @Test
    public void testGetReverseList() {
        Notifications n1 = new Notifications();
        Notifications n2 = new Notifications();

        List<Notifications> list = new ArrayList<>(List.of(n1, n2));
        when(notificationsRepository.findAll()).thenReturn(list);

        List<Notifications> reverseList = notificationsService.getReverseList();

        assertEquals(2, reverseList.size());
        assertEquals(n2, reverseList.get(0));
        assertEquals(n1, reverseList.get(1));
    }
}