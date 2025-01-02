package at.rayman.projecttabs;

import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class ProjectTabs extends JBTabsImpl {

    public ProjectTabs(@NotNull Project project) {
        super(project);
    }

    public void reorderTabs(List<TabInfo> referenceTabs) {
        sortTabs(Comparator.comparing(referenceTabs::indexOf));
    }

}
