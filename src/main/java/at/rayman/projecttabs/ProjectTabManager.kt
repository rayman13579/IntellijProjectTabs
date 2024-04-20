package at.rayman.projecttabs

import at.rayman.projecttabs.settings.SettingsState
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectCloseListener
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.WindowManager

class ProjectTabManager : ProjectCloseListener {

    companion object {
        val projectTabGroup: ProjectTabActionGroup
            get() = ActionManager.getInstance().getAction("ProjectTabs") as ProjectTabActionGroup
    }

    private class StartupActivity : ProjectActivity {
        override suspend fun execute(project: Project) {
            projectTabGroup.addProject(project)
            val frame = WindowManager.getInstance().getFrame(project)
            frame?.addWindowFocusListener(ProjectFocusListener())
        }
    }

    override fun projectClosed(project: Project) {
        projectTabGroup.removeProject(project)
        SettingsState.getInstance().removeProject(project)
    }

}