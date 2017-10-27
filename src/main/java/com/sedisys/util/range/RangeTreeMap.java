package com.sedisys.util.range;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Steve on 11/4/2016.
 */
public class RangeTreeMap<T extends Comparable<? super T>, Referenced> implements RangeMap<T, Referenced> {
	private final Map<Range<T>, List<Referenced>> rangeToReferencedMap;

	private Node rootNode;

	public RangeTreeMap() {
		rangeToReferencedMap = new HashMap<>();
	}


	@Override
	public Map<Range<T>, List<Referenced>> getRangeToReferencedMap() {
		return rangeToReferencedMap;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public boolean isBalanced(){
		return rootNode.isBalanced();
	}

	private Node insert(Node node, Range<T> nodeRange) {
		if (node == null) {
			return new Node(nodeRange);
		} else {
			if (nodeRange.compareTo(node.getNodeRange()) < 0) {
				node.setLeftNode(insert(node.getLeftNode(), nodeRange));
			} else {
				node.setRightNode(insert(node.getRightNode(), nodeRange));
			}
			int depthDiff = node.depthDifference();
			if (depthDiff < -1) {
				if (node.getRightNode() != null && node.rightNode.depthDifference() > 0) {
					node.setRightNode(node.getRightNode().rightRotate());
					return node.leftRotate();
				} else {
					return node.leftRotate();
				}
			} else if (depthDiff > 1) {
				if (node.getLeftNode() != null && node.getLeftNode().depthDifference() < 0) {
					node.setLeftNode(node.getLeftNode().leftRotate());
					return node.rightRotate();
				} else {
					return node.rightRotate();
				}
			}
		}
		return node;
	}

	@Override
	public void addRangedReference(Range<T> keyRange, Referenced referenced) {
		if (!rangeToReferencedMap.containsKey(keyRange)) {
			rangeToReferencedMap.put(keyRange, new ArrayList<>());
			if (rootNode == null) {
				rootNode = new Node(keyRange);
			} else {
				insert(rootNode, keyRange);
				while (rootNode.getParentNode()!=null){
					rootNode = rootNode.getParentNode();
				}
			}
		}
		rangeToReferencedMap.get(keyRange).add(referenced);
	}

	@Override
	public void addRangedReference(Referenced referenced, Function<Referenced, Range<T>> rangeExtractor) {
		addRangedReference(rangeExtractor.apply(referenced), referenced);
	}

	@Override
	public Set<Range<T>> findMatchingRanges(T searchValue) {
		if (rootNode != null) {
			Set<Node> matchingNodes = rootNode.findMatchingNodes(searchValue);
			return matchingNodes.stream().map(Node::getNodeRange).collect(Collectors.toSet());
		}
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<Referenced> findMatchingObjects(T searchValue) {
		Set<Referenced> returnMatchingObjects = new HashSet<>();
		if (rootNode != null) {
			Set<Node> matchingNodes = rootNode.findMatchingNodes(searchValue);
			for (Node matchingNode : matchingNodes) {
				returnMatchingObjects.addAll(getRangeToReferencedMap().get(matchingNode.getNodeRange()));
			}
		}
		return returnMatchingObjects;
	}

	@Override
	public Set<Range<T>> findOverlappingRanges(Range<T> searchRange) {
		if (rootNode != null) {
			Set<Node> matchingNodes = rootNode.findOverlappingNodes(searchRange);
			return matchingNodes.stream().map(Node::getNodeRange).collect(Collectors.toSet());
		}
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<Referenced> findOverlappingObjects(Range<T> searchRange) {
		Set<Referenced> returnMatchingObjects = new HashSet<>();
		if (rootNode != null) {
			Set<Node> matchingNodes = rootNode.findOverlappingNodes(searchRange);
			for (Node matchingNode : matchingNodes) {
				returnMatchingObjects.addAll(getRangeToReferencedMap().get(matchingNode.getNodeRange()));
			}
		}
		return returnMatchingObjects;
	}

	@Override
	public Set<Range<T>> findContainingRanges(Range<T> searchRange) {
		if (rootNode != null) {
			Set<Node> matchingNodes = rootNode.findContainingNodes(searchRange);
			return matchingNodes.stream().map(Node::getNodeRange).collect(Collectors.toSet());
		}
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<Referenced> findContainingObjects(Range<T> searchRange) {
		Set<Referenced> returnMatchingObjects = new HashSet<>();
		if (rootNode != null) {
			Set<Node> matchingNodes = rootNode.findContainingNodes(searchRange);
			for (Node matchingNode : matchingNodes) {
				returnMatchingObjects.addAll(getRangeToReferencedMap().get(matchingNode.getNodeRange()));
			}
		}
		return returnMatchingObjects;
	}

	@Override
	public String toString() {
		if (rootNode == null) {
			return "Empty";
		}
		StringBuilder builder = new StringBuilder();
		Stack<Node> fullNodeStack = new Stack<>();
		fullNodeStack.push(rootNode);
		int emptyLeaf = 2^rootNode.getLevelCount();
		boolean isRowEmpty = false;

		while (!isRowEmpty) {
			Stack<Node> levelNodeStack = new Stack<>();
			isRowEmpty = true;
			for (int i = 0; i < emptyLeaf; i++) {
				builder.append(' ');
			}
			while (!fullNodeStack.isEmpty()) {
				Node currentNode = fullNodeStack.pop();
				if (currentNode != null) {
					builder.append(currentNode.getNodeRange());
					levelNodeStack.push(currentNode.getLeftNode());
					levelNodeStack.push(currentNode.getRightNode());
					if (!currentNode.isLeaf()) {
						isRowEmpty = false;
					}
				} else {
					builder.append("--");
					levelNodeStack.push(null);
					levelNodeStack.push(null);
				}
				for (int i = 0; i < emptyLeaf * 2 - 2; i++)
					builder.append(' ');
			}
			builder.append(System.lineSeparator());
			emptyLeaf /= 2;
			while (!levelNodeStack.isEmpty()) {
				fullNodeStack.push(levelNodeStack.pop());
			}
		}
		return builder.toString();
	}

	public class Node {
		private Range<T> nodeRange;
		private Range<T> totalRange;
		private Node leftNode;
		private Node rightNode;
		private Node parentNode;

		private Node(Range<T> nodeRange) {
			this.nodeRange = nodeRange;
			this.totalRange = new Range<>(nodeRange.getStart(), nodeRange.getEnd());
			setSubtreeMax(nodeRange.getEnd());
		}

		public Range<T> getNodeRange() {
			return nodeRange;
		}

		public void setNodeRange(Range<T> nodeRange) {
			this.nodeRange = nodeRange;
		}

		public Range<T> getTotalRange() {
			return totalRange;
		}

		public T getSubtreeMin() {
			return totalRange.getStart();
		}

		public void setSubtreeMin(T subtreeMin) {
			totalRange.setStart(subtreeMin);
			if (getParentNode() != null) {
				if (getSubtreeMin().compareTo(parentNode.getSubtreeMin()) < 0) {
					parentNode.setSubtreeMin(subtreeMin);
				}
			}
		}

		public T getSubtreeMax() {
			return totalRange.getEnd();
		}

		public void setSubtreeMax(T subtreeMax) {
			totalRange.setEnd(subtreeMax);
			if (parentNode != null) {
				if (getSubtreeMax().compareTo(parentNode.getSubtreeMax()) > 0) {
					parentNode.setSubtreeMax(getSubtreeMax());
				}
			}
		}

		public Node getParentNode() {
			return parentNode;
		}

		public void setParentNode(Node parentNode) {
			this.parentNode = parentNode;
			if (parentNode!=null) {
				if (getSubtreeMax().compareTo(parentNode.getSubtreeMax()) > 0) {
					parentNode.setSubtreeMax(getSubtreeMax());
				}
			}
		}

		public Node getLeftNode() {
			return leftNode;
		}

		public void setLeftNode(Node toSet) {
			leftNode = toSet;
			if (toSet != null) {
				setSubtreeMin(toSet.getSubtreeMin());
				toSet.setParentNode(this);
			} else{
				setSubtreeMin(getNodeRange().getStart());
			}
		}

		public Node getRightNode() {
			return rightNode;
		}

		public void setRightNode(Node toSet) {
			rightNode = toSet;
			if (toSet != null) {
				setSubtreeMax(toSet.getSubtreeMax());
				toSet.setParentNode(this);
			} else if (getLeftNode()!=null && getLeftNode().getSubtreeMax().compareTo(getNodeRange().getEnd())>0){
				setSubtreeMax(getLeftNode().getSubtreeMax());
			} else{
				setSubtreeMax(getNodeRange().getEnd());
			}
		}

		public boolean isLeaf(){
			return getLeftNode()==null && getRightNode()==null;
		}

		public int getLevelCount() {
			int leftDepth = leftNode != null ? leftNode.getLevelCount() : 0;
			int rightDepth = rightNode != null ? rightNode.getLevelCount() : 0;
			return Math.max(leftDepth, rightDepth) + 1;
		}

		private boolean doesLeftNodeContain(T searchValue) {
			return getLeftNode() != null && searchValue.compareTo(getNodeRange().getStart()) < 0 && getLeftNode().getTotalRange().contains(searchValue);
		}

		private boolean doesRightNodeContain(T searchValue) {
			return getRightNode() != null && searchValue.compareTo(getSubtreeMax()) <= 0 && getRightNode().getTotalRange().contains(searchValue);
		}

		private void findMatchingNodesStep(T searchValue, Set<Node> matchingNodes) {
			if (getTotalRange().contains(searchValue)) {
				if (getNodeRange().contains(searchValue)) {
					matchingNodes.add(this);
				}
				if (doesLeftNodeContain(searchValue)) {
					getLeftNode().findMatchingNodesStep(searchValue, matchingNodes);
				} else if (doesRightNodeContain(searchValue)) {
					getRightNode().findMatchingNodesStep(searchValue, matchingNodes);
				}
			}
		}

		private Set<Node> findMatchingNodes(T searchValue) {
			Set<Node> matchingNodes = new HashSet<>();
			findMatchingNodesStep(searchValue, matchingNodes);
			return matchingNodes;
		}

		private boolean doesLeftNodeOverlap(Range<T> searchRange) {
			return getLeftNode() != null && getLeftNode().getTotalRange().overlaps(searchRange);
		}

		private boolean doesRightNodeOverlap(Range<T> searchRange) {
			return getRightNode() != null && getRightNode().getTotalRange().overlaps(searchRange);
		}

		private void findOverlappingNodesStep(Range<T> searchRange, Set<Node> matchingNodes) {
			if (getTotalRange().overlaps(searchRange)) {
				if (getNodeRange().overlaps(searchRange)) {
					matchingNodes.add(this);
				}
				if (doesLeftNodeOverlap(searchRange)) {
					getLeftNode().findOverlappingNodesStep(searchRange, matchingNodes);
				}
				if (doesRightNodeOverlap(searchRange)) {
					getRightNode().findOverlappingNodesStep(searchRange, matchingNodes);
				}
			}
		}

		private Set<Node> findOverlappingNodes(Range<T> searchRange) {
			Set<Node> matchingNodes = new HashSet<>();
			findOverlappingNodesStep(searchRange, matchingNodes);
			return matchingNodes;
		}

		private boolean doesLeftNodeContain(Range<T> searchRange) {
			return getLeftNode() != null && getLeftNode().getTotalRange().contains(searchRange);
		}

		private boolean doesRightNodeContain(Range<T> searchRange) {
			return getRightNode() != null && getRightNode().getTotalRange().contains(searchRange);
		}

		private void findContainingNodesStep(Range<T> searchRange, Set<Node> matchingNodes) {
			if (getTotalRange().contains(searchRange)) {
				if (getNodeRange().contains(searchRange)) {
					matchingNodes.add(this);
				}
				if (doesLeftNodeContain(searchRange)) {
					getLeftNode().findContainingNodesStep(searchRange, matchingNodes);
				}
				if (doesRightNodeContain(searchRange)) {
					getRightNode().findContainingNodesStep(searchRange, matchingNodes);
				}
			}
		}

		private Set<Node> findContainingNodes(Range<T> searchRange) {
			Set<Node> matchingNodes = new HashSet<>();
			findContainingNodesStep(searchRange, matchingNodes);
			return matchingNodes;
		}

		private Node leftRotate() {
			Node rightHolder = getRightNode();
			rightHolder.setParentNode(getParentNode());
			setRightNode(rightHolder.getLeftNode());
			rightHolder.setLeftNode(this);
			return rightHolder;
		}

		private Node rightRotate() {
			Node leftHolder = getLeftNode();
			leftHolder.setParentNode(getParentNode());
			setLeftNode(leftHolder.getRightNode());
			leftHolder.setRightNode(this);
			return leftHolder;
		}

		private int depthDifference() {
			int leftDepth = getLeftNode() != null ? getLeftNode().getLevelCount() : 0;
			int rightDepth = getRightNode() != null ? getRightNode().getLevelCount() : 0;
			return leftDepth - rightDepth;
		}

		private boolean isBalanced(){
			return depthDifference() == 0;
		}
	}
}
