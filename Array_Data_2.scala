package org.example

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{ArrayType, StringType, StructType}

object Array_Data_2 {
  def main(args: Array[String]): Unit = {

    val spark = org.apache.spark.sql.SparkSession.builder.master("local").appName("Spark CSV Reader").getOrCreate;

    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)

    val arrayStructureData = Seq(
      Row("James,,Smith", List("Java", "Scala", "C++"), List("Spark", "Java"), "OH", "CA"),
      Row("Michael,Rose,", List("Spark", "Java", "C++"), List("Spark", "Java"), "NY", "NJ"),
      Row("Robert,,Williams", List("CSharp", "VB"), List("Spark", "Python"), "UT", "NV")
    )

    val arrayStructureSchema = new StructType()
      .add("name", StringType)
      .add("languagesAtSchool", ArrayType(StringType))
      .add("languagesAtWork", ArrayType(StringType))
      .add("currentState", StringType)
      .add("previousState", StringType)

    var df = spark.createDataFrame(
      spark.sparkContext.parallelize(arrayStructureData), arrayStructureSchema)//.show
    // fetching all the columns which have array data type
    val columns = df.schema.fields.filter(x => x.dataType == ArrayType(StringType))
    val selectColumns = df.select(columns.map(x => col(x.name)): _*).toDF().columns.toList

    import org.apache.spark.sql.functions._
    val value = udf((arr: Seq[String]) => arr.mkString(","))
    //converting all the array data type column into string
    for(sle_name <- selectColumns)
    {
      df = df.withColumn(sle_name, value(col(sle_name)))
    }
    // save data into csv format
    df.show()
    df.printSchema()
    df.write.option("header",true).csv(path="C:\\Folder_A\\CSV_ARRAY_4")
  }
  }
