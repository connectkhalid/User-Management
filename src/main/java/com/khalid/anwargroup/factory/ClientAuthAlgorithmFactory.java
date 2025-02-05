package com.khalid.anwargroup.factory;

import com.auth0.jwt.algorithms.Algorithm;
import com.khalid.anwargroup.config.jwt.ClientAuthConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientAuthAlgorithmFactory extends JwtAlgorithmFactory{
    private final ClientAuthConfig clientAuthConfig;
    @Override
    public Optional<Algorithm> create() {
        return Optional.of(Algorithm.HMAC512(clientAuthConfig.getSecretKey()));
    }
}
