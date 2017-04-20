package MDRevealer;

import static MDRevealer.Parser.calculateCall;
import static MDRevealer.Parser.drugIdentifierCheck;
import static MDRevealer.Parser.identifierCheck;
import static MDRevealer.Util._;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/* GenericParser - Parses files using a custom parser file
 * 
 * Copyright (C) 2012-2015 Robin Clifford and Robert Clifford
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
public class GenericParser {

	public static ArrayList<Bacterium> multilineDrugs(ArrayList<Bacterium> bacteriacollection,
			HashMap<String, String> my_parser, int bacindex, ArrayList<String> drugs_list, String[] line,
			FileWriter warnwriter, int name_tab, int mic_tab, int call_tab, int dia_size, FileWriter debugwriter,
			int tabs) throws IOException {

		/*
		 * Sometimes the line we're reading is smaller than it should be. These
		 * next lines add blank lines to the ArrayList line to make sure that
		 * it's big enough.
		 */
		ArrayList<String> array_line = new ArrayList<String>(Arrays.asList(line));
		while (array_line.size() < tabs) {
			array_line.add(" ");
		}
		int drug_to_read = 0;

		for (int i = 0; i + mic_tab < (Integer.parseInt(my_parser.get("Drugs per line")) * dia_size) + mic_tab; i = i
				+ dia_size) {
			bacteriacollection.get(bacindex).addDrug(drugs_list.get(drug_to_read));
			drug_to_read++;

			// Debug output goes here
			debugwriter.write(
					bacteriacollection.get(bacindex).getDrug(bacteriacollection.get(bacindex).drugSize() - 1) + '\n');
			debugwriter.write("i = " + i + '\n');
			debugwriter.write("Getting MIC " + array_line.get(i + mic_tab) + " from tab " + (i + mic_tab) + '\n');
			bacteriacollection.get(bacindex).addOldMIC(array_line.get(i + mic_tab));
			debugwriter.flush();
			try {
				bacteriacollection.get(bacindex)
						.addMIC(Resistances.MICConvert(array_line.get(i + mic_tab), warnwriter));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				debugwriter
						.write("Getting call " + array_line.get(i + call_tab) + " from tab " + (i + call_tab) + '\n');
				bacteriacollection.get(bacindex).addCall(call.convert(array_line.get(i + call_tab), warnwriter));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (my_parser.containsKey("AMP C")) {
			switch (array_line.get(Integer.parseInt(my_parser.get("AMP C")))) {
			case "Pos":
				bacteriacollection.get(bacindex).setMachineESBL("true");
				break;
			case "Neg":
				bacteriacollection.get(bacindex).setMachineESBL("false");
				break;
			}
		}
		if (my_parser.containsKey("ESBL")) {
			switch (array_line.get(Integer.parseInt(my_parser.get("ESBL")))) {
			case "-":
				bacteriacollection.get(bacindex).setMachineAMPC("true");
				break;
			case "+":
				bacteriacollection.get(bacindex).setMachineAMPC("false");
				break;
			}
		}

		return bacteriacollection;
	}

	public static ArrayList<Bacterium> parse(ArrayList<mtf> mtfcollection, FileWriter warnwriter, int drugindex,
			ArrayList<DrugName> drugnamecollection, Boolean readdrugfile, ArrayList<String> in, String sourcehospital,
			ArrayList<Breakpoint> breakpointcollection, Boolean calcen, String parser,
			ArrayList<Organism> organismcollection, String custom_parser, FileWriter debugwriter)
			throws IOException, ParseException {
		ArrayList<String> identifiers = new ArrayList<>(0);
		ArrayList<Bacterium> bacteriacollection = new ArrayList<>(0);
		String[] line;
		String tempacc;
		String tempiso;
		String tempidentifier;
		String drugname;
		int bacindex;
		Bacterium bacterium;
		Double mic;
		String oldmic;
		String s;
		Boolean break_loops = false;
		int linenum = 0;
		String[] cparser_splitter;
		HashMap<String, String> my_parser = new HashMap<String, String>();

		ArrayList<String> drugs_list = new ArrayList<String>();

		File cparser = new File(custom_parser);

		ArrayList<String> cparser_lines = (ArrayList<String>) Files.readAllLines(cparser.toPath(),
				StandardCharsets.UTF_8);

		for (int i = 0; i < cparser_lines.size(); i++) {
			cparser_splitter = cparser_lines.get(i).split("\t");

			// Decrement the offsets in the custom parser so they start from 0
			// and not 1
			if (!cparser_splitter[1].matches("\\D+") && !cparser_splitter[0].contains("Drugs per line")) {
				cparser_splitter[1] = Integer.toString(Integer.parseInt(cparser_splitter[1]) - 1);
			}
			my_parser.put(cparser_splitter[0], cparser_splitter[1]);
		}

		int drug_info = Integer.parseInt(my_parser.get("Drug Info"));
		String[] drug_info_array = my_parser.get("Drug Info Format").split(",");
		int dia_size = drug_info_array.length;

		int name_tab = 0, mic_tab = 0, call_tab = 0;
		for (int i = 0; i < dia_size; i++) {
			switch (drug_info_array[i]) {
			case "Name":
				name_tab = i + drug_info;
				break;
			case "Call":
				call_tab = i + drug_info;
				break;
			case "MIC":
				mic_tab = i + drug_info;
				break;
			default:
				break;
			}
		}

		if (Integer.parseInt(my_parser.get("Drugs per line")) > 1) {
			String file_drugs = in.get(0);
			String[] file_drugs_splitter = file_drugs.split("\t");
			String trimmed_name = "null";
			int fds_length = file_drugs_splitter.length;
			for (int i = Integer.parseInt(my_parser.get("Drug Info")); i < fds_length; i++) {
				if (file_drugs_splitter[i].contains("Interpretation")) {
					trimmed_name = file_drugs_splitter[i].substring(0, file_drugs_splitter[i].indexOf(" "));
				} else {
					trimmed_name = file_drugs_splitter[i];
				}
				drugindex = drugIdentifierCheck(trimmed_name.toUpperCase(), drugnamecollection, true);
				if (drugindex != -2) {
					drugs_list.add(drugnamecollection.get(drugindex).getProperName());
				} else if (!file_drugs_splitter[i].contains("Instrument")
						&& !file_drugs_splitter[i].contains("Expertized") && !file_drugs_splitter[i].contains("MIC")) {
					drugs_list.add(file_drugs_splitter[i]);
				}
			}
		}

		int tabs = in.get(0).split("\t").length;

		for (int file = 1; file < in.size(); file++) {
			s = in.get(file);
			linenum++;
			debugwriter.write(_("Current line: " + linenum) + '\n');

			/* Skip blank lines */
			while (s.equals("")) {
				if (file + 1 == in.size()) {
					break_loops = true;
					break;
				} else {
					in.remove(file);
					s = in.get(file);
					linenum++;
					debugwriter.write(_("Current line: " + linenum) + '\n');
				}
			}

			if (break_loops == true) {
				break;
			}

			// Get a line
			line = s.split("\t");

			/*
			 * Get organism, antibiotic, mic, call, accession and isolate lines
			 */
			tempacc = line[Integer.parseInt(my_parser.get("Accession"))];
			tempiso = line[Integer.parseInt(my_parser.get("Isolate Number"))];

			/*
			 * We want to prevent false positives, so separate accession and
			 * isolate with a dash
			 */
			tempidentifier = tempacc + "-" + tempiso;

			/*
			 * When multiple bacteria are in the same culture, they won't always
			 * be listed on subsequent lines. This chunk of code checks to see
			 * if the bacteria's identifier is already known, and if so, add the
			 * data to that.
			 */
			bacindex = identifierCheck(identifiers, tempidentifier);

			if (bacindex == -1) {
				identifiers.add(tempidentifier);
				bacterium = new Bacterium(tempacc, tempiso);
				bacteriacollection.add(bacterium);
				bacindex = identifierCheck(identifiers, tempidentifier);
			}

			if (my_parser.containsKey("Full Name")) {
				String fullname = line[Integer.parseInt(my_parser.get("Full Name"))].replace("\"", "");
				String[] fullnamesplit = fullname.split(",");
				bacteriacollection.get(bacindex).setLastName(fullnamesplit[0]);
				bacteriacollection.get(bacindex).setFirstName(fullnamesplit[1]);
				bacteriacollection.get(bacindex).setMiddle(Parser.middleInitial(fullnamesplit[2]));
			}

			if (my_parser.containsKey("Last Name")) {
				bacteriacollection.get(bacindex).setLastName(line[Integer.parseInt(my_parser.get("Last Name"))]);
			}

			if (my_parser.containsKey("First Name")) {
				bacteriacollection.get(bacindex).setFirstName(line[Integer.parseInt(my_parser.get("First Name"))]);
			}

			if (my_parser.containsKey("Middle Initial")) {
				bacteriacollection.get(bacindex)
						.setMiddle(Parser.middleInitial(line[Integer.parseInt(my_parser.get("Middle Initial"))]));
			}

			bacteriacollection.get(bacindex).setLocation(mtf.convert(sourcehospital, mtfcollection, warnwriter));

			if (my_parser.containsKey("Social Security Number")) {
				bacteriacollection.get(bacindex)
						.setSSN(line[Integer.parseInt(my_parser.get("Social Security Number"))]);
			}

			if (my_parser.containsKey("SSN Prefix")) {
				bacteriacollection.get(bacindex).setSSNPrefix(line[Integer.parseInt(my_parser.get("SSN Prefix"))]);
			}

			if (my_parser.containsKey("Date of Birth")) {
				bacteriacollection.get(bacindex).setDOB(line[Integer.parseInt(my_parser.get("Date of Birth"))]);
			}

			if (my_parser.containsKey("Gender")) {
				bacteriacollection.get(bacindex).setGender(line[Integer.parseInt(my_parser.get("Gender"))]);
			}

			if (my_parser.containsKey("Isolation Date")) {
				bacteriacollection.get(bacindex)
						.setIsolationDate(line[Integer.parseInt(my_parser.get("Isolation Date"))]);
			}

			if (my_parser.containsKey("Culture Type")) {
				bacteriacollection.get(bacindex).setCultureType(line[Integer.parseInt(my_parser.get("Culture Type"))]);
			}

			if (my_parser.containsKey("Equipment")) {
				bacteriacollection.get(bacindex).setEquipment(line[Integer.parseInt(my_parser.get("Equipment"))]);
			}

			if (my_parser.containsKey("Source")) {
				bacteriacollection.get(bacindex).setSource(line[Integer.parseInt(my_parser.get("Source"))]);
			}

			if (my_parser.containsKey("Location Type")) {
				bacteriacollection.get(bacindex)
						.setLocationType(line[Integer.parseInt(my_parser.get("Location Type"))]);
			}

			if (my_parser.containsKey("Location")) {
				bacteriacollection.get(bacindex).setSpecific(line[Integer.parseInt(my_parser.get("Location"))]);
			}

			if (my_parser.containsKey("Date Format")) {
				bacteriacollection.get(bacindex).setDateFormat(my_parser.get("Date Format"));
			}

			if (my_parser.containsKey("Organism Name")) {
				bacteriacollection.get(bacindex).setName(Organism.convert(
						line[Integer.parseInt(my_parser.get("Organism Name"))].trim(), organismcollection, warnwriter));

				bacteriacollection.get(bacindex).setShortName(Organism.convertShort(
						line[Integer.parseInt(my_parser.get("Organism Name"))].trim(), organismcollection, warnwriter));
			}

			if (my_parser.containsKey("Alternate Organism Name")) {
				bacteriacollection.get(bacindex)
						.setAltName(line[Integer.parseInt(my_parser.get("Alternate Organism Name"))]);
			}
			
			if (my_parser.containsKey("Alternate Organism Name")) {
				bacteriacollection.get(bacindex)
						.setAltName(line[Integer.parseInt(my_parser.get("Alternate Organism Name"))]);
			}
            
            if (my_parser.containsKey("Machine Name")) {
				bacteriacollection.get(bacindex).setEquipment(my_parser.get("Machine Name"));
			}

			Boolean ignore_date = false;
			bacteriacollection.get(bacindex)
					.setSpin(Parser.makeSpin(bacteriacollection.get(bacindex), sourcehospital, ignore_date));

			if (line.length >= 22) {
				/*
				 * Determine if the file contains one line per drug per isolate,
				 * or one line per isolate
				 */
				if (Integer.parseInt(my_parser.get("Drugs per line")) > 1) {
					bacteriacollection = multilineDrugs(bacteriacollection, my_parser, bacindex, drugs_list, line,
							warnwriter, name_tab, mic_tab, call_tab, dia_size, debugwriter, tabs);

				} else {
					drugindex = drugIdentifierCheck(line[name_tab], drugnamecollection, readdrugfile);
					if (drugindex == -2) {
						warnwriter.write(MessageFormat.format(
								_("Warning: the drug name \"{0}\" " + "does not appear in {1}. Either "
										+ "this is a typo, or the name is " + "missing from the file."),
								new Object[] { line[name_tab], "drugnames.txt" }) + '\n');
						bacteriacollection.get(bacindex).addDrug(line[name_tab]);
						drugname = line[name_tab];
					} else {
						bacteriacollection.get(bacindex).addDrug(drugnamecollection.get(drugindex).getProperName());
						drugname = drugnamecollection.get(drugindex).getProperName();
					}
					bacteriacollection.get(bacindex).addOldMIC(line[mic_tab]);
					bacteriacollection.get(bacindex).addMIC(Resistances.MICConvert(line[mic_tab], warnwriter));
					mic = Resistances.MICConvert(line[mic_tab], warnwriter);
					oldmic = line[mic_tab];
					if (calcen.equals(true)) {
						bacteriacollection.get(bacindex)
								.addCall(calculateCall(bacteriacollection.get(bacindex), drugname, mic, oldmic,
										breakpointcollection, warnwriter, call.convert(line[call_tab], warnwriter)));
					} else {
						bacteriacollection.get(bacindex).addCall(call.convert(line[call_tab], warnwriter));
					}
				}
			}
		}
		return bacteriacollection;
	}
}
