package com.khalid.anwargroup.factory;

import com.khalid.anwargroup.provider.JwtWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtWrapperFactory {
    public JwtWrapper create(JwtAlgorithmFactory jwtAlgorithmFactory) {
        return new JwtWrapper(jwtAlgorithmFactory);
    }
}
