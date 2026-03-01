param(
    [string]$DbHost = '127.0.0.1',
    [string]$DbUser = 'root',
    [string]$DbPassword = '',
    [string]$DbName = 'ats_db'
)

$env:DB_HOST = $DbHost
$env:DB_USER = $DbUser
$env:DB_PASSWORD = $DbPassword
$env:DB_NAME = $DbName

mvn spring-boot:run
