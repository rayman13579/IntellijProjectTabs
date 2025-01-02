package at.rayman.projecttabs;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;

import javax.swing.*;
import java.util.*;

public class TabManager {

    private static final TabManager INSTANCE = new TabManager();

    private final Map<Project, ProjectTabs> projectTabsMap = new HashMap<>();

    private List<TabInfo> referenceTabs = new ArrayList<>();

    private TabManager() {
    }

    public static TabManager getInstance() {
        return INSTANCE;
    }

    public void addPlaceholderProject(Project project) {
        projectTabsMap.put(project, null);
    }

    public boolean hasPlaceholderProject(Project project) {
        return projectTabsMap.containsKey(project) && projectTabsMap.get(project) == null;
    }

    public void addProjectTabs(Project project, ProjectTabs projectTabs) {
        projectTabsMap.put(project, projectTabs);
        TabInfo newTab = new TabInfo(new JPanel()).setText(project.getName());
        referenceTabs.add(newTab);
        for (Map.Entry<Project, ProjectTabs> tabsEntry : projectTabsMap.entrySet()) {
            if (tabsEntry.getValue() != null) {
                if (tabsEntry.getKey().equals(project)) {
                    referenceTabs.forEach(tab -> tabsEntry.getValue().addTab(tab));
                } else {
                    tabsEntry.getValue().addTab(newTab);
                }

                ProjectTabAction action = (ProjectTabAction) ActionManager.getInstance().getAction("ProjectTabs");
                action.adjustTabsWidth(tabsEntry.getValue().getComponent().getParent());
            }
        }
    }

    public void removeProjectTabs(Project project) {
        projectTabsMap.remove(project);
        referenceTabs.removeIf(tab -> tab.getText().equals(project.getName()));
        for (ProjectTabs tab : projectTabsMap.values()) {
            for (TabInfo tabInfo : tab.getTabs()) {
                if (tabInfo.getText().equals(project.getName())) {
                    tab.removeTab(tabInfo);
                    break;
                }
            }
            ProjectTabAction action = (ProjectTabAction) ActionManager.getInstance().getAction("ProjectTabs");
            action.adjustTabsWidth(tab.getComponent().getParent());
        }
    }

    public void selectTab(Project project, String tabName) {
        ProjectTabs tabs = projectTabsMap.get(project);
        for (TabInfo tabInfo : tabs.getTabs()) {
            if (tabInfo.getText().equals(tabName)) {
                tabs.select(tabInfo, false);
                break;
            }
        }
    }

    public void reorderTabs() {
        for (ProjectTabs tabs : projectTabsMap.values()) {
            if (!tabs.getTabs().equals(referenceTabs)) {
                referenceTabs = new ArrayList<>(tabs.getTabs());
                break;
            }
        }
        for (ProjectTabs tabs : projectTabsMap.values()) {
            tabs.getTabs().sort(Comparator.comparing(tabInfo -> referenceTabs.indexOf(tabInfo)));
            tabs.reorderTabs(referenceTabs);
        }
    }
}
