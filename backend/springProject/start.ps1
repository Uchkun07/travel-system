# 加载环境变量并启动 Spring Boot 应用
# 使用方法: .\start-with-env.ps1

Write-Host "正在加载环境变量..." -ForegroundColor Green

# 设置环境变量
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "102030"
$env:REDIS_HOST = "localhost"
$env:REDIS_PORT = "6379"
$env:REDIS_PASSWORD = ""
$env:SSL_KEYSTORE_PASSWORD = "123456"
$env:MAIL_HOST = "smtp.qq.com"
$env:MAIL_PORT = "587"
$env:MAIL_USERNAME = "2208844091@qq.com"
$env:MAIL_PASSWORD = "tczyvqbqpplyeahe"
$env:JWT_SECRET = "travel-system-secret-key-for-jwt-token-generation-and-validation-2024"

Write-Host "环境变量加载完成!" -ForegroundColor Green
Write-Host "  DB_PASSWORD: ********"
Write-Host "  MAIL_USERNAME: $env:MAIL_USERNAME"
Write-Host "  MAIL_PASSWORD: ********"
Write-Host ""
Write-Host "正在启动 Spring Boot 应用..." -ForegroundColor Green
Write-Host ""

# 启动应用
.\mvnw.cmd spring-boot:run