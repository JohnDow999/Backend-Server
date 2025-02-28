//mongodb+srv://dvir_test:TAK3LAIkGIwIvaRW@cluster0.baciqob.mongodb.net/
package com.yotpo.metorikku.code.steps

import com.yotpo.metorikku.exceptions.MetorikkuException
import org.apache.log4j.{LogManager, Logger}
import org.apache.spark.sql.{Row}


object LoadIfExists {
  val message = "You need to send 2 parameters with the names of a df and a name of a table to try to load: dfName, tableName"

  private val log: Logger = LogManager.getLogger(this.getClass)
  private val LoadIfExistsInputMatcher = InputMatcher("dfName", "tableName")

  def run(ss: org.apache.spark.sql.SparkSession, metricName: String, dataFrameName: String, params: Option[Map[String, String]]): Unit = {
    params.get match {
      case LoadIfExistsInputMatcher(dfName, tableName) => {
        log.info(s"Attempting to load $tableName")

        if (ss.catalog.tableExists(tableName)) {
          ss.table(tableName).createOrReplaceTempView(dataFrameName)
        }
        else {
          val df = ss.table(dfName)
          ss.createDataFrame(ss.sparkContext.emptyRDD[Row], df.schema).createOrReplaceTempView(dataFrameName)
        }
      }
      case _ => throw MetorikkuException(message)
    }
  }
}
