package cf.wynntils.core.utils;

import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 25/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class LimitedList<T> {

    ArrayList<T> array = new ArrayList<>();
    int amount = 0;

    public LimitedList(int amount) {
        this.amount = amount;
    }

    public T get(int index) {
        return array.get(index);
    }

    public void add(T t) {
        if(array.size() + 1 > amount) {
            remove(size() - 1);
        }
        array.add(t);
    }

    public void remove(int index) {
        array.remove(index);
    }

    public void remove(Object object) {
        array.remove(object);
    }

    public int size() {
        return array.size();
    }

    public void clear() {
        array.clear();
    }

    public ArrayList<T> getArray() {
        return array;
    }

}
