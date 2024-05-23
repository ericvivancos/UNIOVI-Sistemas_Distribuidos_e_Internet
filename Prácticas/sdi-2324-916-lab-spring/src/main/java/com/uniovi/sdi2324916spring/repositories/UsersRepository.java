package com.uniovi.sdi2324916spring.repositories;


import com.uniovi.sdi2324916spring.entities.*;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User,Long> {
    User findByDni(String dni);
}
