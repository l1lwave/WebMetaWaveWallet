package com.web.metawave.test;

import org.junit.jupiter.api.Test;
import web.meta.wave.model.*;
import web.meta.wave.repository.NotificationsRepository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationServiceTest {
    @Test
    public void testAddNotification() {
        NotificationsRepository mockRepo = mock(NotificationsRepository.class);

        Notifications notification = new Notifications(1L, "Title", "Content");
        when(mockRepo.save(notification)).thenReturn(notification);

        assertEquals("Title", notification.getTitle());
    }
}
