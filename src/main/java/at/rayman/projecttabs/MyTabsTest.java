package at.rayman.projecttabs;

import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.impl.headertoolbar.MainToolbar;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class MyTabsTest extends JBTabsImpl {

    public MyTabsTest(@NotNull Project project) {
        super(project);
    }

    @Override
    public @NotNull TabInfo addTab(@NotNull TabInfo tabInfo) {
        return super.addTab(tabInfo);
    }

    @Override
    public @NotNull Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        MainToolbar mainToolbar = (MainToolbar) this.getParent().getParent().getParent();
        if (mainToolbar == null) {
            return preferredSize;
        }
        Component[] components = mainToolbar.getComponents();
        ActionToolbar leftToolbar = (ActionToolbar) components[0];
        ActionToolbar centerToolbar = (ActionToolbar) components[1];
        ActionToolbar rightToolbar = (ActionToolbar) components[2];
        int mainWidth = mainToolbar.getSize().width;
        int leftWidth = leftToolbar.getComponent().getSize().width;
        int centerWidth = centerToolbar.getComponent().getSize().width;
        int rightWidth = rightToolbar.getComponent().getSize().width;

        int width = (mainWidth / 3 * 2) - (Math.max(leftWidth, rightWidth));

        if (preferredSize.width < width) {
       //     return preferredSize;
        }
        return new Dimension(width, super.getPreferredSize().height);
    }

    @Override
    public void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof JSeparator) {
            return;
        }
        super.addImpl(comp, constraints, index);
    }

}
