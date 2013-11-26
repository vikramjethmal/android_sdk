/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.beans;

import java.util.ArrayList;

public class OperatorsRespBean extends ResponseBaseBean
{
	private ArrayList<OperatorDetails> operators = null;

	public ArrayList<OperatorDetails> getOperators() {
		return operators;
	}

	public void setOperators(ArrayList<OperatorDetails> operators) {
		this.operators = operators;
	}
}
