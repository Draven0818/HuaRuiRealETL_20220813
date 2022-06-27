package cn.td.etl.utils.trans

abstract class GenUids[T] {
  def genUid01(bean: T): T
}
