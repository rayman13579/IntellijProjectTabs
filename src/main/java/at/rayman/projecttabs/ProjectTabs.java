package at.rayman.projecttabs;

import at.rayman.projecttabs.yoinked.tabs.TabInfo;
import at.rayman.projecttabs.yoinked.tabs.impl.JBEditorTabs;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class ProjectTabs extends JBEditorTabs {

    public ProjectTabs(@NotNull Project project) {
        super(project, Disposer.newDisposable());
    }

    public void reorderTabs(List<TabInfo> referenceTabs) {
        sortTabs(Comparator.comparing(referenceTabs::indexOf));
    }

}
