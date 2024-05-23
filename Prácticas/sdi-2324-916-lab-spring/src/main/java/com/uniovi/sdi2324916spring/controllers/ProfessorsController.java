package com.uniovi.sdi2324916spring.controllers;

import com.uniovi.sdi2324916spring.entities.Professor;
import com.uniovi.sdi2324916spring.services.ProfessorsService;
import com.uniovi.sdi2324916spring.validators.AddProfessorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfessorsController {

    private final ProfessorsService professorsService;
    private final AddProfessorValidator addProfessorValidator;

    public ProfessorsController(ProfessorsService professorsService,AddProfessorValidator addProfessorValidator){
        this.professorsService = professorsService;
        this.addProfessorValidator = addProfessorValidator;
    }

    @RequestMapping("/professor/list")
    public String getList(Model model){
        model.addAttribute("professorsList",professorsService.getProfessors());
        return "professor/list";
    }
    @RequestMapping(value = "/professor/add",method = RequestMethod.GET)
    public String getProfessors(Model model)
    {
        model.addAttribute("professor",new Professor());
        return "professor/add";
    }

    @RequestMapping(value = "/professor/add", method = RequestMethod.POST)
    public String setProfessor(@Validated Professor professor, BindingResult result){
        this.addProfessorValidator.validate(professor,result);
        if(result.hasErrors()){
            return "professor/add";
        }
        professorsService.addProfessor(professor);
        return "redirect:/professor/list";
    }
    @RequestMapping("/professor/details/{id}")
    public String getDetail(Model model,@PathVariable Long id){
        model.addAttribute("professor",professorsService.getProfessor(id));
        return "/professor/list";
    }

    @RequestMapping("professor/delete/{id}")
    public  String deleteProfessor(@PathVariable Long id){
        professorsService.deleteProfessor(id);
        return "redirect:/professor/list";
    }
}
