# Databricks Data Pipelines: Medallion Architecture for Financial Fraud & Stock Trend Analytics

## Project Overview

This repository showcases the implementation of production-ready data pipelines within Databricks, highlighting both traditional batch ETL workflows and modern declarative Delta Live Tables (DLT) architectures. The goal is to ingest multi-source raw datasets, apply medallion transformations (Bronze → Silver → Gold), and serve optimized dimensional tables to power downstream BI reporting and executive decision-making.

The project is structured into two core engineering tracks:
1. **Financial Fraud Detection Pipeline (Batch ETL)**: Integrates heterogeneous banking records—including transactional streams, user demographics, credit card accounts, merchant classifications, and supervised fraud labels—to deliver aggregated risk metrics and behavioral insight models.
2. **Equity Market Trend Engine (Declarative DLT)**: Ingests automated REST API feeds tracking enterprise equities (Apple, Microsoft, Meta, IBM), generating rolling performance indicators, volume shifts, and multi-horizon trend evaluations.

---

## 1. Batch ETL Pipeline: Financial Fraud Analytics

### Business Objective
To construct an automated data pipeline that unifies fragmented payment systems, cleans unformatted transactional logs, and isolates anomalous purchasing patterns. Curated gold datasets enable risk analytics teams to evaluate fraud velocity, identify compromised user profiles, detect suspicious merchant classifications, and measure loss exposure across temporal windows.

### Source Datasets
* `transactions_data.csv`: Transaction logs containing timestamps, transaction amounts, point-of-sale mechanisms, and merchant identifiers.
* `cards_data.csv`: Cardholder metadata, credit limits, expiration periods, and dark web alert indicators.
* `users_data.csv`: Customer demographic records, credit score bands, debt profiles, and address coordinates.
* `mcc_codes.json`: Relational mapping of 4-digit Merchant Category Codes to business descriptions.
* `train_fraud_labels.json`: Historical transaction fraud identifiers used for label enrichment.

### Technical Stack
* **Platform**: Azure Databricks, Unity Catalog
* **Compute / Query Engine**: Apache PySpark, Spark SQL
* **Storage & Relational Services**: Azure Data Lake Storage Gen2 (ADLS), Azure SQL Database
* **Connectivity**: JDBC connectors, DBFS Volumes
* **Orchestration & BI**: Databricks Workflows (Multi-task Jobs), Databricks SQL Dashboards

### Pipeline Architecture & Medallion Design
* **Ingestion Layer**:
  * Extracted core transaction and card logs directly from Azure SQL Database instances via secure JDBC connections.
  * Loaded user records and JSON payloads (`mcc_codes`, `train_fraud_labels`) through ADLS Gen2 external locations and Databricks Volumes.
* **Bronze Layer (Raw Storage)**:
  * Ingested raw datasets with schema-on-read logic and persisted raw Delta tables (`jarvis_training.bronze.*`) with full schema preservation.
* **Silver Layer (Validation & Enrichment)**:
  * Handled data cleansing, casting currency strings into standardized numeric types (`double`), and parsing ISO date/time structures.
  * Removed duplicate entries across primary key boundaries (`id`, `transaction_id`, `client_id`).
  * Enriched baseline transactions by joining MCC definitions and appending ground-truth fraud flags.
* **Gold Layer (Analytics & Aggregations)**:
  * Engineered reporting datasets including daily fraud rate timelines, merchant risk tiers, and customer profile behaviors categorized by first-fraud exposure timelines.
* **Workflow Automation**:
  * Scheduled through Databricks Workflows, executing sequential tasks from Bronze ingestion through Gold aggregation and automated dashboard visual updates.

---

## 2. Declarative Pipeline: Stock Market Trend Analytics

### Business Objective
To build a scalable, declarative ingestion and feature-engineering framework for financial equities. The pipeline processes daily price action for high-cap tech equities (AAPL, MSFT, META, IBM) to calculate dynamic momentum indicators, historical price changes, and volume shifts over multiple rolling windows.

### Ingestion & Raw Data Feed
* Utilizes Alpha Vantage Market API endpoints.
* Tracks daily OHLCV parameters (Open, High, Low, Close, Volume) and trade calendar dates across monitored ticker symbols.

### Technical Stack
* **Framework**: Delta Live Tables (DLT) / PySpark Pipelines API (`pyspark.pipelines`)
* **Engine**: Databricks Auto Loader (`cloudFiles`)
* **Governance**: Databricks Unity Catalog
* **Data Transformations**: Structured Streaming, PySpark Window Functions

### DLT Pipeline Architecture
* **Ingestion (Streaming Bronze Layer)**:
  * Configured Auto Loader (`format("cloudFiles")`) streaming endpoints watching raw JSON drop locations per ticker symbol to ingest new records incrementally.
* **Silver Layer (Standardization & Deduplication)**:
  * Cast schema attributes, enforced double-precision floats on price fields, converted dates, and eliminated redundant records using composite key constraints (`symbol`, `trade_date`).
* **Gold Layer (Materialized Views & Trend Analytics)**:
  * Consolidated multi-ticker silver tables via schema-aligned unions.
  * Applied analytic window functions (`pyspark.sql.window.Window`) partitioned by ticker symbol to compute:
    * Absolute and percentage price variance over 7-day, 30-day, and 90-day intervals.
    * Trading volume deviations across corresponding 7d/30d/90d evaluation periods.
* **Dashboards & Refresh Orchestration**:
  * Connected materialized Gold tables directly to Databricks Lakeview Dashboards, refreshed continuously through automated pipeline triggers.
