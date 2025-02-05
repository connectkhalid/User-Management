package com.khalid.anwargroup.form.validation;

import com.khalid.anwargroup.util.ParameterCheckInDatabaseUtil;
import com.khalid.anwargroup.util.ParameterCheckUtil;
import com.khalid.anwargroup.form.annotation.MailAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@Slf4j
public class MailAddressValidator implements ConstraintValidator<MailAddress, String> {
    @Autowired
    ParameterCheckUtil parameterCheckUtil;
    @Autowired
    ParameterCheckInDatabaseUtil parameterCheckInDatabaseUtil;

    @Override
    public boolean isValid(String mailAddress, ConstraintValidatorContext cvc) {
        log.info("START isValid");
        if (! StringUtils.hasText(mailAddress)) {
            log.info("END isValid parameter is empty");
            return true;
        }

        boolean result = parameterCheckUtil.checkMail(mailAddress);
        if(result){
            result = parameterCheckInDatabaseUtil.checkMailinDatabase(mailAddress);
        }
        log.info("END isValid. result:{}", result);
        return result;
    }
}
