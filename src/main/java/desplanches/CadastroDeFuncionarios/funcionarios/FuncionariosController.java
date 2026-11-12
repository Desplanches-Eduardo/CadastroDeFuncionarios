package desplanches.CadastroDeFuncionarios.funcionarios;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
public class FuncionariosController {

    private final FuncionariosService service;

    public FuncionariosController(FuncionariosService service) {
        this.service = service;
    }

    @PostMapping("/criar")
    @Operation(summary = "Cria um novo funcionário", description = "Essa rota cria um novo funcionário e insere no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na criação do funcionário"),
            @ApiResponse(responseCode = "404", description = "Tarefa informada não encontrada")
    })
    public ResponseEntity<FuncionariosDTO> criarFuncionario(@RequestBody FuncionariosDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarFuncionario(dto));
    }

    @GetMapping("/listar")
    @Operation(summary = "Lista todos os funcionários", description = "Essa rota lista todos os funcionários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna todos os funcionários"),
    })
    public ResponseEntity<List<FuncionariosDTO>> listarFuncionarios() {
        List<FuncionariosDTO> funcionarios = service.listarFuncionarios();
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/listar/{id}")
    @Operation(summary = "Lista o funcionário por id", description = "Essa rota lista um funcionário pelo seu id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado."),
    })
    public ResponseEntity<FuncionariosDTO> listarFuncionarioPorId(
            @Parameter(description = "Usuário manda id no caminho da requisição")
            @PathVariable Long id) {
        FuncionariosDTO funcionario = service.listarFuncionariosPorId(id);
        return ResponseEntity.ok(funcionario);
    }

    @PutMapping("/alterar/{id}")
    @Operation(summary = "Altera um funcionário", description = "Essa rota altera um funcionário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Funcionário ou tarefa informada não encontrados"),
    })
    public ResponseEntity<FuncionariosDTO> alterarFuncionario(
            @Parameter(description = "Usuário manda o id no caminho da requisição")
            @PathVariable Long id,
            @Parameter(description = "Usuário manda os dados do funcionário a ser atualizado no corpo da requisição")
            @RequestBody FuncionariosDTO funcionarioAlterado) {
        FuncionariosDTO funcionario = service.alterarFuncionario(id, funcionarioAlterado);
        return ResponseEntity.ok(funcionario);
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(summary = "Deleta um funcionário", description = "Essa rota deleta o funcionário do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "funcionário é apagado"),
            @ApiResponse(responseCode = "404", description = "funcionário não encontrado.")
    })
    public ResponseEntity<Void> deletarFuncionario(
            @Parameter(description = "Usuário manda o id no caminho da requisição")
            @PathVariable Long id) {
        service.deletarFuncionario(id);
        return ResponseEntity.noContent().build();
    }
}


