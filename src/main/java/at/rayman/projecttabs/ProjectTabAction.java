package at.rayman.projecttabs;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.Optional;


public class ProjectTabAction extends DumbAwareAction implements CustomComponentAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        Project tmpProject = ProjectManager.getInstance().getDefaultProject();
        ProjectTabs tabs = new ProjectTabs(tmpProject);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabs.getComponent(), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void updateCustomComponent(@NotNull JComponent component, @NotNull Presentation presentation) {
        Project project = CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(component));
        if (project != null && TabManager.getInstance().hasPlaceholderProject(project)) {
            ProjectTabs tabs = (ProjectTabs) component.getComponent(0);
            tabs.getPresentation().setTabDraggingEnabled(true);
            tabs.addListener(new ProjectTabListener());
            TabManager.getInstance().addProjectTabs(project, tabs);
            TabManager.getInstance().selectTab(project, project.getName());

            WindowManager.getInstance().getFrame(project).addComponentListener(new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent e) {
                    adjustTabsWidth(component);
                }

                @Override
                public void componentMoved(ComponentEvent e) {

                }

                @Override
                public void componentShown(ComponentEvent e) {

                }

                @Override
                public void componentHidden(ComponentEvent e) {

                }
            });
        }
    }

    public void adjustTabsWidth(Container component) {
        if (component != null && component.getParent() != null) {
            ProjectTabs tabs = (ProjectTabs) component.getComponent(0);
            Container mainToolbar = getMainToolbar(component);
            Component[] components = mainToolbar.getComponents();
            Optional<Integer> usedToolbarWidth = Arrays.stream(components)
                .map(c -> (ActionToolbarImpl) c)
                .flatMap(toolbar -> Arrays.stream(toolbar.getComponents()))
                .filter(c -> !c.equals(component))
                .map(Component::getWidth)
                .reduce(Integer::sum);
            if (usedToolbarWidth.isPresent()) {
                int availableWidth = mainToolbar.getWidth() - usedToolbarWidth.get();
                Optional<Integer> neededWidth = tabs.getTabs().stream()
                    .map(t -> tabs.getInfoToLabel().get(t).getPreferredSize().getWidth())
                    .reduce(Double::sum)
                    .map(Double::intValue);
                if (neededWidth.isPresent()) {
                    if (neededWidth.get() > availableWidth) {
                        component.setPreferredSize(new Dimension(availableWidth, mainToolbar.getHeight()));
                    } else {
                        component.setPreferredSize(new Dimension(neededWidth.get() + 1, mainToolbar.getHeight()));
                    }
                }
            }
        }
    }

    private Container getMainToolbar(Container component) {
        Container parent = component.getParent();
        while (parent != null) {
            if (parent.getClass().getName().contains("MainToolbar")) {
                return parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

}
