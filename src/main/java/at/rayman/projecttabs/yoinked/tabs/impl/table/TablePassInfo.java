// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package at.rayman.projecttabs.yoinked.tabs.impl.table;

import at.rayman.projecttabs.yoinked.tabs.TabInfo;
import at.rayman.projecttabs.yoinked.tabs.impl.JBTabsImpl;
import at.rayman.projecttabs.yoinked.tabs.impl.LayoutPassInfo;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * @deprecated use {@link .impl.multiRow.MultiRowLayout}
 * with {@link .impl.multiRow.MultiRowPassInfo} instead
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
public final class TablePassInfo extends LayoutPassInfo {
  final List<TableRow> table = new ArrayList<>();
  public final Rectangle toFitRec;
  public final Rectangle tabRectangle = new Rectangle();
  final Map<TabInfo, TableRow> myInfo2Row = new HashMap<>();
  final JBTabsImpl myTabs;
  public final List<TabInfo> invisible = new ArrayList<>();
  final Map<TabInfo, Integer> lengths = new LinkedHashMap<>();
  final Map<TabInfo, Rectangle> bounds = new HashMap<>();
  int requiredLength = 0;

  TablePassInfo(TableLayout layout, List<TabInfo> visibleInfos) {
    super(visibleInfos);
    myTabs = layout.myTabs;
    final Insets insets = myTabs.getLayoutInsets();
    toFitRec =
      new Rectangle(insets.left, insets.top, myTabs.getWidth() - insets.left - insets.right, myTabs.getHeight() - insets.top - insets.bottom);
  }

  public boolean isInSelectionRow(final TabInfo tabInfo) {
    final TableRow row = myInfo2Row.get(tabInfo);
    final int index = table.indexOf(row);
    return index != -1 && index == table.size() - 1;
  }

  @Override
  public int getRowCount() {
    return table.size();
  }

  @Override
  public Rectangle getHeaderRectangle() {
    return (Rectangle)tabRectangle.clone();
  }

  @Override
  public int getRequiredLength() {
    return requiredLength;
  }

  @Override
  public int getScrollExtent() {
    return !moreRect.isEmpty() ? moreRect.x - toFitRec.x - myTabs.getActionsInsets().left
           : table.size() > 1 || entryPointRect.isEmpty() ? toFitRec.width
           : entryPointRect.x - toFitRec.x - myTabs.getActionsInsets().left;
  }
}
