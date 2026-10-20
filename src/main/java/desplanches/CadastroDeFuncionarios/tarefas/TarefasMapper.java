package desplanches.CadastroDeFuncionarios.tarefas;

import org.springframework.stereotype.Component;

@Component
public class TarefasMapper {
    public TarefasModel map(TarefasDTO tarefasDTO){
        TarefasModel tarefasModel = new TarefasModel();
        tarefasModel.setId(tarefasDTO.getId());
        tarefasModel.setNome(tarefasDTO.getNome());
        tarefasModel.setDificuldade(TarefasDTO.getDificuldade());


        return tarefasModel;
    }
    public TarefasDTO map(TarefasModel tarefasModel){
        TarefasDTO funcionariosDTO = new TarefasDTO();
        tarefasDTO.setId(tarefasModel.getId());
        tarefasDTO.setNome(tarefasModel.getNome());
        tarefasDTO.setDificuldade(tarefasModel.getDificuldade());
        return funcionariosDTO;
    }
}
