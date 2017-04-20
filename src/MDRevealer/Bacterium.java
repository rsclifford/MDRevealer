package MDRevealer;

import java.util.ArrayList;
import java.util.HashMap;

/* Bacterium - defines a class for storing information read from reports
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
public class Bacterium {

	private ArrayList<String> resistant;
	private ArrayList<String> sensitive;
	private ArrayList<String> drug;
	private ArrayList<Double> mic;
	private ArrayList<String> call;
	private String location;
	private String accession;
	private String BAMCaccession;
	private String isolate;
	private String name;
	private String shortname;
	private String identifier;
	private String mdr;
	private Boolean carb;
	private String esbl;
	private String machine_esbl;
	private String machine_amp_c;
	private Boolean vre;
	private Boolean oxa;
	private Boolean generictest;
	private String lastname;
	private String firstname;
	private String middle;
	private String spin;
	private String ssnprefix;
	private String ssn;
	private String eidpn;
	private String dob;
	private String gender;
	private String isolationdate;
	private String culturetype;
	private String source;
	private String locationtype;
	private String altname;
	private String equipment;
	private String specific;
	private String id;
	private ArrayList<String> oldmic;
	private ArrayList<String> custom;
	private ArrayList<HashMap<String, String>> vitek_identifier;
	private String dateformat;

	public Bacterium() {
		resistant = new ArrayList<>(0);
		sensitive = new ArrayList<>(0);
		drug = new ArrayList<>(0);
		mic = new ArrayList<>(0);
		call = new ArrayList<>(0);
		location = "";
		accession = "";
		BAMCaccession = "null";
		isolate = "01";
		name = "temp";
		shortname = "temp";
		identifier = "t";
		mdr = "not MDR";
		carb = false;
		esbl = "null";
		machine_esbl = "null";
		machine_amp_c = "null";
		vre = false;
		oxa = false;
		generictest = true;
		lastname = "";
		firstname = "";
		middle = "";
		spin = "";
		ssnprefix = "";
		ssn = "";
		dob = "";
		gender = "";
		isolationdate = "";
		culturetype = "";
		source = "";
		locationtype = "";
		altname = "";
		equipment = "";
		specific = "";
		oldmic = new ArrayList<>(0);
		custom = new ArrayList<>(0);
		vitek_identifier = new ArrayList<>();
		dateformat = "MM/dd/YYYY";
	}

	public Bacterium(String acc, String iso) {
		resistant = new ArrayList<>(0);
		sensitive = new ArrayList<>(0);
		drug = new ArrayList<>(0);
		mic = new ArrayList<>(0);
		call = new ArrayList<>(0);
		location = "";
		accession = acc;
		BAMCaccession = acc;
		isolate = iso;
		name = "temp";
		shortname = "temp";
		identifier = acc + "-" + iso;
		mdr = "not MDR";
		carb = false;
		esbl = "null";
		machine_esbl = "null";
		machine_amp_c = "null";
		vre = false;
		oxa = false;
		generictest = true;
		lastname = "";
		firstname = "";
		middle = "";
		spin = "";
		ssnprefix = "";
		ssn = "";
		dob = "";
		gender = "";
		isolationdate = "";
		culturetype = "";
		source = "";
		locationtype = "";
		altname = "";
		equipment = "";
		specific = "";
		oldmic = new ArrayList<>(0);
		custom = new ArrayList<>(0);
		vitek_identifier = new ArrayList<>();
		dateformat = "MM/dd/YYYY";
	}

	public String getResistant(int index) {
		return resistant.get(index);
	}

	public void addResistant(String resistant) {
		this.resistant.add(resistant);
	}

	public Boolean resistantIsEmpty() {
		return this.resistant.isEmpty();
	}

	public int resistantSize() {
		return this.resistant.size();
	}

	public void clearResistant() {
		this.resistant.clear();
	}

	public String getSensitive(int index) {
		return sensitive.get(index);
	}

	public void addSensitive(String sensitive) {
		this.sensitive.add(sensitive);
	}

	public Boolean sensitiveIsEmpty() {
		return this.sensitive.isEmpty();
	}

	public int sensitiveSize() {
		return this.sensitive.size();
	}

	public void clearSensitive() {
		this.sensitive.clear();
	}

	public String getDrug(int index) {
		return drug.get(index);
	}

	public void addDrug(String drugname) {
		this.drug.add(drugname);
	}

	public void clearDrug() {
		this.drug.clear();
	}

	public void removeDrug(int index) {
		this.drug.remove(index);
	}

	public int drugSize() {
		return this.drug.size();
	}

	public String getCall(int index) {
		if (call.get(index).isEmpty() == false) {
			return call.get(index);
		} else {
			return "";
		}
	}

	public void addCall(String call) {
		this.call.add(call);
	}

	public int callSize() {
		return this.call.size();
	}

	public void removeCall(int index) {
		this.call.remove(index);
	}

	public Double getMIC(int index) {
		return mic.get(index);
	}

	public void addMIC(Double mic) {
		this.mic.add(mic);
	}

	public int MICSize() {
		return this.mic.size();
	}

	public void removeMIC(int index) {
		this.mic.remove(index);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getBAMCAccession() {
		return BAMCaccession;
	}

	public void setBAMCAccession(String BAMCaccession) {
		this.BAMCaccession = BAMCaccession;
	}

	public String getIsolate() {
		return isolate;
	}

	public void setIsolate(String isolate) {
		this.isolate = isolate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortname;
	}

	public void setShortName(String shortname) {
		this.shortname = shortname;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String accession, String isolate) {
		this.identifier = accession + "-" + isolate;
	}

	public void setIdentifier(String linenum) {
		this.identifier = linenum;
	}

	public String getMdr() {
		return mdr;
	}

	public void setMdr(String mdr) {
		this.mdr = mdr;
	}

	public Boolean getCarb() {
		return carb;
	}

	public void setCarb(Boolean carb) {
		this.carb = carb;
	}

	public String getESBL() {
		return esbl;
	}

	public void setESBL(String esbl) {
		this.esbl = esbl;
	}

	public String getMachineESBL() {
		return machine_esbl;
	}

	public void setMachineESBL(String machine_esbl) {
		this.machine_esbl = machine_esbl;
	}

	public String getMachineAMPC() {
		return machine_amp_c;
	}

	public void setMachineAMPC(String machine_amp_c) {
		this.machine_amp_c = machine_amp_c;
	}

	public Boolean getVRE() {
		return vre;
	}

	public void setVRE(Boolean vre) {
		this.vre = vre;
	}

	public Boolean getOxa() {
		return oxa;
	}

	public void setOxa(Boolean oxa) {
		this.oxa = oxa;
	}

	public Boolean getGeneric() {
		return generictest;
	}

	public void setGeneric(Boolean generictest) {
		this.generictest = generictest;
	}

	public String getFirstName() {
		return firstname;
	}

	public void setFirstName(String firstname) {
		this.firstname = firstname;
	}

	public String getLastName() {
		return lastname;
	}

	public void setLastName(String lastname) {
		this.lastname = lastname;
	}

	public String getMiddle() {
		return middle;
	}

	public void setMiddle(String middle) {
		this.middle = middle;
	}

	public String getSpin() {
		return spin;
	}

	public void setSpin(String spin) {
		this.spin = spin;
	}

	public String getSSNPrefix() {
		return ssnprefix;
	}

	public void setSSNPrefix(String ssnprefix) {
		if (this.ssnprefix.equals("")) {
			this.ssnprefix = ssnprefix;
		}
	}

	public String getSSN() {
		return ssn;
	}

	/*
	 * Set up to handle datafiles where the SSN prefix is stuck onto the
	 * beginning of the SSN
	 */
	public void setSSN(String ssn) {
		String tempspref = "";
		while (ssn.length() > 9) {
			tempspref = tempspref + ssn.substring(0, 1);
			ssn = ssn.substring(1);
		}
		this.ssn = ssn;
		if (tempspref != null) {
			this.ssnprefix = tempspref;
		}
	}

	public String getEIDPN() {
		return ssnprefix;
	}

	public void setEIDPN(String eidpn) {
		if (this.eidpn.equals("")) {
			this.eidpn = eidpn;
		}
	}

	public String getDOB() {
		return dob;
	}

	public void setDOB(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIsolationDate() {
		return isolationdate;
	}

	public void setIsolationDate(String isolationdate) {
		this.isolationdate = isolationdate;
	}

	public String getCultureType() {
		return culturetype;
	}

	public void setCultureType(String culturetype) {
		this.culturetype = culturetype;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getLocationType() {
		return locationtype;
	}

	public void setLocationType(String locationtype) {
		this.locationtype = locationtype;
	}

	public String getAltName() {
		return altname;
	}

	public void setAltName(String altname) {
		this.altname = altname;
	}

	public String getEquipment() {
		return equipment;
	}

	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}

	public String getSpecific() {
		return specific;
	}

	public void setSpecific(String specific) {
		this.specific = specific;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getOldMIC(int index) {
		return oldmic.get(index);
	}

	public void addOldMIC(String oldmic) {
		this.oldmic.add(oldmic);
	}

	public String getCustom(int index) {
		return custom.get(index);
	}

	public void setCustom(String c) {
		this.custom.add(c);
	}

	public int getCustomSize() {
		return custom.size();
	}

	public HashMap<String, String> getVitekIdentifier() {
		return vitek_identifier.get(0);
	}

	public void addVitekIdentifier(HashMap<String, String> h) {
		this.vitek_identifier.add(h);
	}
	
	public String getDateFormat() {
		return this.dateformat;
	}
	
	public void setDateFormat(String s) {
		this.dateformat = s;
	}
}
