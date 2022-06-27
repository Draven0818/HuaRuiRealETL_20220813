package cn.td.etl.utils

import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.commons.lang3.time.DateFormatUtils

import java.util.Date
import java.util.regex.Matcher

object LogTool {

  private val CONSOLE = true


  def threadInfo(pattern: String, args: Any*): Unit = {
    if (CONSOLE) System.out.println(getMsgPrefix("THREAD INFO") + getContent(pattern, args: _*))
  }

  def threadError(pattern: String, args: Any*): Unit = {
    if (CONSOLE) System.out.println(getMsgPrefix("THREAD ERROR") + getContent(pattern, args: _*))
  }

  def info(pattern: String, args: Any*): Unit = {
    if (CONSOLE) System.out.println(getMsgPrefix("INFO") + getContent(pattern, args: _*))
  }

  def warn(pattern: String, args: Any*): Unit = {
    if (CONSOLE) System.out.println(getMsgPrefix("WARN") + getContent(pattern, args: _*))
  }

  def error(pattern: String, args: Any*): Unit = {
    if (CONSOLE) System.out.println(getMsgPrefix("ERROR") + getContent(pattern, args: _*))
  }

  private def getMsgPrefix(level: String) = DateFormatUtils.format(new Date, "yyyy-MM-dd HH:mm:ss.SSS") + " [" + level + "] DataFlowLogger "

  def getContent(f: String, args: Any*): String = {
    var i = 0
    var format: String = f
    while (format.contains("{}") && i < args.length) {
      var msg: String = null
      if (args(i).isInstanceOf[Exception]) {
        msg = ExceptionUtils.getStackTrace(args(i).asInstanceOf[Throwable])
      }
      else {
        msg = String.valueOf(args(i))
      }
      try {
        format = format.replaceFirst("\\{}", Matcher.quoteReplacement(String.valueOf(msg)))
        i += 1
      }
      catch {
        case e: Exception =>
        //do nothing
      }
    }
    format
  }


}
