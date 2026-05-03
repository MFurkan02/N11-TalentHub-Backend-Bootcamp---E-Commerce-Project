@echo off

set services=user-service product-service order-service stock-service payment-service shopping-cart-service favorite-list-service api-gateway discovery-server config-server

for %%s in (%services%) do (
  echo Building %%s...
  cd %%s
  mvnw.cmd clean compile jib:dockerBuild -DskipTests
  cd ..
)

echo ALL SERVICES BUILT 🚀