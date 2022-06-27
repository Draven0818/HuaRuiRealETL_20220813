package cn.td.etl.utils

import scala.beans.BeanProperty

case class IndicatorResult(@BeanProperty key: String,
                           @BeanProperty rowkey: String,
                           @BeanProperty uid: String,
                           @BeanProperty indicatorName: String,
                           @BeanProperty indicatorValue: String,
                           @BeanProperty calTime: String,
                           @BeanProperty token: String)
