# Grafana Alloy (Observability Stack)

This module configures **Grafana Alloy** for collecting **logs and metrics**
from GreenCity services running in Kubernetes and sending them to **Grafana Cloud**.

---

## 📌 What is Grafana Alloy?

Grafana Alloy is a vendor-neutral observability collector that can replace:
- Promtail (logs)
- Prometheus Agent (metrics)

In this project Alloy is used to:
- Collect **application logs** from Kubernetes pods
- Scrape **Prometheus metrics** from Spring Boot Actuator
- Forward logs to **Grafana Cloud Loki**
- Forward metrics to **Grafana Cloud Prometheus**

---

## 🧩 What is collected?

### 🔹 Logs
- Kubernetes pod logs
- Filtered by application labels:
    - `greencity`
    - `greencity-ubs`
    - `greencity-user`
- Logs are enriched with labels:
    - `service`
    - `namespace`
    - `pod`
    - `container`

### 🔹 Metrics
- Spring Boot Actuator endpoint:
  /actuator/prometheus


- Metrics are scraped from Kubernetes services avoiding ingress:
- `greencity-greencity-service`
- `ubs-greencity-ubs-service`
- `user-greencity-user-service`

---

## 🔐 Secrets & Security

Grafana Cloud credentials **must NOT be hardcoded**.

They are stored in Kubernetes Cluster External Secrets and injected into Alloy via Helm values:

Required secrets:
- `GRAFANA-PROMETHEUS-USERNAME`
- `GRAFANA-PROMETHEUS-PASSWORD`
- `GRAFANA-PROMTAIL-USERNAME`
- `GRAFANA-PROMTAIL-PASSWORD`

Secrets are typically managed via:
- Kubernetes Secrets
- External Secrets / Azure Key Vault

---

## 🚀 Deployment

Alloy is deployed using **Helm** via **Azure DevOps pipeline**.

### Manual Helm install (for local testing)

``` bash
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

helm upgrade --install alloy grafana/alloy \
  -f greencity-alloy-chart/values.yaml
```
### Azure DevOps Pipeline
Deployment is automated via a dedicated pipeline:

- Triggered on changes in greencity-alloy-chart/**
- Uses HelmDeploy@0
- Executes helm upgrade --install

### Grafana Dashboards
- After deployment, data is available in Grafana Cloud:
- Logs → Explore → Loki
- Metrics → Prometheus datasource

### Useful commands
``` bash

# Check Alloy pods
kubectl get pods

# Check Alloy logs
kubectl logs <pod name>

# Check applied pod
kubectl describe pod <pod name>

# Check applied config
kubectl describe configmap <pod name>
```

### Note

The /actuator/prometheus endpoint is enabled in the application, but explicitly blocked at the Ingress level.
All other application endpoints remain publicly accessible.

- Metrics are exposed only via the Kubernetes Service
- Grafana Alloy scrapes metrics using service-based discovery
- Ingress does not route /actuator/prometheus to the application

Reason:
- To prevent public access to internal metrics while still allowing in-cluster monitoring.
