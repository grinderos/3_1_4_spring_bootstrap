package ru.kata.spring.bootstrap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.kata.spring.bootstrap.model.User;

@Component
public class UserValidator implements Validator {

    private UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.getUsername().length() < 3 || user.getUsername().length() > 32) {
            errors.rejectValue("username", "", "Имя пользователя должно быть длиной от 3 до 32 символов");
        }
        if (userService.findByUsername(user.getUsername()) != null) {
//            errors.rejectValue("username", "Duplicate.userForm.username");
            errors.rejectValue("username", "", "Пользователь с таким именем уже существует");
        }

//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "", "Поле не может быть пустым");
        if (user.getPassword().length() < 4 || user.getPassword().length() > 32) {
//            errors.rejectValue("password", "Size.userForm.password");
            errors.rejectValue("password", "", "Пароль должен быть от 4 до 32 символов");
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
//            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
            errors.rejectValue("passwordConfirm", "", "Пароли не совпадают");
        }
    }
}
