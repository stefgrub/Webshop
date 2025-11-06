package com.example.shop.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

public class Cart implements Serializable {
    private final Map<Long, Integer> items = new LinkedHashMap<>();

    public void add(Long productId, int qty) {
        if (qty <= 0) return;
        items.merge(productId, qty, Integer::sum);
    }

    public void update(Long productId, int qty) {
        if (qty <= 0) {
            items.remove(productId);
        } else {
            items.put(productId, qty);
        }
    }

    public void remove(Long productId) {
        items.remove(productId);
    }

    public Map<Long, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}