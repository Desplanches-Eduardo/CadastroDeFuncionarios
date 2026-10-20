package desplanches.CadastroDeFuncionarios.funcionarios;

import desplanches.CadastroDeFuncionarios.tarefas.TarefasModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FuncionariosDTO {
    private Long id;
    private String nome;
    private String email;
    private String url_img;
    private String cargo;
    private int idade;
    private TarefasModel tarefas;
}

