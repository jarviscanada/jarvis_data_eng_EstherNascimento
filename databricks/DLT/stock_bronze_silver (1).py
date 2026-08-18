from pyspark import pipelines as dp
from pyspark.sql.functions import col, to_date, round

# Bronze streaming tables
@dp.table(
    name="jarvis_training.bronze_dlt.apple_bronze"
)
def apple_bronze():
    return (
        spark.readStream
        .format("cloudFiles")
        .option("cloudFiles.format", "json")
        .load("/Volumes/jarvis_training/bronze_dlt/stock_api_raw/apple")
    )


@dp.table(
    name="jarvis_training.bronze_dlt.microsoft_bronze"
)
def microsoft_bronze():
    return (
        spark.readStream
        .format("cloudFiles")
        .option("cloudFiles.format", "json")
        .load("/Volumes/jarvis_training/bronze_dlt/stock_api_raw/microsoft")
    )


@dp.table(
    name="jarvis_training.bronze_dlt.meta_bronze"
)
def meta_bronze():
    return (
        spark.readStream
        .format("cloudFiles")
        .option("cloudFiles.format", "json")
        .load("/Volumes/jarvis_training/bronze_dlt/stock_api_raw/meta")
    )


@dp.table(
    name="jarvis_training.bronze_dlt.ibm_bronze",
)
def ibm_bronze():
    return (
        spark.readStream
        .format("cloudFiles")
        .option("cloudFiles.format", "json")
        .load("/Volumes/jarvis_training/bronze_dlt/stock_api_raw/ibm")
    )


# Silver streaming tables
@dp.table(
    name="jarvis_training.silver_dlt.apple_silver"
)
def apple_silver():
    return (
        spark.readStream.table("jarvis_training.bronze_dlt.apple_bronze")
        .withColumn("trade_date", to_date(col("trade_date")))
        .withColumn("open", round(col("open").cast("double"), 2))
        .withColumn("high", round(col("high").cast("double"), 2))
        .withColumn("low", round(col("low").cast("double"), 2))
        .withColumn("close", round(col("close").cast("double"), 2))
        .withColumn("volume", col("volume").cast("long"))
        .dropDuplicates(["symbol", "trade_date"])
        .select(
            "symbol",
            "trade_date",
            "open",
            "high",
            "low",
            "close",
            "volume"
        )
    )


@dp.table(
    name="jarvis_training.silver_dlt.microsoft_silver"
)
def microsoft_silver():
    return (
        spark.readStream.table("jarvis_training.bronze_dlt.microsoft_bronze")
        .withColumn("trade_date", to_date(col("trade_date")))
        .withColumn("open", round(col("open").cast("double"), 2))
        .withColumn("high", round(col("high").cast("double"), 2))
        .withColumn("low", round(col("low").cast("double"), 2))
        .withColumn("close", round(col("close").cast("double"), 2))
        .withColumn("volume", col("volume").cast("long"))
        .dropDuplicates(["symbol", "trade_date"])
        .select(
            "symbol",
            "trade_date",
            "open",
            "high",
            "low",
            "close",
            "volume"
        )
    )


@dp.table(
    name="jarvis_training.silver_dlt.meta_silver"
)
def meta_silver():
    return (
        spark.readStream.table("jarvis_training.bronze_dlt.meta_bronze")
        .withColumn("trade_date", to_date(col("trade_date")))
        .withColumn("open", round(col("open").cast("double"), 2))
        .withColumn("high", round(col("high").cast("double"), 2))
        .withColumn("low", round(col("low").cast("double"), 2))
        .withColumn("close", round(col("close").cast("double"), 2))
        .withColumn("volume", col("volume").cast("long"))
        .dropDuplicates(["symbol", "trade_date"])
        .select(
            "symbol",
            "trade_date",
            "open",
            "high",
            "low",
            "close",
            "volume"
        )
    )


@dp.table(
    name="jarvis_training.silver_dlt.ibm_silver"
)
def ibm_silver():
    return (
        spark.readStream.table("jarvis_training.bronze_dlt.ibm_bronze")
        .withColumn("trade_date", to_date(col("trade_date")))
        .withColumn("open", round(col("open").cast("double"), 2))
        .withColumn("high", round(col("high").cast("double"), 2))
        .withColumn("low", round(col("low").cast("double"), 2))
        .withColumn("close", round(col("close").cast("double"), 2))
        .withColumn("volume", col("volume").cast("long"))
        .dropDuplicates(["symbol", "trade_date"])
        .select(
            "symbol",
            "trade_date",
            "open",
            "high",
            "low",
            "close",
            "volume"
        )
    )
