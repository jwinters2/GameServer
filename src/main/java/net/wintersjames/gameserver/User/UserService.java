package net.wintersjames.gameserver.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author james
 */
@Service
public class UserService {
    
    @Autowired
    private final UserRepository userRepository;
    
    public UserService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public boolean registerUser(User user) {
        
        User preexisting = userRepository.findByUsername(user.getUsername());
        if(preexisting == null) {
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByUid(int uid) {
        return userRepository.findByUid(uid);
    }
    
}
