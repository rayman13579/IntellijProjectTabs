package at.rayman.projecttabs

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectCloseListener
import com.intellij.openapi.startup.ProjectActivity

class ProjectManager : ProjectCloseListener {

    private class StartupActivity : ProjectActivity {

        override suspend fun execute(project: Project) {
            TabManager.getInstance().addPlaceholderProject(project)
        }

    }

    override fun projectClosed(project: Project) {
        TabManager.getInstance().removeProjectTabs(project)
    }

}