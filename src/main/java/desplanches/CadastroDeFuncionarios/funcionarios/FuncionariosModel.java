package desplanches.CadastroDeFuncionarios.funcionarios;

import desplanches.CadastroDeFuncionarios.tarefas.TarefasModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_cadastro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "tarefas")
public class FuncionariosModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @Column(unique = true)
    private String email;
    @Column(name = "Url_img")
    private String url_img;
    private String cargo;
    private int idade;
    @ManyToOne
    @JoinColumn(name = "tarefa_id")
    private TarefasModel tarefas;
}
