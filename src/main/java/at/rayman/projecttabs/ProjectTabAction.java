package at.rayman.projecttabs;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class ProjectTabAction extends DumbAwareAction implements CustomComponentAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        Project tmpProject = ProjectManager.getInstance().getDefaultProject();
        JBTabsImpl tabs = (JBTabsImpl) JBTabsFactory.createTabs(tmpProject);
        return tabs.getComponent();
    }

    @Override
    public void updateCustomComponent(@NotNull JComponent component, @NotNull Presentation presentation) {
        Project project = CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(component));
        if (project != null && TabManager.getInstance().hasPlaceholderProject(project)) {
            JBTabs tabs = (JBTabs) component;
            tabs.getPresentation().setTabDraggingEnabled(true);
            tabs.addListener(new ProjectTabListener());
            TabManager.getInstance().addProjectTabs(project, tabs);
            TabManager.getInstance().selectTab(project, project.getName());
        }
        if (component != null && component.getParent() != null) {
            component.getParent().getWidth();
        }
    }
}
