package desplanches.CadastroDeFuncionarios.tarefas;


public record TarefasDTO(Long id, String nome, String dificuldade) {
    public static TarefasDTO fromEntity(TarefasModel t){
        return new TarefasDTO(
                t.getId(), t.getNome(), t.getDificuldade()
        );
    }

    public static TarefasModel toEntity(TarefasDTO dto){
        TarefasModel model = new TarefasModel();
        model.setNome(dto.nome);
        model.setDificuldade(dto.dificuldade);
        return model;
    }
}
