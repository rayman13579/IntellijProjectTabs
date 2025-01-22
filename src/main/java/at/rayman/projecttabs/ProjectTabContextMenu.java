package at.rayman.projecttabs;

import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class ProjectTabContextMenu {

    public void show(ProjectTabAction tab, JComponent component, int x, int y, AnActionEvent e) {
        DefaultActionGroup dropDown = new DefaultActionGroup();
    //    dropDown.add(getAbstractAction("Close Project", () -> tab.closeTab(e)));
        dropDown.add(getAbstractAction("Close Other Projects", () -> closeOtherTabs(tab, e)));
   //     dropDown.add(getAbstractAction("Close All Projects", () -> closeAllTabs(e)));
        dropDown.add(getAbstractAction("Close Projects to the Left", () -> closeTabsToTheLeft(tab, e)));
        dropDown.add(getAbstractAction("Close Projects to the Right", () -> closeTabsToTheRight(tab, e)));

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

    private void closeOtherTabs(ProjectTabAction tab, AnActionEvent e) {
        getTabs().stream()
            .filter(action -> !action.equals(tab))
            .forEach(t -> t.closeTab(e));
    }

    private void closeAllTabs(AnActionEvent e) {
        getTabs().forEach(t -> t.closeTab(e));
    }

    private void closeTabsToTheLeft(ProjectTabAction tab, AnActionEvent e) {
        getTabs().stream()
            .limit(getTabs().indexOf(tab))
            .forEach(t -> t.closeTab(e));
    }

    private void closeTabsToTheRight(ProjectTabAction tab, AnActionEvent e) {
        getTabs().stream()
            .skip(getTabs().indexOf(tab) + 1)


            .forEach(t -> t.closeTab(e));
    }

    private List<ProjectTabAction> getTabs() {
        return Arrays.stream(ProjectTabManager.Companion.getProjectTabGroup().getChildren(ActionManager.getInstance()))
            .map(action -> (ProjectTabAction) action)
            .toList();
    }

}
