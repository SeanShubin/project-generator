package com.seanshubin.project.generator.domain

import org.scalatest.FunSuite

class VersionUtilTest extends FunSuite {
  test("is production version") {
    assert(VersionUtil.isProductionVersion("2.3.1") === true)
    assert(VersionUtil.isProductionVersion("2.4.0-RC1") === false)
    assert(VersionUtil.isProductionVersion("2.6.1-rc1") === false)
    assert(VersionUtil.isProductionVersion("2.7.4.final") === false)
    assert(VersionUtil.isProductionVersion("2.7.7.RC1") === false)
    assert(VersionUtil.isProductionVersion("2.8.0.Beta1-RC1") === false)
    assert(VersionUtil.isProductionVersion("2.8.0.r18462-b20090811081019") === false)
    assert(VersionUtil.isProductionVersion("2.11.0-M1") === false)
    assert(VersionUtil.isProductionVersion("2.12.0-RC1-be43eb5") === false)
  }
  test("jetty style versions") {
    assert(VersionUtil.isProductionVersion("9.4.0.M1") === false)
    assert(VersionUtil.isProductionVersion("9.4.0.RC3") === false)
    assert(VersionUtil.isProductionVersion("9.4.6.v20170531") === true)
    assert(VersionUtil.isProductionVersion("9.4.7.RC0") === false)
    assert(VersionUtil.isProductionVersion("9.4.8.v20171121") === true)
  }
}
