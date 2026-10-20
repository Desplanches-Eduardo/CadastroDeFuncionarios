package desplanches.CadastroDeFuncionarios.tarefas;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TarefasService {

    private final TarefasRepository tarefasRepository;
    private final TarefasMapper tarefasMapper;

    public TarefasService(TarefasRepository tarefasRepository, TarefasMapper tarefasMapper) {
        this.tarefasRepository = tarefasRepository;
        this.tarefasMapper = tarefasMapper;
    }

    public List<TarefasDTO> listarTarefas(){
        List<TarefasModel> tarefas = tarefasRepository.findAll();
        return tarefas.stream()
                .map(tarefasMapper::map)
                .collect(Collectors.toList());
    }

    public TarefasDTO listarTarefasPorId(Long id){
        Optional<TarefasModel> tarefasPorId = tarefasRepository.findById(id);
        return tarefasPorId.map(tarefasMapper::map).orElse(null);
    }

    public TarefasDTO criarTarefa(TarefasDTO tarefasDTO){
        TarefasModel tarefas= tarefasMapper.map(tarefasDTO);
        tarefas = tarefasRepository.save(tarefas);
        return tarefasMapper.map(tarefas);
    }

    public void deletarTarefa(Long id){
        tarefasRepository.deleteById(id);
    }

    public TarefasDTO alterarTarefa(Long id, TarefasDTO tarefasDTO){
        Optional<TarefasModel> tarefaid = tarefasRepository.findById(id);
        if (tarefaid.isPresent()){
            TarefasModel tarefaAlterada = tarefasMapper.map(tarefasDTO);
            tarefaAlterada.setId(id);
            TarefasModel missaoSalva = tarefasRepository.save(tarefaAlterada);
            return tarefasMapper.map(missaoSalva);
        }
        return null;
    }
}
