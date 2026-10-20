package desplanches.CadastroDeFuncionarios.tarefas;

import desplanches.CadastroDeFuncionarios.funcionarios.FuncionariosModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarefasDTO {
    private Long id;
    private String nome;
    private String dificuldade;
    private List<FuncionariosModel> funcionarios;
}
