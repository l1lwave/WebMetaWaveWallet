package com.web.metawave.test;

import org.junit.jupiter.api.Test;
import web.meta.wave.model.Network;
import web.meta.wave.repository.NetworkRepository;
import web.meta.wave.service.NetworkService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NetworkServiceTest {
    @Test
    public void testAddNetwork() {
        NetworkRepository mockRepo = mock(NetworkRepository.class);
        NetworkService networkService = new NetworkService(mockRepo);
        Network network = new Network(1L, "testNetwork");

        when(mockRepo.save(network)).thenReturn(network);

        assertEquals("testNetwork", network.getName());
    }

    @Test
    public void testGetAllNetwork() {
        NetworkRepository mockRepo = mock(NetworkRepository.class);
        NetworkService networkService = new NetworkService(mockRepo);
        Network network1 = new Network(1L, "testNetwork1");
        Network network2 = new Network(2L, "testNetwork2");

        when(mockRepo.findAll()).thenReturn(Arrays.asList(network1, network2));

        List<Network> networks = networkService.findAll();
        assertEquals(2, networks.size());

    }
}
