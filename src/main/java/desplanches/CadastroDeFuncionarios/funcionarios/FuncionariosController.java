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
    private final FuncionariosService funcionariosService;

    public FuncionariosController(FuncionariosService funcionariosService) {
        this.funcionariosService = funcionariosService;
    }

    @PostMapping("/criar")
    @Operation(summary = "Cria um novo funcionário", description = "Essa rota cria um novo funcionário e insere no banco de dados")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso" ),
            @ApiResponse(responseCode = "400", description = "Erro na criação do funcionário" ),
    })
    public ResponseEntity<String> criarFuncionario(@RequestBody FuncionariosDTO funcionariosDTO){

        FuncionariosDTO funcionarioCriado = funcionariosService.criarFuncionario(funcionariosDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Funcionário criado com sucesso: " + funcionarioCriado.getNome() + "(ID): " + funcionarioCriado.getId());

    }

    @GetMapping("/listar")
    @Operation(summary = "Lista todos os funcionários", description = "Essa rota lista todos os funcionários")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Retorna todos os funcionários" ),
            @ApiResponse(responseCode = "400", description = "Não retorna os funcionários" ),
    })
    public ResponseEntity<List<FuncionariosDTO>> listarFuncionarios(){
        List<FuncionariosDTO> funcionarios =  funcionariosService.listarFuncionarios();
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/listar/{id}")
    @Operation(summary = "Lista o funcionário por id", description = "Essa rota lista um funcionário pelo seu id")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Funcionário encontrado com sucesso" ),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado." ),
    })
    public ResponseEntity<?> listarFuncionárioporId(
            @Parameter(description = "Usuário manda id no caminho da requisição")
            @PathVariable Long id){
        FuncionariosDTO funcionario =  funcionariosService.listarFuncionariosPorId(id);
        if (funcionario != null){
            return ResponseEntity.ok(funcionario);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Funcionário não encontrado. Funcionário com o ID: " + id + " não existe em nossos registros" );
        }
    }

    @PutMapping("/alterar/{id}")
    @Operation(summary = "Altera um funcionário", description = "Essa rota altera um funcionário")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Funcionário alterado com sucesso" ),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado." ),
    })
    public ResponseEntity<?> alterarFuncionario(
            @Parameter(description = "Usuário manda o id no caminho da requisição")
            @PathVariable Long id,
            @Parameter(description = "Usuário manda os dados do funcionário a ser atualizado no corpo da requisição")
            @RequestBody FuncionariosDTO funcionarioAlterado){
        FuncionariosDTO funcionario = funcionariosService.alterarFuncionario(id, funcionarioAlterado);
        if (funcionario!= null) {
            return ResponseEntity.ok(funcionario);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Funcionário não encontrado. Funcionário com o ID: " + id + " não existe em nossos registros");
        }
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(summary = "Deleta um funcionário", description = "Essa rota deleta o funcionário do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "funcionário é apagado"),
            @ApiResponse(responseCode = "404", description = "funcionário não encontrado.")
    })
    public ResponseEntity<String> deletarFuncionario(
            @Parameter(description = "Usuário manda o id no caminho da requisição")
            @PathVariable Long id){
        if (funcionariosService.listarFuncionariosPorId(id) != null){
            funcionariosService.deletarfuncionario(id);
            return ResponseEntity.ok("Funcionário com ID " + id + " deletado com sucesso");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Funcionário não encontrado. Funcionário com o ID: " + id + " não existe em nossos registros");
        }
    }

}
