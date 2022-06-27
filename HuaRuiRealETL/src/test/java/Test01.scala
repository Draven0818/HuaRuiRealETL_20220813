import cn.td.etl.utils.SourceKafka.{DXM_CREDIT_DECISION_NO, DXM_USE_DECISION_NO, ELM_CREDIT_DECISION_NO, ELM_USE_DECISION_NO, LX_CREDIT_DECISION_NO, LX_USE_DECISION_NO}
import cn.td.etl.utils.trans.GenUids
import cn.td.etl.utils.{Base, DXMSX, DXMYX, ELMSX, ELMYX, LXSX, LXYX}
import com.alibaba.fastjson.JSON
import org.apache.spark.sql.SparkSession


object Test01 {

  def mdfUid[T](label: String, list: List[String], bean: T): T = {
    ???
  }

  implicit val dxmsxetl: GenUids[DXMSX] = new GenUids[DXMSX] {
    override def genUid01(bean: DXMSX): DXMSX = {
      //生成度小满授信各个uid
      ???
    }
  }

  //  implicit val dxmyxetl: GenUids[DXMYX] = new GenUids[DXMYX] {
  //    override def genUid01(bean: DXMYX): DXMYX = println("dxmyxetl")
  //  }
  //
  //  implicit val lxsxetl: GenUids[LXSX] = new GenUids[LXSX] {
  //    override def genUid01(bean: LXSX): DXMYX = println("lxsxetl")
  //  }
  //
  //  implicit val lxsyetl: GenUids[LXYX] = new GenUids[LXYX] {
  //    override def genUid01(bean: LXYX): Unit = println("lxsyetl")
  //  }
  //
  //  implicit val elmsxetl: GenUids[ELMSX] = new GenUids[ELMSX] {
  //    override def genUid01(bean: ELMSX): Unit = println("elmsxetl")
  //  }
  //
  //  implicit val elmyxetl: GenUids[ELMYX] = new GenUids[ELMYX] {
  //    override def genUid01(bean: ELMYX): Unit = println("elmyxetl")
  //  }


  def dataEtl[T](bean: T)(implicit etl: GenUids[T]): Unit = {
    println("当前实体数据" + bean)
    etl.genUid01(bean)
  }

  val entryStr = "{\"applyId\":\"00x1\",\"productCode\":\"度小满\",\"workflowcode\":\"DXM_USE_DECISION_NO\",\"preApplicationId\":\"\",\"productType\":\"个人消费贷款\",\"customerType\":\"个人客户\",\"prcid\":\"340702199304042817\",\"bankcard\":\"6222021308001908878\",\"name\":\"詹三\",\"phonenumber\":\"15012344321\",\"initialAmount\":\"100分\",\"creditValidityDays\":\"100天\",\"risCode\":\"10000\",\"taxMonthlyIncomeSection\":\"5-10k\",\"ocrExpdate\":\"20360401\",\"r21P12FinallyInterest\":\"5%\",\"sexF\":\"男\",\"nationalityF\":\"CHN\",\"occupationF\":\"码农\",\"addressF\":\"上海\",\"certTypeF\":\"居民身份证\",\"creditType\":\"首次授信\",\"pairSimilarity1\":\"0.8\",\"pairSimilarity2\":\"0.7\",\"basicPicSource1\":\"人行\",\"basicPicSource2\":\"高清人像\",\"busDept\":\"信用卡部\",\"busName\":\"李四\"}"
  //  val entryStr = "{\"applyId\":\"00x1\",\"productCode\":\"度小满\",\"workflowcode\":\"DXM_CREDIT_DECISION_NO\",\"preApplicationId\":\"\",\"productType\":\"个人消费贷款\",\"customerType\":\"个人客户\",\"prcid\":\"340702199304042817\",\"bankcard\":\"6222021308001908878\",\"name\":\"詹三\",\"phonenumber\":\"15012344321\",\"initialAmount\":\"100分\",\"creditValidityDays\":\"100天\",\"risCode\":\"10000\",\"taxMonthlyIncomeSection\":\"5-10k\",\"ocrExpdate\":\"20360401\",\"r21P12FinallyInterest\":\"5%\",\"sexF\":\"男\",\"nationalityF\":\"CHN\",\"occupationF\":\"码农\",\"addressF\":\"上海\",\"certTypeF\":\"居民身份证\",\"creditType\":\"首次授信\",\"pairSimilarity1\":\"0.8\",\"pairSimilarity2\":\"0.7\",\"basicPicSource1\":\"人行\",\"basicPicSource2\":\"高清人像\",\"busDept\":\"信用卡部\",\"busName\":\"李四\"}"
  //    val entryStr = ""


  def main(args: Array[String]): Unit = {

    //    check1(entryStr)
    checkActionFunc
  }

  def check1(entryStr: String): Unit = {
    var workflowcode = ""
    val recordJ = JSON.parseObject(entryStr)
    try {
      workflowcode = recordJ.getString("workflowcode")
    } catch {
      case e: NullPointerException => {
        println(s"json字符串为空 ${e.getMessage}")
      }
        return
    }
    val base: Base = workflowcode match {
      case DXM_CREDIT_DECISION_NO => recordJ.toJavaObject(classOf[DXMSX])
      case DXM_USE_DECISION_NO => recordJ.toJavaObject(classOf[DXMYX])
      case LX_CREDIT_DECISION_NO => recordJ.toJavaObject(classOf[LXSX])
      case LX_USE_DECISION_NO => recordJ.toJavaObject(classOf[LXYX])
      case ELM_CREDIT_DECISION_NO => recordJ.toJavaObject(classOf[ELMSX])
      case ELM_USE_DECISION_NO => recordJ.toJavaObject(classOf[ELMYX])
      case _ => null
    }

    base match {
      case a: DXMSX => {
        var personUids: Map[String, String] = a.personUids
        personUids += ("" -> "")
      }

      //      case b: DXMYX => dataEtl(b)
      //
      //      case c: LXSX => dataEtl(c)
      //
      //      case d: LXYX => dataEtl(d)
      //
      //      case e: ELMSX => dataEtl(e)
      //
      //      case f: ELMYX => dataEtl(f)

      case _ => println("qita")
    }


  }


  def checkActionFunc(): Unit = {
    val spark = SparkSession.builder().appName("").master("local").getOrCreate()

    val df1 = spark.createDataFrame(Seq(
      ("zs", "11", "sh"),
      ("ls", "22", "hz"),
      ("ww", "33", "ah")
    )).toDF("name", "age", "home")


    df1.foreach(row => println(row))
    df1.foreach(row => println(row + "2"))
  }
}
