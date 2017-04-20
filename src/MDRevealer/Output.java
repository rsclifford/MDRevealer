package MDRevealer;

import java.awt.FileDialog;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static MDRevealer.Util._;

/* Output - prints out the data after it's been parsed
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
public class Output {

    /*
     * Read through a bacterium's list of drugs. If it was tested, write the
     * call and MIC. Otherwise, write two tab characters.
     */
    public static void spreadDrugWrite(String drugname, Bacterium b,
            FileWriter spreadsheet) throws IOException {
        int drugindex = -1;
        for (int i = 0; i < b.drugSize(); i++) {
            if (b.getDrug(i).equals(drugname)) {
                drugindex = i;
            }
        }
        if (drugindex == -1) {
            spreadsheet.write('\t');
            spreadsheet.write('\t');
        } else {
            spreadsheet.write(b.getCall(drugindex) + '\t');
            spreadsheet.write(b.getOldMIC(drugindex) + '\t');
        }
    }

    public static void printOutput(ArrayList<Bacterium> bacteriacollection,
            ArrayList<MDR> mdrcollection, JFrame mainwindow, String[] defaults)
            throws IOException {
        /* Initialize the stuff for writing to a file */
        FileWriter output;
        if (!defaults[0].equals("null")) {
            output = new FileWriter(defaults[0]);
        } else {
            FileDialog getoutput = new FileDialog(mainwindow,
                    _("Select a place to save the detailed report"),
                    FileDialog.SAVE);
            getoutput.setVisible(true);
            if (getoutput.getFile() == null) {
                output = new FileWriter("output.txt");
            } else {
                output = new FileWriter(getoutput.getDirectory()
                        + getoutput.getFile());
            }
        }
        for (int i = 0; i < bacteriacollection.size(); i++) {

            /* Formatting the results */
            output.write('\n');
            output.write('\n');
            output.write(bacteriacollection.get(i).getShortName()
                    + " (accession "
                    + bacteriacollection.get(i).getAccession() + ", isolate "
                    + bacteriacollection.get(i).getIsolate());
            if (bacteriacollection.get(i).getLocation().equals("test")) {
                output.write(", found at an unspecified location");
            } else {
                output.write(", found at "
                        + bacteriacollection.get(i).getLocation().trim());
            }
            output.write(") is " + bacteriacollection.get(i).getMdr() + "."
                    + '\n');
            if (bacteriacollection.get(i).getCarb() == true) {
                output.write("It is resistant to Carbapenem." + "\n");
            }
            if (bacteriacollection.get(i).getESBL().equals("true")) {
                output.write("It is ESBL." + "\n");
            }
            if (bacteriacollection.get(i).getVRE() == true) {
                output.write("It is resistant to VRE." + "\n");
            }
            if (bacteriacollection.get(i).getOxa() == true) {
                output.write("It is MRSA." + "\n");
            }
            for (int j = 0; j < bacteriacollection.get(i).getCustomSize(); j++) {
                output.write("It is " + bacteriacollection.get(i).getCustom(j)
                        + '\n');
            }

            output.write('\n');
            output.write("It resists:" + '\n');

            if (bacteriacollection.get(i).resistantIsEmpty() == true) {
                output.write("Nothing" + '\n');
            } else {
                for (int m = 0; m <= bacteriacollection.get(i).resistantSize() - 1; m++) {
                    output.write(bacteriacollection.get(i).getResistant(m));
                    for (int n = 0; n < mdrcollection.size(); n++) {
                        if (bacteriacollection.get(i).getShortName()
                                .contains(mdrcollection.get(n).getName())) {
                            for (int o = 0; o < mdrcollection.get(n)
                                    .getDrugnameSize(); o++) {
                                if (bacteriacollection
                                        .get(i)
                                        .getResistant(m)
                                        .contains(
                                                mdrcollection.get(n)
                                                        .getDrugname(o))) {
                                    output.write(" ("
                                            + mdrcollection.get(n)
                                                    .getCategory(o) + ")");
                                }
                            }
                        }
                    }
                    output.write('\n');
                }
            }
            output.write('\n');
            output.write("It is sensitive to:" + '\n');
            if (bacteriacollection.get(i).sensitiveIsEmpty() == true) {
                output.write("Nothing" + '\n');
            } else {
                for (int m = 0; m <= bacteriacollection.get(i).sensitiveSize() - 1; m++) {
                    output.write(bacteriacollection.get(i).getSensitive(m));
                    for (int n = 0; n < mdrcollection.size(); n++) {
                        if (bacteriacollection.get(i).getShortName()
                                .contains(mdrcollection.get(n).getName())) {
                            for (int o = 0; o < mdrcollection.get(n)
                                    .getDrugnameSize(); o++) {
                                if (bacteriacollection
                                        .get(i)
                                        .getSensitive(m)
                                        .contains(
                                                mdrcollection.get(n)
                                                        .getDrugname(o))) {
                                    output.write(" ("
                                            + mdrcollection.get(n)
                                                    .getCategory(o) + ")");
                                }
                            }
                        }
                    }
                    output.write('\n');

                }
            }
        }
        JOptionPane.showMessageDialog(mainwindow,
                _("Detailed report written successfully."));
        output.close();
    }

    public static void printSummary(ArrayList<Bacterium> bacteriacollection,
            ArrayList<MDR> mdrcollection, JFrame mainwindow, String[] defaults)
            throws IOException {
        FileWriter summary;
        if (!defaults[1].equals("null")) {
            summary = new FileWriter(defaults[1]);
        } else {
            FileDialog getsummary = new FileDialog(mainwindow,
                    _("Select a place to save the summary"), FileDialog.SAVE);
            getsummary.setVisible(true);
            if (getsummary.getFile() == null) {
                summary = new FileWriter("summary.txt");
            } else {
                summary = new FileWriter(getsummary.getDirectory()
                        + getsummary.getFile());
            }
        }
        Boolean prevwrote = false;
        Boolean wrotesomething = false;

        for (int i = 0; i < bacteriacollection.size(); i++) {

            if (!"not MDR".equals(bacteriacollection.get(i).getMdr())
                    && !"from a species not tested for MDR status"
                            .equals(bacteriacollection.get(i).getMdr())) {
                summary.write(bacteriacollection.get(i).getMdr());
                prevwrote = true;
                wrotesomething = true;
            }

            if (bacteriacollection.get(i).getCarb().equals(true)) {
                if (prevwrote == true) {
                    summary.write("+");

                }
                summary.write("CARB");
                prevwrote = true;
                wrotesomething = true;
            }

            if (bacteriacollection.get(i).getESBL().equals("true")) {
                if (prevwrote == true) {
                    summary.write("+");

                }
                summary.write("ESBL");
                prevwrote = true;
                wrotesomething = true;
            }

            if (bacteriacollection.get(i).getVRE().equals(true)) {
                if (prevwrote == true) {
                    summary.write("+");

                }
                summary.write("VRE");
                wrotesomething = true;
            }

            if (bacteriacollection.get(i).getOxa().equals(true)) {
                if (prevwrote == true) {
                    summary.write("+");

                }
                summary.write("MRSA");
                wrotesomething = true;
            }

            if (bacteriacollection.get(i).getCustomSize() > 0) {
                if (prevwrote == true) {
                    summary.write("+");

                }
                for (int j = 0; j < bacteriacollection.get(i).getCustomSize(); j++) {
                    summary.write(bacteriacollection.get(i).getCustom(j));
                    if (j + 1 != bacteriacollection.get(i).getCustomSize()) {
                        summary.write("+");
                    }
                }
                wrotesomething = true;
            }

            if (wrotesomething == false) {
                summary.write("-");
            }
            summary.write('\t' + bacteriacollection.get(i).getLastName()
                    + '\t' + bacteriacollection.get(i).getAccession() + '\t'
                    + bacteriacollection.get(i).getIsolate() + '\t'
                    + bacteriacollection.get(i).getShortName() + '\t'
                    + bacteriacollection.get(i).getIsolationDate() + '\t'
                    + bacteriacollection.get(i).getCultureType());
            summary.write('\n');
            prevwrote = false;
            wrotesomething = false;
        }
        JOptionPane.showMessageDialog(mainwindow,
                _("Summary written successfully."));
        summary.close();
    }

    public static void printSpreadsheet(
            ArrayList<Bacterium> bacteriacollection,
            ArrayList<MDR> mdrcollection, JFrame mainwindow, String[] defaults)
            throws IOException {
        Boolean prevwrote = false;
        Boolean wrotesomething = false;
        FileWriter spreadsheet;
        String equipment;
        String prefix = "";

        if (!defaults[2].equals("null")) {
            spreadsheet = new FileWriter(defaults[2]);
        } else {
            FileDialog getspreadsheet = new FileDialog(mainwindow,
                    _("Select a place to save the spreadsheet"),
                    FileDialog.SAVE);
            getspreadsheet.setVisible(true);
            if (getspreadsheet.getFile() == null) {
                spreadsheet = new FileWriter("spreadsheet.txt");
            } else {
                spreadsheet = new FileWriter(getspreadsheet.getDirectory()
                        + getspreadsheet.getFile());
            }
        }
        spreadsheet
                .write("type	Last Name	First Name	Middle I	Defense Medical Information System Identifier	SPIN	SSN prefix	SSN	DOB	Gender	CHCS Specimen Accession Number	Date of Culture	Culture Type	Culture site	Patient Location Type	Specific Patient Location	Isolate Number	Organism ID	Alternative Organism ID	Diagnostic Equipment Used	amikacin	amikacin MIC	amox/clavulanate	amox/clav MIC	amp/sulbactam	amp/sulb MIC	ampicillin	ampicillin MIC	azithromycin	azithro MIC	aztreonam	aztreonam MIC	cefazolin	cefazolin MIC	cefepime	cefepime MIC	cefotaxime	cefotaxime MIC	cefotetan	cefotetan MIC	cefoxitin	cefoxitin MIC	ceftaroline	ceftaroline MIC	ceftazidime	ceftaz MIC	ceftriaxone	ceftriaxone MIC	cefuroxime	cefurox MIC	cephalothin	cephalothin MIC	chloramphenicol	chlor MIC	ciprofloxacin	cipro MIC	clarithromycin	clari MIC	clindamycin	clinda MIC	colistin	colistin MIC	daptomycin	dapto MIC	dicloxacillin	diclox MIC	doripenem	doripenem MIC	doxycycline	doxycycline MIC	ertapenem	ertapenem MIC	erythromycin	erythro MIC	gatifloxacin	gatifloxacin MIC	gentamicin	gent MIC	imipenem	imi MIC	levofloxacin	levoflox MIC	linezolid	linezolid MIC	meropenem	merop MIC	metronidazole	metro MIC	minocycline	mino MIC	moxifloxacin	moxi MIC	nitrofurantoin	nitro MIC	norfloxacin	norfloxacin MIC	oxacillin	oxacillin MIC	penicillin	penicillin MIC	pip/tazobactam	pip/tazo MIC	polymixin B	polymix B MIC	quinupristin/dalfop	Synercid - quini/dalf MIC	rifampin	rifampin MIC	streptomycin	strepto MIC	telavancin	telavancin MIC	tetracycline	tetracycline MIC	ticar/clavulanate	ticar/clav MIC	tigecycline	tigecycline MIC	tinidazole	tinidazole MIC	tobramycin	tobra MIC	trimethorprim/sulfa	tmp/smx MIC	vancomycin	vancomycin MIC	vancomycin PO	vancomycin PO MIC	Other	ESBL	AMP C" + '\n');
        for (int i = 0; i < bacteriacollection.size(); i++) {
            switch (bacteriacollection.get(i).getEquipment()) {
            case "MicroScan":
                equipment = "S-microscan";
                break;
            case "Phoenix":
                equipment = "BD-phoenix";
                break;
            case "Vitek":
                equipment = "BM-vitek";
                break;
            default:
                equipment = bacteriacollection.get(i).getEquipment();
                break;
            }
            if (!"not MDR".equals(bacteriacollection.get(i).getMdr())
                    && !"from a species not tested for MDR status"
                            .equals(bacteriacollection.get(i).getMdr())) {
                spreadsheet.write(bacteriacollection.get(i).getMdr());
                prevwrote = true;
                wrotesomething = true;
            }

            if (bacteriacollection.get(i).getCarb().equals(true)) {
                if (prevwrote == true) {
                    spreadsheet.write("+");

                }
                spreadsheet.write("CARB");
                prevwrote = true;
                wrotesomething = true;
            }

            if (bacteriacollection.get(i).getESBL().equals("true")) {
                if (prevwrote == true) {
                    spreadsheet.write("+");
                }
                spreadsheet.write("ESBL");
                prevwrote = true;
                wrotesomething = true;
            }

            if (bacteriacollection.get(i).getVRE().equals(true)) {
                if (prevwrote == true) {
                    spreadsheet.write("+");

                }
                spreadsheet.write("VRE");
                wrotesomething = true;
            }

            if (bacteriacollection.get(i).getOxa().equals(true)) {
                if (prevwrote == true) {
                    spreadsheet.write("+");

                }
                spreadsheet.write("MRSA");
                wrotesomething = true;
            }

            if (bacteriacollection.get(i).getCustomSize() > 0) {
                if (prevwrote == true) {
                    spreadsheet.write("+");

                }
                for (int j = 0; j < bacteriacollection.get(i).getCustomSize(); j++) {
                    spreadsheet.write(bacteriacollection.get(i).getCustom(j));
                    if (j + 1 < bacteriacollection.get(i).getCustomSize()) {
                        spreadsheet.write("+");
                    }
                }
                wrotesomething = true;
            }

            if (wrotesomething == false) {
                spreadsheet.write("-");
            }
            spreadsheet.write('\t');
            prevwrote = false;
            wrotesomething = false;

            spreadsheet.write(bacteriacollection.get(i).getLastName() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getFirstName() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getMiddle() + '\t');
            spreadsheet
                    .write(bacteriacollection.get(i).getLocation().trim() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getSpin() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getSSNPrefix() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getSSN() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getDOB() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getGender() + '\t');
            if (bacteriacollection.get(i).getLocation().trim().equals("9067")) {
                prefix = "NBA ";
                if (bacteriacollection.get(i).getCultureType().toUpperCase()
                        .contains("BLOOD")
                        || bacteriacollection.get(i).getCultureType()
                                .toUpperCase().contains("CATHETER")) {
                    prefix = "NBL ";
                }
                spreadsheet.write(prefix);
            }
            spreadsheet.write(bacteriacollection.get(i).getAccession() + '\t');
            spreadsheet
                    .write(bacteriacollection.get(i).getIsolationDate() + '\t');
            spreadsheet
                    .write(bacteriacollection.get(i).getCultureType() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getSource() + '\t');
            spreadsheet
                    .write(bacteriacollection.get(i).getLocationType() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getSpecific() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getIsolate() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getShortName() + '\t');
            spreadsheet.write(bacteriacollection.get(i).getAltName() + '\t');
            spreadsheet.write(equipment + '\t');
            spreadDrugWrite("Amikacin", bacteriacollection.get(i), spreadsheet);
            spreadDrugWrite("Amox/K Clav", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Amp/Sulbactam", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Ampicillin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Azithromycin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Aztreonam", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Cefazolin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Cefepime", bacteriacollection.get(i), spreadsheet);
            spreadDrugWrite("Cefotaxime", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Cefotetan", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Cefoxitin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Ceftaroline", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Ceftazidime", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Ceftriaxone", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Cefuroxime", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Cephalothin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Chloramphenicol", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Ciprofloxacin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Clarithromycin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Clindamycin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Colistin", bacteriacollection.get(i), spreadsheet);
            spreadDrugWrite("Daptomycin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Dicloxacillin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Doripenem", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Doxycycline", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Ertapenem", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Erythromycin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Gatifloxacin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Gentamicin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Imipenem", bacteriacollection.get(i), spreadsheet);
            spreadDrugWrite("Levofloxacin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Linezolid", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Meropenem", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Metronidazole", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Minocycline", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Moxifloxacin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Nitrofurantoin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Norfloxacin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Oxacillin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Penicillin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Pip/Tazo", bacteriacollection.get(i), spreadsheet);
            spreadDrugWrite("Polymixin B", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Quinup/Dalfop", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Rifampin", bacteriacollection.get(i), spreadsheet);
            spreadDrugWrite("Streptomycin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Telavancin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Tetracycline", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Ticar/K Clav", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Tigecycline", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Tinidazole", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Tobramycin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Trimeth/Sulfa", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Vancomycin", bacteriacollection.get(i),
                    spreadsheet);
            spreadDrugWrite("Vancomycin PO", bacteriacollection.get(i),
                    spreadsheet);
            spreadsheet.write('\t'); /* Other - always empty AFAIK */
            switch (bacteriacollection.get(i).getMachineESBL()) {
            case "true":
                spreadsheet.write("+" + '\t');
                break;
            case "false":
                spreadsheet.write("-" + '\t');
                break;
            default:
                spreadsheet.write('\t');
                break;
            }
            switch (bacteriacollection.get(i).getMachineAMPC()) {
            case "true":
                spreadsheet.write("Pos" + '\t');
                break;
            case "false":
                spreadsheet.write("Neg" + '\t');
                break;
            default:
                spreadsheet.write('\t');
                break;
            }
            spreadsheet.write('\n');
        }
        JOptionPane.showMessageDialog(mainwindow,
                _("Spreadsheet written successfully."));
        spreadsheet.close();
    }

    public static void printDat(ArrayList<Bacterium> bacteriacollection,
            ArrayList<MDR> mdrcollection, JFrame mainwindow,
            String[] defaults, ArrayList<String> datconfig,
            FileWriter warnwriter) throws IOException {
        FileWriter dat;
        if (!defaults[3].equals("null")) {
            dat = new FileWriter(defaults[3]);
        } else {
            FileDialog getdat = new FileDialog(mainwindow,
                    _("Select a place to save the .dat file"), FileDialog.SAVE);
            getdat.setVisible(true);
            if (getdat.getFile() == null) {
                dat = new FileWriter("dat.txt");
            } else {
                dat = new FileWriter(getdat.getDirectory() + getdat.getFile());
            }
        }
        for (int i = 0; i < datconfig.size(); i++) {
            dat.write(datconfig.get(i) + '\t');
        }
        dat.write('\n');
        for (int i = 0; i < bacteriacollection.size(); i++) {
            for (int j = 0; j < bacteriacollection.get(i).callSize(); j++) {
                /* Don't write lines if they're missing both MIC and call data */
                if (!(bacteriacollection.get(i).getOldMIC(j).equals("X") && (bacteriacollection
                        .get(i).getCall(j).equals("N") || bacteriacollection
                        .get(i).getCall(j).equals("X")))) {
                    for (int k = 0; k < datconfig.size(); k++) {
                        switch (datconfig.get(k)) {
                        case "type":
                            dat.write(bacteriacollection.get(i).getMdr() + '\t');
                            break;
                        case "lastName":
                            dat.write(bacteriacollection.get(i).getLastName() + '\t');
                            break;
                        case "firstName":
                            dat.write(bacteriacollection.get(i).getFirstName() + '\t');
                            break;
                        case "middleI":
                            dat.write(bacteriacollection.get(i).getMiddle() + '\t');
                            break;
                        case "dmisId":
                            dat.write(bacteriacollection.get(i).getLocation()
                                    .trim() + '\t');
                            break;
                        case "spin":
                            dat.write(bacteriacollection.get(i).getSpin() + '\t');
                            break;
                        case "ssnPrefix":
                            dat.write(bacteriacollection.get(i).getSSNPrefix() + '\t');
                            break;
                        case "ssn":
                            dat.write(bacteriacollection.get(i).getSSN() + '\t');
                            break;
                        case "dob":
                            dat.write(bacteriacollection.get(i).getDOB() + '\t');
                            break;
                        case "gender":
                            dat.write(bacteriacollection.get(i).getGender() + '\t');
                            break;
                        case "accession":
                            dat.write(bacteriacollection.get(i).getAccession() + '\t');
                            break;
                        case "isolationDate":
                            dat.write(bacteriacollection.get(i)
                                    .getIsolationDate() + '\t');
                            break;
                        case "cultureType":
                            dat.write(bacteriacollection.get(i)
                                    .getCultureType() + '\t');
                            break;
                        case "source":
                            dat.write(bacteriacollection.get(i).getSource() + '\t');
                            break;
                        case "locationType":
                            dat.write(bacteriacollection.get(i)
                                    .getLocationType() + '\t');
                            break;
                        case "location":
                            dat.write(bacteriacollection.get(i).getSpecific() + '\t');
                            break;
                        case "isolate":
                            dat.write(bacteriacollection.get(i).getIsolate() + '\t');
                            break;
                        case "organism":
                            dat.write(bacteriacollection.get(i).getShortName() + '\t');
                            break;
                        case "longOrganism":
                            dat.write(bacteriacollection.get(i).getName() + '\t');
                            break;
                        case "alternateOrganism":
                            dat.write(bacteriacollection.get(i).getAltName() + '\t');
                            break;
                        case "equipment":
                            dat.write(bacteriacollection.get(i).getEquipment() + '\t');
                            break;
                        case "antibiotic":
                            dat.write(bacteriacollection.get(i).getDrug(j) + '\t');
                            break;
                        case "mic":
                            dat.write(bacteriacollection.get(i).getOldMIC(j) + '\t');
                            break;
                        case "call":
                            dat.write(bacteriacollection.get(i).getCall(j) + '\t');
                            break;
                        default:
                            warnwriter.write(MessageFormat.format(
                                    _("Unrecognized option: {0}"),
                                    new Object[] { datconfig.get(k) }) + '\n');
                            break;
                        }
                    }
                    dat.write('\n');
                }
            }
        }
        JOptionPane.showMessageDialog(mainwindow,
                ".dat file written successfully.");
        dat.close();
    }

    public static void printLocalizedOutput(
            ArrayList<Bacterium> bacteriacollection,
            ArrayList<MDR> mdrcollection, JFrame mainwindow, String[] defaults)
            throws IOException {
        FileWriter output;
        if (!defaults[4].equals("null")) {
            output = new FileWriter(defaults[4]);
        } else {
            /* TRANSLATORS: Replace "English" with the name of your language. */
            FileDialog getoutput = new FileDialog(
                    mainwindow,
                    _("Select a place to save the detailed report (in English)"),
                    FileDialog.SAVE);
            getoutput.setVisible(true);
            if (getoutput.getFile() == null) {
                output = new FileWriter("output.txt");
            } else {
                output = new FileWriter(getoutput.getDirectory()
                        + getoutput.getFile());
            }
        }
        for (int i = 0; i < bacteriacollection.size(); i++) {

            /* Formatting the results */
            output.write('\n');
            output.write('\n');
            /*
             * TRANSLATORS: A sample: EC (accession 49651, isolate 01, found at
             * 9009 TEST HOSPITAL) is not MDR.
             */
            output.write(MessageFormat.format(
                    _("{0} (accession {1}, isolate {2}") + '\n', new Object[] {
                            bacteriacollection.get(i).getShortName(),
                            bacteriacollection.get(i).getAccession(),
                            bacteriacollection.get(i).getIsolate() }));
            if (bacteriacollection.get(i).getLocation().equals("test")) {
                output.write(MessageFormat.format(
                        _(", found at an unspecified location, is {0}"),
                        new Object[] { bacteriacollection.get(i).getMdr() }));
            } else {
                output.write(MessageFormat
                        .format(_(", found at {0}, is {1})"),
                                new Object[] {
                                        bacteriacollection.get(i).getMdr(),
                                        bacteriacollection.get(i)
                                                .getLocation().trim() }));
            }
            if (bacteriacollection.get(i).getCarb() == true) {
                output.write(_("It is resistant to Carbapenem.") + "\n");
            }
            if (bacteriacollection.get(i).getESBL().equals("true")) {
                /*
                 * TRANSLATORS: Leave "ESBL" untranslated. Doctors, regardless
                 * of their native language, will understand what it means.
                 */
                output.write(_("It is ESBL.") + "\n");
            }
            if (bacteriacollection.get(i).getVRE() == true) {
                /*
                 * TRANSLATORS: Leave "VRE" untranslated. Doctors, regardless of
                 * their native language, will understand what it means.
                 */
                output.write(_("It is VRE.") + "\n");
            }
            if (bacteriacollection.get(i).getOxa() == true) {
                /*
                 * TRANSLATORS: Leave "MRSA" untranslated. Doctors, regardless
                 * of their native language, will understand what it means.
                 */
                output.write(_("It is MRSA." + "\n"));
            }

            for (int j = 0; j < bacteriacollection.get(i).getCustomSize(); j++) {
                output.write(MessageFormat
                        .format(_("It is {0}.") + '\n',
                                new Object[] { bacteriacollection.get(i)
                                        .getCustom(j) }));
            }

            output.write('\n');
            output.write(_("It resists:") + '\n');

            if (bacteriacollection.get(i).resistantIsEmpty() == true) {
                output.write(_("Nothing") + '\n');
            } else {
                for (int m = 0; m <= bacteriacollection.get(i).resistantSize() - 1; m++) {
                    output.write(bacteriacollection.get(i).getResistant(m) + '\n');
                }
            }
            output.write('\n');
            output.write(_("It is sensitive to:") + '\n');
            if (bacteriacollection.get(i).sensitiveIsEmpty() == true) {
                output.write(_("Nothing") + '\n');
            } else {
                for (int m = 0; m <= bacteriacollection.get(i).sensitiveSize() - 1; m++) {
                    output.write(bacteriacollection.get(i).getSensitive(m) + '\n');
                }
            }
        }
        /* TRANSLATORS: Replace "English" with the name of your language. */
        JOptionPane.showMessageDialog(mainwindow,
                "Report (in English) written successfully.");
        output.close();
    }
}
