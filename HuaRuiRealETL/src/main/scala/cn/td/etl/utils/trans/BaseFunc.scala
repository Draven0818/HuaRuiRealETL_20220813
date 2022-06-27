package cn.td.etl.utils.trans

import cn.td.etl.utils.ConfigUtils
import com.alibaba.fastjson.JSONObject

import java.security.MessageDigest

object BaseFunc {
  /**
   * md5加密
   *
   * @param text
   * @return
   */
  def md5(text: String): String = {
    if (text == null | text.isEmpty) return null
    val digest = MessageDigest.getInstance("MD5")
    digest.digest(text.getBytes("utf-8")).map("%02x".format(_)).mkString.toLowerCase
  }


  /**
   * 根据传入字段生成uid
   * 判断是否为空
   */
  def fieldBlackUid(bType: String, jObj: JSONObject, fields: List[(String, String)]): JSONObject = {
    val keySet = jObj.keySet()
    for (field <- fields) {
      if (keySet.contains(field._1)) {
        if (jObj.getString(field._1).nonEmpty) {
          jObj.put(s"%s%sUid".format(bType, field._1), md5(field._2 + jObj.getString(field._1)))
        }
      }
    }
    jObj
  }

  /**
   * 根据传入字段生成uid
   * 判断是否为空
   */
  def fieldUid(jObj: JSONObject, fields: List[(String, String)]): JSONObject = {
    val keySet = jObj.keySet()
    for (field <- fields) {
      if (keySet.contains(field._1)) {
        if (jObj.getString(field._1).nonEmpty) {
          jObj.put(s"%sUid".format(field._1), md5(field._2 + jObj.getString(field._1)))
        }
      }
    }
    jObj
  }

  /**
   * 组合uid生成
   */
  def fieldsUid(jObj: JSONObject, fields: List[(String, String, String, String)]): JSONObject = {
    val keySet = jObj.keySet()
    for (field <- fields) {
      if (keySet.contains(field._1) & keySet.contains(field._2)) {
        if (jObj.getString(field._1).nonEmpty & jObj.getString(field._2).nonEmpty) {
          jObj.put(s"%sUid".format(field._3), md5(field._4 + jObj.getString(field._1) + jObj.getString(field._2)))
        }
      }
    }
    jObj
  }

  /**
   * 根据字段是否存在添加 各关系中的relation_type
   *
   * @param jObj
   */
  def defineRelationType(jObj: JSONObject): JSONObject = {

    jObj.put("ownerTelType", "申请手机号")

    if (jObj.containsKey("ContactsRelationTelUid")) {
      jObj.put("ContactsRelationTelUid", "联系人手机号")
    }

    if (jObj.containsKey("bankCardMobileUid")) {
      jObj.put("bankCardMobileType", "银行预留手机号")
    }
    if (jObj.containsKey("PersonBankTelUid")) {
      jObj.put("PersonBankTelUid", "人行电话")
    }

    if (jObj.containsKey("idAddrUid")) {
      jObj.put("idAddrType", "身份证地址")
    }

    if (ConfigUtils.`workFlowCode.lxsx`.equals(jObj.getString("workflowcode"))) {
      if (jObj.containsKey("contactAddrUid")) {
        jObj.put("contactAddrType", "居住地址")
      }
    } else {
      if (jObj.containsKey("contactAddrUid")) {
        jObj.put("contactAddrType", "单位地址")
      }
    }

    if (jObj.containsKey("addressFUid")) {
      jObj.put("addressFType", "居住地址")
    }

    if (jObj.containsKey("contactEntityUid")) {
      jObj.put("contactEntityType", "紧急联系人")
    }

    if (jObj.containsKey("familyContactsEntityUid")) {
      jObj.put("familyContactsEntityType", "联系人")
    }

    if (jObj.containsKey("otherContactsEntityUid")) {
      jObj.put("otherContactsEntityType", "联系人")
    }


    jObj
  }

}
