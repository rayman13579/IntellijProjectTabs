package at.rayman.projecttabs;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.BitUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class ProjectTabAction extends ToggleAction implements CustomComponentAction, DumbAware {

    private ProjectTabAction previousTab;

    private ProjectTabAction nextTab;

    private String projectName;

    private String displayName;

    private String projectLocation;

    public ProjectTabAction(String projectName, String projectLocation, ProjectTabAction previousTab) {
        super();
        this.projectName = projectName;
        this.displayName = projectName;
        this.projectLocation = projectLocation;
        if (previousTab != null) {
            this.previousTab = previousTab;
            this.nextTab = previousTab.nextTab;
            this.nextTab.previousTab = this;
            this.previousTab.nextTab = this;
        } else {
            this.previousTab = this;
            this.nextTab = this;
        }
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        JButton tab = new JButton(displayName);
        tab.setFocusable(false);
        tab.addActionListener(e -> actionPerformed(AnActionEvent.createFromDataContext(place, presentation, DataManager.getInstance().getDataContext())));
        return tab;
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return false;
        }

        boolean isCurrentTab = projectLocation.equals(project.getPresentableUrl());
        JButton tab = (JButton) e.getPresentation().getClientProperty(COMPONENT_KEY);
        if (tab != null) {
            tab.setEnabled(!isCurrentTab);
            tab.setText(displayName);
        }
        return isCurrentTab;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean selected) {
        Project project = findProject();
        if (project == null) {
            return;
        }
        JFrame projectFrame = WindowManager.getInstance().getFrame(project);
        if (projectFrame == null) {
            return;
        }

        int frameState = projectFrame.getExtendedState();
        if (BitUtil.isSet(frameState, Frame.ICONIFIED)) {
            projectFrame.setExtendedState(BitUtil.set(frameState, Frame.ICONIFIED, false));
        }
        projectFrame.toFront();
        IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> {
            Component mostRecentFocusOwner = projectFrame.getMostRecentFocusOwner();
            if (mostRecentFocusOwner != null) {
                IdeFocusManager.getGlobalInstance().requestFocus(mostRecentFocusOwner, true);
            }
        });
    }

    private Project findProject() {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            if (projectLocation.equals(project.getPresentableUrl())) {
                return project;
            }
        }
        return null;
    }

    public ProjectTabAction getPreviousTab() {
        return previousTab;
    }

    public ProjectTabAction getNextTab() {
        return nextTab;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void dispose() {
        if (previousTab == this) {
            assert nextTab == this;
            return;
        }
        if (nextTab == this) {
            assert false;
            return;
        }
        previousTab.nextTab = nextTab;
        nextTab.previousTab = previousTab;
    }

}
