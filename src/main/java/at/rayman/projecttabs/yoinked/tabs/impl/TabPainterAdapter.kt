// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package at.rayman.projecttabs.yoinked.tabs.impl

import at.rayman.projecttabs.yoinked.tabs.JBTabPainter
import at.rayman.projecttabs.yoinked.tabs.impl.themes.TabTheme
import java.awt.Graphics

interface TabPainterAdapter {
  fun paintBackground(label: TabLabel, g: Graphics, tabs: JBTabsImpl)
  val tabPainter: JBTabPainter
  fun getTabTheme(): TabTheme = tabPainter.getTabTheme()
}
