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
    @Column(name = "url_img")
    private String urlImg;
    private String cargo;
    private int idade;
    @ManyToOne
    @JoinColumn(name = "tarefa_id")
    private TarefasModel tarefas;
}
