package MDRevealer;

import static MDRevealer.Parser.calculateCall;
import static MDRevealer.Parser.drugIdentifierCheck;
import static MDRevealer.Parser.identifierCheck;
import static MDRevealer.Util._;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;

/* Parse9067 - parses files from facilities #9067 and #0123
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
public class Phoenix1 {

	public static ArrayList<Bacterium> parse(ArrayList<mtf> mtfcollection,
			FileWriter warnwriter, int drugindex,
			ArrayList<DrugName> drugnamecollection, Boolean readdrugfile,
			ArrayList<String> in, String sourcehospital,
			ArrayList<Breakpoint> breakpointcollection, Boolean calcen,
			String parser, ArrayList<Organism> organismcollection,
			FileWriter debugwriter) throws IOException, ParseException {
		ArrayList<Bacterium> bacteriacollection = new ArrayList<>(0);
		ArrayList<String> identifiers = new ArrayList<>(0);
		String[] line;
		String tempacc;
		String[] namesplitter;
		String[] ssnsplitter;
		String[] datesplitter;
		int bacindex = 0;
		String tempidentifier = "null";
		Bacterium bacterium;
		String lastacc = "null";
		int bfisolate = 1;
		Boolean reordered = false;
		int linenum = 1;
		String drugname;
		Double mic;
		String oldmic;
		String s;
		Boolean break_loops = false;

		for (int file = 0; file < in.size(); file++) {
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

			debugwriter.write(_("Current line: " + linenum));
			/* Get a line */
			line = s.split("\t");

			/* Fix blank names and write a warning about them */
			if (line[1].trim().equals("")) {
				line[1] = "NA NA";
				warnwriter.write(MessageFormat.format(
						_("Warning: accession \"{0}\" is missing a name"),
						new Object[] { line[3] }) + '\n');

			}
			tempacc = line[3];

			/*
			 * Files from this facility contain no isolate numbers, so I have to
			 * brute force them. Hence the spaghetti code.
			 */
			if (tempacc.equals(lastacc)) {
				for (int i = 0; i < bacteriacollection.size(); i++) {
					if (bacteriacollection.get(i).getAccession()
							.equals(tempacc)
							&& bacteriacollection
									.get(i)
									.getName()
									.equals(Organism.convert(line[6].trim(),
											organismcollection, warnwriter))) {
						tempidentifier = bacteriacollection.get(i)
								.getIdentifier();
						reordered = true;
					}
				}
				if (reordered == false) {
					bfisolate++;
				}
			} else {
				bfisolate = 1;
			}

			if (reordered == false) {
				tempidentifier = tempacc + "-" + "0" + bfisolate;
			}

			/*
			 * When multiple bacteria are in the same culture, they won't always
			 * be listed on subsequent lines. This chunk of code checks to see
			 * if the bacteria's identifier is already known, and if so, add the
			 * data to that.
			 */
			/*
			 * This first line scans through our list of identifiers for this
			 * bacterium's identifier. If it's there, add its information to the
			 * bacterium in the collection with the matching identifier. If not,
			 * add it to the temporary bacterium.
			 */
			bacindex = identifierCheck(identifiers, tempidentifier);

			if (bacindex == -1) {
				identifiers.add(tempidentifier);
				bacterium = new Bacterium(tempacc, "0" + bfisolate);
				bacteriacollection.add(bacterium);
				bacindex = identifierCheck(identifiers, tempidentifier);
			}

			/* Get date */
			datesplitter = line[0].split(" ");
			bacteriacollection.get(bacindex).setIsolationDate(datesplitter[0]);

			// Remove quotes
			while (line[1].contains("\"")) {
				line[1] = line[1].replace("\"", "");
			}
			while (line[1].contains("\'")) {
				line[1] = line[1].replace("\'", "");
			}

			// Remove commas in names
			while (line[1].contains(",")) {
				line[1] = line[1].replace(",", " ");
			}

			// Remove excess whitespace
			while (line[1].contains("  ")) {
				line[1] = line[1].replace("  ", " ");
			}
			namesplitter = line[1].split(" ");
			bacteriacollection.get(bacindex).setLastName(namesplitter[0]);
			if (namesplitter.length > 1) {
				bacteriacollection.get(bacindex).setFirstName(namesplitter[1]);
			}
			if (namesplitter.length == 3) {
				namesplitter[2] = namesplitter[2].substring(0,
						namesplitter[2].length() - 1);
				if (namesplitter[2].length() > 1) {
					namesplitter[2] = namesplitter[2].substring(0, 1);
				}
				bacteriacollection.get(bacindex).setMiddle(namesplitter[2]);
			}
			ssnsplitter = line[2].split("/");
			if (ssnsplitter.length == 2) {
				bacteriacollection.get(bacindex).setSSN(ssnsplitter[1]);
				bacteriacollection.get(bacindex).setSSNPrefix(ssnsplitter[0]);
			} else {
				bacteriacollection.get(bacindex).setSSN(ssnsplitter[0]);
			}
			bacteriacollection.get(bacindex).setSpecific(line[4]);
			bacteriacollection.get(bacindex).setCultureType(line[5]);
			bacteriacollection.get(bacindex).setName(
					Organism.convert(line[6].trim(), organismcollection,
							warnwriter));
			bacteriacollection.get(bacindex).setShortName(
					Organism.convertShort(line[6].trim(), organismcollection,
							warnwriter));
			bacteriacollection.get(bacindex).setLocation(
					mtf.convert(sourcehospital, mtfcollection, warnwriter));
			bacteriacollection.get(bacindex).setEquipment("Phoenix");

			if (line.length == 11) {
				drugindex = drugIdentifierCheck(line[7], drugnamecollection,
						readdrugfile);
				if (drugindex == -2) {
					warnwriter
							.write(MessageFormat
									.format(_("Warning: the drug name \"{0}\" does not appear in {1}. Either this is a typo, or the name is missing from the file."),
											new Object[] { line[19],
													"drugnames.txt" }) + '\n');
					/*
					 * Since Phoenix doesn't print isolate numbers, I have any
					 * drugs with the same names as existing ones overwrite the
					 * ones in a bacterium. This errs on the side of caution,
					 * since Phoenix sorts its drugs by MIC.
					 */
					for (int i = 0; i < bacteriacollection.get(bacindex)
							.drugSize(); i++) {
						if (line[7].equals(bacteriacollection.get(bacindex)
								.getDrug(i))) {
							bacteriacollection.get(bacindex).removeDrug(i);
							bacteriacollection.get(bacindex).removeCall(i);
							bacteriacollection.get(bacindex).removeMIC(i);
						}
					}
					bacteriacollection.get(bacindex).addDrug(line[7]);
					drugname = line[7];
				} else {
					for (int i = 0; i < bacteriacollection.get(bacindex)
							.drugSize(); i++) {
						if (drugnamecollection
								.get(drugindex)
								.getProperName()
								.equals(bacteriacollection.get(bacindex)
										.getDrug(i))) {
							bacteriacollection.get(bacindex).removeDrug(i);
							bacteriacollection.get(bacindex).removeCall(i);
							bacteriacollection.get(bacindex).removeMIC(i);
						}
					}
					bacteriacollection.get(bacindex).addDrug(
							drugnamecollection.get(drugindex).getProperName());
					drugname = drugnamecollection.get(drugindex)
							.getProperName();
				}
				bacteriacollection.get(bacindex).addOldMIC(line[8]);
				bacteriacollection.get(bacindex).addMIC(
						Resistances.MICConvert(line[8], warnwriter));
				mic = Resistances.MICConvert(line[8], warnwriter);
				oldmic = line[8];
				if (calcen.equals(true)) {
					bacteriacollection.get(bacindex).addCall(
							calculateCall(bacteriacollection.get(bacindex),
									drugname, mic, oldmic,
									breakpointcollection, warnwriter,
									call.convert(line[10], warnwriter)));
				} else {
					bacteriacollection.get(bacindex).addCall(
							call.convert(line[10], warnwriter));
				}
			}
			Boolean ignore_date = true;
			bacteriacollection.get(bacindex).setSpin(
					Parser.makeSpin(bacteriacollection.get(bacindex),
							sourcehospital, ignore_date));
;
			lastacc = bacteriacollection.get(bacindex).getAccession();
			linenum++;
			reordered = false;
		}
		/* Clean out duplicates */
		return bacteriacollection;
	}
}
