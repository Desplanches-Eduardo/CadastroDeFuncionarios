package desplanches.CadastroDeFuncionarios.tarefas;

import desplanches.CadastroDeFuncionarios.funcionarios.FuncionariosModel;
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
    private final TarefasService tarefasService;

    public TarefasController(TarefasService tarefasService) {
        this.tarefasService = tarefasService;
    }

    @PostMapping("/criar")
    @Operation(summary = "Cria uma nova tarefa", description = "Essa rota cria uma nova tarefa e insere no banco de dados")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso" ),
            @ApiResponse(responseCode = "400", description = "Erro na criação de tarefa" ),
    })
    public ResponseEntity<String> criarTarefa(@RequestBody TarefasDTO tarefasDTO){
        TarefasDTO tarefaCriada = tarefasService.criarTarefa(tarefasDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Tarefa criada com sucesso: " + tarefaCriada.getNome() + "(ID): " + tarefaCriada.getId());
    }

    @GetMapping("/listar")
    @Operation(summary = "Lista todas as tarefas", description = "Essa rota lista todas as tarefas do banco de dados")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Lista todas as tarefas em JSON" ),
            @ApiResponse(responseCode = "400", description = "Erro na criação de tarefas" ),
    })

    public ResponseEntity<List<TarefasDTO>> listarMissoes(){
        List<TarefasDTO> tarefas = tarefasService.listarTarefas();
        return ResponseEntity.ok(tarefas);
    }


    @GetMapping("/listar/{id}")
    @Operation(summary = "Lista a tarefa por id", description = "Essa rota lista uma tarefa pelo seu id")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada com sucesso" ),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada." ),
    })
    public ResponseEntity<?> listarMissoesPorId(
            @Parameter(description = "Usuário manda id no caminho da requisição")
            @PathVariable Long id){
        TarefasDTO tarefas =  tarefasService.listarTarefasPorId(id);
        if(tarefas != null){
            return ResponseEntity.ok(tarefas);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tarefa não encontrada. Tarefa com o ID: " + id + " não existe em nossos registros" );
        }
    }

    @PutMapping("/alterar/{id}")
    @Operation(summary = "Altera a tarefa por id", description = "Essa rota altera uma tarefa pelo seu id")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Tarefa alterada com sucesso" ),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada." ),
    })
    public ResponseEntity<?> alterarTarefas(
            @Parameter(description = "Usuário manda id no caminho da requisição")
            @PathVariable Long id,
            @Parameter(description = "Usuário manda os dados da tarefa a ser atualizada no corpo da requisição")
            @RequestBody TarefasDTO tarefaAlterada){
        TarefasDTO tarefa = tarefasService.alterarTarefa(id, tarefaAlterada);
        if (tarefa != null){
            return ResponseEntity.ok(tarefa);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tarefa não encontrada. Tarefa com o ID: " + id + " não existe em nossos registros");
        }
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(summary = "Deleta a tarefa por id", description = "Essa rota deleta uma tarefa pelo seu id")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Tarefa deletada com sucesso" ),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada." ),
            @ApiResponse(responseCode = "409", description = "Tarefa com ninjas vinculados" ),
    })
    public ResponseEntity<String> deletarTarefa(
            @Parameter(description = "Usuário manda id no caminho da requisição")
            @PathVariable Long id){
        TarefasDTO tarefa = tarefasService.listarTarefasPorId(id);
        if (tarefa == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tarefa não encontrada. Tarefa com o ID: " + id + " não existe em nossos registros");
        }
        List<FuncionariosModel> funcionarios = tarefa.getFuncionarios();
        if (funcionarios != null && !funcionarios.isEmpty()){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Tarefa não deletada: " + funcionarios.size() + " funcionários(s) ainda estão vinculados a ela");
        }
        tarefasService.deletarTarefa(id);
        return ResponseEntity.ok("A tarefa com ID " + id + " foi deletada com sucesso");
    }
}
