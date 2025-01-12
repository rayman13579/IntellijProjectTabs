package at.rayman.projecttabs;

import at.rayman.projecttabs.yoinked.tabs.TabInfo;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.project.Project;

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
        List<Project> duplicateProjects = getDuplicateProjectNames(project);
        if (!duplicateProjects.isEmpty()) {
            for (Project duplicateProject : duplicateProjects) {
                referenceTabs.stream()
                    .filter(tab -> tab.getText().equals(duplicateProject.getName()))
                    .forEach(tab -> tab.setText(getDuplicateName(duplicateProject)));
            }
        }
        projectTabsMap.put(project, projectTabs);
        String tabName = duplicateProjects.isEmpty() ? project.getName() : getDuplicateName(project);
        TabInfo newTab = new TabInfo(new JLabel()).setText(tabName);
        referenceTabs.add(newTab);
        for (Map.Entry<Project, ProjectTabs> tabsEntry : projectTabsMap.entrySet()) {
            if (tabsEntry.getValue() != null) {
                if (tabsEntry.getKey().equals(project)) {
                    referenceTabs.forEach(tabsEntry.getValue()::addTab);
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
        List<Project> duplicateProjects = getDuplicateProjectNames(project);
        if (!duplicateProjects.isEmpty()) {
            referenceTabs.removeIf(tab -> tab.getText().equals(getDuplicateName(project)));
            for (Project duplicateProject : duplicateProjects) {
                referenceTabs.stream()
                    .filter(tab -> tab.getText().equals(getDuplicateName(duplicateProject)))
                    .forEach(tab -> tab.setText(duplicateProject.getName()));
            }
        } else {
            referenceTabs.removeIf(tab -> tab.getText().equals(project.getName()));
        }
        for (ProjectTabs tab : projectTabsMap.values()) {
            for (TabInfo tabInfo : tab.getTabs()) {
                String tabName = duplicateProjects.isEmpty() ? project.getName() : getDuplicateName(project);
                if (tabInfo.getText().equals(tabName)) {
                    tab.removeTab(tabInfo);
                    break;
                }
            }
            ProjectTabAction action = (ProjectTabAction) ActionManager.getInstance().getAction("ProjectTabs");
            action.adjustTabsWidth(tab.getComponent().getParent());
        }
    }

    public void selectTab(Project project) {
        ProjectTabs tabs = projectTabsMap.get(project);
        if (tabs == null) {
            return;
        }
        List<Project> duplicateProjects = getDuplicateProjectNames(project);
        for (TabInfo tabInfo : tabs.getTabs()) {
            String duplicateTabName = duplicateProjects.isEmpty() ? project.getName() : getDuplicateName(project);
            if (tabInfo.getText().equals(duplicateTabName)) {
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

    private List<Project> getDuplicateProjectNames(Project newProject) {
        return projectTabsMap.keySet().stream()
            .filter(project -> project.getName().equals(newProject.getName()) && !project.equals(newProject)
                && projectTabsMap.get(project) != null)
            .toList();
    }

    private String getDuplicateName(Project project) {
        int index = project.getPresentableUrl().lastIndexOf('/', project.getPresentableUrl().lastIndexOf('/') - 1);
        return project.getPresentableUrl().substring(index + 1);
    }

}
