from pyspark import pipelines as dp
from pyspark.sql.functions import col, lag, when, round, datediff
from pyspark.sql.window import Window

@dp.materialized_view(
    name="jarvis_training.gold_dlt.stock_gold_trends"
)

def stock_gold_trends():
    apple = spark.read.table("jarvis_training.silver_dlt.apple_silver")
    microsoft = spark.read.table("jarvis_training.silver_dlt.microsoft_silver")
    meta = spark.read.table("jarvis_training.silver_dlt.meta_silver")
    ibm = spark.read.table("jarvis_training.silver_dlt.ibm_silver")

    all_stocks = (
        apple.unionByName(microsoft)
             .unionByName(meta)
             .unionByName(ibm)
    )

    w = Window.partitionBy("symbol").orderBy("trade_date")

    gold_df = (
        all_stocks
        .withColumn("close_7d_ago", lag("close", 7).over(w))
        .withColumn("close_30d_ago", lag("close", 30).over(w))
        .withColumn("close_90d_ago", lag("close", 90).over(w))
        .withColumn("volume_7d_ago", lag("volume", 7).over(w))
        .withColumn("volume_30d_ago", lag("volume", 30).over(w))
        .withColumn("volume_90d_ago", lag("volume", 90).over(w))

        # Compute price change over 7/30/90 days
        .withColumn("price_change_7d", round(col("close") - col("close_7d_ago"),2))
        .withColumn("price_change_30d", round(col("close") - col("close_30d_ago"),2))
        .withColumn("price_change_90d", round(col("close") - col("close_90d_ago"),2))
        .withColumn("price_pct_change_7d", when(col("close_7d_ago").isNotNull(), round((col("close") - col("close_7d_ago")) / col("close_7d_ago") * 100, 2)))
        .withColumn("price_pct_change_30d", when(col("close_30d_ago").isNotNull(), round((col("close") - col("close_30d_ago")) / col("close_30d_ago") * 100, 2)))
        .withColumn("price_pct_change_90d", when(col("close_90d_ago").isNotNull(), round((col("close") - col("close_90d_ago")) / col("close_90d_ago") * 100, 2)))

        # Compute volume change over 7/30/90 days
        .withColumn("volume_change_7d", round(col("volume") - col("volume_7d_ago"),2))
        .withColumn("volume_change_30d", round(col("volume") - col("volume_30d_ago"),2))
        .withColumn("volume_change_90d", round(col("volume") - col("volume_90d_ago"),2))
        .select(
            "symbol",
            "trade_date",
            "open",
            "high",
            "low",
            "close",
            "volume",
            "price_change_7d",
            "price_change_30d",
            "price_change_90d",
            "price_pct_change_7d",
            "price_pct_change_30d",
            "price_pct_change_90d",
            "volume_change_7d",
            "volume_change_30d",
            "volume_change_90d"
            )
        )
    return gold_df
