package desplanches.CadastroDeFuncionarios.funcionarios;

import desplanches.CadastroDeFuncionarios.tarefas.TarefasDTO;
import desplanches.CadastroDeFuncionarios.tarefas.TarefasModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FuncionariosService {
    private final FuncionariosRepository  funcionariosRepository;
    private final FuncionariosMapper funcionariosMapper;

    public FuncionariosService(FuncionariosRepository funcionariosRepository, FuncionariosMapper funcionariosMapper) {
        this.funcionariosRepository = funcionariosRepository;
        this.funcionariosMapper = funcionariosMapper;
    }

    public List<FuncionariosDTO> listarFuncionarios(){
        List<FuncionariosModel> funcionarios = funcionariosRepository.findAll();
        return funcionarios.stream()
                .map(funcionariosMapper::map)
                .collect(Collectors.toList());
    }

    public FuncionariosDTO listarFuncionariosPorId(Long id){
        Optional<FuncionariosModel> funcionariosPorId = funcionariosRepository.findById(id);
        return funcionariosPorId.map(funcionariosMapper::map).orElse(null);
    }

    public FuncionariosDTO criarFuncionario(FuncionariosDTO funcionariosDTO){
        FuncionariosModel funcionario= funcionariosMapper.map(funcionariosDTO);
        funcionario = funcionariosRepository.save(funcionario);
        return funcionariosMapper.map(funcionario);
    }

    public void deletarfuncionario(Long id){
        funcionariosRepository.deleteById(id);
    }

    public FuncionariosDTO alterarFuncionario(Long id, FuncionariosDTO funcionariosDTO){
        Optional<FuncionariosModel> funcionarioid = funcionariosRepository.findById(id);
        if (funcionarioid.isPresent()){
            FuncionariosModel funcionarioAlterado = funcionariosMapper.map(funcionariosDTO);
            funcionarioAlterado.setId(id);
            FuncionariosModel missaoSalva = funcionariosRepository.save(funcionarioAlterado);
            return funcionariosMapper.map(missaoSalva);
        }
        return null;
    }
}
