#  :pushpin:Checklist Obrigatório

- O trabalho possui um índice direto implementado com a tabela hash extensível? **SIM** :white_check_mark:
- A operação de inclusão insere um novo registro no fim do arquivo e no índice e retorna o ID desse registro? **SIM** :white_check_mark:
- A operação de busca retorna os dados do registro, após localizá-lo por meio do índice direto? **SIM** :white_check_mark:
- A operação de alteração altera os dados do registro e trata corretamente as reduções e aumentos no espaço do registro? **SIM** :white_check_mark:
- A operação de exclusão marca o registro como excluído e o remove do índice direto? **SIM** :white_check_mark:
- O trabalho está funcionando corretamente? **SIM** :white_check_mark:
- O trabalho está completo? **SIM** :white_check_mark:
- O trabalho é original e não a cópia de um trabalho de outro grupo? **SIM** :white_check_mark:

---

# :pushpin:Sobre o Trabalho

O objetivo do trabalho é implementar um sistema de gerenciamento de Tarefas utilizando uma tabela hash extensível para o índice direto, permitindo operações de inclusão, busca, alteração e exclusão de registros, com armazenamento eficiente em memória secundária por meio de um arquivo em bytes e tratamento adequado para remoções lógicas e alterações de tamanho.

---

# :pushpin:Estruturação Geral

## Estrutura de Dados na Classe

- **id (INT)**: Identificador único para cada instância da classe.
- **Nome (STRING)**: Nome descritivo associado à instância.
- **Data de Criação (LOCALDATE)**: Data em que a instância foi criada.
- **Data de Conclusão (LOCALDATE)**: Data em que a instância foi concluída.
- **Status (TIPO 'STATUS')**: Indicador textual do estado atual da instância (por exemplo, "pendente", "progresso", "concluído").
- **Prioridade (BYTE)**: Nível de prioridade da instância, representado como um valor numérico.

## Estrutura do Registro

Cada objeto será representado como um vetor de bytes, preparado para ser armazenado na memória secundária (em forma de arquivo). A conversão dos registros em bytes será realizada usando as classes `ByteArrayInputStream`, `ByteArrayOutputStream`, `DataInputStream` e `DataOutputStream`, que facilitam a leitura e escrita dos dados no formato binário adequado. A estrutura do vetor de bytes será definida da seguinte forma:

- **id** = (INT)
- **Nome** = (STRING UTF-8)
- **Data de Criação** = (INT)
- **Data de Conclusão** = (INT)
- **Status** = (STRING UTF-8)
- **Prioridade** = (BYTE)

## Estrutura do Arquivo

O arquivo de armazenamento conterá um cabeçalho e uma sequência de registros. A estrutura do arquivo será organizada da seguinte forma:

- **Cabeçalho**:
  - Último ID Registrado: Um inteiro que armazena o último ID utilizado até o momento.

- **Registros**:
  - Lápide (BYTE): Indicador de remoção lógica, onde 0 significa ativo e 1 significa removido.
  - Tamanho do Registro (SHORT): Um valor de 2 bytes que indica o tamanho do registro em bytes.
  - Registro (Vetor de Bytes): A representação binária do objeto, conforme descrito na estrutura de registro.

[Último ID] -> [Lápide 1] -> [Tamanho do Registro 1] -> [Registro 1 em Bytes] -> [Lápide 2] -> [Tamanho do Registro 2] -> [Registro 2 em Bytes] -> ... -> EOF


---

# :pushpin:Classes Criadas e seus Métodos

## Classe Arquivo

A classe `Arquivo<T>` gerencia registros genéricos que implementam a interface `Registro`, realizando operações de CRUD (criar, ler, atualizar e deletar) em um arquivo de bytes. Ela utiliza a tabela hash extensível (`HashExtensivel<ParEnderecoId>`) para armazenar índices diretos.

### Construtor

- **Arquivo(String na, Constructor<T> c)**: Inicializa o arquivo de dados e o índice hash extensível. Se o arquivo não existir, cria o arquivo e o cabeçalho.

### Métodos

- `int create(T obj)`: Cria um novo registro no arquivo, atribui um novo ID, armazena o registro no final do arquivo, e insere a referência no índice hash.
- `T read(int id)`: Lê um registro a partir de seu ID, utilizando o índice hash para localizar o endereço no arquivo.
- `boolean delete(int id)`: Marca um registro como excluído (usando uma lápide) e remove sua entrada do índice hash.
- `boolean update(T novoObj)`: Atualiza um registro existente. Se o novo registro for maior que o anterior, move-o para o final do arquivo e ajusta o índice hash.
- `void close()`: Fecha o arquivo de dados e o índice hash associado.

---

## Classe Tarefas

A classe `Tarefas` implementa a interface `Registro` e representa uma tarefa com informações como o nome, a data de criação, a data de conclusão (se houver), e seu status (usando o enum `Status`).

### Construtores

- **Tarefas(int id, String nome, LocalDate createdAt)**: Inicializa uma tarefa com um ID, nome e data de criação definidos. O status inicial é PENDENTE e a data de conclusão (doneAt) é null.
- **Tarefas()**: Inicializa uma tarefa com valores padrão: ID 0, nome vazio, a data de criação como a data atual, status PENDENTE, e doneAt como null.

### Métodos

- `void setStatus(Status status)`: Define o status da tarefa. O status é um valor do enum `Status`, que pode ser PENDENTE, PROGRESSO ou CONCLUIDO.
- `void setDoneAt(LocalDate doneAt)`: Define a data de conclusão (doneAt) da tarefa. Usado apenas quando a tarefa é marcada como CONCLUIDO.

### Serialização e Deserialização

- `byte[] toByteArray()`: Converte o objeto `Tarefas` em um array de bytes para armazenamento. Isso inclui o ID, nome, data de criação (em dias desde o epoch), status, e, se disponível, a data de conclusão (doneAt). Se a data de conclusão for null, o método grava um valor booleano false para indicar que doneAt não existe.
- `void fromByteArray(byte[] b)`: Reconstrói o objeto `Tarefas` a partir de um array de bytes. Lê os dados armazenados, incluindo o ID, nome, data de criação, status, e, se aplicável, a data de conclusão.

### Relacionamento com Status

A enumeração `Status` define três possíveis estados para uma tarefa:

- **PENDENTE**: A tarefa ainda não foi iniciada.
- **PROGRESSO**: A tarefa está em andamento.
- **CONCLUIDO**: A tarefa foi finalizada. Quando uma tarefa é marcada como CONCLUIDO, a data de conclusão (doneAt) deve ser definida.

Esses valores são usados pela classe `Tarefas` para indicar em qual fase a tarefa se encontra e impactam a maneira como a tarefa é exibida ou manipulada.

**OBS**: Os métodos e classes que não foram abordados aqui são autoexplicativos ou seguem o mesmo padrão apresentado em sala de aula, o que acreditamos tornar redundantes suas explicações.

---

# :pushpin:Opinião do Grupo sobre o Desenvolvimento

Dividimos as tarefas deste trabalho de forma democrática entre os quatro integrantes e planejamos alternar as funções em cada trabalho futuro.  
:white_check_mark:Desenvolvimento do programa, Organização e Implementação da Hashtable - João, André  
:white_check_mark:Arquitetura do projeto, Testes e Documentação - Victor, Douglas  

A parte mais desafiadora foi entender o código da tabela hash extensível; embora não fosse necessário compreender todos os detalhes, precisávamos ler o código para adiantar implementação.  
Durante uma das fases de teste, identificamos um problema na classe `Arquivo`, onde os métodos da hashtable não estavam sendo acionados corretamente, mas já corrigimos essa questão.  
Todos os requisitos foram implementados e, como já trabalhamos em grupo anteriormente, todos colaboraram e aprenderam em conjunto. Como grande parte dos códigos foi discutida em sala, apesar dos problemas, não enfrentamos grandes dificuldades na implementação.
