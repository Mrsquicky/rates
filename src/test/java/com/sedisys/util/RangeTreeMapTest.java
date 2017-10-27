package com.sedisys.util;

import com.sedisys.util.range.Range;
import com.sedisys.util.range.RangeTreeMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RangeTreeMapTest {

	private void verifyZeroToThirteenTree(RangeTreeMap<Integer, Integer> testMap) {
		Assert.assertEquals(new Range<>(6, 7), testMap.getRootNode().getNodeRange());
		Assert.assertEquals(new Range<>(0, 13), testMap.getRootNode().getTotalRange());

		Assert.assertEquals(new Range<>(2, 3), testMap.getRootNode().getLeftNode().getNodeRange());
		Assert.assertEquals(new Range<>(0, 5), testMap.getRootNode().getLeftNode().getTotalRange());

		Assert.assertEquals(new Range<>(0, 1), testMap.getRootNode().getLeftNode().getLeftNode().getNodeRange());
		Assert.assertEquals(new Range<>(0, 1), testMap.getRootNode().getLeftNode().getLeftNode().getTotalRange());

		Assert.assertEquals(new Range<>(4, 5), testMap.getRootNode().getLeftNode().getRightNode().getNodeRange());
		Assert.assertEquals(new Range<>(4, 5), testMap.getRootNode().getLeftNode().getRightNode().getTotalRange());

		Assert.assertEquals(new Range<>(10, 11), testMap.getRootNode().getRightNode().getNodeRange());
		Assert.assertEquals(new Range<>(8, 13), testMap.getRootNode().getRightNode().getTotalRange());

		Assert.assertEquals(new Range<>(8, 9), testMap.getRootNode().getRightNode().getLeftNode().getNodeRange());
		Assert.assertEquals(new Range<>(8, 9), testMap.getRootNode().getRightNode().getLeftNode().getTotalRange());

		Assert.assertEquals(new Range<>(12, 13), testMap.getRootNode().getRightNode().getRightNode().getNodeRange());
		Assert.assertEquals(new Range<>(12, 13), testMap.getRootNode().getRightNode().getRightNode().getTotalRange());
	}

	@Test
	public void testRangeMapStraightLoad(){
		RangeTreeMap<Integer, Integer> testMap = new RangeTreeMap<>();

		for (int i=0;i<=12;i+=2){
			testMap.addRangedReference(i, start -> new Range<>(start, start+1));
		}

		verifyZeroToThirteenTree(testMap);
	}

	@Test
	public void testRangeMapBackwardsLoad(){
		RangeTreeMap<Integer, Integer> testMap = new RangeTreeMap<>();

		for (int i=12;i>=0;i-=2){
			testMap.addRangedReference(i, start -> new Range<>(start, start+1));
		}

		verifyZeroToThirteenTree(testMap);
	}

	private boolean validateNode(RangeTreeMap<Integer, Integer>.Node node){
		if (node.getLeftNode()!=null){
			if (!node.getLeftNode().getSubtreeMin().equals(node.getSubtreeMin())){
				return false;
			}
			validateNode(node.getLeftNode());
		} else{
			if (!node.getSubtreeMin().equals(node.getNodeRange().getStart())){
				return false;
			}
		}

		if (node.getRightNode()!=null) {
			if (node.getLeftNode() != null) {
				Integer max = Integer.max(node.getLeftNode().getSubtreeMax(), node.getRightNode().getSubtreeMax());
				if (!node.getSubtreeMax().equals(max)) {
					return false;
				}
			} else {
				if (!node.getSubtreeMax().equals(node.getRightNode().getSubtreeMax())) {
					return false;
				}
			}
			validateNode(node.getRightNode());
		} else{
			if (!node.getSubtreeMax().equals(node.getNodeRange().getEnd())){
				return false;
			}
		}
		return true;
	}

	@Test
	public void testRangeMapRandomLoad(){
		RangeTreeMap<Integer, Integer> testMap = new RangeTreeMap<>();

		List<Range<Integer>> ranges = new ArrayList<>(7);
		for (int i=0;i<=12;i+=2){
			ranges.add(new Range<>(i, i+1));
		}

		for (int i=0;i<6;i++) {
			Collections.shuffle(ranges);

			for (Range<Integer> range : ranges) {
				testMap.addRangedReference(range, range.getStart());
			}

			if (testMap.isBalanced()) {
				verifyZeroToThirteenTree(testMap);
			} else{
				Assert.assertTrue(validateNode(testMap.getRootNode()));
			}
		}
	}

}
