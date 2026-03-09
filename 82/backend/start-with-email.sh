#!/bin/bash
export SENDGRID_API_KEY="SGJc-X3sTrT82albBISVmMSQ.uEouloJx-WALvayQpDX1iD3hFibp0CKuGaUi9wxu-xs"
echo "SENDGRID_API_KEY set to: ${SENDGRID_API_KEY:0:20}..."
java -jar target/careapp-*.jar 2>&1 | tee /tmp/careapp-backend.log &
# Or use mvnw with environment
# ./mvnw spring-boot:run -Dsendgrid.api-key="$SENDGRID_API_KEY" 2>&1 | tee /tmp/careapp-backend.log &
