package com.khalid.anwargroup.form.validation;

import com.khalid.anwargroup.util.ParameterCheckUtil;
import com.khalid.anwargroup.form.annotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@Slf4j
public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Autowired
    ParameterCheckUtil parameterCheckUtil;

    @Override
    public void initialize(Password password) {
        //do nothing
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext cvc) {
        log.info("START isValid.");

        if (!StringUtils.hasText(password)) {
            log.info("END isValid parameter is empty");
            return true;
        }

        boolean result = parameterCheckUtil.checkPassword(password);
        log.info("END isValid: {}", result);
        return result;
    }
}
