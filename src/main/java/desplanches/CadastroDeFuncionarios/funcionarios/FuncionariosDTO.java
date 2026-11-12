package desplanches.CadastroDeFuncionarios.funcionarios;


import desplanches.CadastroDeFuncionarios.tarefas.TarefasModel;

public record FuncionariosDTO(
        Long id, String nome, String email, String urlImg,
        String cargo, int idade, Long tarefasId
){
    public static FuncionariosDTO fromEntity(FuncionariosModel f) {
        Long tarefaId = f.getTarefas() != null ? f.getTarefas().getId() : null;
        return new FuncionariosDTO(
                f.getId(), f.getNome(), f.getEmail(), f.getUrlImg(),
                f.getCargo(),f.getIdade(), tarefaId
        );
    }

    public static FuncionariosModel toEntity(FuncionariosDTO dto, TarefasModel tarefa) {
        FuncionariosModel f = new FuncionariosModel();
        f.setNome(dto.nome());
        f.setEmail(dto.email());
        f.setUrlImg(dto.urlImg());
        f.setCargo(dto.cargo());
        f.setIdade(dto.idade());
        f.setTarefas(tarefa);
        return f;
    }

}
