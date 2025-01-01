package at.rayman.projecttabs;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.util.BitUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

public class ProjectTabListener implements TabsListener {

    @Override
    public void selectionChanged(@Nullable TabInfo oldSelection, @Nullable TabInfo newSelection) {
        PropertiesComponent.getInstance().setValue("currentTab", newSelection.getText());
        Project project = findProject(newSelection);
        bringProjectToFront(project);
        TabManager.getInstance().selectTab(project, newSelection.getText());
    }

    private Project findProject(TabInfo tabInfo) {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            if (tabInfo.getText().equals(project.getName())) {
                return project;
            }
        }
        return null;
    }

    private void bringProjectToFront(Project project) {
        if (project == null) {
            return;
        }
        JFrame projectFrame = WindowManager.getInstance().getFrame(project);
        if (projectFrame == null) {
            return;
        }

        Optional<GraphicsDevice> currentScreen = getCurrentScreen();
        if (currentScreen.isPresent()) {
            Rectangle bounds = currentScreen.get().getDefaultConfiguration().getBounds();
            if (!projectFrame.getBounds().contains(MouseInfo.getPointerInfo().getLocation())) {
                projectFrame.setLocation(bounds.x, bounds.y);
                projectFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
            }
        } else {
            int frameState = projectFrame.getExtendedState();
            if (BitUtil.isSet(frameState, Frame.ICONIFIED)) {
                projectFrame.setExtendedState(BitUtil.set(frameState, Frame.ICONIFIED, false));
            }
        }
        projectFrame.toFront();
    }

    private Optional<GraphicsDevice> getCurrentScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        return Arrays.stream(screens)
            .filter(screen -> screen.getDefaultConfiguration().getBounds().contains(MouseInfo.getPointerInfo().getLocation()))
            .findFirst();
    }

}