package at.rayman.projecttabs;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Key;
import com.intellij.ui.tabs.TabInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectTabAction extends AnAction implements CustomComponentAction, DumbAware {

    private static final Key<Integer> PROJECT_KEY = Key.create("TABS_PROJECT_KEY");

    private JPanel tab;

    private MyTabsTest tabs;

    private static final List<TabInfo> openTabs = new ArrayList<>();

    public ProjectTabAction() {
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        tab = new JPanel();
        tabs = new MyTabsTest(ProjectManager.getInstance().getDefaultProject());
        tabs.setTabDraggingEnabled(true);
        tab.add(tabs);
        for (int i = 0; i < 12; i++) {
            addTab("Tab " + i);
        }

        return tab;
    }

    private void addTab(String name) {
        JSeparator separator = new JSeparator();
        separator.setSize(1, 1);
        TabInfo tabInfo = new TabInfo(separator);
        tabInfo.setText(name);
        tabs.addTab(tabInfo);
    }

    public void addTab(Project project) {
        JSeparator separator = new JSeparator();
        separator.setSize(1, 1);
        TabInfo tabInfo = new TabInfo(separator);
        tabInfo.setText(project.getName());
        JComponent component = tabInfo.getComponent();
        component.putClientProperty(PROJECT_KEY, project);
        openTabs.add(tabInfo);
    }

    public void removeTab(Project project) {
        for (TabInfo tabInfo : ((MyTabsTest) tab.getComponents()[0]).getTabs()) {
            Project tabProject = (Project) tabInfo.getComponent().getClientProperty(PROJECT_KEY);
            if (project.equals(tabProject)) {
                openTabs.remove(tabInfo);
            }
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

}
