package web.meta.wave.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.meta.wave.model.Network;
import web.meta.wave.repository.NetworkRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NetworkService {
    private final NetworkRepository networkRepository;
    public NetworkService(NetworkRepository networkRepository) {
        this.networkRepository = networkRepository;
    }

    @Transactional(readOnly = true)
    public List<Network> findAll() {
        return networkRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Network> findById(Long id) {
        return networkRepository.findById(id);
    }

    @Transactional
    public boolean addNetwork(String name) {
        if(networkRepository.existsByName(name)) {
            return false;
        }
        networkRepository.save(new Network(name));
        return true;
    }

    @Transactional
    public boolean existByName(String name) {
        return networkRepository.existsByName(name);
    }

    @Transactional
    public Long countAll(){
        return networkRepository.count();
    }
}
