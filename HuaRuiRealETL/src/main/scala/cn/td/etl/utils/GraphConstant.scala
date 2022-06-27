package cn.td.etl.utils

object GraphConstant {
  //输入组件
  val VERTEX_UNIQUEID: String = "uid"
  val VERTEX: String = "vertex"
  val EDGE: String = "edge"
  val VERTEX_ID: String = "id"
  val LABEL: String = "label"
  val PROPS: String = "props"
  val SRC_VERTEXID: String = "srcId"
  val SRC_VERTEXLABEL: String = "srcLabel"
  val DST_VERTEXID: String = "dstId"
  val DST_VERTEXLABEL: String = "dstLabel"
  val TYPE: String = "type"
  val FIELD_SCHEMA: String = "schema"
  val INDEX_SCHEMA: String = "index"
  val JANUSGRAPH: String = "janus"
  val NEO4J: String = "neo4j"
  val NEBULA: String = "nebula"
  val DB: String = "db"
  val EXTRACT_TYPE: String = "extractType"
  /**
   * 数据类型
   */
  val MAX_GROUP_NODE_NUM: Int = 1000
  val DEEPEST_FIND_DEGREE: Int = 50
  val NODE_KIND_CODE: String = "1"
  val LINK_KIND_CODE: String = "2"
  val ALL_KIND_CODE: String = "3"
  /**
   * 功能类型
   */
  val FUNCTION_BLANK: String = "blank"
  val FUNCTION_SEARCH_NODE: String = "searchNode"
  val GROUP_NAME: String = "groupName"
  //图名称

  val netDiscoveryList = List("louvain", "lpa", "netComponent")

  /**
   * 图库导入组件
   */
  val INTERNAL_PROP_DEFAULT_TIME = "_default_time"
  val OLD_PROP_DEFAULT_TIME = "defaultTime"
  val INTERNAL_EDGE_MULTI_FIELD = "_edge_multi"
  val INTERNAL_EDGE_UID = "edge_uid"
  val PROP_START_TIME = "start_time"

  val EDGE_TYPE_MULTI: String = "MULTI"
  val EDGE_TYPE_SIMPLE: String = "SIMPLE"


  val SCHEMA_DATA_TYPE = "dataType"
  val SCHEMA_CARDINALITY = "cardinality"

  val HBASE_GRAPH_SINK = "hbase"
  val HIVE_GRAPH_SINK = "hive"
}

