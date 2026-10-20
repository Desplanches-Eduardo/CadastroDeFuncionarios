package desplanches.CadastroDeFuncionarios.funcionarios;

import org.springframework.stereotype.Component;
@Component
public class FuncionariosMapper {
        public FuncionariosModel map(FuncionariosDTO funcionariosDTO){
            FuncionariosModel funcionariosModel = new FuncionariosModel();
            funcionariosModel.setId(funcionariosDTO.getId());
            funcionariosModel.setNome(funcionariosDTO.getNome());
            funcionariosModel.setUrl_img(funcionariosDTO.getUrl_img());
            funcionariosModel.setCargo(funcionariosDTO.getCargo());
            funcionariosModel.setIdade(funcionariosDTO.getIdade());
            funcionariosModel.setTarefas(funcionariosDTO.getTarefas());

            return funcionariosModel;
        }

    public FuncionariosDTO map(FuncionariosModel funcionariosModel){
        FuncionariosDTO funcionariosDTO = new FuncionariosDTO();
        funcionariosDTO.setId(funcionariosModel.getId());
        funcionariosDTO.setNome(funcionariosModel.getNome());
        funcionariosDTO.setUrl_img(funcionariosModel.getUrl_img());
        funcionariosDTO.setCargo(funcionariosModel.getCargo());
        funcionariosDTO.setIdade(funcionariosModel.getIdade());
        funcionariosDTO.setTarefas(funcionariosModel.getTarefas());

        return funcionariosDTO;
    }

}
