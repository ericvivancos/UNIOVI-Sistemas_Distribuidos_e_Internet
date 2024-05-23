package com.uniovi.sdi2324916spring.services;

import com.uniovi.sdi2324916spring.entities.Professor;
import com.uniovi.sdi2324916spring.entities.User;
import com.uniovi.sdi2324916spring.repositories.ProfessorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class ProfessorsService {

    private final ProfessorsRepository professorsRepository;

    public ProfessorsService(ProfessorsRepository professorsRepository){
        this.professorsRepository = professorsRepository;
    }
    @PostConstruct
    public void init(){

    }
    public List<Professor> getProfessors(){
        List<Professor> professors = new ArrayList<>();
        professorsRepository.findAll().forEach(professors::add);
        return professors;
    }
    public Professor getProfessor(Long id){
        return professorsRepository.findById(id).get();
    }
    public void addProfessor(Professor professor){
        professorsRepository.save(professor);
    }
    public Professor getProfessorByDni(String dni){
        return professorsRepository.findByDni(dni);
    }
    public void deleteProfessor(Long id){
        professorsRepository.deleteById(id);
    }
}
