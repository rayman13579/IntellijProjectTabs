package at.rayman.projecttabs;

import at.rayman.projecttabs.settings.SettingsState;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.actionSystem.impl.PresentationFactory;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.JBColor;
import com.intellij.util.BitUtil;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Optional;

public class ProjectTabAction extends ToggleAction implements CustomComponentAction, DumbAware {

    private static final Icon ICON_CLOSE_HIDDEN = IconLoader.getIcon("closeHidden.svg", ProjectTabAction.class.getClassLoader());

    private static final Icon ICON_PROGRESS = AnimatedIcon.Default.INSTANCE;

    private static final Border BORDER_SELECTED = JBUI.Borders.customLine(UIManager.getColor("TabbedPane.underlineColor"), 0, 0, 3, 0);

    private static final Border BORDER_EMPTY = JBUI.Borders.empty();

    private final String projectName;

    private String displayName;

    private final String projectLocation;

    private boolean isHovered = false;

    private boolean isClosing = false;

    public ProjectTabAction(String projectName, String projectLocation) {
        super();
        this.projectName = projectName;
        this.displayName = projectName;
        this.projectLocation = projectLocation;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
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
        Optional<Project> project = findProject();
        if (project.isEmpty()) {
            return;
        }
        JFrame projectFrame = WindowManager.getInstance().getFrame(project.get());
        if (projectFrame == null) {
            return;
        }

        Optional<GraphicsDevice> currentScreen = getCurrentScreen();
        if (SettingsState.getInstance().moveProjectToScreen && currentScreen.isPresent()) {
            Rectangle bounds = currentScreen.get().getDefaultConfiguration().getBounds();
            projectFrame.setLocation(bounds.x, bounds.y);
            projectFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        } else {
            int frameState = projectFrame.getExtendedState();
            if (BitUtil.isSet(frameState, Frame.ICONIFIED)) {
                projectFrame.setExtendedState(BitUtil.set(frameState, Frame.ICONIFIED, false));
            }
        }
        projectFrame.toFront();
        IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> {
            Component mostRecentFocusOwner = projectFrame.getMostRecentFocusOwner();
            if (mostRecentFocusOwner != null) {
                IdeFocusManager.getGlobalInstance().requestFocus(mostRecentFocusOwner, true);
            }
        });
    }

    private Optional<Project> findProject() {
        return Arrays.stream(ProjectManager.getInstance().getOpenProjects())
            .filter(project -> projectLocation.equals(project.getPresentableUrl()))
            .findFirst();
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
        tab.setName(projectName);
        JLabel label = new JLabel(displayName);
        JLabel icon = new JLabel(AllIcons.Actions.Close);
        tab.add(label);
        tab.add(icon);
        tab.setBorder(BORDER_SELECTED);

        icon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2) {
                    closeTab(AnActionEvent.createFromDataContext(place, presentation, DataManager.getInstance().getDataContext(tab)));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                label.setForeground(JBColor.BLACK);
                if (!isClosing) {
                    icon.setIcon(AllIcons.Actions.CloseHovered);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                label.setForeground(JBColor.DARK_GRAY);
                if (!isClosing) {
                    icon.setIcon(AllIcons.Actions.Close);
                }
            }
        });
        MouseAdapter tabMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    actionPerformed(AnActionEvent.createFromDataContext(place, presentation, DataManager.getInstance().getDataContext(tab)));
                }
                if (e.getButton() == MouseEvent.BUTTON2) {
                    closeTab(AnActionEvent.createFromDataContext(place, presentation, DataManager.getInstance().getDataContext(tab)));
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    new ProjectTabContextMenu().show(ProjectTabAction.this, label, e.getX(), e.getY(), AnActionEvent.createFromDataContext(place, presentation, DataManager.getInstance().getDataContext(tab)));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (tab.getBorder().equals(BORDER_EMPTY)) {
                    isHovered = true;
                    label.setForeground(JBColor.BLACK);
                    if (!isClosing) {
                        icon.setIcon(AllIcons.Actions.Close);
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (tab.getBorder().equals(BORDER_EMPTY)) {
                    isHovered = false;
                    label.setForeground(JBColor.DARK_GRAY);
                    if (!isClosing) {
                        icon.setIcon(ICON_CLOSE_HIDDEN);
                    }
                }
            }
        };
        tab.addMouseListener(tabMouseAdapter);
        label.addMouseListener(tabMouseAdapter);
        return tab;
    }

    public void closeTab(AnActionEvent e) {
        isClosing = true;
        Component component = e.getPresentation().getClientProperty(COMPONENT_KEY).getComponents()[1];
        ((JLabel) component).setIcon(ICON_PROGRESS);
        findProject().ifPresent(ProjectManager.getInstance()::closeAndDispose);
    }

    private Optional<GraphicsDevice> getCurrentScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        return Arrays.stream(screens)
            .filter(screen -> screen.getDefaultConfiguration().getBounds().contains(MouseInfo.getPointerInfo().getLocation()))
            .findFirst();
    }

}
