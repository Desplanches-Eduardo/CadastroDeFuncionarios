package desplanches.CadastroDeFuncionarios.funcionarios;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionariosRepository extends JpaRepository<FuncionariosModel,Long> {
    boolean existsByTarefasId(Long tarefaId);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}
