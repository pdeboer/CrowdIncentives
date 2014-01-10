package controllers.utils

import java.text.{DecimalFormat, NumberFormat, SimpleDateFormat}

/**
 * @author pdeboer
 *         First created on 10/12/13 at 17:58
 */
object Config {
  val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS")

  val df = NumberFormat.getInstance().asInstanceOf[DecimalFormat]
}
