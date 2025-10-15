package at.rayman.projecttabs;


import com.intellij.openapi.actionSystem.AnAction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public enum TabOrder {

    ALPHABETICAL(Comparator.comparing(tab -> ((ProjectTabAction) tab).getProjectName())),
    CHRONOLOGICAL((tab1, tab2) -> 0),
    MANUAL(getManualComparator());

    private static final List<String> manualOrderList = new ArrayList<>();

    private final Comparator<AnAction> comparator;

    TabOrder(Comparator<AnAction> comparator) {
        this.comparator = comparator;
    }

    public Comparator<AnAction> getComparator() {
        return comparator;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    private static Comparator<AnAction> getManualComparator() {
        return (tab1, tab2) -> {
            int index1 = manualOrderList.indexOf(((ProjectTabAction) tab1).getProjectLocation());
            int index2 = manualOrderList.indexOf(((ProjectTabAction) tab2).getProjectLocation());
            return Integer.compare(index1, index2);
        };
    }

    public static void moveTabLeft(String projectLocation) {
        int currentIndex = manualOrderList.indexOf(projectLocation);
        if (currentIndex <= 0 || currentIndex >= manualOrderList.size()) {
            return;
        }
        String previousTabLocation = manualOrderList.get(currentIndex - 1);
        manualOrderList.set(currentIndex, previousTabLocation);
        manualOrderList.set(currentIndex - 1, projectLocation);
    }

    public static void moveTabRight(String projectLocation) {
        int currentIndex = manualOrderList.indexOf(projectLocation);
        if (currentIndex < 0 || currentIndex >= manualOrderList.size() - 1) {
            return;
        }
        String nextTabLocation = manualOrderList.get(currentIndex + 1);
        manualOrderList.set(currentIndex, nextTabLocation);
        manualOrderList.set(currentIndex + 1, projectLocation);
    }

    public static void addTab(String projectLocation) {
        manualOrderList.add(projectLocation);
    }

    public static void removeTab(String projectLocation) {
        manualOrderList.remove(projectLocation);
    }

}
