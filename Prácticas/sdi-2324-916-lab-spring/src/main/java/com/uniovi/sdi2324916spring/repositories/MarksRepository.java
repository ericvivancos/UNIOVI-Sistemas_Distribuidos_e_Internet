package com.uniovi.sdi2324916spring.repositories;

import com.uniovi.sdi2324916spring.entities.Mark;
import com.uniovi.sdi2324916spring.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface MarksRepository extends CrudRepository<Mark,Long> {
    @Query("SELECT r from Mark r WHERE (LOWER(r.description) like LOWER(?1) OR lower(r.user.name) like lower(?1) )")
    Page<Mark> searchByDescriptionAndName(Pageable pageable,String seachtext);

    @Query("SELECT r from Mark  r where (lower(r.description) like lower(?1) )")
    Page<Mark> searchByDescriptionNameAndUser(Pageable pageable,String searchText, User user);

    @Query("SELECT r FROM Mark r WHERE r.user = ?1 ORDER BY r.id ASC")
    Page<Mark> findAllByUser(Pageable pageable,User user);

    Page<Mark> findAll(Pageable pageable);
@Modifying
@Transactional
    @Query("UPDATE Mark SET resend = ?1 WHERE id = ?2")
    void updateResend(Boolean resend ,Long id);
}
