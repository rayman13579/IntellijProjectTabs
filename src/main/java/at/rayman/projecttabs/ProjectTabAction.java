package at.rayman.projecttabs;

import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.JBColor;
import com.intellij.util.BitUtil;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProjectTabAction extends ToggleAction implements CustomComponentAction, DumbAware {

    private static final Icon ICON_CLOSE_HIDDEN = IconLoader.getIcon("closeHidden.svg", ProjectTabAction.class.getClassLoader());

    private static final Border BORDER_SELECTED = JBUI.Borders.customLine(UIManager.getColor("TabbedPane.underlineColor"), 0, 0, 3, 0);

    private static final Border BORDER_EMPTY = JBUI.Borders.empty();

    private String projectName;

    private String displayName;

    private String projectLocation;

    private boolean isHovered = false;

    public ProjectTabAction(String projectName, String projectLocation) {
        super();
        this.projectName = projectName;
        this.displayName = projectName;
        this.projectLocation = projectLocation;
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return buildTab(presentation, place);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return false;
        }
        boolean isCurrentTab = projectLocation.equals(project.getPresentableUrl());
        JPanel tab = (JPanel) e.getPresentation().getClientProperty(COMPONENT_KEY);
        if (tab != null) {
            tab.setBorder(isCurrentTab ? BORDER_SELECTED : BORDER_EMPTY);
            JLabel label = (JLabel) tab.getComponent(0);
            label.setText(displayName);
            if (!isHovered) {
                label.setForeground(isCurrentTab ? JBColor.BLACK : JBColor.DARK_GRAY);
                JLabel icon = (JLabel) tab.getComponent(1);
                icon.setIcon(isCurrentTab ? AllIcons.Actions.Close : ICON_CLOSE_HIDDEN);
            }
        }
        return isCurrentTab;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean selected) {
        bringProjectWindowToFront();
    }

    public void bringProjectWindowToFront() {
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

    public String getProjectName() {
        return projectName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    private JPanel buildTab(Presentation presentation, String place) {
        JPanel tab = new JPanel();
        JLabel label = new JLabel(displayName);
        JLabel icon = new JLabel(AllIcons.Actions.Close);
        tab.add(label);
        tab.add(icon);
        tab.setBorder(BORDER_SELECTED);

        icon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2) {
                    ProjectManager.getInstance().closeAndDispose(findProject());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                icon.setIcon(AllIcons.Actions.CloseHovered);
                label.setForeground(JBColor.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                icon.setIcon(AllIcons.Actions.Close);
            }
        });
        MouseAdapter tabMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    actionPerformed(AnActionEvent.createFromDataContext(place, presentation, DataManager.getInstance().getDataContext()));
                }
                if (e.getButton() == MouseEvent.BUTTON2) {
                    ProjectManager.getInstance().closeAndDispose(findProject());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (tab.getBorder().equals(BORDER_EMPTY)) {
                    isHovered = true;
                    label.setForeground(JBColor.BLACK);
                    icon.setIcon(AllIcons.Actions.Close);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (tab.getBorder().equals(BORDER_EMPTY)) {
                    isHovered = false;
                    label.setForeground(JBColor.DARK_GRAY);
                    icon.setIcon(ICON_CLOSE_HIDDEN);
                }
            }
        };
        tab.addMouseListener(tabMouseAdapter);
        label.addMouseListener(tabMouseAdapter);
        return tab;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

}
