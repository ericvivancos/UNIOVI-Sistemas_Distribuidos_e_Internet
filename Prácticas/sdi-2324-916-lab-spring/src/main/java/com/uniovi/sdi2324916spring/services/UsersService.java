package com.uniovi.sdi2324916spring.services;

import com.uniovi.sdi2324916spring.entities.*;
import com.uniovi.sdi2324916spring.repositories.UsersRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;


@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsersService(UsersRepository usersRepository,BCryptPasswordEncoder bCryptPasswordEncoder){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.usersRepository = usersRepository;
    }


    @PostConstruct
    public void init(){

    }
    public List<User> getUsers(){
        List<User> users = new ArrayList<User>();
        usersRepository.findAll().forEach(users::add);
        return users;
    }
    public User getUser(Long id){
        return usersRepository.findById(id).get();
    }
    public void addUser(User user){
        if(user.getPassword() != null){
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        usersRepository.save(user);
    }
    public void deleteUser(Long id){
        usersRepository.deleteById(id);
    }
    public User getUserByDni(String dni){
        return usersRepository.findByDni(dni);
    }
    public void updateUser(User user){
        User currentUser = getUser(user.getId());
        if(!currentUser.equals(null)){
            currentUser.setDni(user.getDni());
            currentUser.setName(user.getName());
            currentUser.setLastName(user.getLastName());
            usersRepository.save(currentUser);
        }
    }
}
