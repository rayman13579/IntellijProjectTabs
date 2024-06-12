package at.rayman.projecttabs;

import at.rayman.projecttabs.settings.SettingsState;
import com.intellij.ide.IdeDependentActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProjectTabActionGroup extends IdeDependentActionGroup {

    @Override
    public boolean isDumbAware() {
        return true;
    }

    public void addProject(Project project) {
        String projectLocation = project.getPresentableUrl();
        if (projectLocation == null) {
            return;
        }

        ProjectTabAction projectTabAction = new ProjectTabAction(project.getName(), projectLocation);
        List<ProjectTabAction> duplicateProjectNames = findDuplicateProjectNames(project.getName());
        if (!duplicateProjectNames.isEmpty()) {
            for (ProjectTabAction action : duplicateProjectNames) {
                setDuplicateProjectName(action);
            }
            setDuplicateProjectName(projectTabAction);
        }
        add(projectTabAction);
    }

    public void removeProject(Project project) {
        ProjectTabAction projectTabAction = findProjectTabAction(project.getPresentableUrl());
        if (projectTabAction == null) {
            return;
        }

        remove(projectTabAction);

        if (SettingsState.getInstance().focusLastProject) {
            Optional<Project> previousProject = SettingsState.getInstance().getLastFocusedProject();
            previousProject
                .map(p -> findProjectTabAction(p.getPresentableUrl()))
                .ifPresent(ProjectTabAction::bringProjectWindowToFront);
        }

        List<ProjectTabAction> duplicateProjectNames = findDuplicateProjectNames(project.getName());
        if (duplicateProjectNames.size() == 1) {
            duplicateProjectNames.get(0).setDisplayName(duplicateProjectNames.get(0).getProjectName());
        }
    }

    private void setDuplicateProjectName(ProjectTabAction projectTabAction) {
        int index = projectTabAction.getProjectLocation().lastIndexOf('/', projectTabAction.getProjectLocation().lastIndexOf('/') - 1);
        projectTabAction.setDisplayName(projectTabAction.getProjectLocation().substring(index + 1));
    }

    private List<ProjectTabAction> findDuplicateProjectNames(String projectName) {
        List<ProjectTabAction> duplicateProjectNames = new ArrayList<>();

        for (AnAction action : getChildren(null)) {
            if (action instanceof ProjectTabAction projectTabAction
                && projectName.equals(projectTabAction.getProjectName())) {
                duplicateProjectNames.add(projectTabAction);
            }
        }

        return duplicateProjectNames;
    }

    public ProjectTabAction findProjectTabAction(String projectLocation) {
        for (AnAction action : getChildren(null)) {
            if (action instanceof ProjectTabAction projectTabAction
                && (projectLocation.equals(projectTabAction.getProjectLocation()))) {
                return projectTabAction;
            }
        }
        return null;
    }

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent event) {
        AnAction[] children = super.getChildren(null, ActionManager.getInstance());
        Arrays.sort(children, SettingsState.getInstance().tabOrder.getComparator());
        return children;
    }

}
