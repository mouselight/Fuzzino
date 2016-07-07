//   Copyright 2012-2013 Fraunhofer FOKUS
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.generators.string;

import java.util.ArrayList;
import java.util.ListIterator;

import de.fraunhofer.fokus.fuzzing.fuzzino.FuzzedValue;
import de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.ComputableFuzzingHeuristic;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.StringSpecification;

/**
 * This class creates step-wise all combinations of elements of several lists.
 * Each elements generated by this class consists of one element of each list
 * that was added to this combinator class.
 * The size is the product of all lists' sizes.
 * 
 * @author Martin Schneider (martin.schneider@fokus.fraunhofer.de)
 *
 */
public class Combinator extends ComposedStringGenerator {

	private static final long serialVersionUID = 7750996340980526568L;

	/**
	 * Creates a new instance with all the lists whose elements shall be combined.
	 * @param theLists all the lists whose elements shall be combined.
	 */
	@SafeVarargs
	public Combinator(StringSpecification stringSpec, long seed, ComputableFuzzingHeuristic<?> owner, ComputableFuzzingHeuristic<String> ...theLists) {
		super(stringSpec, seed, owner);
		heuristics = new ArrayList<>(theLists.length);
		for (ComputableFuzzingHeuristic<String> list : theLists) {
			heuristics.add(list);
		}
	}
	
	@Override
	public FuzzedValue<String> computeElement(int index) {
		if (index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		String result = "";
		ListIterator<ComputableFuzzingHeuristic<String>> i = heuristics.listIterator();
		while (i.hasNext()) {
			int currentIndex = i.nextIndex();
			ComputableFuzzingHeuristic<String> currentList = i.next();
			int indexForCurrentList = getIndex(currentIndex, index);
			result += currentList.get(indexForCurrentList).getValue();
		}
		
		FuzzedValue<String> fuzzedValue = new FuzzedValue<>(result, owner);
		
		return fuzzedValue;
	}

	@Override
	public int size() {
		int result = 1;
		for (ComputableFuzzingHeuristic<String> list : heuristics) {
			result *= list.size();
		}
		return result;
	}
	
	/**
	 * Auxiliary function that calculates the index of an element to be taken from
	 * a specific list depending on the index that was requested from this combinator.
	 * 
	 * @param listIndex specifies the list to be taken by its index.
	 * @param index the index that was requested from this combinator.
	 * @return the list-specific index. This index identifies the next element
	 *         taken from the specified list.
	 */
	protected int getIndex(int listIndex, int index) {
		int result = (index / listLengthProduct(listIndex-1)) % heuristics.get(listIndex).size();
		return result;
		
	}
	/**
	 * Calculates the product of all list lengths up to a specified list identified by its index.
	 * 
	 * @param lastListNo the index of the list the product should be calculated up to.
	 * @return the product of all list sizes up to <code>lastListNo</code>.
	 */
	protected int listLengthProduct(int lastListNo) {
		int result = 1;
		ListIterator<ComputableFuzzingHeuristic<String>> i = heuristics.listIterator(); 
		while (i.nextIndex() <= lastListNo) {
			result *= i.next().size();
		}
		
		return result;
	}

	@Override
	public boolean canCreateValuesFor(StringSpecification stringSpecification) {
		return true; // TODO check against FuzzingHeuristics
	}

	@Override
	public String getName() {
		return owner.getName();
	}

}
