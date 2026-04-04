package web.meta.wave.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import web.meta.wave.model.Network;
import web.meta.wave.repository.NetworkRepository;
import web.meta.wave.service.NetworkService;

import java.util.Optional;
import java.util.List;

public class NetworkServiceTest {

    @Mock
    private NetworkRepository networkRepository;

    private NetworkService networkService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        networkService = new NetworkService(networkRepository);
    }

    @Test
    public void testFindAll() {
        Network network1 = new Network("Network1");
        Network network2 = new Network("Network2");
        when(networkRepository.findAll()).thenReturn(List.of(network1, network2));

        List<Network> networks = networkService.findAll();

        assertEquals(2, networks.size());
        assertEquals("Network1", networks.get(0).getName());
        assertEquals("Network2", networks.get(1).getName());
    }

    @Test
    public void testFindById() {
        Network network = new Network("Network1");
        when(networkRepository.findById(1L)).thenReturn(Optional.of(network));

        Optional<Network> result = networkService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Network1", result.get().getName());
    }

    @Test
    public void testAddNetwork() {
        String networkName = "Network1";
        when(networkRepository.existsByName(networkName)).thenReturn(false);

        networkService.addNetwork(networkName);

        verify(networkRepository, times(1)).save(any(Network.class));
    }

    @Test
    public void testAddNetworkAlreadyExists() {
        String networkName = "Network1";
        when(networkRepository.existsByName(networkName)).thenReturn(true);

        networkService.addNetwork(networkName);

        verify(networkRepository, times(0)).save(any(Network.class));
    }

    @Test
    public void testExistByName() {
        String networkName = "Network1";
        when(networkRepository.existsByName(networkName)).thenReturn(false);

        boolean exists = networkService.existByName(networkName);

        assertFalse(exists);
    }

    @Test
    public void testCountAll() {
        when(networkRepository.count()).thenReturn(5L);

        Long count = networkService.countAll();

        assertEquals(5L, count);
    }
}

