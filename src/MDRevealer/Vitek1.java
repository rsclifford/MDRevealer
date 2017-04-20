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
import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

/* Vitek1 - parses files from Vitek machines
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
public class Vitek1 {

	public static ArrayList<Bacterium> parse(ArrayList<mtf> mtfcollection, FileWriter warnwriter, int drugindex,
			ArrayList<DrugName> drugnamecollection, Boolean readdrugfile, ArrayList<String> in,
			ArrayList<Bacterium> bacteriacollection, String sourcehospital, ArrayList<Breakpoint> breakpointcollection,
			Boolean calcen, String parser, ArrayList<Organism> organismcollection, FileWriter debugwriter)
			throws IOException, ParseException {
		String[] stringsplitter;
		String[] ssnsplitter;
		String[] namesplitter;
		String[] isolationsplitter;
		int bacindex;
		bacindex = 0;
		int isolatenum = 0;
		String tempacc = "null";
		Boolean drugs = false;
		String tempiso;
		String tempidentifier;
		String tempfirst = "null";
		String templast = "null";
		String tempmiddle = "null";
		String tempdob = "null";
		String tempssnprefix = "";
		String tempssn = "null";
		String tempgender = "null";
		String tempculture = "null";
		String tempspecific = "null";
		String tempisolation = "null";
		String bamcaccession = "null";
		String isolate_line = "null";
		ArrayList<String> identifiers = new ArrayList<>(0);
		Bacterium bacterium;
		String drugname;
		Double mic;
		String oldmic;
		String s;
		Boolean break_loops = false;
		int linenum = 0;
		HashMap<String, String> hm = new HashMap<String, String>();
		ArrayList<HashMap<String, String>> vitek_isolates = new ArrayList<HashMap<String, String>>();
		for (int file = 0; file < in.size(); file++) {
			s = in.get(file);
			linenum++;
			debugwriter.write(_("Current line: " + linenum) + '\n');
			System.out.print(_("Current line: " + linenum) + '\n');

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

			/*
			 * Files from this facility basically read like "detailed outputs",
			 * so this parser's a lot different than the others.
			 */
			if (s.toUpperCase().contains("ACCESSION REPORT") && drugs == true) {
				drugs = false;
				tempfirst = "";
				templast = "";
				tempmiddle = "";
				tempdob = "";
				tempssnprefix = "";
				tempssn = "";
				tempgender = "";
				tempculture = "";
				tempspecific = "";
				tempisolation = "";
				isolatenum = 0;
				vitek_isolates = new ArrayList<HashMap<String, String>>();
			} else if (s.toUpperCase().contains("DEDUCED")) {
				continue;
			} else if (s.toUpperCase().contains("ACCESSION NUMBER")) {
				stringsplitter = s.split(":");
				tempacc = stringsplitter[1].trim();
			} else if (s.toUpperCase().contains("ISOLATE NUMBER")) {
				// Sometimes Vitek machines record the wrong isolate numbers
				stringsplitter = s.split("\t");
				namesplitter = stringsplitter[1].split("<");
				namesplitter[0] = namesplitter[0].trim();
				tempiso = "0" + stringsplitter[0].split(" ")[2];
				tempidentifier = tempacc + "-" + tempiso;
				bacindex = identifierCheck(identifiers, tempidentifier);

				if (bacindex == -1) {
					identifiers.add(tempidentifier);
					bacterium = new Bacterium(tempacc, tempiso);
					bacteriacollection.add(bacterium);
					bacindex = identifierCheck(identifiers, tempidentifier);
				}
				hm.put(stringsplitter[0].split(" ")[2], namesplitter[1].substring(0, namesplitter[1].length() - 1));
				bacteriacollection.get(bacindex).addVitekIdentifier(hm);
				hm = new HashMap<String, String>();
				bacteriacollection.get(bacindex).setFirstName(tempfirst);
				bacteriacollection.get(bacindex).setLastName(templast);
				bacteriacollection.get(bacindex).setMiddle(tempmiddle);
				bacteriacollection.get(bacindex).setDOB(tempdob);
				bacteriacollection.get(bacindex).setSSN(tempssn);
				if (bacteriacollection.get(bacindex).getSSNPrefix().equals("")) {
					bacteriacollection.get(bacindex).setSSNPrefix(tempssnprefix);
				}
				bacteriacollection.get(bacindex).setGender(tempgender);
				bacteriacollection.get(bacindex).setCultureType(tempculture);
				bacteriacollection.get(bacindex).setSpecific(tempspecific);
				bacteriacollection.get(bacindex).setIsolationDate(tempisolation);
				if (bacteriacollection.get(bacindex).getIsolationDate().contains("/")) {
					Boolean ignore_date = true;
					bacteriacollection.get(bacindex)
							.setSpin(Parser.makeSpin(bacteriacollection.get(bacindex), sourcehospital, ignore_date));

				}
				bacteriacollection.get(bacindex)
						.setName(Organism.convert(namesplitter[0].trim(), organismcollection, warnwriter));
				bacteriacollection.get(bacindex)
						.setShortName(Organism.convertShort(namesplitter[0].trim(), organismcollection, warnwriter));
				bacteriacollection.get(bacindex).setLocation(mtf.convert(sourcehospital, mtfcollection, warnwriter));
				bacteriacollection.get(bacindex).setEquipment("Vitek");
				/* BAMC appends stuff onto their accession numbers */
				if ("0109".equals(sourcehospital) && tempisolation.contains("/")) {

					isolationsplitter = tempisolation.split("/");
					if (!(tempacc.contains("BAB"))) {
						String year = isolationsplitter[2].substring(2, 4);
						/* Zero-padding */
						if (isolationsplitter[0].length() < 2) {
							isolationsplitter[0] = "0" + isolationsplitter[0];
						}
						if (isolationsplitter[1].length() < 2) {
							isolationsplitter[1] = "0" + isolationsplitter[1];
						}
						if (year.length() < 2) {
							year = "0" + year;
						}
						bamcaccession = year + isolationsplitter[0] + isolationsplitter[1] + "BAB" + tempacc;
					}
					bacteriacollection.get(bacindex).setBAMCAccession(bamcaccession);
					bamcaccession = "null";
				}
			} else if (s.toUpperCase().contains("LAST NAME")) {
				stringsplitter = s.split(":");
				namesplitter = stringsplitter[1].split(",");
				templast = namesplitter[0].trim();
				tempfirst = namesplitter[1].trim();
				if (namesplitter.length >= 3) {
					if (namesplitter[2].length() > 1) {
						namesplitter[2] = namesplitter[2].substring(0, 1);
					}
					tempmiddle = namesplitter[2].trim();
				}
			} else if (s.toUpperCase().contains("BIRTH DATE:")) {
				stringsplitter = s.split(":");
				tempdob = stringsplitter[1].trim();
			} else if (s.toUpperCase().contains("PATIENT ID:")) {
				stringsplitter = s.split(":");
				stringsplitter[1] = stringsplitter[1].trim();
				if (stringsplitter[1].contains("/")) {
					ssnsplitter = stringsplitter[1].split("/");
					tempssnprefix = ssnsplitter[0];
					tempssn = ssnsplitter[1];
				} else {
					tempssn = stringsplitter[1];
				}
			} else if (s.toUpperCase().contains("GENDER:")) {
				stringsplitter = s.split(":");
				tempgender = stringsplitter[1].trim();
			} else if (s.toUpperCase().contains("SOURCE:")) {
				stringsplitter = s.split(":");
				tempculture = stringsplitter[1].trim();
			} else if (s.toUpperCase().contains("LOCATION (COLLECTION):")) {
				stringsplitter = s.split(":");
				tempspecific = stringsplitter[1].trim();
			} else if (s.toUpperCase().contains("DATE RECEIVED:")) {
				stringsplitter = s.split(" ");
				tempisolation = stringsplitter[2].trim();
			} else if (s.toUpperCase().contains("DEDUCED") && s.toUpperCase().contains("MANUAL")) {
				drugs = false;
			} else if (s.toUpperCase().contains("DIAM.")) {
				drugs = true;
				isolate_line = in.get(file);
				String v_isolate_line = in.get(file - 1);
				String[] v_isolate_splitter = v_isolate_line.split("\t");
				for (int v_iso = 0; v_iso < v_isolate_splitter.length; v_iso++) {
					try {
						String[] hash_splitter = v_isolate_splitter[v_iso].split(" ");
						hm.put(hash_splitter[0], hash_splitter[1]);
						vitek_isolates.add(hm);
						hm = new HashMap<String, String>();
					} catch (ArrayIndexOutOfBoundsException e) {
						continue;
					}

				}
			} else if (drugs == true) {
				/*
				 * To make sure we don't have missing calls (in case we have a
				 * drug and MIC, but no call), we replace each tab with "Y " and
				 * split on "Y". Adding an XXX at the beginning of each entry
				 * and deleting it ensures every stringsplitter has a size of
				 * (isolates * 3) + 1.
				 */
				s = s.replaceAll("\t", "Y XXX");
				stringsplitter = s.split("Y ");
				for (int x = 1; x < stringsplitter.length; x++) {
					stringsplitter[x] = stringsplitter[x].substring(3);
				}
				String[] split_line = isolate_line.split("\t");
				if (isolatenum == 0) {
					for (int x = 1; x < split_line.length; x++) {
						if (split_line[x].contains("Cat.")) {
							isolatenum++;
						}
					}
				}

				/* Add drug info, mics, and calls */
				if (s.length() > 1) {
					for (int i = 0; i < bacteriacollection.size(); i++) {
						if (bacteriacollection.get(i).getAccession().equals(tempacc)) {
							for (int j = 0; j < vitek_isolates.size(); j++) {
								// System.out.println(bacteriacollection.get(i).getVitekIdentifier().toString());
								// System.out.println(vitek_isolates.get(j).toString());
								if (bacteriacollection.get(i).getVitekIdentifier().toString()
										.equals(vitek_isolates.get(j).toString())) {
									drugindex = drugIdentifierCheck(stringsplitter[0].trim(), drugnamecollection,
											readdrugfile);
									if (drugindex == -2) {
										warnwriter.write(MessageFormat.format(
												_("Warning: the drug name \"{0}\" does not appear in {1}. Either this is a typo, or the name is missing from the file."),
												new Object[] { stringsplitter[0].trim(), "drugnames.txt" }) + '\n');
										bacteriacollection.get(i).addDrug(stringsplitter[0].trim());
										drugname = stringsplitter[0].trim();
									} else {
										bacteriacollection.get(i)
												.addDrug(drugnamecollection.get(drugindex).getProperName());
										drugname = drugnamecollection.get(drugindex).getProperName();
									}
									try {
									bacteriacollection.get(i).addOldMIC(stringsplitter[((j + 1) * 2) + (j - 1)]);
									bacteriacollection.get(i).addMIC(Resistances
											.MICConvert(stringsplitter[((j + 1) * 2) + (j - 1)], warnwriter));
									mic = Resistances.MICConvert(stringsplitter[((j + 1) * 2) + (j - 1)], warnwriter);
									oldmic = stringsplitter[((j + 1) * 2) + (j - 1)];
									if (calcen.equals(true)) {
										bacteriacollection.get(i).addCall(calculateCall(
												bacteriacollection.get(bacindex), drugname, mic, oldmic,
												breakpointcollection, warnwriter,
												call.convert(stringsplitter[((j + 1) * 2) + (j + 1)], warnwriter)));
									} else {
										bacteriacollection.get(i).addCall(stringsplitter[((j + 1) * 2) + (j + 1)]);
									}
									} catch (ArrayIndexOutOfBoundsException e) {
										bacteriacollection.get(i).addMIC(-1.0);
										bacteriacollection.get(i).addOldMIC("-1");
										bacteriacollection.get(i).addCall("N");
									}

								}
							}
						}
					}
				}
			}
		}
		return bacteriacollection;
	}
}
