package at.rayman.projecttabs

import at.rayman.projecttabs.settings.SettingsState
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectCloseListener
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.WindowManager

class ProjectTabManager : ProjectCloseListener {

    companion object {
        val projectTabAction: ProjectTabAction
            get() = ActionManager.getInstance().getAction("ProjectTabs") as ProjectTabAction
    }

    private class StartupActivity : ProjectActivity {
        override suspend fun execute(project: Project) {
     //       projectTabAction.addTab(project)
            val frame = WindowManager.getInstance().getFrame(project)
            frame?.addWindowFocusListener(ProjectFocusListener())
        }
    }

    override fun projectClosed(project: Project) {
  //      projectTabAction.removeTab(project)
        SettingsState.getInstance().removeProject(project)
    }

}