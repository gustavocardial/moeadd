//  UMDAc.java
//
//  Authors:
//       Gustavo Cardial <gustavo@cardial.com.br>
//       Olacir Castro Jr. <olacir.junior@ufac.br>
//


package jmetal.operators.crossover;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;

import java.lang.Math;

import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XReal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class UMDAc extends Crossover {

	private static final List VALID_TYPES = Arrays.asList(
			RealSolutionType.class, ArrayRealSolutionType.class);


	public UMDAc(HashMap<String, Object> parameters) {
		super(parameters);
	}

	/**
	 * Perform the crossover operation.
	 * 
	 * @param probability
	 *            Crossover probability
	 * @param parent1
	 *            The first parent
	 * @param parent2
	 *            The second parent
	 * @return An array containing the two offsprings
	 */
	public Solution[] doCrossover(Object object) throws JMException {

		Random random = new Random();
		
		Solution[] parents = (Solution[]) object;
		XReal xParent = new XReal(parents[0]);
		
		int numberOfParents = parents.length;
		int numberOfVariables = xParent.getNumberOfDecisionVariables();

		double[] m = new double[numberOfVariables];
		double[] std = new double[numberOfVariables];
		
		//initialize with zeros
		//for (int i = 0; i < numberOfVariables; i++) {
		//	m[i] = 0;
		//	std[i] = 0;
		//}
		
		//compute mean
		for (int i = 0; i < numberOfVariables; i++) {

			for (int j = 0; j < numberOfParents; j++) {

				XReal x = new XReal(parents[j]);								
				m[i] += x.getValue(i);
			}
			m[i] = m[i] / numberOfParents;
		}
	
		//compute standard deviation					
		for (int i = 0; i < numberOfVariables; i++){

			for (int j = 0; j < numberOfParents; j++){

				XReal x = new XReal(parents[j]);
				std[i] += Math.pow( (x.getValue(i) - m[i]) , 2);
			}

			std[i] = Math.sqrt( std[i] / numberOfParents );
		}

		//generate offspring
		Solution[] offspring = new Solution[numberOfParents];
		
		for (int i = 0; i < numberOfParents; i++) {
			//TODO: IS THE LINE BELOW CORRECT?
			Solution child = new Solution(parents[0]); 
			XReal xChild = new XReal(child);
			
			for (int j = 0; j < numberOfVariables; j++) {
			
				double v = m[j] + random.nextGaussian() * std[j];
				xChild.setValue(j, v);
			}
			offspring[i] = child;
		}
		
		return offspring;
	} // doCrossover

	/**
	 * Executes the operation
	 * 
	 * @param object
	 *            An object containing an array of two parents
	 * @return An object containing the offSprings
	 */
	 public Object execute(Object object) throws JMException {
		Solution[] parents = (Solution[]) object;

		// TODO: Check all parents
		if (!(VALID_TYPES.contains(parents[0].getType().getClass()) && VALID_TYPES
				.contains(parents[1].getType().getClass()))) {
			Configuration.logger_.severe("UMDAc.execute: the solutions "
					+ "type " + parents[0].getType()
					+ " is not allowed with this operator");

			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if

		Solution[] offSpring;

		offSpring = doCrossover(parents);

		return offSpring;
	} // execute
} // SBXCrossover
