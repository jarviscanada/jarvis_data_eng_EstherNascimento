# Retail Data Analytics and World Development Indicators with PySpark

## Introduction
This project explores big data processing and analytical workflows using PySpark across two distinct environments: Azure Databricks and Apache Zeppelin. The primary objective was to leverage distributed computing to transform raw, large-scale datasets into actionable business intelligence. 

By utilizing **PySpark Structured APIs**, I implemented data pipelines that handle data cleaning, complex aggregations, and advanced customer segmentation. The project demonstrates the versatility of Hadoop-based ecosystems in processing both retail transaction data and global development indicators.

---

## Databricks and Hadoop Implementation

### Business Context & Analytics
Using the **Online Retail II dataset**, I built a pipeline to analyze customer purchasing patterns for a UK-based retailer. The analytics work involved:
- **Cleaning:** Standardizing schemas and handling missing values in a distributed environment.
- **Metrics:** Calculating monthly revenue, order volume, and sales growth rates.
- **RFM Segmentation:** Segmenting customers based on Recency, Frequency, and Monetary values to identify "Champions" and "At-Risk" users.

The full notebook implementation can be found here:
- [Retail Data Analytics Notebook](./notebook/Retail_Data_Analytics_with_PySpark.ipynb)

### Architecture
The architecture leverages a cloud-native Hadoop environment:
- **Azure Databricks:** Provides the managed Spark clusters for distributed processing.
- **DBFS (Databricks File System):** Used for persistent storage of the raw CSV files.
- **PySpark:** The engine used for data transformation.
- **Data Flow:** Data is ingested from DBFS, processed in Spark memory (RAM), and analyzed using DataFrame APIs.

### Architecture Diagram


---

## Zeppelin and Hadoop Implementation

### Dataset & Analytics
In this phase, I evaluated PySpark on **Apache Zeppelin** using the **World Development Indicators (WDI)** dataset. This dataset tracks global development across various countries and years. The analysis focused on:
- Querying structured Hive tables using Spark SQL.
- Exploring indicator trends (like GDP or education metrics) using Zeppelin's interactive visualizations.

The notebook implementation can be found here:
- [Spark Dataframe - WDI Data Analytics Notebook](./notebook/spark_dataframe_wdi_data_analytics.json)

### Architecture
This implementation utilizes a traditional Hadoop stack:
- **GCP Dataproc:** Managed Hadoop and Spark cluster environment.
- **Apache Zeppelin:** The web-based notebook for data exploration and visualization.
- **Hive Metastore:** Used to manage the metadata for the WDI tables stored in HDFS.
- **PySpark:** Used to perform DataFrame-based queries on top of Hive tables.

### Architecture Diagram


---

## Future Improvements
1. **Pipeline Automation:** Implement an automated ETL trigger using Azure Data Factory or Google Cloud Composer to ingest new data daily.
2. **Predictive Modeling:** Integrate Spark MLlib to predict customer churn or future sales based on the RFM scores.
3. **Advanced Visualization:** Connect the processed Spark DataFrames to a BI tool like PowerBI or Tableau for real-time stakeholder reporting.
