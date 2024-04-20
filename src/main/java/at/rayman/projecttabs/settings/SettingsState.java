package at.rayman.projecttabs.settings;

import at.rayman.projecttabs.TabOrder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@State(
    name = "at.rayman.projecttabs.settings.SettingsState",
    storages = {@Storage("projectTabs.xml")}
)
public class SettingsState implements PersistentStateComponent<SettingsState> {

    public boolean focusLastProject = false;

    public TabOrder tabOrder = TabOrder.CHRONOLOGICAL;

    @Transient
    private final Set<Project> lastFocusedProjects = new LinkedHashSet<>();

    public static SettingsState getInstance() {
        return ApplicationManager.getApplication().getService(SettingsState.class);
    }

    @Override
    public SettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public Optional<Project> getLastFocusedProject() {
        if (lastFocusedProjects.size() < 2) {
            return Optional.empty();
        }
        return Optional.of((Project) lastFocusedProjects.toArray()[lastFocusedProjects.size() - 2]);
    }

    public void addProject(Project project) {
        lastFocusedProjects.remove(project);
        lastFocusedProjects.add(project);
    }

    public void removeProject(Project project) {
        lastFocusedProjects.remove(project);
    }

}
