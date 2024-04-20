package at.rayman.projecttabs;

import at.rayman.projecttabs.settings.SettingsState;
import com.intellij.openapi.wm.IdeFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ProjectFocusListener extends WindowAdapter {

    @Override
    public void windowGainedFocus(WindowEvent e) {
        IdeFrame frame = (IdeFrame) e.getSource();
        SettingsState.getInstance().addProject(frame.getProject());
    }

}
