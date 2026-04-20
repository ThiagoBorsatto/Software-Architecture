@echo off
echo ========================================
echo   MICROSERVICOS DEMO — UniSENAI ADS
echo   Iniciando todos os servicos...
echo ========================================
echo.

echo [1/4] Iniciando produto-service na porta 8081...
start "produto-service :8081" cmd /k "cd produto-service && mvn spring-boot:run -q"
timeout /t 5 /nobreak > nul

echo [2/4] Iniciando usuario-service na porta 8083...
start "usuario-service :8083" cmd /k "cd usuario-service && mvn spring-boot:run -q"
timeout /t 5 /nobreak > nul

echo [3/4] Iniciando pedido-service na porta 8082...
start "pedido-service :8082" cmd /k "cd pedido-service && mvn spring-boot:run -q"
timeout /t 5 /nobreak > nul

echo [4/4] Iniciando API Gateway + Frontend na porta 8080...
start "gateway :8080" cmd /k "cd gateway && mvn spring-boot:run -q"
timeout /t 8 /nobreak > nul

echo.
echo ========================================
echo   TUDO INICIADO!
echo.
echo   Acesse: http://localhost:8080
echo.
echo   Consoles H2 (bancos separados):
echo   Produtos:  http://localhost:8081/h2-console
echo   Pedidos:   http://localhost:8082/h2-console
echo   Usuarios:  http://localhost:8083/h2-console
echo ========================================
echo.
start http://localhost:8080
pause
