package at.rayman.projecttabs.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SettingsConfigurable implements Configurable {

    private SettingsComponent component;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Project Tabs";
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new SettingsComponent();
        return component.getPanel();
    }

    @Override
    public boolean isModified() {
        SettingsState settings = SettingsState.getInstance();
        return component.isFocusLastProject() != settings.focusLastProject
            || component.getTabOrder() != settings.tabOrder;
    }

    @Override
    public void apply() {
        SettingsState settings = SettingsState.getInstance();
        settings.focusLastProject = component.isFocusLastProject();
        settings.tabOrder = component.getTabOrder();
    }

    @Override
    public void reset() {
        SettingsState settings = SettingsState.getInstance();
        component.setFocusLastProject(settings.focusLastProject);
        component.setTabOrder(settings.tabOrder);
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }

}
