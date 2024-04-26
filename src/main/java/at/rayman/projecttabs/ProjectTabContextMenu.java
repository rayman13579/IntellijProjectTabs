package at.rayman.projecttabs;

import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class ProjectTabContextMenu {

    public void show(ProjectTabAction tab, JComponent component, int x, int y) {
        DefaultActionGroup dropDown = new DefaultActionGroup();
        dropDown.add(getAbstractAction("Close Project", tab::closeTab));
        dropDown.add(getAbstractAction("Close Other Projects", () -> closeOtherTabs(tab)));
        dropDown.add(getAbstractAction("Close All Projects", this::closeAllTabs));
        dropDown.add(getAbstractAction("Close Projects to the Left", () -> closeTabsToTheLeft(tab)));
        dropDown.add(getAbstractAction("Close Projects to the Right", () -> closeTabsToTheRight(tab)));

        ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu("TestAction", dropDown);
        popupMenu.getComponent().show(component, x + 5, y + 5);
    }

    private AnAction getAbstractAction(String name, Runnable actionPerformed) {
        return new AnAction(name) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                actionPerformed.run();
            }
        };
    }

    private void closeOtherTabs(ProjectTabAction tab) {
        getTabs().stream()
            .filter(action -> !action.equals(tab))
            .forEach(ProjectTabAction::closeTab);
    }

    private void closeAllTabs() {
        getTabs().forEach(ProjectTabAction::closeTab);
    }

    private void closeTabsToTheLeft(ProjectTabAction tab) {
        getTabs().stream()
            .limit(getTabs().indexOf(tab))
            .forEach(ProjectTabAction::closeTab);
    }

    private void closeTabsToTheRight(ProjectTabAction tab) {
        getTabs().stream()
            .skip(getTabs().indexOf(tab) + 1)
            .forEach(ProjectTabAction::closeTab);
    }

    private List<ProjectTabAction> getTabs() {
        return Arrays.stream(ProjectTabManager.Companion.getProjectTabGroup().getChildren(null))
            .map(action -> (ProjectTabAction) action)
            .toList();
    }

}
