#  :pushpin:Checklist Obrigatório

- Há uma rotina de compactação usando o algoritmo LZW para fazer backup dos arquivos? :white_check_mark:
- Há uma rotina de descompactação usando o algoritmo LZW para recuperação dos arquivos? :white_check_mark:
- O usuário pode escolher a versão a recuperar? :white_check_mark:
- Qual foi a taxa de compressão alcançada por esse backup? (Compare o tamanho dos arquivos compactados com os arquivos originais) :white_check_mark: ~50%, o arquivo original tem ~4.5kb e o backup possui ~2kb 
- O trabalho está funcionando corretamente? :white_check_mark:
- O trabalho está completo? :white_check_mark:
- O trabalho é original e não a cópia de um trabalho de um colega? :white_check_mark:

---

# :pushpin:Sobre o Trabalho

O objetivo deste trabalho é implementar um sistema de gerenciamento de tarefas com backup, que utilize uma tabela hash extensível para o índice direto e uma árvore B+ para indexação do relacionamento 1:N entre tarefas e categorias. Também envolve implementar um índice invertido para busca de tarefas, usando as palavras da descrição e ordenando os resultados por relevância (TFxIDF). Criar uma entidade de rótulos, armazená-los em uma árvore B+ e implementar CRUD. Além disso, é necessário estabelecer um relacionamento N:N entre tarefas e rótulos, permitindo vinculação e exclusão dinâmica, bem como buscas eficientes por termos, categorias e rótulos.

---

# :pushpin:Estruturação Geral

## Estrutura de Dados na Classe Tarefa

- **id (INT)**: Identificador único para cada instância da classe.
- **Nome (STRING)**: Nome descritivo associado à instância.
- **Data de Criação (LOCALDATE)**: Data em que a instância foi criada.
- **Data de Conclusão (LOCALDATE)**: Data em que a instância foi concluída.
- **Status (TIPO 'STATUS')**: Indicador textual do estado atual da instância (por exemplo, "pendente", "progresso", "concluído").
- **Prioridade (BYTE)**: Nível de prioridade da instância, representado como um valor numérico.
- **id Categoria (INT)**: Chave estrangeira que referencia o id da categoria à qual a tarefa pertence.
- **Descrição (STRING)**: Texto explicativo ou detalhamento adicional associado à tarefa.

## Estrutura do Registro de Tarefa

Cada objeto será representado como um vetor de bytes, preparado para ser armazenado na memória secundária (em forma de arquivo). A conversão dos registros em bytes será realizada usando as classes `ByteArrayInputStream`, `ByteArrayOutputStream`, `DataInputStream` e `DataOutputStream`, que facilitam a leitura e escrita dos dados no formato binário adequado. A estrutura do vetor de bytes será definida da seguinte forma:

- **id** = (INT)
- **Nome** = (STRING UTF-8)
- **Data de Criação** = (INT)
- **Data de Conclusão** = (INT)
- **Status** = (STRING UTF-8)
- **Prioridade** = (BYTE)
- **id Categoria** = (INT)
- **Descricao** = (STRING UTF-8)

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
- `void setIdCategoria(int idCategoria)`: Define o id da categoria a qual a tarefa pertence.
- `String getNomeCategoria(int id)`: Realiza uma busca no arquivo de categorias com base no id e retorna o nome da categoria em questão.
- `void setDescricao(String descricao):` Define a descrição detalhada da tarefa.
- `String getDescricao():` Retorna a descrição detalhada da tarefa.
- `String[] splitDescricao():` Realiza o tratamento e a separação das palavras da descrição em um vetor de strings, removendo acentos, caracteres especiais e transformando tudo em letras minúsculas.

### Serialização e Deserialização

- `byte[] toByteArray()`: Converte o objeto `Tarefas` em um array de bytes para armazenamento. Isso inclui o ID, nome, data de criação (em dias desde o epoch), status, id da categoria e, se disponível, a data de conclusão (doneAt). Se a data de conclusão for null, o método grava um valor booleano false para indicar que doneAt não existe.
- `void fromByteArray(byte[] b)`: Reconstrói o objeto `Tarefas` a partir de um array de bytes. Lê os dados armazenados, incluindo o ID, nome, data de criação, status, id de categoria e, se aplicável, a data de conclusão.

### Relacionamento com Status

A enumeração `Status` define três possíveis estados para uma tarefa:

- **PENDENTE**: A tarefa ainda não foi iniciada.
- **PROGRESSO**: A tarefa está em andamento.
- **CONCLUIDO**: A tarefa foi finalizada. Quando uma tarefa é marcada como CONCLUIDO, a data de conclusão (doneAt) deve ser definida.

Esses valores são usados pela classe `Tarefas` para indicar em qual fase a tarefa se encontra e impactam a maneira como a tarefa é exibida ou manipulada.

## Classe Categoria

A classe `Categoria` implementa a interface `Registro` e representa uma categoria com informações de Nome e ID.

### Construtores

- **Categoria(int i, String n)**: Inicializa uma categoria com os valores de id e nome determinados por parametro.
- **Categoria(String n)**: Inicializa uma categoria com ID padrão "-1" e define o nome com base no valor passado por paramentro.
- **Categoria()**: Inicializa uma categoria com valores padrão: id = -1, nome = "".

### Métodos

- `void setId(int id)`: Determina o id da categoria.
- `int getId()`: Recupera o id da categoria.
- `void setNome(String nome)`: Determina o nome da categoria.
- `String getNome()`: Recupera o Nome da tarefa.
- `String toString()`: Retorna o objeto categoria como uma string.

### Serialização e Deserialização

- `byte[] toByteArray()`: Converte o objeto `Categotia` em um array de bytes para armazenamento. Isso inclui o ID e nome do objeto.
- `void fromByteArray(byte[] b)`: Reconstrói o objeto `Categoria` a partir de um array de bytes. Lê os dados armazenados, incluindo o ID e o nome.

## Classe Rotulo

A classe `Rotulo` implementa a interface `Registro` e é usada para representar um relacionamento entre uma categoria e um status de tarefa. Contém informações como o ID do relacionamento, o ID da categoria e o ID do status.

### Construtores

- **Rotulo()**: Inicializa um objeto Rotulo com os IDs de relacionamento, categoria e status configurados como -1.
- **Rotulo(int categoriaId, int statusId)**: Inicializa um objeto Rotulo com o ID de relacionamento como -1, e os IDs de categoria e status conforme os parâmetros fornecidos.
- **Rotulo(int idRelacionamento, int categoriaId, int statusId)**: Inicializa um objeto Rotulo com os IDs de relacionamento, categoria e status conforme os parâmetros fornecidos.

### Métodos
- `getStatusId()`: Retorna o ID do status associado ao relacionamento.
- `getCategoriaId()`: Retorna o ID da categoria associada ao relacionamento.
- `setId(int id)`: Define o ID de relacionamento.
- `getId()`: Retorna o ID do relacionamento.

 ### Serialização e Deserialização

- `toByteArray()`: Converte o objeto Rotulo para um array de bytes para armazenamento. Os dados incluem o ID de relacionamento, ID de categoria e ID de status.
- `fromByteArray(byte[] b)`: Reconstrói o objeto Rotulo a partir de um array de bytes, validando o tamanho do array antes da deserialização.
- `getStatusName()`: Retorna o nome correspondente ao ID do status associado ao relacionamento, utilizando uma estrutura switch para mapear os valores de status numéricos para seus nomes legíveis.

- Já a classe `ArquivoRotuloStatus`, gerencia rótulos que associam categorias e status, utilizando um arquivo e uma árvore B+ para otimizar buscas. Ela permite criar novos rótulos, registrar relacionamentos entre categorias e status, e associá-los a tarefas. A classe oferece métodos para buscar rótulos por categoria ou status, e imprimir relacionamentos entre tarefas, categorias e status. Também permite imprimir tarefas por status ou por categoria, evitando duplicações. Além disso, ela integra outras classes, como ArquivoCategoria e ArquivoTarefa, para fornecer informações detalhadas sobre as categorias e tarefas associadas aos rótulos.


**OBS**: Os métodos e classes que não foram abordados aqui são autoexplicativos ou seguem o mesmo padrão apresentado em sala de aula, o que acreditamos tornar redundantes suas explicações.

---

# :pushpin:Opinião do Grupo sobre o Desenvolvimento

Dividimos as tarefas deste trabalho de forma democrática entre os quatro integrantes.

:white_check_mark:Desenvolvimento do programa, Organização e Implementação dos indices e classes - André, João
:white_check_mark:Arquitetura do projeto, Testes e Documentação - Victor, Douglas

A parte mais desafiadora do projeto foi garantir a sincronização entre a leitura e gravação de dados no sistema de arquivos, assegurando a integridade das entidades e a manutenção de seus relacionamentos. Como a maior parte do código foi discutida em sala de aula, não tivemos problemas graves de implementação durante essa etapa, apenas pequenos erros de coerência entre as classes e seus métodos, que estão corrigidos.
Todos os requisitos foram implementados com sucesso. Como já havíamos trabalhado juntos anteriormente, a colaboração foi eficaz e todos aprenderam em conjunto. 

# :pushpin:Integrantes

- **André Luiz Rocha Cabral**
- **Douglas Nicolas Silva Gomes**
- **João Paulo Dias Estevão**
- **Victor Sousa Lima**
