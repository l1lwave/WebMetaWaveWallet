package web.meta.wave.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.meta.wave.model.CustomUser;
import web.meta.wave.model.UserRole;
import web.meta.wave.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BanUsersService {
    private final UserRepository userRepository;

    public BanUsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean isBanned(String email) {
        return userRepository.existsByEmailAndRole(email, UserRole.BANNED);
    }

    @Transactional
    public void banUsers(long[] idList) {
        for (long id : idList){
            Optional<CustomUser> user = userRepository.findById(id);
            if(user.isPresent()){
                user.get().setRole(UserRole.BANNED);
                userRepository.save(user.get());
            }
        }
    }

    @Transactional
    public void unbanUsers(long[] idList) {
        for (long id : idList){
            Optional<CustomUser> user = userRepository.findById(id);
            if(user.isPresent()){
                user.get().setRole(UserRole.USER);
                userRepository.save(user.get());
            }
        }
    }

    @Transactional
    public void getAdminUsers(long[] idList) {
        for (long id : idList){
            Optional<CustomUser> user = userRepository.findById(id);
            if(user.isPresent()){
                user.get().setRole(UserRole.ADMIN);
                userRepository.save(user.get());
            }
        }
    }

    @Transactional
    public void unAdminUser(long[] idList) {
        for (long id : idList){
            Optional<CustomUser> user = userRepository.findById(id);
            if(user.isPresent()){
                user.get().setRole(UserRole.USER);
                userRepository.save(user.get());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<CustomUser> getBannedUsers() {
        return userRepository.findAllByRole(UserRole.BANNED);
    }

    @Transactional(readOnly = true)
    public List<CustomUser> getNormalUsers() {
        return userRepository.findAllByRole(UserRole.USER);
    }

    @Transactional(readOnly = true)
    public List<CustomUser> getAdminUsers() {
        return userRepository.findAllByRole(UserRole.ADMIN);
    }

}
