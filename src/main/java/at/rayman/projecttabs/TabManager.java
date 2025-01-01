package at.rayman.projecttabs;

import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabManager {

    private static final TabManager INSTANCE = new TabManager();

    private final Map<Project, JBTabs> tabsList = new HashMap<>();

    private final List<TabInfo> tabs = new ArrayList<>();

    private TabManager() {
    }

    public static TabManager getInstance() {
        return INSTANCE;
    }

    public void addPlaceholderProject(Project project) {
        tabsList.put(project, null);
    }

    public boolean hasPlaceholderProject(Project project) {
        return tabsList.containsKey(project) && tabsList.get(project) == null;
    }

    public void addProjectTabs(Project project, JBTabs tabs) {
        tabsList.put(project, tabs);
        this.tabs.add(new TabInfo(new JPanel()).setText(project.getName()));
        for (JBTabs tab : tabsList.values()) {
            if (tab != null) {
                tab.removeAllTabs();
                this.tabs.forEach(tab::addTab);
            }
        }
    }

    public void removeProjectTabs(Project project) {
        tabsList.remove(project);
        this.tabs.removeIf(tab -> tab.getText().equals(project.getName()));
        for (JBTabs tab : tabsList.values()) {
            for (TabInfo tabInfo : tab.getTabs()) {
                if (tabInfo.getText().equals(project.getName())) {
                    tab.removeTab(tabInfo);
                    break;
                }
            }
        }
    }

    public void selectTab(Project project, String tabName) {
        JBTabs tabs = tabsList.get(project);
        for (TabInfo tabInfo : tabs.getTabs()) {
            if (tabInfo.getText().equals(tabName)) {
                tabs.select(tabInfo, false);
                break;
            }
        }
    }

}
