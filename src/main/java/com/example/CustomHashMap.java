package com.example;

import java.util.Objects;

public class CustomHashMap<K, V> implements CustomMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private Node<K, V>[] table;
    private int size;
    private int threshold;

    public CustomHashMap() {
        this.table = new Node[DEFAULT_CAPACITY];
        this.threshold = (int) (DEFAULT_CAPACITY * DEFAULT_LOAD_FACTOR);
    }

    @Override
    public V put(K key, V value) {
        int hash = hash(key);
        int index = bucketIndex(hash, table.length);
        Node<K, V> node = table[index];
        while (node != null) {
            if (node.hash == hash && Objects.equals(key, node.key)) {
                V oldValue = node.value;
                node.value = value;
                return oldValue;
            }
            node = node.next;
        }
        Node<K, V> newNode = new Node<>(hash, key, value, table[index]);
        table[index] = newNode;
        size++;
        if (size > threshold) {
            resize();
        }
        return null;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = findNode(key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public V remove(K key) {
        int hash = hash(key);
        int index = bucketIndex(hash, table.length);
        Node<K, V> prev = null;
        Node<K, V> cur = table[index];
        while (cur != null) {
            if (cur.hash == hash && Objects.equals(key, cur.key)) {
                if (prev == null) {
                    table[index] = cur.next;
                } else {
                    prev.next = cur.next;
                }
                size--;
                return cur.value;
            }
            prev = cur;
            cur = cur.next;
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return findNode(key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    private static int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private int bucketIndex(int hash, int length) {
        return hash & (length - 1);
    }

    private Node<K, V> findNode(K key) {
        int hash = hash(key);
        int index = bucketIndex(hash, table.length);
        Node<K, V> node = table[index];
        while (node != null) {
            if (node.hash == hash && Objects.equals(key, node.key)) {
                return node;
            }
            node = node.next;
        }
        return null;
    }

    private void resize() {
        Node<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1;
        Node<K, V>[] newTable = new Node[newCapacity];
        for (int i = 0; i < oldCapacity; i++) {
            Node<K, V> node = oldTable[i];
            while (node != null) {
                Node<K, V> next = node.next;
                int newIndex = bucketIndex(node.hash, newCapacity);
                node.next = newTable[newIndex];
                newTable[newIndex] = node;
                node = next;
            }
        }
        table = newTable;
        threshold = (int) (newCapacity * DEFAULT_LOAD_FACTOR);
    }

    private static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}