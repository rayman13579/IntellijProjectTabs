package at.rayman.projecttabs.settings;

import at.rayman.projecttabs.TabOrder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(
    name = "at.rayman.projecttabs.settings.SettingsState",
    storages = {@Storage("projectTabs.xml")}
)
public class SettingsState implements PersistentStateComponent<SettingsState> {

    public boolean focusLastProject = false;

    public TabOrder tabOrder = TabOrder.CHRONOLOGICAL;

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

}
