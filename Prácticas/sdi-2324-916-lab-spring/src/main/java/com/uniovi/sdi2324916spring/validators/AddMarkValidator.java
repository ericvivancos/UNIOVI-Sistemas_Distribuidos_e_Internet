package com.uniovi.sdi2324916spring.validators;

import com.uniovi.sdi2324916spring.entities.Mark;
import com.uniovi.sdi2324916spring.services.MarksService;
import com.uniovi.sdi2324916spring.services.UsersService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AddMarkValidator implements Validator {
    private final MarksService marksService;

    public AddMarkValidator(MarksService marksService){
        this.marksService=marksService;
    }
    @Override
    public boolean supports(Class<?> clazz) {
        return Mark.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Mark mark = (Mark) target;
        if(mark.getScore() < 0 || mark.getScore() > 10){
            errors.rejectValue("score","Error.score.range");
        }
        if(mark.getDescription().length() < 20){
            errors.rejectValue("description","Error.description.length");
        }

    }
}
