package at.rayman.projecttabs;

import com.intellij.openapi.actionSystem.AnAction;

import java.util.Comparator;

public enum TabOrder {

    ALPHABETICAL(Comparator.comparing(tab -> ((ProjectTabAction) tab).getProjectName())),
    CHRONOLOGICAL((tab1, tab2) -> 0);

    private final Comparator<AnAction> comparator;

    TabOrder(Comparator<AnAction> comparator) {
        this.comparator = comparator;
    }

    public Comparator<AnAction> getComparator() {
        return comparator;
    }

}
