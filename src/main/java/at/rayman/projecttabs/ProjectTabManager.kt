package at.rayman.projecttabs

import at.rayman.projecttabs.settings.SettingsState
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectCloseListener
import com.intellij.openapi.startup.ProjectActivity

class ProjectTabManager : ProjectCloseListener {

    companion object {
        val projectTabGroup: ProjectTabActionGroup
            get() = ActionManager.getInstance().getAction("ProjectTabs") as ProjectTabActionGroup
    }

    private class StartupActivity : ProjectActivity {
        override suspend fun execute(project: Project) {
            projectTabGroup.addProject(project)
            projectTabGroup.findProjectTabAction(project.presentableUrl).bringProjectWindowToFront()
        }
    }

    override fun projectClosed(project: Project) {
        projectTabGroup.removeProject(project)
        SettingsState.getInstance().removeProject(project)
    }

}