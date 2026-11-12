package desplanches.CadastroDeFuncionarios.funcionarios;

import desplanches.CadastroDeFuncionarios.common.RecursoNaoEncontradoException;
import desplanches.CadastroDeFuncionarios.tarefas.TarefasModel;
import desplanches.CadastroDeFuncionarios.tarefas.TarefasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionariosService {
    private final FuncionariosRepository  funcionariosRepository;
    private final TarefasRepository tarefasRepository;

    public FuncionariosService(FuncionariosRepository funcionariosRepository, TarefasRepository tarefasRepository) {
        this.funcionariosRepository = funcionariosRepository;
        this.tarefasRepository = tarefasRepository;
    }

    public List<FuncionariosDTO> listarFuncionarios(){
        return funcionariosRepository.findAll().stream()
                .map(FuncionariosDTO::fromEntity)
                .toList();
    }

    public FuncionariosDTO listarFuncionariosPorId(Long id){
        return FuncionariosDTO.fromEntity(buscarOuFalhar(id));
    }

    @Transactional
    public FuncionariosDTO criarFuncionario(FuncionariosDTO dto){
        FuncionariosModel funcionario = FuncionariosDTO.toEntity(dto, buscarTarefa(dto.tarefasId()));
        return FuncionariosDTO.fromEntity(funcionariosRepository.save(funcionario));
    }

    @Transactional
    public void deletarFuncionario(Long id){
        funcionariosRepository.delete(buscarOuFalhar(id));
    }

    @Transactional
    public FuncionariosDTO alterarFuncionario(Long id, FuncionariosDTO dto) {
        FuncionariosModel funcionario = buscarOuFalhar(id);
        funcionario.setNome(dto.nome());
        funcionario.setEmail(dto.email());
        funcionario.setUrlImg(dto.urlImg());
        funcionario.setCargo(dto.cargo());
        funcionario.setIdade(dto.idade());
        funcionario.setTarefas(buscarTarefa(dto.tarefasId()));
        return FuncionariosDTO.fromEntity(funcionario);
    }

    private FuncionariosModel buscarOuFalhar(Long id) {
        return funcionariosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Funcionário com o ID " + id + " não existe em nossos registros"));
    }

    private TarefasModel buscarTarefa(Long tarefasId) {
        if (tarefasId == null) return null;
        return tarefasRepository.findById(tarefasId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Tarefa com o ID " + tarefasId + " não existe"));
    }
}
