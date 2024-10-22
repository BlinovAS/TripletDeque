package ru.mpei;
import java.util.*;
public class TripletDeque<E> implements Deque<E>, Containerable, Iterable<E> {
    private static final int DEFAULT_CONTAINER_CAPACITY = 5;
    private static final int DEFAULT_MAX_CAPACITY = 1000;

    private int containerCapacity;
    private int maxCapacity;
    private int size;

    private Container<E> firstContainer;
    private Container<E> lastContainer;

    public TripletDeque() {
        this(DEFAULT_MAX_CAPACITY, DEFAULT_CONTAINER_CAPACITY);
    }

    public TripletDeque(int maxCapacity) {
        this(maxCapacity, DEFAULT_CONTAINER_CAPACITY);
    }

    public TripletDeque(int maxCapacity, int containerCapacity) {
        this.maxCapacity = maxCapacity;
        this.containerCapacity = containerCapacity;
        this.size = 0;
        this.firstContainer = null;
        this.lastContainer = null;
    }

    private static class Container<E> {
        E[] elements;
        int capacity;
        int leftIndex;
        Container<E> next;
        Container<E> prev;
        public Container(int capacity) {
            this.elements = (E[]) new Object[capacity];
            this.capacity = capacity;
            this.next = null;
            this.prev = null;
        }

        boolean isFull() {
            for (int i = 0; i <= capacity - 1; i++){
                if (elements[i] == null){
                    return false;
                }
            }
            return true;
        }

        boolean isEmpty() {
            for (int i = 0; i <= capacity - 1; i++){
                if (elements[i] != null){
                    return false;
                }
            }
            return true;
        }
    }


    @Override
    public void addFirst(E e) {
        if (size >= maxCapacity) {
            throw new IllegalStateException("Заполнено");
        }
        if (e == null){
            throw new NullPointerException("Элемент не может быть null");
        }
        if (firstContainer == null) {
            firstContainer = lastContainer = new Container<>(containerCapacity);
            firstContainer.leftIndex = containerCapacity;
        }
        // Особый случай, когда лишь один контейнер, чтобы не было пустых мест в середине при вызове addFirst и addLast по очереди
        if (firstContainer == lastContainer){
            // Если имеется последний элемент, то нужно создать новый контейнер
            if (firstContainer.elements[0] != null){
                Container<E> newContainer = new Container<>(containerCapacity);
                firstContainer.leftIndex = containerCapacity - 1;
                newContainer.next = firstContainer;
                firstContainer.prev = newContainer;
                firstContainer = newContainer;
                firstContainer.leftIndex = containerCapacity;
            } else {
                for (int i = containerCapacity - 1; i >= 0; i--){
                    if (firstContainer.elements[i] == null){
                        lastContainer.leftIndex = i + 1;
                        break;
                    }
                }
            }
        }

        // Если заполнен, создаём новый
        if (firstContainer.isFull()) {
            Container<E> newContainer = new Container<>(containerCapacity);
            newContainer.next = firstContainer;
            firstContainer.prev = newContainer;
            firstContainer = newContainer;
            firstContainer.leftIndex = containerCapacity;
        }
        firstContainer.leftIndex--;
        firstContainer.elements[firstContainer.leftIndex] = e;
        size++;
    }

    @Override
    public void addLast(E e) {
        if (size >= maxCapacity) {
            throw new IllegalStateException("Заполнено");
        }
        if (e == null){
            throw new NullPointerException("Элемент не может быть null");
        }

        if (lastContainer == null) {
            firstContainer = lastContainer = new Container<>(containerCapacity);
        }
        // Особый случай, когда лишь один контейнер, чтобы не было пустых мест в середине при вызове addFirst и addLast по очереди
        if (firstContainer == lastContainer){
            // Если имеется последний элемент, то нужно создать новый контейнер
            if (lastContainer.elements[containerCapacity - 1] != null){
                Container<E> newContainer = new Container<>(containerCapacity);
                newContainer.prev = lastContainer;
                lastContainer.next = newContainer;
                lastContainer = newContainer;
                lastContainer.leftIndex = -1;
            } else {
                for (int i = 0; i <= containerCapacity - 1; i++){
                    if (firstContainer.elements[i] == null){
                        lastContainer.leftIndex = i - 1;
                        break;
                    }
                }
                }
        }

        // Если заполнен, создаём новый
        if (lastContainer.isFull()) {
            Container<E> newContainer = new Container<>(containerCapacity);
            lastContainer.next = newContainer;
            newContainer.prev = lastContainer;
            lastContainer = newContainer;
            lastContainer.leftIndex = -1;
        }
        lastContainer.leftIndex++;
        lastContainer.elements[lastContainer.leftIndex] = e;
        size++;
    }

    @Override
    public boolean offerFirst(E e) {
        if (size >= maxCapacity) {
            return false;
        }
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        if (size >= maxCapacity) {
            return false;
        }
        addLast(e);
        return true;
    }

    @Override
    public E removeFirst() {
        E e = pollFirst();
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e;
    }
    @Override
    public E removeLast() {
        E e = pollLast();
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e;
    }

    @Override
    public E pollFirst() {
        if (size == 0) {
            return null;
        }

        // Особый случай, когда лишь один контейнер
        if (firstContainer == lastContainer) {
            for (int i = 0; i <= containerCapacity - 1; i++) {
                if (firstContainer.elements[i] != null) {
                    E e = firstContainer.elements[i];
                    firstContainer.elements[i] = null;
                    firstContainer.leftIndex++;
                    size--;
                    return e;
                }
            }
        }

        E e = null;
        //Пройдём по всему контейнеру с начала и найдём элемент null
        for (int i = 0; i <= containerCapacity - 1; i++) {
            // Если нашли, то элемент первый, его убираем
            if (firstContainer.elements[i] != null) {
                e = firstContainer.elements[i];
                firstContainer.elements[i] = null;
                firstContainer.leftIndex = i+1;
                break;
            }
        }
        // Если не нашли в предыдущем цикле for и первый элемент первого контейнера не null, то это и есть первый элемент.
        if (firstContainer.elements[0] != null){
            e = firstContainer.elements[0];
            firstContainer.elements[0] = null;
            firstContainer.leftIndex = 1;
        }

        if (firstContainer.isEmpty()){
            firstContainer.next.prev = null;
            firstContainer = firstContainer.next;
            firstContainer.leftIndex = 0;
        }
        size--;
        return e;
    }

    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }
        // Особый случай, когда лишь один контейнер
        if (firstContainer == lastContainer) {
            for (int i = containerCapacity - 1; i >= 0; i--) {
                if (firstContainer.elements[i] != null) {
                    E e = firstContainer.elements[i];
                    firstContainer.elements[i] = null;
                    firstContainer.leftIndex++;
                    size--;
                    return e;
                }
            }
        }
        E e = null;
        //Пройдём по всему контейнеру с начала и найдём элемент null
        for (int i = 0; i <= containerCapacity - 1; i++) {
            // Если нашли, то предыдущий элемент последний, его убираем
            if (lastContainer.elements[i] == null) {
                e = lastContainer.elements[i-1];
                lastContainer.elements[i-1] = null;
                lastContainer.leftIndex--;
                break;
            }
        }
        // Если не нашли в предыдущем цикле for и последний элемент последнего контейнера не null, то это и есть последний элемент.
        if (lastContainer.elements[containerCapacity - 1] != null){
            e = lastContainer.elements[containerCapacity - 1];
            lastContainer.elements[containerCapacity - 1] = null;
            lastContainer.leftIndex = containerCapacity - 2;
        }

        if (lastContainer.isEmpty()){
            lastContainer.prev.next = null;
            lastContainer = lastContainer.prev;
            lastContainer.leftIndex = containerCapacity - 1;
        }
        size--;
        return e;
    }

    @Override
    public E getFirst() {
        E e = peekFirst();
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e;
    }

    @Override
    public E getLast() {
        E e = peekLast();
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e;
    }

    @Override
    public E peekFirst() {
        if (size == 0) {
            return null;
        }

        E e = null;
        //Пройдём по всему контейнеру с начала и найдём элемент не null
        for (int i = 0; i <= containerCapacity - 1; i++) {
            if (firstContainer.elements[i] != null) {
                e = firstContainer.elements[i];
                break;
            }
        }
        // Если не нашли в предыдущем цикле for и первый элемент первого контейнера не null, то это и есть первый элемент.
        if (firstContainer.elements[0] != null){
            e = firstContainer.elements[0];
        }
        return e;
    }

    @Override
    public E peekLast() {
        if (size == 0) {
            return null;
        }
        E e = null;
        //Пройдём по всему контейнеру с начала и найдём элемент не null
        for (int i = containerCapacity - 1; i >= 0; i--) {
            if (lastContainer.elements[i] != null) {
                e = lastContainer.elements[i];
                break;
            }
        }
        // Если не нашли в предыдущем цикле for и последний элемент последнего контейнера не null, то это и есть последний элемент.
        if (lastContainer.elements[containerCapacity - 1] != null){
            e = lastContainer.elements[containerCapacity - 1];
        }
        return e;
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            E e = it.next();
            if (Objects.equals(e, o)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        Iterator<E> it = descendingIteratorImpl();
        while (it.hasNext()) {
            E e = it.next();
            if (Objects.equals(e, o)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        addLast(e);
        return true;
    }
    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }
    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        for (E e : c) {
            addLast(e);
            modified = true;
        }
        return modified;
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    @Override
    public boolean contains(Object o) {
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (Objects.equals(it.next(), o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new DequeIterator();
    }

    @Override
    public Iterator<E> descendingIterator() {
        throw new UnsupportedOperationException("Не реализован");
    }

    private Iterator<E> descendingIteratorImpl() {
        return new ReverseIterator();
    }


    private class DequeIterator implements Iterator<E> {
        private Container<E> currentContainer;
        private int currentIndex;

        public DequeIterator() {
            currentContainer = firstContainer;
            if (firstContainer != null) {
                currentIndex = firstContainer.leftIndex - 1;
            }
        }

        @Override
        public boolean hasNext() {
            if (size == 0){
                return false;
            }
            // Если дошли до конца контейнера, проверяем есть ли следующий контейнер(если он есть, значит там есть элементы)
            if (currentIndex == containerCapacity - 1 && currentContainer.next != null){
                    return true;
            } else if (currentIndex == containerCapacity - 1 && currentContainer.next == null){
                return false;
            }
            // Проверяем на наличие следующих элементов в текущем массиве
            if (currentContainer.elements[currentIndex + 1] != null){
                return true;
            }
            return false;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            // Если дошли до конца контейнера
            if (currentIndex == containerCapacity - 1){
                currentContainer = currentContainer.next;
                currentIndex = 0;
                return currentContainer.elements[currentIndex];
            }
            // Возвращяем следующий элемент в текущем массиве
            currentIndex++;
            return currentContainer.elements[currentIndex];
        }

        @Override
        public void remove() {
            while (hasNext()){
                // Если текущий элемент в конце контейнера
                if (currentIndex == containerCapacity - 1) {
                    currentContainer.elements[currentIndex] = currentContainer.next.elements[0];
                    currentContainer = currentContainer.next;
                    currentIndex = 0;
                } // Если не в конце контейнера
                if (currentIndex < containerCapacity - 1){
                    currentContainer.elements[currentIndex] = currentContainer.elements[currentIndex + 1];
                    currentIndex++;
                }
            }
            currentContainer.elements[currentIndex] = null;
            lastContainer.leftIndex--;
            if (currentContainer.isEmpty()){
                currentContainer.prev.next = null;
                lastContainer = currentContainer.prev;
            }
            size--;
        }
    }

    private class ReverseIterator implements Iterator<E> {
        private Container<E> currentContainer;
        private int currentIndex;

        public ReverseIterator() {
            currentContainer = lastContainer;
            currentIndex = lastContainer.leftIndex;
        }

        @Override
        public boolean hasNext() {
            if (size == 0){
                return false;
            }
            // Если дошли до начала контейнера, проверяем есть ли следующий контейнер(если он есть, значит там есть элементы)
            if (currentIndex == 0 && currentContainer.prev != null){
                return true;
            }
            // Проверяем на наличие предыдущих элементов в текущем массиве
            if (currentContainer.elements[currentIndex - 1] != null){
                return true;
            }
            return false;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            // Если дошли до начала контейнера
            if (currentIndex == 0){
                currentContainer = currentContainer.prev;
                currentIndex = containerCapacity - 1;
                return currentContainer.elements[currentIndex];
            }
            // Возвращяем предыдущий элемент в текущем массиве
            currentIndex--;
            return currentContainer.elements[currentIndex];
        }

        @Override
        public void remove() {
            while (hasNext()){
                // Если текущий элемент в конце контейнера
                if (currentIndex == containerCapacity - 1) {
                    currentContainer.elements[currentIndex] = currentContainer.next.elements[0];
                    currentContainer = currentContainer.next;
                    currentIndex = 0;
                }
                if (currentIndex < containerCapacity - 1){
                    currentContainer.elements[currentIndex] = currentContainer.elements[currentIndex + 1];
                    currentIndex++;
                }
            }
            currentContainer.elements[currentIndex] = null;
            lastContainer.leftIndex--;
            if (currentContainer.isEmpty()){
                currentContainer.prev.next = null;
                lastContainer = currentContainer.prev;
            }
            size--;
        }
    }


    public boolean isEmpty() {
        return size == 0;
    }

    public Object[] getContainerByIndex(int index) {
        Container<E> container = firstContainer;
        int idx = 0;
        while (container != null && idx < index) {
            container = container.next;
            idx++;
        }
        if (container != null) {
            return container.elements;
        } else {
            return null;
        }
    }
}