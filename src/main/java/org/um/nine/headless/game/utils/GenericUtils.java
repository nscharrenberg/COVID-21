package org.um.nine.headless.game.utils;

import java.util.List;
import java.util.Map;

public class GenericUtils<T> {
    private static final long SerialVersionUID = 0L;

    public GenericUtils() {
    }
    public static record MapEquals<T>(Map<?, T> map1, Map<?, T> map2) {
        public boolean test() {
            return new ListEquals<T>(
                    map1.values().stream().toList(),
                    map2.values().stream().toList()
            ).test();
        }
    }
    public static record ListEquals<T>(List<T> list1, List<T> list2) {

        public boolean test() {
            if (list1 == null && list2 == null) return true;
            if (list1 == null || list2 == null) return false;
            if (list1.size() != list2.size()) return false;
            for (int i = 0; i < list1.size(); i++) {
                if (!list1.get(i).equals(list2.get(i))) return false;
            }
            return true;
        }

    }
}
