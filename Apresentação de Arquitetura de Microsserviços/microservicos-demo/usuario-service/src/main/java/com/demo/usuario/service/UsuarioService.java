package com.demo.usuario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.usuario.model.Usuario;
import com.demo.usuario.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @jakarta.annotation.PostConstruct
    public void inicializarDados() {
        if (repository.count() == 0) {
            repository.save(new Usuario("Ana Silva", "ana@email.com"));
            repository.save(new Usuario("Bruno Costa", "bruno@email.com"));
            repository.save(new Usuario("Carla Mendes", "carla@email.com"));
            repository.save(new Usuario("Thiago Borsatto", "thiago@email.com"));
            System.out.println("[usuario-service] ✅ Usuários de exemplo carregados!");
        }
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Usuario salvar(Usuario usuario) {
        return repository.save(usuario);
    }
}
