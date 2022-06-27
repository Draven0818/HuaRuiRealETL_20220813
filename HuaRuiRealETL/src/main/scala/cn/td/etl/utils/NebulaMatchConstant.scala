package cn.td.etl.utils

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.util.Properties

case class NebulaMatchConstant(idNum: String = "", tel: String = "") extends Serializable {

  private val nebulaProp = new Properties()
  private val inputStream: InputStream = this.getClass.getClassLoader.getResourceAsStream("indicator_query.properties")
  private val bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))
  nebulaProp.load(bufferedReader)

  //val query_indicator026: (String, String) = Tuple2("indicator026", nebulaProp.getProperty("indicator026").replaceAll("idNumValue", idNum))
  //val query_indicator071: (String, String) = Tuple2("indicator071", nebulaProp.getProperty("indicator071").replaceAll("idNumValue", idNum))
  //val query_indicator028: (String, String) = Tuple2("indicator028", nebulaProp.getProperty("indicator028").replaceAll("idNumValue", idNum))
  //val query_indicator031: (String, String) = Tuple2("indicator031", nebulaProp.getProperty("indicator031").replaceAll("idNumValue", idNum))
  val query_indicator027: (String, String) = Tuple2("indicator027", nebulaProp.getProperty("indicator027").replaceAll("idNumValue", idNum))
  val query_indicator032: (String, String) = Tuple2("indicator032", nebulaProp.getProperty("indicator032").replaceAll("idNumValue", idNum))
  val query_indicator061: (String, String) = Tuple2("indicator061", nebulaProp.getProperty("indicator061").replaceAll("idNumValue", idNum))
  val query_indicator062: (String, String) = Tuple2("indicator062", nebulaProp.getProperty("indicator062").replaceAll("idNumValue", idNum))
  val query_indicator063: (String, String) = Tuple2("indicator063", nebulaProp.getProperty("indicator063").replaceAll("telValue", tel))
  val query_indicator064: (String, String) = Tuple2("indicator064", nebulaProp.getProperty("indicator064").replaceAll("idNumValue", idNum))

  val idNumMatchList = List(query_indicator027, query_indicator032, query_indicator061, query_indicator062, query_indicator064)
  //val idNumMatchList = List(query_indicator026, query_indicator027, query_indicator028, query_indicator031, query_indicator032, query_indicator061, query_indicator062, query_indicator064, query_indicator071)
  val telMatchList = List(query_indicator063)
}