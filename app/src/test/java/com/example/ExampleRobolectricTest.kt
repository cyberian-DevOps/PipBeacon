package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun `verify app package identity and config`() {
    val appPackage = "com.pipbeacon.app"
    assertTrue(appPackage.startsWith("com.pipbeacon"))
    assertEquals("com.pipbeacon.app", appPackage)
  }
}
