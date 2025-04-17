package at.rayman.projecttabs;

import com.intellij.openapi.actionSystem.AnAction;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public enum TabOrder {

    ALPHABETICAL(Comparator.comparing(tab -> ((ProjectTabAction) tab).getProjectName())),
    CHRONOLOGICAL((tab1, tab2) -> 0),
    MANUAL(getManualComparator());

    private static final Map<String, Integer> manualOrder = new HashMap<>();
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
            String loc1 = ((ProjectTabAction) tab1).getProjectLocation();
            String loc2 = ((ProjectTabAction) tab2).getProjectLocation();
            int pos1 = manualOrder.getOrDefault(loc1, 0);
            int pos2 = manualOrder.getOrDefault(loc2, 0);
            return Integer.compare(pos1, pos2);
        };
    }

    public static void moveTabLeft(String projectLocation) {
        int currentIndex = manualOrder.getOrDefault(projectLocation, 0);
        if (currentIndex > 0) {
            String prevTabLocation = null;
            for (Map.Entry<String, Integer> entry : manualOrder.entrySet()) {
                if (entry.getValue() == currentIndex - 1) {
                    prevTabLocation = entry.getKey();
                    break;
                }
            }

            if (prevTabLocation != null) {
                manualOrder.put(projectLocation, currentIndex - 1);
                manualOrder.put(prevTabLocation, currentIndex);
            }
        }
    }

    public static void moveTabRight(String projectLocation) {
        int currentIndex = manualOrder.getOrDefault(projectLocation, 0);
        int maxIndex = -1;

        for (int index : manualOrder.values()) {
            if (index > maxIndex) {
                maxIndex = index;
            }
        }

        if (currentIndex < maxIndex) {
            String nextTabLocation = null;
            for (Map.Entry<String, Integer> entry : manualOrder.entrySet()) {
                if (entry.getValue() == currentIndex + 1) {
                    nextTabLocation = entry.getKey();
                    break;
                }
            }

            if (nextTabLocation != null) {
                manualOrder.put(projectLocation, currentIndex + 1);
                manualOrder.put(nextTabLocation, currentIndex);
            }
        }
    }

    public static void registerTab(String projectLocation) {
        if (!manualOrder.containsKey(projectLocation)) {
            int maxIndex = -1;
            for (int index : manualOrder.values()) {
                if (index > maxIndex) {
                    maxIndex = index;
                }
            }
            manualOrder.put(projectLocation, maxIndex + 1);
        }
    }
}
