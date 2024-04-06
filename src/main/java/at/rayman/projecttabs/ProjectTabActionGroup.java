package at.rayman.projecttabs;

import com.intellij.ide.IdeDependentActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectTabActionGroup extends IdeDependentActionGroup {

    private ProjectTabAction latest;

    @Override
    public boolean isDumbAware() {
        return true;
    }

    public void addProject(Project project) {
        String projectLocation = project.getPresentableUrl();
        if (projectLocation == null) {
            return;
        }

        ProjectTabAction projectTabAction = new ProjectTabAction(project.getName(), projectLocation, latest);
        List<ProjectTabAction> duplicateProjectNames = findDuplicateProjectNames(project.getName());
        if (!duplicateProjectNames.isEmpty()) {
            for (ProjectTabAction action : duplicateProjectNames) {
                setDuplicateProjectName(action);
            }
            setDuplicateProjectName(projectTabAction);
        }
        add(projectTabAction);
        latest = projectTabAction;
    }

    public void removeProject(Project project) {
        ProjectTabAction projectTabAction = findProjectTabAction(project.getPresentableUrl());
        if (projectTabAction == null) {
            return;
        }
        if (latest == projectTabAction) {
            ProjectTabAction previousTab = projectTabAction.getPreviousTab();
            if (previousTab == projectTabAction) {
                latest = null;
            } else {
                latest = previousTab;
            }
        }

        remove(projectTabAction);
        List<ProjectTabAction> duplicateProjectNames = findDuplicateProjectNames(project.getName());
        if (duplicateProjectNames.size() == 1) {
            setDuplicateProjectName(duplicateProjectNames.get(0));
        }
        projectTabAction.dispose();
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

    private ProjectTabAction findProjectTabAction(String projectLocation) {
        for (AnAction action : getChildren(null)) {
            if (action instanceof ProjectTabAction projectTabAction
                    && (projectLocation.equals(projectTabAction.getProjectLocation()))) {
                return projectTabAction;
            }
        }
        return null;
    }

}
