package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    public static final int USER_PER_PAGE = 4;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public List<User> listAll() {
        return userRepository.findAll(Sort.by("firstName").ascending());
    }

    public List<Role> listRole() {
        return (List<Role>) roleRepository.findAll();
    }

    public User save(User user) {
        boolean checkUpdating = (user.getId() != null);
        if(checkUpdating) {
            User existingUser = userRepository.findById(user.getId()).get();
            if(user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }
            else encodePassword(user);
        }
        else encodePassword(user);
        return userRepository.save(user);
    }

    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {

        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);

        if(keyword != null) {
            return userRepository.findAll(keyword, pageable);
        }

        return userRepository.findAll(pageable);
    }

    public void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
       User user = userRepository.getUserByEmail(email);
       if(user == null) return true;
       if(user.getId() == id) return true;
        return false;
    }

    public User get(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
    }

    public void delete(Integer id) throws UserNotFoundException {
        Long count = userRepository.countById(id);
        if(count == null || count == 0) {
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
        userRepository.deleteById(id);
    }


    public User updateAccount(User userInform) {
        User userInDB = userRepository.findById(userInform.getId()).get();
        if(!userInform.getPassword().isEmpty()) {
            userInDB.setPassword(userInform.getPassword());
            encodePassword(userInDB);
        }

        if(userInform.getPhotos() != null) {
            userInDB.setPhotos(userInform.getPhotos());
        }

        userInDB.setFirstName(userInform.getFirstName());
        userInDB.setLastName(userInform.getLastName());

        return userRepository.save(userInDB);
    }
}
