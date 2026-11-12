package desplanches.CadastroDeFuncionarios.tarefas;

import desplanches.CadastroDeFuncionarios.common.RecursoNaoEncontradoException;
import desplanches.CadastroDeFuncionarios.common.RegraDeNegocioException;
import desplanches.CadastroDeFuncionarios.funcionarios.FuncionariosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TarefasService {

    private final TarefasRepository tarefasRepository;
    private final FuncionariosRepository funcionariosRepository;

    public TarefasService(TarefasRepository tarefasRepository, FuncionariosRepository funcionariosRepository) {
        this.tarefasRepository = tarefasRepository;
        this.funcionariosRepository = funcionariosRepository;
    }

    public List<TarefasDTO> listarTarefas(){
       return tarefasRepository.findAll().stream()
               .map(TarefasDTO::fromEntity)
               .toList();
    }

    public TarefasDTO listarTarefasPorId(Long id){
        return TarefasDTO.fromEntity(buscarOuFalhar(id));
    }

    @Transactional
    public TarefasDTO criarTarefa(TarefasDTO dto){
        TarefasModel tarefa = TarefasDTO.toEntity(dto);
        return TarefasDTO.fromEntity(tarefasRepository.save(tarefa));
    }

    @Transactional
    public void deletarTarefa(Long id){
        TarefasModel tarefa = buscarOuFalhar(id);
        if (funcionariosRepository.existsByTarefasId(id)){
            throw new RegraDeNegocioException(
                    "Tarefa não deletada: há funcionários vinculados a ela");
        }
        tarefasRepository.delete(tarefa);
    }

    @Transactional
    public TarefasDTO alterarTarefa(Long id, TarefasDTO dto){
        TarefasModel tarefa = buscarOuFalhar(id);
        tarefa.setNome(dto.nome());
        tarefa.setDificuldade(dto.dificuldade());
        return TarefasDTO.fromEntity(tarefa);
    }

    private TarefasModel buscarOuFalhar(Long id) {
        return tarefasRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Tarefa com o ID " + id + " não existe em nossos registros"));
    }

}
