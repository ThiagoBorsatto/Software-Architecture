# 🚀 Microserviços Demo — UniSENAI ADS

Projeto acadêmico para demonstração prática de **Arquitetura de Microserviços** com Spring Boot.

---

## 🏗️ Arquitetura

```
[Navegador]
     ↓
[API Gateway + Frontend Thymeleaf]  :8080
     ↓
┌─────────────────────────────────────────┐
│  produto-service  :8081  → H2 próprio   │
│  pedido-service   :8082  → H2 próprio   │
│  usuario-service  :8083  → H2 próprio   │
└─────────────────────────────────────────┘
```

Cada serviço tem seu **banco H2 embutido e isolado** — sem instalar nada.

---

## ⚡ Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+

### Windows (recomendado para apresentação)
```
Clique duas vezes em: start-all.bat
```
Aguarde ~30 segundos e acesse: **http://localhost:8080**

### Manual (um terminal por serviço)
```bash
# Terminal 1
cd produto-service && mvn spring-boot:run

# Terminal 2
cd usuario-service && mvn spring-boot:run

# Terminal 3
cd pedido-service && mvn spring-boot:run

# Terminal 4
cd gateway && mvn spring-boot:run
```

---

## 🔍 O que demonstrar na apresentação

### 1. Serviços independentes
- Cada serviço é uma aplicação Spring Boot separada
- Cada um tem seu próprio `Application.java`, `pom.xml`, `application.properties`

### 2. Bancos de dados isolados
Acesse os consoles H2 de cada serviço (JDBC URL: `jdbc:h2:mem:produtodb` etc.):
- Produtos: http://localhost:8081/h2-console
- Pedidos:  http://localhost:8082/h2-console
- Usuários: http://localhost:8083/h2-console

### 3. Comunicação entre serviços via REST
Crie um pedido pela interface e observe nos logs dos terminais:
```
[pedido-service] 🔗 Consultando produto-service para produto ID: 1
[pedido-service] 🔗 Consultando usuario-service para usuário ID: 1
[pedido-service] 💾 Salvando pedido no banco local...
[pedido-service] ✅ Pedido #1 criado com sucesso!
```

### 4. Circuit Breaker simplificado
Pare o `produto-service` (feche o terminal) e tente criar um pedido:
- A aplicação **não cai** — retorna mensagem de erro amigável
- Este é o conceito do Circuit Breaker em ação

### 5. APIs independentes
Acesse diretamente sem passar pelo Gateway:
```
http://localhost:8081/api/produtos        ← direto no produto-service
http://localhost:8082/api/pedidos         ← direto no pedido-service
http://localhost:8083/api/usuarios        ← direto no usuario-service
```

---

## 📁 Estrutura do Projeto

```
microservicos-demo/
├── gateway/                    → API Gateway + Frontend (porta 8080)
│   └── src/main/
│       ├── java/com/demo/gateway/
│       │   ├── GatewayApplication.java
│       │   └── controller/FrontendController.java
│       └── resources/templates/
│           ├── index.html      ← Painel principal
│           ├── produtos.html   ← Listagem de produtos
│           ├── pedidos.html    ← Criar e listar pedidos
│           └── usuarios.html   ← Listagem de usuários
│
├── produto-service/            → Serviço de Produtos (porta 8081)
│   └── src/main/java/com/demo/produto/
│       ├── model/Produto.java
│       ├── repository/ProdutoRepository.java
│       ├── service/ProdutoService.java
│       └── controller/ProdutoController.java
│
├── pedido-service/             → Serviço de Pedidos (porta 8082)
│   └── src/main/java/com/demo/pedido/
│       ├── model/Pedido.java
│       ├── dto/ProdutoDTO.java  ← DTO próprio (não compartilha classe com produto-service!)
│       ├── dto/UsuarioDTO.java
│       ├── service/PedidoService.java  ← Comunicação REST entre serviços
│       └── controller/PedidoController.java
│
├── usuario-service/            → Serviço de Usuários (porta 8083)
│   └── src/main/java/com/demo/usuario/
│       ├── model/Usuario.java
│       ├── service/UsuarioService.java
│       └── controller/UsuarioController.java
│
├── start-all.bat               → Inicia tudo com um clique (Windows)
└── README.md
```

---

## 🎯 Conceitos Demonstrados

| Conceito | Onde ver |
|---|---|
| Serviços independentes | Cada pasta é um projeto Spring Boot separado |
| Banco por serviço | H2 console de cada porta diferente |
| Comunicação via REST | `PedidoService.java` — RestTemplate |
| Circuit Breaker (simplificado) | Derrube produto-service e tente criar pedido |
| API Gateway | Gateway roteia todas as chamadas do frontend |
| DTOs sem acoplamento | `ProdutoDTO.java` no pedido-service |

---

## 📚 Referências

- [Martin Fowler — Microservices](https://martinfowler.com/articles/microservices.html)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [12 Factor App](https://12factor.net/)
