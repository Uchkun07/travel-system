# 设置 Spring Boot 应用的环境变量
# 使用方法: 在 PowerShell 中运行此脚本

Write-Host "设置 Spring Boot 环境变量..." -ForegroundColor Green

# 数据库配置
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "102030"

# Redis 配置
$env:REDIS_HOST = "localhost"
$env:REDIS_PORT = "6379"
$env:REDIS_PASSWORD = ""

# SSL 配置
$env:SSL_KEYSTORE_PASSWORD = "123456"

# 邮箱配置
$env:MAIL_HOST = "smtp.qq.com"
$env:MAIL_PORT = "587"
$env:MAIL_USERNAME = "2208844091@qq.com"
$env:MAIL_PASSWORD = "tczyvqbqpplyeahe"

# JWT 配置
$env:JWT_SECRET = "travel-system-secret-key-for-jwt-token-generation-and-validation-2024"

Write-Host "环境变量设置完成!" -ForegroundColor Green
Write-Host ""
Write-Host "已设置的环境变量:" -ForegroundColor Yellow
Write-Host "  DB_USERNAME: $env:DB_USERNAME"
Write-Host "  DB_PASSWORD: ********"
Write-Host "  REDIS_HOST: $env:REDIS_HOST"
Write-Host "  REDIS_PORT: $env:REDIS_PORT"
Write-Host "  SSL_KEYSTORE_PASSWORD: ********"
Write-Host "  MAIL_HOST: $env:MAIL_HOST"
Write-Host "  MAIL_PORT: $env:MAIL_PORT"
Write-Host "  MAIL_USERNAME: $env:MAIL_USERNAME"
Write-Host "  MAIL_PASSWORD: ********"
Write-Host "  JWT_SECRET: ********"
Write-Host ""
Write-Host "注意: 这些环境变量仅在当前 PowerShell 会话中有效" -ForegroundColor Cyan
Write-Host "关闭窗口后需要重新运行此脚本" -ForegroundColor Cyan
