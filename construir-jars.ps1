# ============================================================
#  Genera el .jar de cada microservicio (necesario antes de Docker).
#  Uso:  .\construir-jars.ps1
# ============================================================
$ErrorActionPreference = "Stop"
$svcs = @(
  "usuarios-service","desarrolladoras-service","categorias-service","juegos-service",
  "carrito-service","pagos-service","biblioteca-service","suscripciones-service",
  "logros-service","resenas-service","gateway-service"
)
foreach ($s in $svcs) {
  Write-Host "==== Empaquetando $s ====" -ForegroundColor Cyan
  mvn -q -f "$s\pom.xml" clean package -DskipTests
}
Write-Host "Listo. Ahora puedes ejecutar: docker compose up --build" -ForegroundColor Green
