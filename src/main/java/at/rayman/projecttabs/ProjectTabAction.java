package at.rayman.projecttabs;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ProjectTabAction extends AnAction implements CustomComponentAction, DumbAware {

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        JBTabs tabs = JBTabsFactory.createTabs(ProjectManager.getInstance().getDefaultProject());
        for (int i = 0; i < 12; i++) {
            TabInfo tabInfo = new TabInfo(new JPanel());
            tabInfo.setText("Tab " + i);
            tabs.addTab(tabInfo);
        }

        return tabs.getComponent();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

}
