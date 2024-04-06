package at.rayman.projecttabs

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectCloseListener
import com.intellij.openapi.startup.ProjectActivity
import kotlinx.coroutines.coroutineScope

class ProjectTabManager : ProjectCloseListener {

    companion object {
        @JvmStatic
        val projectTabGroup: ProjectTabActionGroup
            get() = ActionManager.getInstance().getAction("ProjectTabs") as ProjectTabActionGroup
    }

    private class StartupActivity : ProjectActivity {
        override suspend fun execute(project: Project) {
            coroutineScope {
                ActionManagerEx.withLazyActionManager(scope = this) {
                    (it.getAction("ProjectTabs") as ProjectTabActionGroup).addProject(project)
                }
            }
        }
    }

    override fun projectClosed(project: Project) {
        projectTabGroup.removeProject(project)
    }

}