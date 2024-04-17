package at.rayman.projecttabs.settings;

import at.rayman.projecttabs.TabOrder;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ContextHelpLabel;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class SettingsComponent {

    private final JPanel panel;

    private final JBCheckBox focusLastProject = new JBCheckBox("Refocus last project on close");

    private final ComboBox<TabOrder> comboBox = new ComboBox<>(TabOrder.values());

    public SettingsComponent() {
        panel = new JPanel();
        buildSettingsPanel(panel);
    }

    public JPanel getPanel() {
        return panel;
    }

    public boolean isFocusLastProject() {
        return focusLastProject.isSelected();
    }

    public void setFocusLastProject(boolean focusLastProject) {
        this.focusLastProject.setSelected(focusLastProject);
    }

    public TabOrder getTabOrder() {
        return (TabOrder) comboBox.getSelectedItem();
    }

    public void setTabOrder(TabOrder tabOrder) {
        comboBox.setSelectedItem(tabOrder);
    }

    private void buildSettingsPanel(JPanel panel) {
        JLabel tooltip = ContextHelpLabel.create("When enabled, the last project will be brought to the foreground when the currently open project is closed, instead of returning to the previously opened window.");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel subPanel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        subPanel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        subPanel.add(focusLastProject, gbc);
        gbc.insets = JBUI.insetsLeft(125);
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        subPanel.add(tooltip, gbc);
        gbc.insets = JBUI.insets(10, 3, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        subPanel.add(new JBLabel("Tab ordering:"), gbc);
        gbc.gridx = 1;
        gbc.insets = JBUI.insets(10, 10, 0, 0);
        subPanel.add(comboBox, gbc);
        comboBox.setSelectedItem(SettingsState.getInstance().tabOrder);
        panel.add(subPanel);
    }

}
