package ru.mpei;

import java.util.*;

public class TripletDeque<E> implements Deque<E>, Containerable {

	private static class Node<E> {
		E[] elements;
		int start;
		int end;
		Node<E> next;
		Node<E> prev;

		@SuppressWarnings("unchecked")
		public Node(int capacity, boolean atFront) {
			elements = (E[]) new Object[capacity];
			if (atFront) {
				start = capacity;
				end = capacity;
			} else {
				start = 0;
				end = 0;
			}
			next = null;
			prev = null;
		}

		public boolean isFull() {
			return start == 0 && end == elements.length;
		}

		public boolean isEmpty() {
			return start == end;
		}

		public void addFirst(E e) {
			if (e == null) {
				throw new NullPointerException();
			}
			if (start == 0) {
				throw new IllegalStateException("Node is full at the front");
			}
			elements[--start] = e;
		}

		public void addLast(E e) {
			if (e == null) {
				throw new NullPointerException();
			}
			if (end == elements.length) {
				throw new IllegalStateException("Node is full at the end");
			}
			elements[end++] = e;
		}

		public E removeFirst() {
			if (isEmpty()) {
				throw new NoSuchElementException();
			}
			E e = elements[start];
			elements[start++] = null;
			return e;
		}

		public E removeLast() {
			if (isEmpty()) {
				throw new NoSuchElementException();
			}
			E e = elements[--end];
			elements[end] = null;
			return e;
		}

		public E peekFirst() {
			return isEmpty() ? null : elements[start];
		}

		public E peekLast() {
			return isEmpty() ? null : elements[end - 1];
		}

		public boolean removeFirstOccurrence(Object o) {
			for (int i = start; i < end; i++) {
				if (Objects.equals(elements[i], o)) {
					int numMoved = end - i - 1;
					if (numMoved > 0) {
						System.arraycopy(elements, i + 1, elements, i, numMoved);
					}
					elements[--end] = null;
					return true;
				}
			}
			return false;
		}

		public boolean removeLastOccurrence(Object o) {
			for (int i = end - 1; i >= start; i--) {
				if (Objects.equals(elements[i], o)) {
					int numMoved = end - i - 1;
					if (numMoved > 0) {
						System.arraycopy(elements, i + 1, elements, i, numMoved);
					}
					elements[--end] = null;
					return true;
				}
			}
			return false;
		}

		public boolean contains(Object o) {
			for (int i = start; i < end; i++) {
				if (Objects.equals(elements[i], o)) {
					return true;
				}
			}
			return false;
		}
	}

	private Node<E> head;
	private Node<E> tail;
	private int size;
	private int capacity;
	private int containerCapacity;

	public TripletDeque() {
		this(1000, 5);
	}

	public TripletDeque(int capacity) {
		this(capacity, 5);
	}

	public TripletDeque(int capacity, int containerCapacity) {
		if (capacity <= 0 || containerCapacity <= 0) {
			throw new IllegalArgumentException("Capacities must be positive");
		}
		this.capacity = capacity;
		this.containerCapacity = containerCapacity;
		this.size = 0;
	}

	@Override
	public void addFirst(E e) {
		if (size >= capacity) {
			throw new IllegalStateException("Deque is full");
		}
		if (head == null) {
			head = tail = new Node<>(containerCapacity, true);
		}
		try {
			head.addFirst(e);
		} catch (IllegalStateException ex) {
			Node<E> newNode = new Node<>(containerCapacity, true);
			newNode.addFirst(e);
			newNode.next = head;
			head.prev = newNode;
			head = newNode;
		}
		size++;
	}

	@Override
	public void addLast(E e) {
		if (size >= capacity) {
			throw new IllegalStateException("Deque is full");
		}
		if (tail == null) {
			head = tail = new Node<>(containerCapacity, false);
		}
		try {
			tail.addLast(e);
		} catch (IllegalStateException ex) {
			Node<E> newNode = new Node<>(containerCapacity, false);
			newNode.addLast(e);
			tail.next = newNode;
			newNode.prev = tail;
			tail = newNode;
		}
		size++;
	}

	@Override
	public boolean offerFirst(E e) {
		if (size >= capacity) {
			return false;
		}
		addFirst(e);
		return true;
	}

	@Override
	public boolean offerLast(E e) {
		if (size >= capacity) {
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
		if (head == null) {
			return null;
		}
		E e = head.removeFirst();
		size--;
		if (head.isEmpty()) {
			head = head.next;
			if (head != null) {
				head.prev = null;
			} else {
				tail = null;
			}
		}
		return e;
	}

	@Override
	public E pollLast() {
		if (tail == null) {
			return null;
		}
		E e = tail.removeLast();
		size--;
		if (tail.isEmpty()) {
			tail = tail.prev;
			if (tail != null) {
				tail.next = null;
			} else {
				head = null;
			}
		}
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
		if (head != null){
			return head.peekFirst();
		} else {
			return null;
		}
	}

	@Override
	public E peekLast() {
		if (tail != null){
			return tail.peekLast();
		} else {
			return null;
		}
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		Node<E> current = head;
		while (current != null) {
			if (current.removeFirstOccurrence(o)) {
				size--;
				if (current.isEmpty()) {
					removeNode(current);
				}
				return true;
			}
			current = current.next;
		}
		return false;
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		Node<E> current = tail;
		while (current != null) {
			if (current.removeLastOccurrence(o)) {
				size--;
				if (current.isEmpty()) {
					removeNode(current);
				}
				return true;
			}
			current = current.prev;
		}
		return false;
	}

	private void removeNode(Node<E> node) {
		if (node.prev != null){
			node.prev.next = node.next;
		}
		else head = node.next;
		if (node.next != null){
			node.next.prev = node.prev;
		}
		else tail = node.prev;

		node.prev = null;
		node.next = null;
		node.elements = null;
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
		for (E e : c) {
			addLast(e);
		}
		return true;
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
		Node<E> current = head;
		while (current != null) {
			if (current.contains(o)) {
				return true;
			}
			current = current.next;
		}
		return false;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private Node<E> currentNode = head;
			private int index = (currentNode != null) ? currentNode.start : 0;
			private int lastReturnedIndex = -1;
			private Node<E> lastReturnedNode = null;

			@Override
			public boolean hasNext() {
				while (currentNode != null) {
					if (index < currentNode.end) {
						return true;
					} else {
						currentNode = currentNode.next;
						if (currentNode != null) {
							index = currentNode.start;
						}
					}
				}
				return false;
			}

			@Override
			public E next() {
				if (!hasNext()) throw new NoSuchElementException();
				lastReturnedIndex = index;
				lastReturnedNode = currentNode;
				return currentNode.elements[index++];
			}

			@Override
			public void remove() {
				if (lastReturnedNode == null) {
					throw new IllegalStateException();
				}
				int removeIndex = lastReturnedIndex;
				Node<E> node = lastReturnedNode;
				int numMoved = node.end - removeIndex - 1;
				if (numMoved > 0) {
					System.arraycopy(node.elements, removeIndex + 1, node.elements, removeIndex, numMoved);
				}
				node.elements[--node.end] = null;
				index = removeIndex; // Adjust index after removal
				size--;
				if (node.isEmpty()) {
					removeNode(node);
					if (currentNode == node) {
						currentNode = node.next;
						index = (currentNode != null) ? currentNode.start : 0;
					}
				}
				lastReturnedNode = null;
				lastReturnedIndex = -1;
			}
		};
	}


	@Override
	public Iterator<E> descendingIterator() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public Object[] getContainerByIndex(int cIndex) {
		if (cIndex < 0) {
			return null;
		}
		Node<E> current = head;
		int index = 0;
		while (current != null) {
			if (index == cIndex) {
				return current.elements;
			}
			current = current.next;
			index++;
		}
		return null;
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Object[] toArray() {
		Object[] result = new Object[size];
		int i = 0;
		for (E e : this) result[i++] = e;
		return result;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		int sz = size;
		if (a.length < sz) {
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), sz);
		}
		int i = 0;
		for (E e : this) a[i++] = (T) e;
		if (a.length > sz) a[sz] = null;
		return a;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		Objects.requireNonNull(c);
		for (Object o : c) if (!contains(o)) return false;
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Objects.requireNonNull(c);
		boolean modified = false;
		Iterator<E> it = iterator();
		while (it.hasNext()) {
			if (c.contains(it.next())) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Objects.requireNonNull(c);
		boolean modified = false;
		Iterator<E> it = iterator();
		while (it.hasNext()) {
			E e = it.next();
			if (!c.contains(e)) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}
}
