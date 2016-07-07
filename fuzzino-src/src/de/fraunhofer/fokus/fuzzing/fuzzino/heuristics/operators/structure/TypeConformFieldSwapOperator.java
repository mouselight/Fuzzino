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
package de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.operators.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.fraunhofer.fokus.fuzzing.fuzzino.FuzzedValue;
import de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.operators.SimpleFuzzingOperator;
import de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.operators.StructureOperator;
import de.fraunhofer.fokus.fuzzing.fuzzino.structure.Structure;

public class TypeConformFieldSwapOperator extends SimpleFuzzingOperator<Structure> implements StructureOperator{
	
	private Structure structure;
	private List<List<Integer>> sameTypeIndexGroups;
	private List<Integer> swappableIndices = new ArrayList<>();

	public TypeConformFieldSwapOperator(Structure inputValue, long seed) {
		super(inputValue, seed);
		this.structure = inputValue;
		if(!structure.getSpecification().isOrdered()){
			size=0;
		} else{
			//memorize at which position which type can be found:
			sameTypeIndexGroups = TypeHandler.INSTANCE.getSameTypeIndexGroups(structure); 
			for(List<Integer> typeGroup: sameTypeIndexGroups){
				if(typeGroup.size()!=1){
					swappableIndices.addAll(typeGroup);
				} 
			}
			size = swappableIndices.size();
			Collections.sort(swappableIndices);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return "TypeConformFieldSwapOperator";
	}

	@Override
	public FuzzedValue<Structure> computeElement(int index) {
		index = swappableIndices.get(index);
		Integer swapIndex = TypeHandler.INSTANCE.getDifferentElementFromSameGroup(sameTypeIndexGroups, index);
		//execute swap
		Structure fuzzedStructure = StructureOperatorUtil.copyStructure(structure);
		StructureOperatorUtil.swapFields(fuzzedStructure,index,swapIndex);
		return new FuzzedValue<Structure>(fuzzedStructure,inputValue,this);
	}

	@Override
	public int size() {
		return size;
	}
	
}
