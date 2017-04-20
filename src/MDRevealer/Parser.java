package MDRevealer;

import static MDRevealer.Util._;

import java.awt.FileDialog;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

/* Parser - Parses data
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

public class Parser {
	/*
	 * Scans through a list of identifiers for a given identifier. If it's
	 * there, it returns the index of that identifier. Otherwise, it returns -1.
	 */

	public static int identifierCheck(ArrayList<String> identifiers,
			String identifier) {
		for (int i = 0; i < identifiers.size(); i++) {
			if (identifier.equals(identifiers.get(i)) == true) {
				return i;
			}
		}
		return -1;
	}

	/*
	 * Search each nickname field in each drugname in drugnamecollection for
	 * scrubname. If found, return the index of the drugname. If not found, and
	 * we've read the entire file of aliases, either it's a name not in the
	 * alias file or a typo. Either way, we return -2. Otherwise, return -1.
	 */
	public static int drugIdentifierCheck(String scrubname,
			ArrayList<DrugName> drugnamecollection, Boolean readdrugfile) {
		DrugName checker;
		for (int j = 0; j < drugnamecollection.size(); j++) {
			checker = drugnamecollection.get(j);
			for (int k = 0; k < checker.nicknameSize(); k++) {
				if (checker.getNickname(k).toUpperCase()
						.equals(scrubname.toUpperCase())) {
					return j;
				} else if (checker.getProperName().toUpperCase()
						.equals(scrubname.toUpperCase())) {
					return k;
				}
			}
		}
		if (readdrugfile == true) {
			return -2;
		}
		return -1;
	}

	// Adds entries to mdrcollection.
	public static void mdrAdder(String mdrorgname, String catname,
			String mdrdrugname, ArrayList<MDR> mdrcollection) {
		MDR iba = new MDR();
		Boolean found = false;
		iba.setName(mdrorgname);
		iba.addCategory(catname);
		iba.addDrugname(mdrdrugname);
		for (int i = 0; i < mdrcollection.size(); i++) {
			if (mdrcollection.get(i).getName().equals(mdrorgname)) {
				mdrcollection.get(i).addCategory(catname);
				mdrcollection.get(i).addDrugname(mdrdrugname);
				found = true;
			}
		}
		if (found == false) {
			mdrcollection.add(iba);
		}
	}

	// Make a spin - date of isolation + hospital code + accession + isolate
	public static String makeSpin(Bacterium b, String sourcehospital,
			Boolean ignore_date) throws ParseException {
		String spin = "";
		Date id_converter = null;
		String accession = b.getAccession();
		String isolate = b.getIsolate();

		if (b.getIsolationDate().isEmpty()) {
			return spin;
		}

		// Convert the date to MM/dd/YYYY
		if (ignore_date == false) {
			id_converter = new SimpleDateFormat(b.getDateFormat()).parse(b
					.getIsolationDate());
			b.setIsolationDate(new SimpleDateFormat("MM/dd/YYYY")
					.format(id_converter));
		}

		// Remove any nonnumeric characters from the accession and isolate
		accession = accession.replaceAll("\\D+", "");

		String[] isosplitter = b.getIsolationDate().split("/");
		// Zero-pad acc/isolate, pad the date to XX/YY/ZZZZ
		while (isosplitter[0].length() < 2) {
			isosplitter[0] = "0" + isosplitter[0];
		}
		while (isosplitter[1].length() < 2) {
			isosplitter[1] = "0" + isosplitter[1];
		}
		if (isosplitter[2].length() == 2) {
			isosplitter[2] = "20" + isosplitter[2];
		}

		/* 0-pad accession and isolate */
		while (accession.length() < 6) {
			accession = "0" + accession;
		}
		while (isolate.length() < 2) {
			isolate = "0" + isolate;
		}
		spin = isosplitter[0] + isosplitter[1] + isosplitter[2]
				+ sourcehospital + accession + isolate;
		return spin;
	}

	/*
	 * If the name of the bacterium matches a name in breakpointcollection, and
	 * we find a match for drug names, check the MIC. If it's greater than or
	 * equal to getResMIC, it's resistant. If it's less than or equal to
	 * getSensMIC, it's sensitive. If it's between the two, it's indeterminate.
	 * If it's none of the above, the program will print a warning, and use the
	 * value from the machine.
	 */
	public static String calculateCall(Bacterium b, String drugname,
			Double mic, String oldmic,
			ArrayList<Breakpoint> breakpointcollection, FileWriter warnwriter,
			String s) throws IOException {
		for (int i = 0; i < breakpointcollection.size(); i++) {
			if (b.getName().contains(breakpointcollection.get(i).getName())) {
				for (int j = 0; j < breakpointcollection.get(i).drugSize(); j++) {
					if (drugname.equals(breakpointcollection.get(i).getDrug(j))) {
						if (oldmic.contains(">")) {
							if ((mic >= breakpointcollection.get(i)
									.getResMIC(j))) {
								return "R";
							} else if ((mic >= breakpointcollection.get(i)
									.getSensMIC(j) * 2)) {
								return "I";
							} else {
								return "N";
							}
						} else if (oldmic.contains("<")) {
							if (mic < breakpointcollection.get(i).getSensMIC(j)) {
								return "S";
							} else {
								return "N";
							}

						} else if (mic >= breakpointcollection.get(i)
								.getResMIC(j)) {
							return "R";
						} else if (mic <= breakpointcollection.get(i)
								.getSensMIC(j)) {
							return "S";
						} else {
							return "I";
						}
					}
				}
			}
		}

		warnwriter.write(MessageFormat.format(
				_("No breakpoint found for {0} and {1}"),
				new Object[] { b.getName(), drugname }) + '\n');
		return s;
	}

	public static String middleInitial(String middle) {
		if (middle.length() > 1) {
			middle = middle.substring(0, 1);
		}
		return middle;
	}

	public static void parse(JFrame mainwindow, Boolean deten, Boolean sumen,
			Boolean spren, Boolean daten, Boolean locdeten,
			String sourcehospital, Boolean calcen, String parser,
			String[] defaults, FileWriter warnwriter, String bp, String mdr,
			ArrayList<String> datconfig, String custom_parser)
			throws FileNotFoundException, IOException, XMLStreamException,
			ParseException {

		ArrayList<DrugName> drugnamecollection = new ArrayList<>(0);
		String scrubname;
		String goodname;
		/*
		 * If we find an unknown name after we've finished reading
		 * drugnames.txt, we add it to our list and print a warning message.
		 * Readdrugfile keeps track of whether we've read drugnames.txt or not.
		 */
		Boolean readdrugfile = false;
		int drugindex = 0;

		ArrayList<MDR> mdrcollection = new ArrayList<>(0);
		String mdrorgname;
		String catname;
		String mdrdrugname;

		ArrayList<mtf> mtfcollection = new ArrayList<>(0);
		String mtfold;
		String mtfnew;

		ArrayList<String> carbcollection = new ArrayList<>(0);
		String carbstring;

		ArrayList<Breakpoint> breakpointcollection = new ArrayList<>(0);

		DrugName drugname;

		File input;
		File drugnames;
		File resistanceinfo;
		File hospitals;
		File carbapenem;
		File breakpoints;
		File warnings;
		File organisms;

		FileWriter debugwriter = new FileWriter("debug.txt");

		ArrayList<Bacterium> bacteriacollection;

		Boolean localized = false;

		warnings = new File("warnings.txt");
		if (warnings.canRead() == false) {
			warnings.createNewFile();
		}
		FileDialog getinput = new FileDialog(mainwindow,
				_("Select a file to read from"));
		getinput.setVisible(true);
		input = new File(getinput.getDirectory() + getinput.getFile());
		if (input.exists() == false) {
			JOptionPane.showMessageDialog(mainwindow,
					_("Error: no file specified!"));
			mainwindow.dispatchEvent(new WindowEvent(mainwindow,
					WindowEvent.WINDOW_CLOSING));
		}

		drugnames = new File("data/drugnames.txt");
		if (drugnames.canRead() == false) {
			warnwriter.write(MessageFormat.format(_("{0} not found."),
					new Object[] { "drugnames.txt" }));
			drugnames.createNewFile();
		}

		if (!mdr.equals("null")) {
			resistanceinfo = new File(mdr);
		} else {
			FileDialog getmdr = new FileDialog(mainwindow,
					_("Select an MDR file"));
			getmdr.setVisible(true);
			if (getmdr.getFile() == null) {
				resistanceinfo = new File("data/mdr.txt");
			} else {
				resistanceinfo = new File(getmdr.getDirectory()
						+ getmdr.getFile());
			}
		}
		hospitals = new File("data/hospitals.txt");
		if (hospitals.canRead() == false) {
			warnwriter.write(MessageFormat.format(_("{0} not found."),
					new Object[] { "hospitals.txt" }));
			hospitals.createNewFile();
		}
		carbapenem = new File("data/carbapenem.txt");
		if (carbapenem.canRead() == false) {
			warnwriter.write(MessageFormat.format(_("{0} not found."),
					new Object[] { "carbapenem.txt" }));
			carbapenem.createNewFile();
		}

		if (!bp.equals("null")) {
			breakpoints = new File(bp);
		} else {
			FileDialog getbreakpoints = new FileDialog(mainwindow,
					_("Select a breakpoints file"));
			getbreakpoints.setVisible(true);
			if (getbreakpoints.getFile() == null) {
				breakpoints = new File("data/breakpoints.txt");
			} else {
				breakpoints = new File(getbreakpoints.getDirectory()
						+ getbreakpoints.getFile());
			}
		}

		organisms = new File("data/organisms.txt");
		if (organisms.canRead() == false) {
			warnwriter.write(MessageFormat.format(_("{0} not found."),
					new Object[] { "organisms.txt" }));
			organisms.createNewFile();
		}

		/* Read in organisms */

		ArrayList<String> orgsin = (ArrayList<String>) Files.readAllLines(
				organisms.toPath(), StandardCharsets.UTF_8);
		int orglinenum = 0;
		String orgstring;
		String[] orgsplitter;
		String oldorg;
		String neworg;
		String shortorg;
		Organism org;
		ArrayList<Organism> organismcollection = new ArrayList<>(0);
		Boolean break_loops = false;
		int linenum = 0;
		for (int file = 0; file < orgsin.size(); file++) {
			orgstring = orgsin.get(file);
			orglinenum++;
			debugwriter
					.write(_("Current organisms.txt line: " + orglinenum) + '\n');

			/* Skip blank lines */
			while (orgstring.equals("")) {
				if (file + 1 == orgsin.size()) {
					break_loops = true;
					break;
				} else {
					orgsin.remove(file);
					orgstring = orgsin.get(file);
					linenum++;
					debugwriter.write(_("Current organisms.txt line: "
							+ orglinenum) + '\n');
				}
			}

			if (break_loops == true) {
				break;
			}
			orgsplitter = orgstring.split("\t");
			oldorg = orgsplitter[0].toUpperCase().trim();
			neworg = orgsplitter[1].trim();
			shortorg = orgsplitter[2].trim();
			org = new Organism(oldorg, neworg, shortorg);
			organismcollection.add(org);
		}
		linenum = 0;

		ArrayList<String> drugsin = (ArrayList<String>) Files.readAllLines(
				drugnames.toPath(), StandardCharsets.UTF_8);
		String drugstring = drugsin.get(0);
		String[] drugsplitter = drugstring.split("\t");
		scrubname = drugsplitter[0];
		goodname = drugsplitter[1];
		drugname = new DrugName(goodname);
		drugname.addNickname(scrubname);
		drugnamecollection.add(drugname);
		for (int file = 0; file < drugsin.size(); file++) {
			drugstring = drugsin.get(file);
			linenum++;
			debugwriter
					.write(_("Current drugnames.txt line: " + linenum) + '\n');

			/* Skip blank lines */
			while (drugstring.equals("")) {
				if (file + 1 == drugsin.size()) {
					break_loops = true;
					break;
				} else {
					drugsin.remove(file);
					drugstring = drugsin.get(file);
					linenum++;
					debugwriter.write(_("Current drugnames.txt line: "
							+ linenum) + '\n');
				}
			}

			if (break_loops == true) {
				break;
			}
			drugsplitter = drugstring.split("\t");
			scrubname = drugsplitter[0];
			goodname = drugsplitter[1];
			drugindex = drugIdentifierCheck(goodname, drugnamecollection,
					readdrugfile);
			if (drugindex == -1) {
				drugname = new DrugName(goodname);
				drugname.addNickname(goodname);
				drugnamecollection.add(drugname);
				drugindex = drugIdentifierCheck(goodname, drugnamecollection,
						readdrugfile);
			}
			drugnamecollection.get(drugindex).addNickname(scrubname);
		}
		readdrugfile = true;
		linenum = 0;

		ArrayList<String> mdrin = (ArrayList<String>) Files.readAllLines(
				resistanceinfo.toPath(), StandardCharsets.UTF_8);
		String mdrstring = mdrin.get(0);
		String[] mdrsplitter = mdrstring.split("\t");
		mdrorgname = mdrsplitter[0];
		catname = mdrsplitter[1];
		mdrdrugname = mdrsplitter[2];
		MDR tempmdr = new MDR(mdrorgname);
		String lastcat;
		String lastorg;
		int totalcats = 0;
		int num = 0;
		mdrcollection.add(tempmdr);
		mdrcollection.get(0).addCategory(catname);
		mdrcollection.get(0).addDrugname(mdrdrugname);
		lastcat = catname;
		lastorg = mdrorgname;
		mdrstring = mdrin.get(1);
		for (int file = 0; file < mdrin.size(); file++) {
			mdrstring = mdrin.get(file);
			linenum++;
			debugwriter.write(_("Current mdr.txt line: " + linenum) + '\n');

			/* Skip blank lines */
			while (mdrstring.equals("")) {
				if (file + 1 == mdrin.size()) {
					break_loops = true;
					break;
				} else {
					mdrin.remove(file);
					mdrstring = mdrin.get(file);
					linenum++;
					debugwriter
							.write(_("Current mdr.txt line: " + linenum) + '\n');
				}
			}

			if (break_loops == true) {
				break;
			}
			mdrsplitter = mdrstring.split("\t");
			mdrorgname = mdrsplitter[0];
			catname = mdrsplitter[1];
			mdrdrugname = mdrsplitter[2];

			if (!lastcat.equals(catname)) {
				totalcats++;
			}
			if (!lastorg.equals(mdrorgname)) {
				num++;
				mdrcollection.get(num - 1).setTotalcats(totalcats);
				totalcats = 0;
			}
			mdrAdder(mdrorgname, catname, mdrdrugname, mdrcollection);
			lastcat = catname;
			lastorg = mdrorgname;
		}
		totalcats++;
		num++;
		mdrcollection.get(num - 1).setTotalcats(totalcats);
		linenum = 0;

		ArrayList<String> mtfin = (ArrayList<String>) Files.readAllLines(
				hospitals.toPath(), StandardCharsets.UTF_8);
		ArrayList<String> carbin = (ArrayList<String>) Files.readAllLines(
				carbapenem.toPath(), StandardCharsets.UTF_8);
		carbstring = carbin.get(0);
		for (int file = 0; file < carbin.size(); file++) {
			carbstring = carbin.get(file);
			linenum++;
			debugwriter
					.write(_("Current carbapenem.txt line: " + linenum) + '\n');

			/* Skip blank lines */
			while (carbstring.equals("")) {
				if (file + 1 == carbin.size()) {
					break_loops = true;
					break;
				} else {
					carbin.remove(file);
					carbstring = carbin.get(file);
					linenum++;
					debugwriter.write(_("Current carbapenem.txt line: "
							+ linenum) + '\n');
				}
			}

			if (break_loops == true) {
				break;
			}
			carbcollection.add(carbstring);
		}
		linenum = 0;

		String mtfstring = null;
		for (int file = 1; file < mtfin.size(); file++) {
			mtfstring = mtfin.get(file);
			linenum++;
			debugwriter
					.write(_("Current hospitals.txt line: " + linenum) + '\n');

			/* Skip blank lines */
			while (mtfstring.equals("")) {
				if (file + 1 == mtfin.size()) {
					break_loops = true;
					break;
				} else {
					mtfin.remove(file);
					mtfstring = mtfin.get(file);
					linenum++;
					debugwriter.write(_("Current hospitals.txt line: "
							+ linenum) + '\n');
				}
			}

			if (break_loops == true) {
				break;
			}
			String[] mtfsplitter = mtfstring.split("\t");
			if (mtfsplitter.length == 3) {
				mtfold = mtfsplitter[0];
				mtfnew = mtfsplitter[2];
				mtf tempmtf = new mtf(mtfold, mtfnew);
				mtfcollection.add(tempmtf);
			}
		}
		linenum = 0;

		ArrayList<String> callin = (ArrayList<String>) Files.readAllLines(
				breakpoints.toPath(), StandardCharsets.UTF_8);
		String callstring = null;
		String[] callsplitter;
		String callorg;
		String calldrug;
		Double callsens;
		Double callres;
		Breakpoint b = new Breakpoint();
		for (int file = 0; file < callin.size(); file++) {
			callstring = callin.get(file);
			linenum++;
			debugwriter
					.write(_("Current breakpoints.txt line: " + linenum) + '\n');

			/* Skip blank lines */
			while (callstring.equals("")) {
				if (file + 1 == callin.size()) {
					break_loops = true;
					break;
				} else {
					callin.remove(file);
					callstring = callin.get(file);
					linenum++;
					debugwriter.write(_("Current breakpoints.txt line: "
							+ linenum) + '\n');
				}
			}

			if (break_loops == true) {
				break;
			}
			callsplitter = callstring.split("\t");
			callorg = Organism.convert(callsplitter[0].toUpperCase().trim(),
					organismcollection, warnwriter);
			calldrug = callsplitter[1];
			callsens = Resistances.MICConvert(callsplitter[2], warnwriter);
			callres = Resistances.MICConvert(callsplitter[3], warnwriter);
			b.setName(callorg);
			b.addDrug(calldrug);
			b.addSensMIC(callsens);
			b.addResMIC(callres);
			breakpointcollection.add(b);
			b = new Breakpoint();
		}
		linenum = 0;

		ArrayList<String> in = (ArrayList<String>) Files.readAllLines(
				input.toPath(), StandardCharsets.UTF_8);
		if (parser.equals("dat") || parser.equals("spreadsheet")) {
			in.remove(0);
		}
		bacteriacollection = new ArrayList<>();
		switch (parser) {
		case "Vitek1":
			bacteriacollection = Vitek1.parse(mtfcollection, warnwriter,
					drugindex, drugnamecollection, readdrugfile, in,
					bacteriacollection, sourcehospital, breakpointcollection,
					calcen, parser, organismcollection, debugwriter);
			break;
		case "Phoenix1":
			bacteriacollection = Phoenix1.parse(mtfcollection, warnwriter,
					drugindex, drugnamecollection, readdrugfile, in,
					sourcehospital, breakpointcollection, calcen, parser,
					organismcollection, debugwriter);
			break;
		case "custom":
			bacteriacollection = GenericParser.parse(mtfcollection, warnwriter,
					drugindex, drugnamecollection, readdrugfile, in,
					sourcehospital, breakpointcollection, calcen, parser,
					organismcollection, custom_parser, debugwriter);
			break;
		}

		/* Copy BAMC accessions back to regular accessions for printout */
		for (int i = 0; i < bacteriacollection.size(); i++) {
			if (bacteriacollection.get(i).getBAMCAccession().contains("BAB")) {
				bacteriacollection.get(i).setAccession(
						bacteriacollection.get(i).getBAMCAccession());
			}
		}

		if (deten == true || sumen == true || spren == true || daten == true) {
			Resistances.calculate(bacteriacollection, mdrcollection, localized,
					carbcollection, warnwriter, debugwriter);
		}
		if (deten == true) {
			Output.printOutput(bacteriacollection, mdrcollection, mainwindow,
					defaults);
		}
		if (sumen == true) {
			Output.printSummary(bacteriacollection, mdrcollection, mainwindow,
					defaults);
		}
		if (spren == true) {
			Output.printSpreadsheet(bacteriacollection, mdrcollection,
					mainwindow, defaults);
		}
		if (daten == true) {
			Output.printDat(bacteriacollection, mdrcollection, mainwindow,
					defaults, datconfig, warnwriter);
		}
		if (locdeten == true) {
			localized = true;
			/*
			 * Clear resistance/sensitive info in bacteriacollection, to prevent
			 * duplication of information
			 */
			for (int i = 0; i < bacteriacollection.size(); i++) {
				bacteriacollection.get(i).clearResistant();
				bacteriacollection.get(i).clearSensitive();
			}
			Resistances.calculate(bacteriacollection, mdrcollection, localized,
					carbcollection, warnwriter, debugwriter);
		}
		if (locdeten == true) {
			Output.printLocalizedOutput(bacteriacollection, mdrcollection,
					mainwindow, defaults);
		}
		warnwriter.close();
	}
}
