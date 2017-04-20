package MDRevealer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static MDRevealer.Util._;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

/* Resistances - Computes resistance levels of bacteria
 * 
 * Copyright (C) 2012-2014 Robin Clifford and Robert Clifford
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
public class Resistances {

	/*
	 * Check to see if a bacterium should be excluded from the generic
	 * Carbapenem test
	 */
	public static Boolean namecheck(String name,
			ArrayList<String> carbcollection) {
		for (int i = 0; i < carbcollection.size(); i++) {
			if (name.toUpperCase().contains(carbcollection.get(i))) {
				return false;
			}
		}
		return true;
	}

	/* Checks whether a bacterium is multi-drug resistant */
	public static String mdrCheck(Bacterium bacterium,
			ArrayList<MDR> mdrcollection, Boolean localized,
			ArrayList<String> carbcollection, FileWriter warnwriter) {
		/*
		 * Antibiotic categories - named after constellations (I don't just use
		 * a, b, etc. because it might conflict with the variables in my loops)
		 */
		int antlia = 0;
		int bootes = 0;
		int carina = 0;
		int delphinus = 0;
		int eridanus = 0;
		int fornax = 0;
		int grus = 0;
		int horlogium = 0;
		int indus = 0;
		int jordanus = 0; /*
						 * Former constellation: no constellation recognized by
						 * the IAU begins with J
						 */
		int korchab = 0; /*
						 * A star: no constellations, either historical or
						 * current, begin with K
						 */
		int libra = 0;
		int monoceros = 0;
		int norma = 0;
		int octans = 0;
		int categories = 0;
		int totalcats = 0;
		String resistance = null;

		/*
		 * For each bacterium we pass to mdrCheck, it reads thorugh
		 * mdrcollection. If the bacterium's name matches an entry in
		 * mdrcollection's list of names, the function checks to see if one of
		 * the drugs the bacterium was tested against matches a drug in
		 * mdrcollection. If so, the function checks to see if it was resistant,
		 * and if so, add it to one of antlia, bootes, carina, delphinus,
		 * eridanus, fornax, grus, horlogium, indus, jordanus, korchab, libra,
		 * monoceros, norma, or octans.
		 */
		for (int i = 0; i < mdrcollection.size(); i++) {
			bacterium
					.setGeneric(namecheck(bacterium.getName(), carbcollection));
			if (bacterium.getName().contains(mdrcollection.get(i).getName())) {
				for (int j = 0; j < bacterium.drugSize(); j++) {
					for (int k = 0; k < mdrcollection.get(i).getDrugnameSize(); k++) {
						if (bacterium.getDrug(j).contains(
								mdrcollection.get(i).getDrugname(k))) {
							if (bacterium.getCall(j).equals("R")
									|| bacterium.getCall(j).equals("I")) {
								switch (mdrcollection.get(i).getCategory(k)) {
								case "A":
									antlia++;
									break;
								case "B":
									bootes++;
									break;
								case "C":
									carina++;
									break;
								case "D":
									delphinus++;
									break;
								case "E":
									eridanus++;
									break;
								case "F":
									fornax++;
									break;
								case "G":
									grus++;
									break;
								case "H":
									horlogium++;
									break;
								case "I":
									indus++;
									break;
								case "J":
									jordanus++;
									break;
								case "K":
									korchab++;
									break;
								case "L":
									libra++;
									break;
								case "M":
									monoceros++;
									break;
								case "N":
									norma++;
									break;
								case "O":
									octans++;
									break;
								}
							}
						}
					}
				}
			}
		}
		if (antlia > 0) {
			categories++;
		}
		if (bootes > 0) {
			categories++;
		}
		if (carina > 0) {
			categories++;
		}
		if (delphinus > 0) {
			categories++;
		}
		if (eridanus > 0) {
			categories++;
		}
		if (fornax > 0) {
			categories++;
		}
		if (grus > 0) {
			categories++;
		}
		if (horlogium > 0) {
			categories++;
		}
		if (indus > 0) {
			categories++;
		}
		if (jordanus > 0) {
			categories++;
		}
		if (korchab > 0) {
			categories++;
		}
		if (libra > 0) {
			categories++;
		}
		if (monoceros > 0) {
			categories++;
		}
		if (norma > 0) {
			categories++;
		}
		if (octans > 0) {
			categories++;
		}

		/* Get the total number of categories for the bacterium */
		for (int i = 0; i < mdrcollection.size(); i++) {
			if (bacterium.getName().contains(mdrcollection.get(i).getName())) {
				for (int j = 0; j < bacterium.drugSize(); j++) {
					for (int k = 0; k < mdrcollection.get(i).getDrugnameSize(); k++) {
						if (bacterium.getDrug(j).contains(
								mdrcollection.get(i).getDrugname(k))) {
							totalcats = mdrcollection.get(i).getTotalcats();
						}
					}
				}
			}
		}

		/* If it didn't find any categories, it's not tested for MDR status */
		if (totalcats == 0) {
			if (localized == false) {
				resistance = "from a species not tested for MDR status";
				return resistance;
			} else {
				resistance = _("from a species not tested for MDR status");
				return resistance;
			}
		}

		int pdr = antlia + bootes + carina + delphinus + eridanus + fornax
				+ grus + horlogium + indus + jordanus + korchab + libra
				+ monoceros + norma + octans;

		if (categories >= 3) {
			resistance = "MDR";
		}
		if (categories >= totalcats - 2) {
			resistance = "XDR";
		}
		if (pdr == bacterium.drugSize() && categories == totalcats) {
			resistance = "PDR";
		}
		if (resistance == null) {
			if (localized == false) {
				resistance = "not MDR";
			} else {
				resistance = _("not MDR");
			}
		}
		return resistance;
	}

	/*
	 * Convert MIC values to proper doubles, for Carbapenem testing. - >=X
	 * returns X + 0.001 - >X returns (X * 2) + 0.001 - <=X returns X - 0.001 -
	 * <X returns (X * / 2) - 0.001 If it's none of these, and it doesn't
	 * contain "POS" or "NEG" (to avoid crashes when trying to covert "POS" or
	 * "NEG" to a double), return X. Otherwise, it's an invalid value, so return
	 * -1.0 (we won't use this for anything anyway).
	 */
	public static double MICConvert(String MIC, FileWriter warnwriter)
			throws IOException {
		String newmic;
		double d;
		String[] MICsplitter;
		if (MIC.contains("/")) {
			MICsplitter = MIC.split("/");
			MIC = MICsplitter[0];
		}
		if (MIC.startsWith(">")) {
			newmic = MIC.substring(1, MIC.length());
			if (newmic.startsWith("=")) {
				newmic = newmic.substring(1, newmic.length());
				d = Float.parseFloat(newmic);
				return d + 0.001;
			} else {
				d = Float.parseFloat(newmic);
				return (d * 2) + 0.001;
			}
		} else if (MIC.startsWith("<")) {
			newmic = MIC.substring(1, MIC.length());
			if (newmic.startsWith("=")) {
				newmic = newmic.substring(1, newmic.length());
				d = Float.parseFloat(newmic);
				return d - 0.001;
			} else {
				d = Float.parseFloat(newmic);
				return (d / 2) - 0.001;
			}
		} else if (MIC.matches("\\D+") == false && MIC.startsWith("<") == false
				&& MIC.equals("") == false && MIC.contains("-") == false
				&& MIC.equals(" ") == false) {
			d = Float.parseFloat(MIC);
			return d;
		} else {
			return -1.0;
		}
	}

	/*
	 * Figures out what the machine-calculated ESBL status is. This gets
	 * recorded in the spreadsheet.
	 */
	public static String MachineESBLTest(
			ArrayList<Bacterium> bacteriacollection, int i) {
		String machine_esbl = "null";
		for (int j = 0; j < bacteriacollection.get(i).drugSize(); j++) {
			if (bacteriacollection.get(i).getDrug(j).equals("ESBL")) {
				switch (bacteriacollection.get(i).getCall(j)) {
				case "Pos":
				case "+":
					machine_esbl = "true";
					break;
				case "Neg":
				case "-":
					machine_esbl = "false";
					break;
				}
			}
		}
		return machine_esbl;
	}

	/*
	 * Figures out what the machine-calculated AMP C status is. This gets
	 * recorded in the spreadsheet.
	 */
	public static String MachineAMPCTest(
			ArrayList<Bacterium> bacteriacollection, int i) {
		String machine_amp_c = "null";
		for (int j = 0; j < bacteriacollection.get(i).drugSize(); j++) {
			if (bacteriacollection.get(i).getDrug(j).equals("AMP C")) {
				switch (bacteriacollection.get(i).getCall(j)) {
				case "Pos":
				case "+":
					machine_amp_c = "true";
					break;
				case "Neg":
				case "-":
					machine_amp_c = "false";
					break;
				}
			}
		}
		return machine_amp_c;
	}

	public static ArrayList<Bacterium> calculate(
			ArrayList<Bacterium> bacteriacollection,
			ArrayList<MDR> mdrcollection, Boolean localized,
			ArrayList<String> carbcollection, FileWriter warnwriter,
			FileWriter debugwriter) throws XMLStreamException, IOException {
		for (int i = 0; i < bacteriacollection.size(); i++) {
			bacteriacollection.get(i).setMdr(
					Resistances.mdrCheck(bacteriacollection.get(i),
							mdrcollection, localized, carbcollection,
							warnwriter));
			for (int j = 0; j < bacteriacollection.get(i).drugSize(); j++) {
				switch (bacteriacollection.get(i).getCall(j)) {
				case "S":
					bacteriacollection.get(i).addSensitive(
							bacteriacollection.get(i).getDrug(j));
					break;
				case "R":
					bacteriacollection.get(i).addResistant(
							bacteriacollection.get(i).getDrug(j));
					break;
				case "I":
					bacteriacollection.get(i).addResistant(
							bacteriacollection.get(i).getDrug(j));
					break;
				}
			}
			File xml_dir = new File("data/xml");
			String[] files = xml_dir.list();
			Arrays.sort(files);
			for (int tests = 0; tests < files.length; tests++) {
				File rt_input = new File("data/xml/" + files[tests]);
				debugwriter.write(_(MessageFormat.format("Running test {0}",
						rt_input)) + '\n');
				ResistanceTests.do_tests(bacteriacollection, rt_input, i);
			}
			if (bacteriacollection.get(i).getMachineESBL() == "null") {
				bacteriacollection.get(i).setMachineESBL(
						MachineESBLTest(bacteriacollection, i));
			}
			if (bacteriacollection.get(i).getMachineAMPC() == "null") {
				bacteriacollection.get(i).setMachineAMPC(
						MachineAMPCTest(bacteriacollection, i));
			}
		}
		return bacteriacollection;
	}
}
