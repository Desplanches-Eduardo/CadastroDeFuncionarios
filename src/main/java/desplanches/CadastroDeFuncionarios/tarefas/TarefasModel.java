package desplanches.CadastroDeFuncionarios.tarefas;
import com.fasterxml.jackson.annotation.JsonIgnore;
import desplanches.CadastroDeFuncionarios.funcionarios.FuncionariosModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tb_tarefas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarefasModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String dificuldade;
    @OneToMany(mappedBy = "tarefas")
    @JsonIgnore
    private List<FuncionariosModel> funcionarios;
}
