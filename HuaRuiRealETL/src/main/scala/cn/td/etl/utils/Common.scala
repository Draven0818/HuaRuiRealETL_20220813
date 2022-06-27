package cn.td.etl.utils

object Common {

  /**
   * 时间格式format
   */
  val SIMPLEDATEFORMAT = "yyyy-MM-dd HH:mm:ss:SSS"

  /**
   * 手机验证正则
   */
  val MREG = "/^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$/"

  /**
   * uid生成前添加的label
   */
  val UID_LABEL_PERSON = "person"

  /**
   * uid生成前添加的label
   */
  val UID_LABEL_DEVICE = "device"

  /**
   * uid生成前添加的label
   */
  val UID_LABEL_COMPANY = "company"

  /**
   * uid生成前添加的label
   */
  val UID_LABEL_TEL = "tel"

  /**
   * uid生成前添加的label
   */
  val UID_LABEL_ADDRESS = "address"

  /**
   * uid生成前添加的label
   */
  val UID_LABEL_ENTRY = "entry"


  val UID_FIELD_LIST1: List[(String, String)] =
    List(
      /**
       * 申请件字段
       */
      ("applyId", UID_LABEL_ENTRY),

      /**
       * 自然人字段
       */
      ("prcid", UID_LABEL_PERSON),
      ("certId", UID_LABEL_PERSON),

      /**
       * 手机号字段
       */
      ("phonenumber", UID_LABEL_TEL),
      ("mobilePhone", UID_LABEL_TEL),
      ("bankCardMobile", UID_LABEL_TEL),
      ("familyContactsMobile", UID_LABEL_TEL),
      ("otherContactsMobile", UID_LABEL_TEL),
      ("ContactsRelationTelUid",UID_LABEL_TEL),
      ("PersonBankTelUid",UID_LABEL_TEL),

      /**
       * 地址字段
       */
      ("addressF", UID_LABEL_ADDRESS),
      ("idAddr", UID_LABEL_ADDRESS),
      ("contactAddr", UID_LABEL_ADDRESS),

      /**
       * 公司字段
       */
      ("companyName", UID_LABEL_COMPANY),

      /**
       * 设备字段
       */
      ("deviceId", UID_LABEL_COMPANY)
    )


  val UID_FIELD_LIST2: List[(String, String, String, String)] =
    List(
      /**
       * 自然人字段
       */
      ("contactName", "contactPhone", "contactEntity", UID_LABEL_PERSON),
      ("familyContactsName", "familyContactsMobile", "familyContactsEntity", UID_LABEL_PERSON),
      ("otherContactsName", "otherContactsMobile", "otherContactsEntity", UID_LABEL_PERSON)
    )


  val UID_BLACK_LIST1: List[(String, String)] =
    List(
      //手机黑名单
      ("fieldString", UID_LABEL_TEL)
    )

  val UID_BLACK_LIST2: List[(String, String)] =
    List(
      //身份证黑名单
      ("fieldString", UID_LABEL_PERSON)
    )

  val UID_ENTRY_STATS_LIST: List[(String, String)] =
    List(
      //进件状态
      ("entryId", UID_LABEL_ENTRY)
    )


}


/**
 * 度小满授信接口样例类
 *
 * @param applyId                 请求流水号
 * @param productCode             产品编号
 * @param workflowcode            决策流编号
 * @param preApplicationId        授信申请编号
 * @param productType             产品类型
 * @param customerType            客户类型
 * @param prcid                   身份证号
 * @param bankcard                银行卡号
 * @param name                    姓名
 * @param phonenumber             手机号
 * @param initialAmount           度小满授信度
 * @param creditValidityDays      度小满授信有效期
 * @param risCode                 度小满策略码
 * @param taxMonthlyIncomeSection 税后月收入（区间）
 * @param ocrExpdate              ocr识别失效日期
 * @param r21P12FinallyInterest   等额本息12期利息（循环产品）
 * @param sexF                    性别
 * @param nationalityF            国籍
 * @param occupationF             职业
 * @param addressF                地址
 * @param certTypeF               证件种类
 * @param creditType              授信类型
 * @param pairSimilarity1         相似系数(身份证)
 * @param pairSimilarity2         相似系数(人脸)
 * @param basicPicSource1         身份证标准对比图片来源
 * @param basicPicSource2         人脸标准对比图片来源
 * @param busDept                 业务部门
 * @param busName                 业务经理
 */
case class DXMSX(applyId: String, productCode: String, workflowcode: String, preApplicationId: String, productType: String, customerType: String, prcid: String, bankcard: String, name: String, phonenumber: String, initialAmount: String, creditValidityDays: String, risCode: String, taxMonthlyIncomeSection: String, ocrExpdate: String, r21P12FinallyInterest: String, sexF: String, nationalityF: String, occupationF: String, addressF: String, certTypeF: String, creditType: String, pairSimilarity1: String, pairSimilarity2: String, basicPicSource1: String, basicPicSource2: String, busDept: String, busName: String) extends Base

/**
 * 度小满用信接口
 *
 * @param applyId                  请求流水号
 * @param productCode              产品编号
 * @param workflowcode             决策流编号
 * @param preApplicationId         授信申请编号
 * @param productType              产品类型
 * @param customerType             客户类型
 * @param prcid                    身份证号
 * @param bankcard                 银行卡号
 * @param name                     姓名
 * @param phonenumber              手机号
 * @param creditAmount             授信额度
 * @param cashAmount               消费金额
 * @param orderId                  度小满用信和提款单ID
 * @param risCode                  度小满策略码
 * @param unionLoanUsed            贷款用途（循环产品）
 * @param ocrExpdate               ocr识别失效日期
 * @param term                     期数
 * @param dailyInterestRate        日利息日利率（万分比）
 * @param dailyPenaltyRate         罚息日罚息（万分比）
 * @param compreAnnualInterestRate 综合年化利率
 * @param repayMode                还款方式
 * @param sexF                     性别
 * @param nationalityF             国籍
 * @param occupationF              职业
 * @param addressF                 地址
 * @param certTypeF                证件种类
 * @param contactPhone             紧急联系人电话
 * @param contactName              紧急联系人名称
 * @param contactRalation          紧急联系人关系
 * @param firstRepayDate           首个还款日期
 * @param creditRate               授信利率
 * @param busDept                  业务部门
 * @param busName                  业务经理
 */
case class DXMYX(applyId: String, productCode: String, workflowcode: String, preApplicationId: String, productType: String, customerType: String, prcid: String, bankcard: String, name: String, phonenumber: String, creditAmount: String, cashAmount: String, orderId: String, risCode: String, unionLoanUsed: String, ocrExpdate: String, term: String, dailyInterestRate: String, dailyPenaltyRate: String, compreAnnualInterestRate: String, repayMode: String, sexF: String, nationalityF: String, occupationF: String, addressF: String, certTypeF: String, contactPhone: String, contactName: String, contactRalation: String, firstRepayDate: String, creditRate: String, busDept: String, busName: String) extends Base

/**
 * 乐信授信申请接口
 *
 * @param applyId              请求流水号
 * @param productCode          产品编号
 * @param workflowcode         决策流编号
 * @param preApplicationId     授信申请编号
 * @param productType          产品类型
 * @param channel              产品渠道
 * @param customerType         客户类型
 * @param customerName         客户名称
 * @param certType             客户证件类型
 * @param certId               客户证件号码
 * @param idCardValidDate      证件有效期起
 * @param idCardExpireDate     证件有效期止
 * @param age                  年龄
 * @param nationality          国籍
 * @param maritalStatus        婚姻状况
 * @param userIndustryCategory 客户行业
 * @param creditAmount         授信金额
 * @param nation               民族
 * @param sex                  性别
 * @param customerSerialNo     客户流水号
 * @param coreCustomerId       核心客户号
 * @param mobilePhone          手机号
 * @param idAddr               身份证地址
 * @param familyRelation       第一联系人
 * @param familyContactsName   第一联系人姓名
 * @param familyContactsMobile 第一联系人手机号码
 * @param careerCategory       客户职业
 * @param contactAddr          居住地址
 * @param creditType           授信类型
 * @param returnCode1          返回码
 * @param pairResult1          比对结果
 * @param pairSimilarity1      相似系数
 * @param errorMessage1        错误信息
 * @param returnCode2          返回码
 * @param pairResult2          比对结果
 * @param pairSimilarity2      相似系数
 * @param errorMessage2        错误信息
 * @param basicPicSource       标准对比图片来源
 * @param isSignCreInqAut      是否签署征信查询授权书
 * @param creInqAutStoPath     征信查询授权书路径
 * @param busDept              业务部门
 * @param busName              业务经理
 */
case class LXSX(applyId: String, productCode: String, workflowcode: String, preApplicationId: String, productType: String, channel: String, customerType: String, customerName: String, certType: String, certId: String, idCardValidDate: String, idCardExpireDate: String, age: String, nationality: String, maritalStatus: String, userIndustryCategory: String, creditAmount: String, nation: String, sex: String, customerSerialNo: String, coreCustomerId: String, mobilePhone: String, idAddr: String, familyRelation: String, familyContactsName: String, familyContactsMobile: String, careerCategory: String, contactAddr: String, creditType: String, returnCode1: String, pairResult1: String, pairSimilarity1: String, errorMessage1: String, returnCode2: String, pairResult2: String, pairSimilarity2: String, errorMessage2: String, basicPicSource: String, isSignCreInqAut: String, creInqAutStoPath: String, busDept: String, busName: String) extends Base

/**
 * 乐信用信申请接口
 *
 * @param applyId              请求流水号
 * @param productCode          产品编号
 * @param workflowcode         决策流编号
 * @param preApplicationId     授信申请编号
 * @param productType          产品类型
 * @param channel              产品渠道
 * @param customerType         客户类型
 * @param customerName         客户名称
 * @param certType             客户证件类型
 * @param certId               客户证件号码
 * @param idCardExpireDate     证件有效期止
 * @param idCardValidDate      证件有效期起
 * @param age                  年龄
 * @param sex                  性别
 * @param customerSerialNo     客户流水号
 * @param coreCustomerId       核心客户号
 * @param mobilePhone          手机号
 * @param debitAccountName     借款人收款户名
 * @param debitOpenAccountBank 收款人银行卡开户行
 * @param debitAccountNo       收款人银行卡卡号
 * @param annualRate           合作方利率
 * @param hrannualRate         华瑞利率
 * @param fixedBillDay         固定出账日
 * @param fixedRepayDay        固定还款日
 * @param loanUsage            借款用途
 * @param loanAmount           申请金额
 * @param loanTenor            申请期限
 * @param loanDate             交易日期
 * @param repaymentMethod      还款方式
 * @param accessChannel        进件渠道
 * @param familyRelation       第一联系人
 * @param familyContactsName   第一联系人姓名
 * @param familyContactsMobile 第一联系人手机号码
 * @param careerCategory       客户职业
 * @param companyName          公司名称
 * @param isSignCreInqAut      是否签署征信查询授权书
 * @param creInqAutStoPath     征信查询授权书路径
 * @param busDept              业务部门
 * @param busName              业务经理
 */
case class LXYX(applyId: String, productCode: String, workflowcode: String, preApplicationId: String, productType: String, channel: String, customerType: String, customerName: String, certType: String, certId: String, idCardExpireDate: String, idCardValidDate: String, age: String, sex: String, customerSerialNo: String, coreCustomerId: String, mobilePhone: String, debitAccountName: String, debitOpenAccountBank: String, debitAccountNo: String, annualRate: String, hrannualRate: String, fixedBillDay: String, fixedRepayDay: String, loanUsage: String, loanAmount: String, loanTenor: String, loanDate: String, repaymentMethod: String, accessChannel: String, familyRelation: String, familyContactsName: String, familyContactsMobile: String, careerCategory: String, companyName: String, isSignCreInqAut: String, creInqAutStoPath: String, busDept: String, busName: String) extends Base

/**
 * 饿了么授信申请接口
 *
 * @param applyId              请求流水号
 * @param productCode          产品编号
 * @param workflowcode         决策流编号
 * @param preApplicationId     授信申请编号
 * @param productType          产品类型
 * @param channel              产品渠道
 * @param productId            产品编号
 * @param customerType         客户类型
 * @param customerName         客户名称
 * @param certType             客户证件类型
 * @param certId               客户证件号码
 * @param certExpireDate       证件有效期
 * @param age                  年龄
 * @param sex                  性别
 * @param customerSerialNo     客户流水号
 * @param coreCustomerId       核心客户号
 * @param mobilePhone          手机号
 * @param idAddr               身份证地址
 * @param eleScore1            评分1
 * @param eleScore2            评分2
 * @param eleScore3            评分3
 * @param eleScore4            评分4
 * @param eleScore5            评分5
 * @param eleScore6            评分6
 * @param eleScore7            评分7
 * @param eleScore8            评分8
 * @param eleScore9            评分9
 * @param eleScore10           评分10
 * @param platformLabel        平台标签
 * @param cpVersion            合作方账户号
 * @param deviceId             设备序列号
 * @param familyRelation       第一联系人
 * @param familyContactsName   第一联系人姓名
 * @param familyContactsMobile 第一联系人手机号码
 * @param careerCategory       职业类型
 * @param companyName          公司名称
 * @param contactAddrProvince  工作地址-省
 * @param contactAddrCity      工作地址-市
 * @param contactAddrTown      工作地址-区/县
 * @param companyType          公司类型
 * @param incomeLevel          收入水平
 * @param highestEducation     最高学历
 * @param contactAddr          通讯地址-详细地址
 * @param otherRelation        第二联系人关系
 * @param otherContactsName    第二联系人姓名
 * @param userRealIp           用户ip
 * @param otherContactsMobile  第二联系人手机号码
 * @param creditType           授信类型
 * @param returnCode1          返回码
 * @param pairResult1          比对结果
 * @param pairSimilarity1      相似系数
 * @param errorMessage1        错误信息
 * @param returnCode2          返回码
 * @param pairResult2          比对结果
 * @param pairSimilarity2      相似系数
 * @param errorMessage2        错误信息
 * @param basicPicSource       标准对比图片来源
 * @param isSignCreInqAut      是否签署征信查询授权书
 * @param creInqAutStoPath     征信查询授权书路径
 * @param busDept              业务部门
 * @param busName              业务经理
 */
case class ELMSX(applyId: String, productCode: String, workflowcode: String, preApplicationId: String, productType: String, channel: String, productId: String, customerType: String, customerName: String, certType: String, certId: String, certExpireDate: String, age: String, sex: String, customerSerialNo: String, coreCustomerId: String, mobilePhone: String, idAddr: String, eleScore1: String, eleScore2: String, eleScore3: String, eleScore4: String, eleScore5: String, eleScore6: String, eleScore7: String, eleScore8: String, eleScore9: String, eleScore10: String, platformLabel: String, cpVersion: String, deviceId: String, familyRelation: String, familyContactsName: String, familyContactsMobile: String, careerCategory: String, companyName: String, contactAddrProvince: String, contactAddrCity: String, contactAddrTown: String, companyType: String, incomeLevel: String, highestEducation: String, contactAddr: String, otherRelation: String, otherContactsName: String, userRealIp: String, otherContactsMobile: String, creditType: String, returnCode1: String, pairResult1: String, pairSimilarity1: String, errorMessage1: String, returnCode2: String, pairResult2: String, pairSimilarity2: String, errorMessage2: String, basicPicSource: String, isSignCreInqAut: String, creInqAutStoPath: String, busDept: String, busName: String) extends Base

/**
 * 饿了么用信申请接口
 *
 * @param applyId              请求流水号
 * @param productCode          产品编号
 * @param workflowcode         决策流编号
 * @param preApplicationId     授信申请编号
 * @param productId            产品编号
 * @param productType          产品类型
 * @param channel              产品渠道
 * @param customerType         客户类型
 * @param customerName         客户名称
 * @param certType             客户证件类型
 * @param certId               客户证件号码
 * @param certExpireDate       证件有效期
 * @param age                  年龄
 * @param sex                  性别
 * @param customerSerialNo     客户流水号
 * @param coreCustomerId       核心客户号
 * @param mobilePhone          手机号
 * @param bankCardName         银行户名
 * @param bankCardNo           银行卡号码
 * @param bankCardMobile       银行端预留手机号
 * @param loanUsage            借款用途
 * @param loanAmount           申请金额
 * @param loanTenor            申请期限
 * @param loanDate             交易日期
 * @param repaymentMethod      还款方式
 * @param deviceId             设备序列号
 * @param accessChannel        进件渠道
 * @param eleScore1            评分1
 * @param eleScore2            评分2
 * @param eleScore3            评分3
 * @param eleScore4            评分4
 * @param eleScore5            评分5
 * @param eleScore6            评分6
 * @param eleScore7            评分7
 * @param eleScore8            评分8
 * @param eleScore9            评分9
 * @param eleScore10           评分10
 * @param discountsCode        优惠卷code
 * @param familyRelation       第一联系人
 * @param familyContactsName   第一联系人姓名
 * @param familyContactsMobile 第一联系人手机号码
 * @param careerCategory       职业类型
 * @param companyName          公司名称
 * @param contactAddrProvince  工作地址-省
 * @param contactAddrCity      工作地址-市
 * @param contactAddrTown      工作地址-区/县
 * @param contactAddr          通讯地址-详细地址
 * @param otherRelation        第二联系人关系
 * @param otherContactsName    第二联系人姓名
 * @param userRealIp           用户ip
 * @param otherContactsMobile  第二联系人手机号码
 * @param isSignCreInqAut      是否签署征信查询授权书
 * @param creInqAutStoPath     征信查询授权书路径
 * @param busDept              业务部门
 * @param busName              业务经理
 */
case class ELMYX(applyId: String, productCode: String, workflowcode: String, preApplicationId: String, productId: String, productType: String, channel: String, customerType: String, customerName: String, certType: String, certId: String, certExpireDate: String, age: String, sex: String, customerSerialNo: String, coreCustomerId: String, mobilePhone: String, bankCardName: String, bankCardNo: String, bankCardMobile: String, loanUsage: String, loanAmount: String, loanTenor: String, loanDate: String, repaymentMethod: String, deviceId: String, accessChannel: String, eleScore1: String, eleScore2: String, eleScore3: String, eleScore4: String, eleScore5: String, eleScore6: String, eleScore7: String, eleScore8: String, eleScore9: String, eleScore10: String, discountsCode: String, familyRelation: String, familyContactsName: String, familyContactsMobile: String, careerCategory: String, companyName: String, contactAddrProvince: String, contactAddrCity: String, contactAddrTown: String, contactAddr: String, otherRelation: String, otherContactsName: String, userRealIp: String, otherContactsMobile: String, isSignCreInqAut: String, creInqAutStoPath: String, busDept: String, busName: String) extends Base