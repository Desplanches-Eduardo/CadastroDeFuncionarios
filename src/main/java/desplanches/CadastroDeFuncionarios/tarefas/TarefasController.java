package desplanches.CadastroDeFuncionarios.tarefas;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefasController {
    private final TarefasService service;

    public TarefasController(TarefasService service) {
        this.service = service;
    }

    @PostMapping("/criar")
    @Operation(summary = "Cria uma nova tarefa", description = "Essa rota cria uma nova tarefa e insere no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    public ResponseEntity<TarefasDTO> criarTarefa(@RequestBody TarefasDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarTarefa(dto));
    }

    @GetMapping("/listar")
    @Operation(summary = "Lista todas as tarefas", description = "Essa rota lista todas as tarefas do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista todas as tarefas em JSON"),
    })
    public ResponseEntity<List<TarefasDTO>> listarTarefas() {
        List<TarefasDTO> tarefas = service.listarTarefas();
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/listar/{id}")
    @Operation(summary = "Lista a tarefa por id", description = "Essa rota lista uma tarefa pelo seu id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada."),
    })
    public ResponseEntity<TarefasDTO> listarTarefasPorId(
            @Parameter(description = "Usuário manda id no caminho da requisição")
            @PathVariable Long id) {
        TarefasDTO tarefas = service.listarTarefasPorId(id);
        return ResponseEntity.ok(tarefas);
    }

    @PutMapping("/alterar/{id}")
    @Operation(summary = "Altera a tarefa por id", description = "Essa rota altera uma tarefa pelo seu id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa alterada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada."),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    public ResponseEntity<TarefasDTO> alterarTarefa(
            @Parameter(description = "Usuário manda id no caminho da requisição")
            @PathVariable Long id,
            @Parameter(description = "Usuário manda os dados da tarefa a ser atualizada no corpo da requisição")
            @RequestBody TarefasDTO tarefaAlterada) {
        TarefasDTO tarefa = service.alterarTarefa(id, tarefaAlterada);
        return ResponseEntity.ok(tarefa);
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(summary = "Deleta a tarefa por id", description = "Essa rota deleta uma tarefa pelo seu id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada."),
            @ApiResponse(responseCode = "409", description = "Tarefa com funcionários vinculados"),
    })
    public ResponseEntity<Void> deletarTarefa(
            @Parameter(description = "Usuário manda id no caminho da requisição")
            @PathVariable Long id) {
        service.deletarTarefa(id);
        return ResponseEntity.noContent().build();
    }
}
