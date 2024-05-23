package com.uniovi.sdi2324916spring.validators;

import com.uniovi.sdi2324916spring.entities.Professor;
import com.uniovi.sdi2324916spring.services.ProfessorsService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class AddProfessorValidator implements Validator {
    private final ProfessorsService professorsService;

    public AddProfessorValidator(ProfessorsService professorsService){
        this.professorsService = professorsService;
    }
    @Override
    public boolean supports(Class<?> clazz) {
        return Professor.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Professor professor = (Professor) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"dni","Error.empty");
        if(professor.getDni().length() != 9 && !professor.getDni().substring(professor.getDni().length() - 1).matches("[A-Z]*")){
            errors.rejectValue("dni","Error.professor.dni.format");
        }
        if(professorsService.getProfessorByDni(professor.getDni()) != null){
            errors.rejectValue("dni","Error.professor.dni.duplicate");
        }
        if(professor.getName().startsWith(" ") || professor.getName().endsWith(" ")){
            errors.rejectValue("name","Error.professor.name.spaces");
        }
        if(professor.getName().startsWith(" ") || professor.getName().endsWith(" ")){
            errors.rejectValue("lastname","Error.professor.lastname.spaces");
        }
    }
}
