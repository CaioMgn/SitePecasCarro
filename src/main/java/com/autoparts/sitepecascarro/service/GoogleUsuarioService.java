package com.autoparts.sitepecascarro.service;

import com.autoparts.sitepecascarro.entity.Papel;
import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleUsuarioService extends OidcUserService {

    private final UsuarioRepository usuarioRepository;

    public GoogleUsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Deixa o Spring buscar os dados no Google
        OidcUser googleUser = super.loadUser(userRequest);

        String email = googleUser.getEmail().trim().toLowerCase();
        String nome = googleUser.getFullName();

        // 2. Procura no nosso banco; se não existir, cria como cliente
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseGet(() -> usuarioRepository.save(
                        new Usuario(nome != null ? nome : email, email, null, Papel.USER)));

        // 3. O papel vem do NOSSO banco, não do Google
        List<SimpleGrantedAuthority> papeis =
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPapel().name()));

        return new DefaultOidcUser(papeis, googleUser.getIdToken(), googleUser.getUserInfo(), "email");
    }
}