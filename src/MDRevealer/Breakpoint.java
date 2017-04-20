package MDRevealer;

import java.util.ArrayList;

/*
 * Copyright (C) 2012-2013 Robin Clifford and Robert Clifford
 * This program is part of MDRevealer.
 * MDRevealer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MDRevealer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Breakpoint {

	private String name;
	private ArrayList<String> drugnames;
	private ArrayList<Double> sensMIC;
	private ArrayList<Double> resMIC;

	public Breakpoint() {
		name = "null";
		drugnames = new ArrayList<>(0);
		sensMIC = new ArrayList<>(0);
		resMIC = new ArrayList<>(0);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDrug(int i) {
		return this.drugnames.get(i);
	}

	public void addDrug(String s) {
		this.drugnames.add(s);
	}

	public int drugSize() {
		return this.drugnames.size();
	}

	public Double getSensMIC(int i) {
		return this.sensMIC.get(i);
	}

	public void addSensMIC(Double s) {
		this.sensMIC.add(s);
	}

	public int sensMICSize() {
		return this.sensMIC.size();
	}

	public Double getResMIC(int i) {
		return this.resMIC.get(i);
	}

	public void addResMIC(Double s) {
		this.resMIC.add(s);
	}

	public int resMICSize() {
		return this.resMIC.size();
	}
}
